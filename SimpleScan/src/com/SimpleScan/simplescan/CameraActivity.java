package com.SimpleScan.simplescan;

import java.io.IOException;

import com.SimpleScan.simplescan.Camera.BitmapUtils;
import com.SimpleScan.simplescan.Camera.CameraEngine;
import com.SimpleScan.simplescan.Camera.CameraUtils;
import com.SimpleScan.simplescan.Camera.OCR;
import com.SimpleScan.simplescan.Camera.Views.DragRectView;
import com.SimpleScan.simplescan.Camera.Views.ZoomableImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.ShutterCallback {

	static final String TAG = "DBG_" + "CameraActivity";
	
	private final int TOGGLE = 1;
	private final int OFF = 0;
	
	//For Camera
	//Camera front-end
    private Button shutterButton;
    private Button focusButton;
    private Button flashButton;
    //Camera back-end
    private SurfaceView cameraFrame;
    private CameraEngine cameraEngine;    
    
    //For Preview
    private boolean _preview;
    //Preview front-end
    private Button saveButton;
    private Button retakeButton;
    private Button recordNameButton;
    private Button recordDateButton;
    private Button recordAmtButton;
    private TextView OCR_name;
    private TextView OCR_date;
    private TextView OCR_amt;    
    private ZoomableImageView PreviewImage;
    private DragRectView Rectview;    
	//Preview back-end
    private boolean recordNameOn, recordDateOn, recordAmtOn;
	private String nameText, dateText, amtText;
	private double amt;
	private Bitmap bitmap;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);         
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {   	
    	if(CameraUtils.checkCameraHardware(this)) {
	        if (cameraEngine != null && !cameraEngine.isOn()) cameraEngine.start();
	        if (checkCameraEngine()) return;
	
	        cameraEngine = new CameraEngine(holder);
	        cameraEngine.start();

    	} else finish();    	
    }
    
	@Override
    protected void onResume() {
        super.onResume();
        if(_preview)  setPreview();
        else assignCameraViews();
    }

	@Override
    protected void onPause() {
        super.onPause();

        if (checkCameraEngine()) cameraEngine.stop();

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }
	
	@Override
    public void onClick(View v) {
		if(_preview) {
			
			if(v == saveButton) saveData();
			if(v == retakeButton) restartCamera();
			
			if(v == recordNameButton) updateRecordButtons("name");
			if(v == recordDateButton) updateRecordButtons("date");
			if(v == recordAmtButton) updateRecordButtons("amt");
			
		} else { 
			if(checkCameraEngine()){
		        if(v == shutterButton) cameraEngine.takeShot(this, this, this);	        
		        if(v == focusButton) cameraEngine.requestFocus();
		        if(v == flashButton) changeFlashButtonLayout();
			}
		}
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if(!_preview && checkCameraEngine()) cameraEngine.requestZoom(event);
        return true;
    }
	
	@Override
    public void onPictureTaken(byte[] data, Camera camera) { 	
    	if (data == null) return;
        bitmap = BitmapUtils.createPreviewBitmap(data);
        
        initPreview();
        setPreview();
    }

    @Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	@Override
    public void onShutter() {
    }
	
	private void setPreview() {
    	setContentView(R.layout.image_preview);	
    	assignPreviewViews();
    	setRectView();
    } 
    
	private void changeFlashButtonLayout() {
		if(CameraUtils.isFlashSupported(this)) {
    		cameraEngine.cycleFlashMode(this);
    		
    		switch(cameraEngine.checkFlashMode()) {
    			case 1:
    				flashButton.setBackgroundResource(R.drawable.flash_on_layout);
    				break;
    			case 2:
    				flashButton.setBackgroundResource(R.drawable.flash_auto_layout);
    				break;
				default:
					flashButton.setBackgroundResource(R.drawable.flash_off_layout);
    		}
		} else Toast.makeText(getApplicationContext(), "Flash is not supported on your device", Toast.LENGTH_SHORT).show();
	}
	
    private void restartCamera() {
    	_preview = false;
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
    
    private void saveData() {
    	try {
    		_preview = false;
			String imgPath = Filesystem.saveBitmap (bitmap);
			Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();

			FragmentShareExpense.setDataFromCam(nameText, dateText, amt, imgPath);
			finish();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void updateRecordButtons(String theButton) {
    	if(theButton == "name") {
    		updateRecordNameButton(TOGGLE);
			updateRecordDateButton(OFF);
			updateRecordAmtButton(OFF);
    	}
    	if(theButton == "date") {
    		updateRecordNameButton(OFF);
			updateRecordDateButton(TOGGLE);
			updateRecordAmtButton(OFF);
    	}
    	if(theButton == "date") {
    		updateRecordNameButton(OFF);
			updateRecordDateButton(OFF);
			updateRecordAmtButton(TOGGLE);
    	}
/*
    	switch(theButton) {
    	case "name" :
    		updateRecordNameButton(TOGGLE);
			updateRecordDateButton(OFF);
			updateRecordAmtButton(OFF);
    		break;
    	case "date" :
    		updateRecordNameButton(OFF);
			updateRecordDateButton(TOGGLE);
			updateRecordAmtButton(OFF);
    		break;
    	case "amt" :
    		updateRecordNameButton(OFF);
			updateRecordDateButton(OFF);
			updateRecordAmtButton(TOGGLE);
    		break;
    	}
*/
    	updateOCRIcons();
		updateRectViewStatus();	
    }
    
    private void updateRecordNameButton (int status) {	
		if(status == TOGGLE) {
	    	if(recordNameOn){
				recordNameOn = false;			
				if(nameText=="") OCR_name.setText("Name");
				else OCR_name.setText(nameText);
			} else {
				recordNameOn = true;	
				OCR_name.setText("Crop the name");	
			}
		} else {
			if(recordNameOn){
				recordNameOn = false;			
				if(nameText=="") OCR_name.setText("Name");
				else OCR_name.setText(nameText);
			}
		}
    }
    private void updateRecordDateButton (int status) {
    	if(status == TOGGLE) {
    		if(recordDateOn) {
				recordDateOn = false;			
				if(dateText=="") OCR_date.setText("Date");
				else OCR_date.setText(dateText);
    		} else {
    			recordDateOn = true;
    			OCR_date.setText("Crop the date");
    		}
    	} else {
    		if(recordDateOn) {
				recordDateOn = false;			
				if(dateText=="") OCR_date.setText("Date");
				else OCR_date.setText(dateText);
    		}
    	}
    }
    private void updateRecordAmtButton (int status) {
    	if(status == TOGGLE) {
    		if(recordAmtOn) {
		    	recordAmtOn = false;
				if(amtText=="") OCR_amt.setText("Amount");
				else OCR_amt.setText("$"+amtText);
    		} else {
    			recordAmtOn = true;
				OCR_amt.setText("Crop the amount");
    		}
    	} else {
    		if(recordAmtOn) {
		    	recordAmtOn = false;
				if(amtText=="") OCR_amt.setText("Amount");
				else OCR_amt.setText("$"+amtText);
    		}
    	}
    }
    
    private void updateOCRIcons() {
    	assignRecordButtons();
        
    	recordNameButton.setBackgroundResource(R.drawable.record_name_layout);
		recordDateButton.setBackgroundResource(R.drawable.record_date_layout);
		recordAmtButton.setBackgroundResource(R.drawable.record_amt_layout);
		
    	if(recordNameOn)  recordNameButton.setBackgroundResource(R.drawable.record_name_on_layout);
    	else if(recordDateOn) recordDateButton.setBackgroundResource(R.drawable.record_date_on_layout);
    	else if(recordAmtOn) recordAmtButton.setBackgroundResource(R.drawable.record_amt_on_layout);
    }
    
    private void setCropping(boolean isCropping) {
    	if(PreviewImage != null) PreviewImage.setCroppingMode(isCropping);;
    	if(Rectview != null) Rectview.setCroppingMode(isCropping);
    }
    
    private void setRectView() {
    	if (null != Rectview) {
        	Rectview.setOnUpCallback(new DragRectView.OnUpCallback() {
                @Override
                public void onRectFinished(final Rect rect) {           
                    
                	Bitmap previewBitmap = BitmapUtils.createImageViewBitmap(PreviewImage);
                    
                    if (rect.height() <= previewBitmap.getHeight() && rect.width() <= previewBitmap.getWidth() 
                    &&  rect.height() > 0 && rect.width() > 0) {
                    	Bitmap croppedBitmap = BitmapUtils.createRectBitmap(previewBitmap, rect);
                    	updateRectView(croppedBitmap) ;   	
                    }          
                }
            });	
    	}
    }
    
    private void updateRectViewStatus() {
    	if(recordNameOn || recordDateOn || recordAmtOn) {
    		setCropping(true);
			if(recordNameOn) Rectview.setBorderColor(Color.RED);
			else if(recordDateOn) Rectview.setBorderColor(Color.GREEN);
			else if(recordAmtOn) Rectview.setBorderColor(Color.BLUE);
			Rectview.setVisibility(View.VISIBLE);
    	} else {
    		setCropping(false);
			Rectview.setVisibility(View.INVISIBLE);	
    	}
    }
    
    private void updateRectView(Bitmap croppedBitmap) {
    	if(recordNameOn) {
    		nameText = OCR.detect_text(croppedBitmap, "detect_all");
    		OCR_name.setText(nameText);
    		Toast.makeText(getApplicationContext(), nameText, Toast.LENGTH_SHORT).show();
    	}
    	if(recordDateOn) {
    		dateText = OCR.detect_text(croppedBitmap, "detect_date");       		
    		OCR_date.setText(dateText);
    		Toast.makeText(getApplicationContext(), dateText, Toast.LENGTH_SHORT).show();
    	}
    	if(recordAmtOn) {
    		amtText = OCR.detect_text(croppedBitmap, "detect_numbers");
    		if(amtText != "Couldn't detect amount") OCR_amt.setText("$"+amtText);
    		else OCR_amt.setText(amtText);
    		Toast.makeText(getApplicationContext(), amtText, Toast.LENGTH_SHORT).show();
    		
    		try {
    			amt=OCR.amtStr2double(amtText);
    		} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
	private boolean checkCameraEngine(){
		return (cameraEngine != null && cameraEngine.isOn());
	}
	
	private void assignCameraViews() {
		shutterButton = (Button) findViewById(R.id.shutter_button);
        focusButton = (Button) findViewById(R.id.focus_button);
        flashButton = (Button) findViewById(R.id.flash_button);
        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
        
        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);
        flashButton.setOnClickListener(this);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	private void assignPreviewViews() {
		assignRecordButtons();
		OCR_name = (TextView) findViewById(R.id.OCR_name);
    	OCR_date = (TextView) findViewById(R.id.OCR_date);
    	OCR_amt = (TextView) findViewById(R.id.OCR_amt);
        saveButton = (Button) findViewById(R.id.saveButton);
        retakeButton = (Button) findViewById(R.id.retakeButton);
        PreviewImage = (ZoomableImageView) findViewById(R.id.previewImage);
    	Rectview = (DragRectView) findViewById(R.id.dragRect);
    	
    	saveButton.setOnClickListener(this);
    	retakeButton.setOnClickListener(this);
    	PreviewImage.setImageBitmap(bitmap); //Assign the bitmap to ImageView   
    	Rectview.setVisibility(View.INVISIBLE);     
	}
	
	private void assignRecordButtons(){
		recordNameButton = (Button) findViewById(R.id.recordNameButton);
        recordDateButton = (Button) findViewById(R.id.recordDateButton);
        recordAmtButton = (Button) findViewById(R.id.recordAmtButton); 
        
        recordNameButton.setOnClickListener(this);
        recordDateButton.setOnClickListener(this);
        recordAmtButton.setOnClickListener(this);
	}
	
	private void initPreview(){
    	recordNameOn = false;
        recordDateOn = false;
        recordAmtOn = false;
        
        nameText = "";
        dateText = ""; 
        amtText  = "";
        
        amt=0.;
        
    	_preview = true;
    }
}

