package com.SimpleScan.simplescan.Camera;

import java.io.IOException;

import com.SimpleScan.simplescan.Tools.TouchGeometry;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class CameraEngine {
	
	static final String TAG = "DBG_" + CameraUtils.class.getName();
	
	private boolean on;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private int flashMode;
    
    private float touch_oldDist = 0f;
    private float touch_diffDist = 0f;
    private boolean zooming = false;
    
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };
    
    /**
     * Show whether the camera is on
     * @return boolean on
     */
    public boolean isOn() {
        return on;
    }
    
    /**
     * Obtain the current camera instance
     * @return the camera instance
     */
    public Camera getCamera(){
    	return camera;
    }
    
    /**
     * Obtain the surfaceHolder that represents the camera view
     * @return the surfaceHolder
     */
    public SurfaceHolder getSurfaceHolder(){
    	return surfaceHolder;
    }
    
    /**
     * CameraEngine constructor
     * @param surfaceHolder
     */
    public CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }
    
    /**
     * Request auto-focus
     */
    public void requestFocus() {
        if (camera == null) return;
        if (isOn()) camera.autoFocus(autoFocusCallback);
    }
    
    /**
     * Check the current flash mode
     * @return current flash mode
     */
    public int checkFlashMode() {
    	return flashMode;
    }
    
    /**
     * Change/cycle the flash mode
     * @param context
     */
    public void cycleFlashMode(Context context) {
    	if(camera == null) return;
    	if(isOn()){
	    	if(CameraUtils.isFlashSupported(context)) {
	    		switch(flashMode) {
	    		case 0: 
	    			flashMode = 1; //on 
	    			break;
	    		case 1:
	    			flashMode = 2; //auto 
	    			break;
    			default:
    				flashMode = 0; //off
	    		}
	    	} else 	flashMode = 0;
    	}
    }
    
    /**
     * Set the flash mode for the camera
     */
    public void requestFlash() {
    	Parameters cam_parameters = camera.getParameters();
    	
    	switch(flashMode) {
    		case 1:
    		    cam_parameters.setFlashMode(Parameters.FLASH_MODE_ON);
    			break;
    		case 2:
    		    cam_parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
    		    break;
    		default:
    		    cam_parameters.setFlashMode(Parameters.FLASH_MODE_OFF); 
    	}
    	camera.setParameters(cam_parameters);
    }
    
    /**
     * Zoom the camera based on the touch gesture
     * @param event: the MotionEvent that represents the touch event
     */
    public void requestZoom(MotionEvent event) {
    	switch(event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_POINTER_DOWN:
            touch_oldDist = TouchGeometry.spacing(event);           
            if(touch_oldDist > 10f) zooming = true;
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	touch_diffDist = 0f;
            zooming = false;
        break;
        case MotionEvent.ACTION_MOVE:           
            if(zooming) {
	        	float touch_newDist = TouchGeometry.spacing(event);               
                if(touch_newDist > 10f) {
                	touch_diffDist = touch_newDist - touch_oldDist;
                	if(Math.abs(touch_diffDist) > 200) {
                		int zoomAmt = (int) (touch_diffDist/200);
                		doZoom(zoomAmt);
                		touch_diffDist = 0;
                	}
                }	
            }
        break;                               
        }
    }
    
    private void doZoom(int zoomAmt) {
    	Parameters cam_parameters = camera.getParameters();
    	if(cam_parameters.isZoomSupported()) {
	    	int maxZoom = cam_parameters.getMaxZoom();
	    	int zoom = cam_parameters.getZoom();
	    	
	    	zoom += zoomAmt;
			
			if(zoom < 0) zoom = 0;
			if(zoom > maxZoom) zoom = maxZoom;

			cam_parameters.setZoom(zoom);
			camera.setParameters(cam_parameters);
		}
    }
    
    /**
     * Start and initialize CameraEngine
     */
    public void start() {
        this.camera = CameraUtils.getCameraInstance();
        if (this.camera == null) return;        
        
        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);//Portrait Camera
            this.camera.startPreview();

            on = true;
            flashMode = 0;
        } catch (IOException e) {
            Log.e(TAG, "Error in setPreviewDisplay");
        }
    }
    
    /**
     * Stop and release CameraEngine
     */
    public void stop(){
        if(camera != null){
        	camera.cancelAutoFocus();
            camera.release();
            camera = null;
        }
        on = false;
        flashMode = 0;
    }
    
    /**
     * Request camera instance to take a picture
     * @param shutterCallback
     * @param rawPictureCallback
     * @param jpegPictureCallback
     */
    public void takeShot(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawPictureCallback, Camera.PictureCallback jpegPictureCallback ){
    	if(isOn()) {
    		requestFlash();
    		camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
    	}
    } 
}
