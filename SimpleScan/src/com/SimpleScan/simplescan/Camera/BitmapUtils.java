package com.SimpleScan.simplescan.Camera;

import com.SimpleScan.simplescan.Camera.Views.ZoomableImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

public class BitmapUtils extends BitmapFactory {

	public static Bitmap createImageViewBitmap(ZoomableImageView theImageView) {
		return Bitmap.createScaledBitmap(theImageView.getPhotoBitmap(), theImageView.getWidth(), theImageView.getHeight(), false);            	            		
	}
	
	public static Bitmap createRectBitmap(Bitmap origBitmap, Rect rect) {
		return Bitmap.createBitmap(origBitmap, rect.left, rect.top, rect.width(), rect.height()); 
	}
	
	public static Bitmap createPreviewBitmap(byte[] data) {
        return createSampledBitmapFromData(data, 4);
    }
	
	public static Bitmap createSampledBitmap(String imgPath, int sampleSize) {
		BitmapFactory.Options options = getBitmapOptions(sampleSize);
		return BitmapFactory.decodeFile(imgPath, options);
	}
	
	public static Bitmap createSampledBitmapFromData(byte[] data, int sampleSize) {
		BitmapFactory.Options options = getBitmapOptions(sampleSize);
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);   
        bitmap = rotateBitmap(bitmap);
        return bitmap;
	}
	
	private static BitmapFactory.Options getBitmapOptions (int sampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize; //down-sampling the image
		return options;
	}
	
	private static Bitmap rotateBitmap(Bitmap im) {
		//Rotating bitmap to the right orientation
        Matrix rotate_matrix = new Matrix();
        rotate_matrix.postRotate(90);
        Bitmap bitmap = Bitmap.createBitmap(im, 0, 0, im.getWidth(), im.getHeight(), rotate_matrix, true);
        return bitmap;
	}
}
