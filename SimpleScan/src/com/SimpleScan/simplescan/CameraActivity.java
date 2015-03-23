package com.SimpleScan.simplescan;

import java.io.IOException;

import com.SimpleScan.simplescan.Camera.BitmapUtils;
import com.SimpleScan.simplescan.Camera.CameraEngine;
import com.SimpleScan.simplescan.Camera.OCR;
import com.SimpleScan.simplescan.Camera.Views.DragRectView;
import com.SimpleScan.simplescan.Camera.Views.ZoomableImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
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
	
	//For Camera
	//Camera front-end
    Button shutterButton;
    Button focusButton;
    Button flashButton;
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
    
	//ImageView PreviewImage;
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
	        flashButton = (Button) findViewById(R.id.flash_button);
	        
	        shutterButton.setOnClickListener(this);
	        focusButton.setOnClickListener(this);
	        flashButton.setOnClickListener(this);
	
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

        if(v == flashButton) {
        	if(cameraEngine!=null && cameraEngine.isOn()){
        		
        		//cameraEngine.toggleFlash(this);
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
            }
        }
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
        //PreviewImage = (ImageView) findViewById(R.id.previewImage);
    	Rectview = (DragRectView) findViewById(R.id.dragRect);
    	Rectview.setVisibility(View.INVISIBLE);
              
        PreviewImage.setImageBitmap(bitmap); //Assign the bitmap to ImageView   
        
        saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("saveButton", "clicked");
				try {
					Filesystem.saveBitmap (bitmap);
					Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_LONG).show();
					//Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_LONG).show();
					
					/*
					getActivity().setTitle("Edit Expense");
					
					Fragment fragment = FragmentShareExpense.createNewExpense(getActivity());
					FragmentShareExpense.setDataFromCam(nameText, dateText, amt);
								
					changeFragment(fragment, "Edit Expense", false);
					finish();
					*/
					FragmentShareExpense.setDataFromCam(nameText, dateText, amt);
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
			}
		});       	    
        
     	if (null != Rectview) {
        	Rectview.setOnUpCallback(new DragRectView.OnUpCallback() {
                @Override
                public void onRectFinished(final Rect rect) {
                    Toast.makeText(getApplicationContext(), 
                    		       "Rect is ("+rect.left+", "+rect.top+", "+rect.right+", "+rect.bottom+")", Toast.LENGTH_LONG).show();                                       
                    //Bitmap previewBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) PreviewImage.getDrawable()).getBitmap(), PreviewImage.getWidth(), PreviewImage.getHeight(), false);
                    Bitmap previewBitmap = Bitmap.createScaledBitmap(PreviewImage.getPhotoBitmap(), PreviewImage.getWidth(), PreviewImage.getHeight(), false);            	
                	/*
                    try {
            			Filesystem.saveBitmap(PreviewImage.getPhotoBitmap());
            		} catch (IOException e1) {
            			// TODO Auto-generated catch block
            			e1.printStackTrace();
            		}
                	try {
            			Filesystem.saveBitmap(previewBitmap);
            		} catch (IOException e1) {
            			// TODO Auto-generated catch block
            			e1.printStackTrace();
            		}
            		*/
                    //System.out.println(rect.height()+"    "+previewBitmap.getHeight()+"      "+rect.width()+"    "+previewBitmap.getWidth());
                    if (rect.height() <= previewBitmap.getHeight() && rect.width() <= previewBitmap.getWidth() 
                    &&  rect.height() > 0 && rect.width() > 0) {                    
                    	Bitmap croppedBitmap = Bitmap.createBitmap(previewBitmap, rect.left, rect.top, rect.width(), rect.height()); 
                    	/*
                    	try {
							Filesystem.saveBitmap(croppedBitmap);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						*/
                    	if(recordNameOn) {
                    		nameText = OCR.detect_text(croppedBitmap, "detect_all");
                    		OCR_name.setText(nameText);
                    		Toast.makeText(getApplicationContext(), nameText, Toast.LENGTH_LONG).show();
                    	}
                    	if(recordDateOn) {
                    		dateText = OCR.detect_text(croppedBitmap, "detect_date");       		
                    		OCR_date.setText(dateText);
                    		Toast.makeText(getApplicationContext(), dateText, Toast.LENGTH_LONG).show();
                    	}
                    	if(recordAmtOn) {
                    		amtText = OCR.detect_text(croppedBitmap, "detect_numbers");
                    		if(amtText != "Couldn't detect amount") OCR_amt.setText("$"+amtText);
                    		else OCR_amt.setText(amtText);
                    		Toast.makeText(getApplicationContext(), amtText, Toast.LENGTH_LONG).show();
                    		
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

