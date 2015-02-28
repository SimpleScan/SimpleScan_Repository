package com.SimpleScan.simplescan;

import java.io.File;
import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OCR extends Activity {
	
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected String _traindataPath;
	protected boolean _taken;
	
	protected static final String PHOTO_TAKEN	= "photo_taken";
		
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
       
        _image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        _button = ( Button ) findViewById( R.id.button ); //get id/button from main.xml
        _button.setOnClickListener( new ButtonClickHandler() );
        
        File im_direct = new File( Environment.getExternalStorageDirectory() + "/photo_capture/images" );
        if(!im_direct.exists()) {
        	if(im_direct.mkdir()) {
        		
        	}
        }
        _path = Environment.getExternalStorageDirectory() + "/photo_capture/images/make_machine_example.jpg";
        
        _traindataPath = Environment.getExternalStorageDirectory() + "/photo_capture/tesseract/tessdata";
        File traindata_direct = new File(_traindataPath);
        if(!traindata_direct.exists()) {
        	if(traindata_direct.mkdir()) {
        		Log.i( "traindata", "directory made" );
        		//copyAssets();
        	}
        	else Log.i( "traindata", "traindata_direct could not be made" );
        }
        else Log.i( "traindata", "traindata_direct exists" );
    }
    
    public class ButtonClickHandler implements View.OnClickListener 
    {
    	public void onClick( View view ){
    		Log.i("MakeMachine", "ButtonClickHandler.onClick()" );
    		startCameraActivity();
    	}
    }
    
    protected void startCameraActivity()
    {
    	Log.i("MakeMachine", "startCameraActivity()" );
    	File file = new File( _path );
    	Uri outputFileUri = Uri.fromFile( file );
    	
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	
    	startActivityForResult( intent, 0 ); //tells the system that when the user is done with the camera app
    										//return to this activity with some result.
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {	
    	Log.i( "MakeMachine", "resultCode: " + resultCode );
    	switch( resultCode )
    	{
    		case 0:
    			Log.i( "MakeMachine", "User cancelled" );
    			break;
    			
    		case -1:
			try {
				onPhotoTaken();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    			break;
    	}
    }
    
    protected void onPhotoTaken() throws IOException
    {
    	Log.i( "MakeMachine", "onPhotoTaken" );
    	
    	_taken = true;
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3; //down-sampling the image
    	
    	Bitmap bitmap = BitmapFactory.decodeFile( _path, options ); //create a bitmap from _path with options
    	
    	
    	//correcting photo for OCR(tess-two)
    	ExifInterface exif = new ExifInterface( _path );
    	int exifOrientation = exif.getAttributeInt(
    	        ExifInterface.TAG_ORIENTATION,
    	        ExifInterface.ORIENTATION_NORMAL);

    	int rotate = 0;

    	switch (exifOrientation) {
    	case ExifInterface.ORIENTATION_ROTATE_90:
    	    rotate = 90;
    	    break;
    	case ExifInterface.ORIENTATION_ROTATE_180:
    	    rotate = 180;
    	    break;
    	case ExifInterface.ORIENTATION_ROTATE_270:
    	    rotate = 270;
    	    break;
    	}

    	if (rotate != 0) {
    	    int w = bitmap.getWidth();
    	    int h = bitmap.getHeight();

    	    // Setting pre rotate
    	    Matrix mtx = new Matrix();
    	    mtx.preRotate(rotate);

    	    // Rotating Bitmap & convert to ARGB_8888, required by tess
    	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
    	}
    	bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    	
    	
    	_image.setImageBitmap(bitmap); //Assign the bitmap to ImageView
    	
    	_field.setVisibility( View.GONE );
    	
    	
    	TessBaseAPI baseApi = new TessBaseAPI();
    	Log.i("tessrect", "new tess object created");
    	baseApi.init(Environment.getExternalStorageDirectory() + "/photo_capture/tesseract/", "eng");
    	Log.i("tessrect", "initialized");
    	// Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");
    	baseApi.setImage(bitmap);
    	Log.i("tessrect", "bitmap/image set");
    	String recognizedText = baseApi.getUTF8Text();
    	Log.d("tessrect", recognizedText);
    	baseApi.end();
    	
    }

    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState){ //When the rotation is complete
    																  //restore from Bundle object
    	Log.i( "MakeMachine", "onRestoreInstanceState()");
    	if( savedInstanceState.getBoolean( OCR.PHOTO_TAKEN ) ) {
    		try {
				onPhotoTaken();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) { //When the device is rotated, this is called
    														//Save any details about app into a Bundle object
    	outState.putBoolean( OCR.PHOTO_TAKEN, _taken );
    }
}

