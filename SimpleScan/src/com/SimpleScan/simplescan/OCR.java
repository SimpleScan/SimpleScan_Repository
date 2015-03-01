package com.SimpleScan.simplescan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OCR extends Activity {
	
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _appPath = Environment.getExternalStorageDirectory() + "/SimpleScan";
	protected String _ImgDirPath = Environment.getExternalStorageDirectory() + "/SimpleScan/images";
	protected String _ImgPath = Environment.getExternalStorageDirectory() + "/SimpleScan/images/recipt_image.jpg";
	protected String _tessPath = Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract";
	protected String _traindataPath = Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract/tessdata";
	protected boolean _taken;
	
	protected static final String PHOTO_TAKEN	= "photo_taken";
		
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
       
        _image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        _button = ( Button ) findViewById( R.id.button ); //get id/button from main.xml
        _button.setOnClickListener( new ButtonClickHandler() );
        
        File app_dir = new File(_appPath);
        if(!app_dir.exists()) {
        	if(app_dir.mkdir()) {
        		Log.i( "app_dir", "app_dir made" );
        		
        		File im_direct = new File( Environment.getExternalStorageDirectory() + "/SimpleScan/images" );
                if(!im_direct.exists()) {
                	if(im_direct.mkdir()) {
                		
                	}
                }
        		
                File tess_dir = new File(_tessPath);
                if(!tess_dir.exists()) {
                	if(tess_dir.mkdir()) {
                		Log.i( "tess_dir", "tess_dir made" );
                		
                		File traindata_direct = new File(_traindataPath);
                        if(!traindata_direct.exists()) {
                        	if(traindata_direct.mkdir()) {
                        		Log.i( "traindata", "directory made" );
                        		CopyAssets();
                        	}
                        	else Log.i( "traindata", "traindata_direct could not be made" );
                        }
                        else Log.i( "traindata", "traindata_direct exists" );
                		
                	}
                	else Log.i( "tess_dir", "tess_dir could not be made" );
                }
                else Log.i( "tess_dir", "tess_dir exists" );
        		
        	}
        	else Log.i( "app_dir", "app_dir could not be made" );
        }
        else Log.i( "app_dir", "app_dir exists" );        
    }
    
    public class ButtonClickHandler implements View.OnClickListener 
    {
    	public void onClick( View view ){
    		Log.i("xml Button", "ButtonClickHandler.onClick()" );
    		startCameraActivity();
    	}
    }
    
    protected void startCameraActivity()
    {
    	Log.i("native_camApp", "startCameraActivity()" );
    	File file = new File( _ImgPath );
    	Uri outputFileUri = Uri.fromFile( file );
    	
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	startActivityForResult( intent, 0 ); //tells the system that when the user is done with the camera app
    										//return to this activity with some result.
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	Log.i( "result_from_camera", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "result_from_camera", "User cancelled" );
    			break;
    			
    		case -1:
			try {
				onPhotoTaken();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    			break;
    	}
    }
    
    protected void onPhotoTaken() throws IOException
    {
    	Log.i( "photo_taken", "onPhotoTaken" );
    	
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; //down-sampling the image
    	
    	Bitmap bitmap = BitmapFactory.decodeFile( _ImgPath, options ); //create a bitmap from _ImgPath with options
    	bitmap = correct_photo(bitmap);

    	_image.setImageBitmap(bitmap); //Assign the bitmap to ImageView
    	String recognizedText = detect_text(bitmap);
    	
    	_field.setText(recognizedText);
    	
    }
    
    protected Bitmap correct_photo(Bitmap bitmap) throws IOException { //correcting photo for OCR(tess-two)
    	ExifInterface exif = new ExifInterface( _ImgPath );
    	int exifOrientation = exif.getAttributeInt(
    	        ExifInterface.TAG_ORIENTATION,
    	        ExifInterface.ORIENTATION_NORMAL);

    	int rotate = 0;

    	switch (exifOrientation) {
    	case ExifInterface.ORIENTATION_ROTATE_90:
    	    rotate = 90;
    	    break;
    	case ExifInterface.ORIENTATION_ROTATE_180:
    	    rotate = 180;
    	    break;
    	case ExifInterface.ORIENTATION_ROTATE_270:
    	    rotate = 270;
    	    break;
    	}

    	if (rotate != 0) {
    	    int w = bitmap.getWidth();
    	    int h = bitmap.getHeight();

    	    // Setting pre rotate
    	    Matrix mtx = new Matrix();
    	    mtx.preRotate(rotate);

    	    // Rotating Bitmap & convert to ARGB_8888, required by tess
    	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
    	}
    	bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    	
    	return bitmap;
    }
    
    protected String detect_text(Bitmap bitmap){
    	TessBaseAPI baseApi = new TessBaseAPI();
    	Log.i("tessrect", "new tess object created");   	
    	baseApi.init(Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract/", "eng");
    	Log.i("tessrect", "initialized");
    	// Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");
    	
    	baseApi.setImage(bitmap);
    	Log.i("tessrect", "bitmap/image set");
    	
    	String recognizedText = baseApi.getUTF8Text();
    	
    	baseApi.end();
    	
    	return recognizedText;
    }
    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){ //When the rotation is complete
    																  //restore from Bundle object
    	Log.i( "restoreFrom_bundle", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( OCR.PHOTO_TAKEN ) ) {
    		try {
				onPhotoTaken();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) { //When the device is rotated, this is called
    														//Save any details about app into a Bundle object
    	outState.putBoolean( OCR.PHOTO_TAKEN, _taken );
    }
    
    private void CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("tessdata");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
 
        for(String filename : files) {
            System.out.println("File name => "+filename);
            InputStream in = null;
            OutputStream out = null;
            try {
              in = assetManager.open("tessdata/"+filename);   // if files resides inside the "Files" directory itself
              out = new FileOutputStream(_traindataPath +"/" + filename);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }
    
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
}

