package com.SimpleScan.simplescan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.SimpleScan.simplescan.Camera.CameraEngine;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.ShutterCallback {

	static final String TAG = "DBG_" + "CameraActivity";

	public static final int MEDIA_TYPE_IMAGE = 1;
	
    Button shutterButton;
    Button focusButton;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;
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
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
    protected void onResume() {
        super.onResume();

        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame);
        shutterButton = (Button) findViewById(R.id.shutter_button);
        focusButton = (Button) findViewById(R.id.focus_button);
        
        shutterButton.setOnClickListener(this);
        focusButton.setOnClickListener(this);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraFrame.setOnClickListener(this);
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
    }
    

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        
        if (pictureFile == null){
            Log.d("PictureCallback", "Error creating media file, check storage permissions: ");
            return;
        } else if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("PictureCallback", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("PictureCallback", "Error accessing file: " + e.getMessage());
        }
        
        bitmap = BitmapFactory.decodeFile(pictureFile.getPath());
    }

	
	@Override
    public void onShutter() {

    }
	
	/** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}

