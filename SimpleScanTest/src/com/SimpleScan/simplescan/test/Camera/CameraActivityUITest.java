package com.SimpleScan.simplescan.test.Camera;

import com.SimpleScan.simplescan.CameraActivity;
import com.SimpleScan.simplescan.Camera.CameraEngine;

import android.graphics.Camera;
import android.hardware.Camera.Parameters;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class CameraActivityUITest extends ActivityInstrumentationTestCase2<CameraActivity>{
	
	private CameraActivity mTestCameraActivity;
	private Button shutterButton;
	private Button focusButton;
	private Button flashButton;
	private Button zoominButton;
	private Button zoomoutButton;
	private SurfaceView cameraFrame;
	private CameraEngine cameraEngine; 
	
	public CameraActivityUITest() {
		super(CameraActivity.class);
	}

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        
        mTestCameraActivity = getActivity();
        
        cameraFrame = (SurfaceView) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.camera_frame);
        shutterButton = (Button) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.shutter_button);
        focusButton = (Button) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.focus_button);
        flashButton = (Button) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.flash_button);
        zoominButton = (Button) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.zoomin_button);
        zoomoutButton = (Button) mTestCameraActivity.findViewById(com.SimpleScan.simplescan.R.id.zoomout_button);  
        
        cameraEngine = new CameraEngine(cameraFrame.getHolder());
    }
	
	public void testPreconditions() {
	    assertNotNull("mTestCameraActivity is null", mTestCameraActivity);
	    
	    assertNotNull("cameraFrame is null", cameraFrame);
	    assertNotNull("shutterButton is null", shutterButton);
	    assertNotNull("focusButton is null", focusButton);
	    assertNotNull("flashButton is null", flashButton);
	    assertNotNull("zoominButton is null", zoominButton);
	    assertNotNull("zoomoutButton is null", zoomoutButton);
	    
	    assertNotNull("cameraEngine is null", cameraEngine);
	    assertEquals(cameraFrame.getHolder(), cameraEngine.getSurfaceHolder());
	}
	
	@MediumTest
	public void testCameraFrame_layout() {
	    final View decorView = mTestCameraActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, cameraFrame);
	    final ViewGroup.LayoutParams layoutParams = cameraFrame.getLayoutParams();
	    assertNotNull(layoutParams);
	    assertEquals(layoutParams.width, WindowManager.LayoutParams.FILL_PARENT);
	    assertEquals(layoutParams.height, WindowManager.LayoutParams.FILL_PARENT);
	}
	
	@MediumTest
	public void testShutterButton_layout() {
	    final View decorView = mTestCameraActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, shutterButton);
	    final ViewGroup.LayoutParams layoutParams = shutterButton.getLayoutParams();
	    assertNotNull(layoutParams);
	}
	
	@MediumTest
	public void testFocusButton_layout() {
	    final View decorView = mTestCameraActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, focusButton);
	    final ViewGroup.LayoutParams layoutParams = focusButton.getLayoutParams();
	    assertNotNull(layoutParams);
	}
	
	@MediumTest
	public void testFlashButton_layout() {
	    final View decorView = mTestCameraActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, flashButton);
	    final ViewGroup.LayoutParams layoutParams = flashButton.getLayoutParams();
	    assertNotNull(layoutParams);
	}
	
	@MediumTest
	public void testZoomInButton_layout() {
	    final View decorView = mTestCameraActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, zoominButton);
	    final ViewGroup.LayoutParams layoutParams = zoominButton.getLayoutParams();
	    assertNotNull(layoutParams);
	}
	
	@MediumTest
	public void testZoomOutButton_layout() {
	    final View decorView = mTestCameraActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, zoomoutButton);
	    final ViewGroup.LayoutParams layoutParams = zoomoutButton.getLayoutParams();
	    assertNotNull(layoutParams);
	}
	
}
