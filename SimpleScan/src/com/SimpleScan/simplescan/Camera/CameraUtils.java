package com.SimpleScan.simplescan.Camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;

public class CameraUtils {

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	    	Log.i("checkCameraHardware", "this device has a camera");
	        return true;
	    } else {
	        Log.e("checkCameraHardware", "No Camera Found");
	        return false;
	    }
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open();
	        Log.i("getCameraInstance", "attempt to get a Camera instance");
	    }
	    catch (Exception e){
	    	Log.e("getCameraInstance", "Camera is not available (in use or does not exist)");
	    }
	    return c; // returns null if camera is unavailable
	}
	
	public static boolean isFlashSupported(Context context){ 
    	return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);   	  
	}
	
}
