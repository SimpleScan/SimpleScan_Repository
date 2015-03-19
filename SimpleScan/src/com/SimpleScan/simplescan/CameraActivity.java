package com.SimpleScan.simplescan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.SimpleScan.simplescan.Camera.CameraEngine;
import com.SimpleScan.simplescan.Camera.DragRectView;
import com.SimpleScan.simplescan.Camera.OCR;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.ShutterCallback {

	static final String TAG = "DBG_" + "CameraActivity";

	public static final int MEDIA_TYPE_IMAGE = 1;
	
	private DragRectView Rectview;
	
	protected boolean _preview;
	
    Button shutterButton;
    Button focusButton;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;
    
    Button saveButton;
    Button retakeButton;
    Button flashButton;
    TextView OCRtext;
	ImageView PreviewImage;
	
	Bitmap bitmap;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "Surface Created - starting camera");

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }

        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera engine already on");
            return;
        }

        cameraEngine = new CameraEngine(holder);
        cameraEngine.start();

        Log.d(TAG, "Camera engine started");
    }
    
	@Override
    protected void onResume() {
        super.onResume();

        if(_preview) {
        	setPreview();
        } else{
        	cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
	        shutterButton = (Button) findViewById(R.id.shutter_button);
	        focusButton = (Button) findViewById(R.id.focus_button);
	        //flashButton = (Button) findViewById(R.id.flash_button);

	        shutterButton.setOnClickListener(this);
	        focusButton.setOnClickListener(this);
	        //flashButton.setOnClickListener(this);
	
	        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
	        surfaceHolder.addCallback(this);
	        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	
	        cameraFrame.setOnClickListener(this);
        }
    }

	@Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }
	
	@Override
    public void onClick(View v) {
        if(v == shutterButton){
            if(cameraEngine != null && cameraEngine.isOn()){
                cameraEngine.takeShot(this, this, this);
            }
        }
        
        if(v == focusButton){
            if(cameraEngine!=null && cameraEngine.isOn()){
                cameraEngine.requestFocus();
            }
        }
        /*
        if(v == flashButton) {
        	if(cameraEngine!=null && cameraEngine.isOn()){
                cameraEngine.toggleFlash();
            }
        }
        */
    }
    

    @Override
    public void onPictureTaken(byte[] data, Camera camera) { 	
    	if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }
    	
        bitmap = createPreviewBitmap(data);
        
        setPreview();
    }
    
    public Bitmap createPreviewBitmap(byte[] data) {
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; //down-sampling the image
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);   
        
        //Rotating bitmap to the right orientation
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        
        return bitmap;
    }
    
    public void setPreview() {
    	setContentView(R.layout.image_preview);
    	
    	_preview = true;
    	
    	PreviewImage = (ImageView) findViewById(R.id.previewImage);
        OCRtext = (TextView) findViewById(R.id.OCRtext);
        saveButton = (Button) findViewById(R.id.saveButton);
        retakeButton = (Button) findViewById(R.id.retakeButton);
        
        PreviewImage.setImageBitmap(bitmap); //Assign the bitmap to ImageView
        saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("saveButton", "clicked");
				try {
					//_preview = false;
					saveBitmap (bitmap);					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        
        retakeButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.i("retakeButton", "clicked");
				_preview = false;
				restartCamera();	
			}
		});
        
        Rectview = (DragRectView) findViewById(R.id.dragRect);
        if (null != Rectview) {
        	Rectview.setOnUpCallback(new DragRectView.OnUpCallback() {
                @Override
                public void onRectFinished(final Rect rect) {
                    Toast.makeText(getApplicationContext(), 
                    		"Rect is ("+rect.left+", "+rect.top+", "+rect.right+", "+rect.bottom+")", Toast.LENGTH_LONG).show();
                    Bitmap previewBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) PreviewImage.getDrawable()).getBitmap(), PreviewImage.getWidth(), PreviewImage.getHeight(), false);
                    //System.out.println(rect.height()+"    "+previewBitmap.getHeight()+"      "+rect.width()+"    "+previewBitmap.getWidth());
                    if (rect.height() <= previewBitmap.getHeight() && rect.width() <= previewBitmap.getWidth()) {                    
                    	Bitmap croppedBitmap = Bitmap.createBitmap(previewBitmap, rect.left, rect.top, rect.width(), rect.height());  
                    	OCRtext.setText(OCR.detect_text(croppedBitmap, "detect_numbers"));
                    }          
                }
            });
        }
        
    }
    
    public void restartCamera() {
    	//Restarting Activity
    	if (Build.VERSION.SDK_INT >= 11) {
		    recreate();
		} else {
		    Intent intent = getIntent();
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
		    
		    overridePendingTransition(0, 0);
		    startActivity(intent);
		}   	
    }
    
    public void saveBitmap (Bitmap bitmap) throws IOException {
    	
    	File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
    	
    	if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
    	
    	FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();  
    }

	/** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Filesystem._ImgDirPath);
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
    
    /*
    protected String detect_text(Bitmap targetBitmap, String detectOption){
    	TessBaseAPI baseApi = new TessBaseAPI();
    	Log.i("tessrect", "new tess object created");   	
    	baseApi.init(Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract/", "eng");
    	Log.i("tessrect", "initialized");
    	
    	baseApi.setImage(targetBitmap);
    	Log.i("tessrect", "bitmap/image set");
    	
    	String recognizedText = baseApi.getUTF8Text();
    	
    	if (detectOption == DETECT_NUMBERS) recognizedText = extractNumbers(recognizedText);
    	
    	baseApi.end();
    	
    	return recognizedText;
    }
    
    private String extractNumbers(String inputString) {
    	String outputString="";
    	
    	for(int i=0; i<inputString.length(); i++) {
    		if(isNumber(inputString.charAt(i))) outputString += inputString.charAt(i);
    	}
    	
    	return outputString;
    }
    
    private boolean isNumber(char input) {
    	if(input == '0' || input == '1' || input == '2' || input == '3' || input == '4'
    	|| input == '5' || input == '6' || input == '7' || input == '8' || input == '9'
    	|| input == '.' ) return true;
    	else return false;
    }
    */
    
    @Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	@Override
    public void onShutter() {

    }

}

