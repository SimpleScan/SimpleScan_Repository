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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.ShutterCallback {

	static final String TAG = "DBG_" + "CameraActivity";
	
	//For Camera
	//Camera front-end
    Button shutterButton;
    Button focusButton;
    Button flashButton;
    Button zoominButton;
    Button zoomoutButton;
    //Camera back-end
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;    
    
    //For Preview
    protected boolean _preview;
    //Preview front-end
    Button saveButton;
    Button retakeButton;
    Button recordNameButton;
    Button recordDateButton;
    Button recordAmtButton;
    TextView OCR_name;
    TextView OCR_date;
    TextView OCR_amt;    
	ZoomableImageView PreviewImage;
    private DragRectView Rectview;    
	//Preview back-end
	boolean recordNameOn, recordDateOn, recordAmtOn;
	String nameText, dateText, amtText;
	double amt;
	Bitmap bitmap;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);         
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {   	
    	if(CameraUtils.checkCameraHardware(this)) {
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
    	} else finish();    	
    }
    
	@Override
    protected void onResume() {
        super.onResume();

        if(_preview) {
        	setPreview();
        } else{
	        shutterButton = (Button) findViewById(R.id.shutter_button);
	        focusButton = (Button) findViewById(R.id.focus_button);
	        flashButton = (Button) findViewById(R.id.flash_button);
	        zoominButton = (Button) findViewById(R.id.zoomin_button);
	        zoomoutButton = (Button) findViewById(R.id.zoomout_button);
	        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
	        
	        shutterButton.setOnClickListener(this);
	        focusButton.setOnClickListener(this);
	        flashButton.setOnClickListener(this);
	        zoominButton.setOnClickListener(this);
	        zoomoutButton.setOnClickListener(this);
	
	        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
	        surfaceHolder.addCallback(this);
	        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        //cameraFrame.setOnClickListener(this);
        }
    }

	@Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) cameraEngine.stop();

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }
	
	@Override
    public void onClick(View v) {
        if(v == shutterButton){
            if(cameraEngine != null && cameraEngine.isOn()) cameraEngine.takeShot(this, this, this);
        }
        
        if(v == focusButton){
            if(cameraEngine!=null && cameraEngine.isOn()) cameraEngine.requestFocus();
        }

        if(v == flashButton) {
        	if(cameraEngine!=null && cameraEngine.isOn()){
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
        }
        if(v == zoominButton) cameraEngine.requestZoom("in");
		if(v == zoomoutButton) cameraEngine.requestZoom("out");
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) { 	
    	if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }
    	
        bitmap = BitmapUtils.createPreviewBitmap(data);
        
        initPreview();
        setPreview();
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
    
    public void setPreview() {
    	setContentView(R.layout.image_preview);	
    	
    	OCR_name = (TextView) findViewById(R.id.OCR_name);
    	OCR_date = (TextView) findViewById(R.id.OCR_date);
    	OCR_amt = (TextView) findViewById(R.id.OCR_amt);
        saveButton = (Button) findViewById(R.id.saveButton);
        retakeButton = (Button) findViewById(R.id.retakeButton);
        recordNameButton = (Button) findViewById(R.id.recordNameButton);
        recordDateButton = (Button) findViewById(R.id.recordDateButton);
        recordAmtButton = (Button) findViewById(R.id.recordAmtButton);
        
        PreviewImage = (ZoomableImageView) findViewById(R.id.previewImage);
    	Rectview = (DragRectView) findViewById(R.id.dragRect);
    	Rectview.setVisibility(View.INVISIBLE);
              
        PreviewImage.setImageBitmap(bitmap); //Assign the bitmap to ImageView   
        
        saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("saveButton", "clicked");
				try {
					String imgPath = Filesystem.saveBitmap (bitmap);
					Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();

					FragmentShareExpense.setDataFromCam(nameText, dateText, amt, imgPath);
					finish();
					
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
        
        recordNameButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.i("recordNameButton", "clicked");
				if(recordNameOn){
					recordNameOn = false;
					setCropping(false);
					
					if(nameText=="") OCR_name.setText("Name");
					else OCR_name.setText(nameText);
					
					Rectview.setVisibility(View.INVISIBLE);					
				}
				else {
					recordNameOn = true;
					setCropping(true);
					
					if(recordDateOn) {
						recordDateOn = false;
						
						if(dateText=="") OCR_date.setText("Date");
						else OCR_date.setText(dateText);
					}
					else if(recordAmtOn) {
						recordAmtOn = false;
						
						if(amtText=="") OCR_amt.setText("Amount");
						else OCR_amt.setText("$"+amtText);
					}
					
					Rectview.setVisibility(View.VISIBLE);
					Rectview.setBorderColor(Color.RED);
					OCR_name.setText("Crop the name");					
				}
				switchOCRIcons();
			}
		});
        
        recordDateButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.i("recordDateButton", "clicked");
				if(recordDateOn) {
					recordDateOn = false;
					setCropping(false);
					
					if(dateText=="") OCR_date.setText("Date");
					else OCR_date.setText(dateText);
					
					Rectview.setVisibility(View.INVISIBLE);
				}
				else {
					recordDateOn = true;
					setCropping(true);
					
					if(recordNameOn) {
						recordNameOn = false;
						
						if(nameText=="") OCR_name.setText("Name");
						else OCR_name.setText(nameText);
					}
					else if(recordAmtOn) {
						recordAmtOn = false;
						
						if(amtText=="") OCR_amt.setText("Amount");
						else OCR_amt.setText("$"+amtText);
					}
					
					Rectview.setVisibility(View.VISIBLE);
					Rectview.setBorderColor(Color.BLUE);
					OCR_date.setText("Crop the date");	
				}
				switchOCRIcons();
			}
		});
        
        recordAmtButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.i("recordAmtButton", "clicked");
				if(recordAmtOn) {
					recordAmtOn = false;
					setCropping(false);
					
					if(amtText=="") OCR_amt.setText("Amount");
					else OCR_amt.setText("$"+amtText);
					
					Rectview.setVisibility(View.INVISIBLE);
				}
				else {
					recordAmtOn = true;
					setCropping(true);
					
					if(recordNameOn) {
						recordNameOn = false;
						
						if(nameText=="") OCR_name.setText("Name");
						else OCR_name.setText(nameText);
					}
					else if(recordDateOn) {
						recordDateOn = false;
						
						if(dateText=="") OCR_date.setText("Date");
						else OCR_date.setText(dateText);
					}
					
					Rectview.setVisibility(View.VISIBLE);
					Rectview.setBorderColor(Color.GREEN);
					OCR_amt.setText("Crop the amount");
				}
				switchOCRIcons();
			}
		});       	    
        
     	if (null != Rectview) {
        	Rectview.setOnUpCallback(new DragRectView.OnUpCallback() {
                @Override
                public void onRectFinished(final Rect rect) {
                    /*Toast.makeText(getApplicationContext(), 
                    		       "Rect is ("+rect.left+", "+rect.top+", "+rect.right+", "+rect.bottom+")", Toast.LENGTH_SHORT).show();*/                                       
                    //Bitmap previewBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) PreviewImage.getDrawable()).getBitmap(), PreviewImage.getWidth(), PreviewImage.getHeight(), false);
                    Bitmap previewBitmap = Bitmap.createScaledBitmap(PreviewImage.getPhotoBitmap(), PreviewImage.getWidth(), PreviewImage.getHeight(), false);            	            		
                    //System.out.println(rect.height()+"    "+previewBitmap.getHeight()+"      "+rect.width()+"    "+previewBitmap.getWidth());
                    if (rect.height() <= previewBitmap.getHeight() && rect.width() <= previewBitmap.getWidth() 
                    &&  rect.height() > 0 && rect.width() > 0) {
                    	
                    	Bitmap croppedBitmap = Bitmap.createBitmap(previewBitmap, rect.left, rect.top, rect.width(), rect.height()); 

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
    
    private void setCropping(boolean isCropping) {
    	if(PreviewImage != null) PreviewImage.setCroppingMode(isCropping);;
    	if(Rectview != null) Rectview.setCroppingMode(isCropping);
    }
    
    private void switchOCRIcons() {
    	recordNameButton = (Button) findViewById(R.id.recordNameButton);
        recordDateButton = (Button) findViewById(R.id.recordDateButton);
        recordAmtButton = (Button) findViewById(R.id.recordAmtButton);
        
    	if(recordNameOn) {
    		recordNameButton.setBackgroundResource(R.drawable.record_name_on_layout);
    		recordDateButton.setBackgroundResource(R.drawable.record_date_layout);
    		recordAmtButton.setBackgroundResource(R.drawable.record_amt_layout);
    	}
    	else if(recordDateOn) {
    		recordNameButton.setBackgroundResource(R.drawable.record_name_layout);
    		recordDateButton.setBackgroundResource(R.drawable.record_date_on_layout);
    		recordAmtButton.setBackgroundResource(R.drawable.record_amt_layout);
    	}
    	else if(recordAmtOn) {
    		recordNameButton.setBackgroundResource(R.drawable.record_name_layout);
    		recordDateButton.setBackgroundResource(R.drawable.record_date_layout);
    		recordAmtButton.setBackgroundResource(R.drawable.record_amt_on_layout);
    	}
    	else {
    		recordNameButton.setBackgroundResource(R.drawable.record_name_layout);
    		recordDateButton.setBackgroundResource(R.drawable.record_date_layout);
    		recordAmtButton.setBackgroundResource(R.drawable.record_amt_layout);
    	}
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
	
	public Bitmap getPicture() {
		return bitmap;
	}
}

