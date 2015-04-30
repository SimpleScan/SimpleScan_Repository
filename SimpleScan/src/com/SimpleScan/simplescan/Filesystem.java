package com.SimpleScan.simplescan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Filesystem {
	
	public static final int MEDIA_TYPE_IMAGE = 1;

	protected static String _appPath = Environment.getExternalStorageDirectory() + "/SimpleScan";	
	protected static String _tessPath = Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract";
	protected static String _traindataPath = Environment.getExternalStorageDirectory() + "/SimpleScan/tesseract/tessdata";
	public static String _ImgDirPath = Environment.getExternalStorageDirectory() + "/SimpleScan/images";
	
	public static void init(Context context) {
		File app_dir = new File(_appPath);
        if(!app_dir.exists()) {
        	if(app_dir.mkdir()) {
        		makeAppSubdirs(context);
        	}
        	else Log.i( "app_dir", "app_dir could not be made" );
        }
        else {
        	Log.i( "app_dir", "app_dir exists" );
        	makeAppSubdirs(context);
        }
	}
	
	public static String saveBitmap (Bitmap bitmap) throws IOException {
    	
    	File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
    	
    	if (pictureFile == null){
            Log.d("saveBitmap", "Error creating media file, check storage permissions: ");
            return null;
        }
    	
    	FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
        
        return pictureFile.getPath();
    }
	
	private static void makeAppSubdirs(Context context) {
		File im_direct = new File( Environment.getExternalStorageDirectory() + "/SimpleScan/images" );
		
        if(!im_direct.exists()) {
        	if(im_direct.mkdir()) {
        		
        	}
        }
		
        File tess_dir = new File(_tessPath);
        if(!tess_dir.exists()) {
        	if(tess_dir.mkdir()) {
        		Log.i( "tess_dir", "tess_dir made" );
        		makeTessSubdir(context);
        	}
        	else Log.i( "tess_dir", "tess_dir could not be made" );
        }
        else {
        	Log.i( "tess_dir", "tess_dir exists" );
        	makeTessSubdir(context);
        }
	}
	
	private static void makeTessSubdir(Context context) {
		File traindata_direct = new File(_traindataPath);
        if(!traindata_direct.exists()) {
        	if(traindata_direct.mkdir()) {
        		Log.i( "traindata", "directory made" );
        		CopyAssets(context);
        	}
        	else Log.i( "traindata", "traindata_direct could not be made" );
        }
        else {
        	Log.i( "traindata", "traindata_direct exists" );
        	CopyAssets(context);
        }
	}
	
	/** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Filesystem._ImgDirPath);
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
	
	private static void CopyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("tessdata");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
 
        for(String filename : files) {
            System.out.println("File name => "+filename);
            InputStream in = null;
            OutputStream out = null;
            try {
              in = assetManager.open("tessdata/"+filename);   // if files resides inside the "Files" directory itself
              out = new FileOutputStream(_traindataPath +"/" + filename);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }
    
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
}
