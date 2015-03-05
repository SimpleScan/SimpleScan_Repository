package com.SimpleScan.simplescan.Camera;

import java.io.IOException;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;


public class CameraEngine {
	
	static final String TAG = "DBG_" + CameraUtils.class.getName();
	
	boolean on;
    Camera camera;
    SurfaceHolder surfaceHolder;
    
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };
    
    public boolean isOn() {
        return on;
    }
    
    public CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }
    
    public void requestFocus() {
        if (camera == null) return;
        if (isOn()) camera.autoFocus(autoFocusCallback);

    }
    
    public void start() {

        Log.d(TAG, "Entered CameraEngine - start()");
        this.camera = CameraUtils.getCameraInstance();

        if (this.camera == null)
            return;
        
        Log.d(TAG, "Got camera hardware");
        
        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);//Portrait Camera
            this.camera.startPreview();

            on = true;

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

        Log.d(TAG, "CameraEngine Stopped");
    }
    
    public void takeShot(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawPictureCallback, Camera.PictureCallback jpegPictureCallback ){
    	if(isOn()) camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
    }
    
}
