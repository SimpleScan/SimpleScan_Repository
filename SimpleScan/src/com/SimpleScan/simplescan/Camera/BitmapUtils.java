package com.SimpleScan.simplescan.Camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapUtils extends BitmapFactory {
	
	public static Bitmap createSampledBitmap(String imgPath, int sampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize; //down-sampling the image
		return BitmapFactory.decodeFile(imgPath, options);
	}
	
	public static Bitmap createPreviewBitmap(byte[] data) {
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; //down-sampling the image
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);   
        
        bitmap = rotateBitmap(bitmap);
        
        return bitmap;
    }
	
	private static Bitmap rotateBitmap(Bitmap im) {
		//Rotating bitmap to the right orientation
        Matrix rotate_matrix = new Matrix();
        rotate_matrix.postRotate(90);
        Bitmap bitmap = Bitmap.createBitmap(im, 0, 0, im.getWidth(), im.getHeight(), rotate_matrix, true);
        return bitmap;
	}
}
