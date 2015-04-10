package com.SimpleScan.simplescan.test.Camera;

import com.SimpleScan.simplescan.Camera.CameraUtils;

import android.hardware.Camera;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class CameraUtilsTest extends AndroidTestCase{
	
	private RenamingDelegatingContext context;
	
	public void setUp() {
		context = new RenamingDelegatingContext(getContext(), "test_");
		assertNotNull("context is null", context);
	}

	public void testCheckCameraHardware(){
		setUp();
		assertNotNull("checkCameraHardware is null", CameraUtils.checkCameraHardware(context));
	}
	
	public void testGetCameraInstance(){
		setUp();
		if(CameraUtils.checkCameraHardware(context)) {
			Camera testCamera = CameraUtils.getCameraInstance();
			assertNotNull("CameraUtils.camera is null", testCamera);
			testCamera.release();
		}			
	}
	
	public void testIsFlashSupported() {
		setUp();
		assertNotNull("isFlashSupported is null", CameraUtils.isFlashSupported(context));
	}
	
}
