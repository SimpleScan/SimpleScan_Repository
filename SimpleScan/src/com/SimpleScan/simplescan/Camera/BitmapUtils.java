package com.SimpleScan.simplescan.Camera;

import com.SimpleScan.simplescan.Camera.Views.ZoomableImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

public class BitmapUtils extends BitmapFactory {

	/**
	 * Create a Bitmap from a ZoomableImageView
	 * @param theImageView: the ZoomableImageView that represents the Bitmap 
	 * @return the Bitmap extracted
	 */
	public static Bitmap createImageViewBitmap(ZoomableImageView theImageView) {
		return Bitmap.createScaledBitmap(theImageView.getPhotoBitmap(), theImageView.getWidth(), theImageView.getHeight(), false);            	            		
	}
	
	/**
	 * Sub-sample a bitmap based on the ROI represented by rect
	 * @param origBitmap: the original input Bitmap
	 * @param rect: represent the ROI of the Bitmap
	 * @return sub-sampled Bitmap
	 */
	public static Bitmap createRectBitmap(Bitmap origBitmap, Rect rect) {
		return Bitmap.createBitmap(origBitmap, rect.left, rect.top, rect.width(), rect.height()); 
	}
	
	/**
	 * Create a Bitmap from camera data, specifically for camera's image preview
	 * @param data: data from the camera
	 * @return Bitmap extracted
	 */
	public static Bitmap createPreviewBitmap(byte[] data) {
        return createSampledBitmapFromData(data, 4);
    }
	
	/**
	 * Decode the file path and create a Bitmap
	 * @param imgPath: the file path for the Bitmap stored
	 * @param sampleSize: size of the output Bitmap
	 * @return Bitmap extracted
	 */
	public static Bitmap createSampledBitmap(String imgPath, int sampleSize) {
		BitmapFactory.Options options = getBitmapOptions(sampleSize);
		return BitmapFactory.decodeFile(imgPath, options);
	}
	
	/**
	 * Decompress the data to create a Bitmap
	 * @param data: data that represents the Bitmap
	 * @param sampleSize: size of the output Bitmap
	 * @return Bitmap extracted
	 */
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
