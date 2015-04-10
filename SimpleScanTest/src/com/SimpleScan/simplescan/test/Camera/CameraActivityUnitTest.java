package com.SimpleScan.simplescan.test.Camera;

import com.SimpleScan.simplescan.CameraActivity;
import com.SimpleScan.simplescan.Camera.CameraEngine;

import android.content.Intent;
import android.hardware.Camera.Parameters;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.SurfaceView;


public class CameraActivityUnitTest extends ActivityUnitTestCase<CameraActivity>{
	
	private Intent CameraIntent;
	private CameraActivity mTestCameraActivity;
	private SurfaceView cameraFrame;
	private CameraEngine cameraEngine; 
	
	public CameraActivityUnitTest() {
		super(CameraActivity.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        
        CameraIntent = new Intent(getInstrumentation().getTargetContext(), CameraActivity.class);
        startActivity(CameraIntent, null, null);
        
        mTestCameraActivity = getActivity();
        cameraFrame = (SurfaceView) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.camera_frame);
    }
	
	@MediumTest
	public void testInstantiateCameraEngine() {
		cameraEngine = new CameraEngine(cameraFrame.getHolder());
		assertNotNull(cameraEngine);
		assertEquals(false, cameraEngine.isOn());
	}
	
	@MediumTest
	public void testCameraEngineStartAndStop() {
		cameraEngine = new CameraEngine(cameraFrame.getHolder());
		cameraEngine.start();
		assertNotNull(cameraEngine.getCamera());
		assertTrue(cameraEngine.isOn());
		assertEquals(0, cameraEngine.checkFlashMode());
		
		cameraEngine.stop();
		assertNull(cameraEngine.getCamera());
		assertFalse(cameraEngine.isOn());
		assertEquals(0, cameraEngine.checkFlashMode());
	}
	
	@MediumTest
	public void testCameraFocus() {
		cameraEngine = new CameraEngine(cameraFrame.getHolder());
		cameraEngine.start();
		
		cameraEngine.requestFocus();
		assertEquals(Parameters.FOCUS_MODE_AUTO, cameraEngine.getCamera().getParameters().getFocusMode());
		
		cameraEngine.stop();
	}
	
	@MediumTest
	public void testCameraFlash() {
		cameraEngine = new CameraEngine(cameraFrame.getHolder());
		
		cameraEngine.start();
		assertEquals(0, cameraEngine.checkFlashMode());
		assertEquals(Parameters.FLASH_MODE_OFF, cameraEngine.getCamera().getParameters().getFlashMode());
		
		cameraEngine.cycleFlashMode(mTestCameraActivity);
		cameraEngine.requestFlash();
		assertEquals(1, cameraEngine.checkFlashMode());
		assertEquals(Parameters.FLASH_MODE_ON, cameraEngine.getCamera().getParameters().getFlashMode());
		
		cameraEngine.cycleFlashMode(mTestCameraActivity);
		cameraEngine.requestFlash();
		assertEquals(2, cameraEngine.checkFlashMode());
		assertEquals(Parameters.FLASH_MODE_AUTO, cameraEngine.getCamera().getParameters().getFlashMode());
		
		cameraEngine.cycleFlashMode(mTestCameraActivity);
		cameraEngine.requestFlash();
		assertEquals(0, cameraEngine.checkFlashMode());
		assertEquals(Parameters.FLASH_MODE_OFF, cameraEngine.getCamera().getParameters().getFlashMode());
		
		cameraEngine.stop();
	}
	
	@MediumTest
	public void testCameraZoom_inRange() {
		cameraEngine = new CameraEngine(cameraFrame.getHolder());
		cameraEngine.start();
		
		int maxZoom = cameraEngine.getCamera().getParameters().getMaxZoom();
		int zoomFactor = maxZoom/5;
		
		int curZoom = cameraEngine.getCamera().getParameters().getZoom();
		cameraEngine.requestZoom("in");
		assertEquals(curZoom+zoomFactor, cameraEngine.getCamera().getParameters().getZoom());
		
		curZoom = cameraEngine.getCamera().getParameters().getZoom();
		cameraEngine.requestZoom("out");
		assertEquals(curZoom-zoomFactor, cameraEngine.getCamera().getParameters().getZoom());
		
		cameraEngine.stop();
	}
	
	@MediumTest
	public void testCameraZoom_outOfRange() {
		cameraEngine = new CameraEngine(cameraFrame.getHolder());
		cameraEngine.start();

		int curZoom = cameraEngine.getCamera().getParameters().getZoom();
		cameraEngine.requestZoom("out");
		assertEquals(curZoom, cameraEngine.getCamera().getParameters().getZoom());
		
		int maxZoom = cameraEngine.getCamera().getParameters().getMaxZoom();
		while(curZoom < maxZoom) {
			cameraEngine.requestZoom("in");
			curZoom = cameraEngine.getCamera().getParameters().getZoom();
		}
		cameraEngine.requestZoom("in");
		assertEquals(maxZoom, cameraEngine.getCamera().getParameters().getZoom());
		
		cameraEngine.stop();
	}
}
