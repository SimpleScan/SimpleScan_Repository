package com.SimpleScan.simplescan.Camera;

import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraEngine {
	
	static final String TAG = "DBG_" + CameraUtils.class.getName();
	
	boolean on;
    Camera camera;
    SurfaceHolder surfaceHolder;
    int flashMode;
    
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };
    
    public boolean isOn() {
        return on;
    }
    
    public Camera getCamera(){
    	return camera;
    }
    
    public SurfaceHolder getSurfaceHolder(){
    	return surfaceHolder;
    }
    
    public CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }
    
    public void requestFocus() {
        if (camera == null) return;
        if (isOn()) camera.autoFocus(autoFocusCallback);
    }
    
    public void cycleFlashMode(Context context) {
    	if(camera == null) return;
    	if(isOn()){
	    	if(CameraUtils.isFlashSupported(context)) {
	    		if(flashMode == 0) {
	    			flashMode = 1; //on
	    		} else if(flashMode == 1) {
	    			flashMode = 2; //auto
	    		} else {
	    			flashMode = 0; //off
	    		}
	    	} else 	flashMode = 0;
    	}
    }
    
    public int checkFlashMode() {
    	return flashMode;
    }
    
    public void requestFlash() {
    	Parameters cam_parameters = camera.getParameters();
    	
    	switch(flashMode) {
    		case 1:
    			Log.i("requestFlash", "flash is turn on!");
    		    cam_parameters.setFlashMode(Parameters.FLASH_MODE_ON);
    		    camera.setParameters(cam_parameters);
    			break;
    		case 2:
    			Log.i("requestFlash", "flash is turn on!");
    		    cam_parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
    		    camera.setParameters(cam_parameters);
    		    break;
    		default:
    			Log.i("requestFlash", "flash is turn off!");
    		    cam_parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
    		    camera.setParameters(cam_parameters);
    	}
    }
    
    public void requestZoom(String zoomMode) {
    	Parameters cam_parameters = camera.getParameters();
    	if(cam_parameters.isZoomSupported()) {
	    	int maxZoom = cam_parameters.getMaxZoom();
	    	int zoomFactor = maxZoom/5;
	    	int zoom = cam_parameters.getZoom();
			
	    	if(zoomMode == "in") zoom += zoomFactor;
	    	else if(zoomMode == "out") zoom -= zoomFactor;
	    	/*
			switch(zoomMode) {
			case "in" :
				zoom += zoomFactor;
				break;
			case "out" :
				zoom -= zoomFactor;
				break;
			}
			*/
			
			if(zoom < 0) zoom = 0;
			if(zoom > maxZoom) zoom = maxZoom;

			cam_parameters.setZoom(zoom);
			camera.setParameters(cam_parameters);
		}
    }
    
    public void start() {

        Log.d(TAG, "Entered CameraEngine - start()");
        this.camera = CameraUtils.getCameraInstance();

        if (this.camera == null) return;        
        Log.d(TAG, "Got camera hardware");
        
        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);//Portrait Camera
            this.camera.startPreview();

            on = true;
            flashMode = 0;

            Log.d(TAG, "CameraEngine preview started");

        } catch (IOException e) {
            Log.e(TAG, "Error in setPreviewDisplay");
        }
    }
    
    public void stop(){

        if(camera != null){
            //this.autoFocusEngine.stop();
        	camera.cancelAutoFocus();
            camera.release();
            camera = null;
        }

        on = false;
        flashMode = 0;

        Log.d(TAG, "CameraEngine Stopped");
    }
    
    public void takeShot(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawPictureCallback, Camera.PictureCallback jpegPictureCallback ){
    	if(isOn()) {
    		requestFlash();
    		camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
    	}
    } 
}
