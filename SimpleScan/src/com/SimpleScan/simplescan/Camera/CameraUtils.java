package com.SimpleScan.simplescan.Camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

public class CameraUtils {

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) return true;
	    else return false;
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open();
	    } catch (Exception e){
	    	Log.e("getCameraInstance", "Camera is not available (in use or does not exist)");
	    } 
	    return c; // returns null if camera is unavailable
	}
	
	public static boolean isFlashSupported(Context context){ 
    	return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);   	  
	}
}
