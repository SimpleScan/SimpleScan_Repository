package com.SimpleScan.simplescan.Camera.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DragRectView extends View {
	
	private Paint mRectPaint;
	private int mStartX = 0;
	private int mStartY = 0;
	private int mEndX = 0;
	private int mEndY = 0;
	private boolean mDrawRect = false;
	private TextPaint mTextPaint = null;
	private OnUpCallback mCallback = null;
	private boolean isCropping;
	
	/**
	 * Check whether the user requested cropping
	 * @return boolean that represents whether it is in the cropping mode
	 */
	public boolean isCroppingMode() {
    	return isCropping;
    }
    
    /**
     * Set the cropping mode upon user's request
     * @param newCroppingMode
     */
    public void setCroppingMode (boolean newCroppingMode) {
    	isCropping = newCroppingMode;
    }
	
	/**
	 * @author Tai Wu
	 *	Callback function for finishing dragging
	 */
	public interface OnUpCallback {
	    void onRectFinished(Rect rect);
	}

	/**
	 * DragRectView constructor that initialize related variables
	 * @param context
	 */
	public DragRectView(final Context context) {
	    super(context);
	    init();
	}

	/**
	 * DragRectView constructor that initialize related variables with specified attributes
	 * @param context
	 * @param attrs: attributes to be applied
	 */
	public DragRectView(final Context context, final AttributeSet attrs) {
	    super(context, attrs);
	    init(); 
	    isCropping = false;
	}
	
	/**
	 * DragRectView constructor that initialize related variables with specified attributes and style
	 * @param context
	 * @param attrs: attributes to be applied
	 * @param defStyle: style to be applied
	 */
	public DragRectView(final Context context, final AttributeSet attrs, final int defStyle) {
	    super(context, attrs, defStyle);
	    init(); 
	    isCropping = false;
	}
	
	/**
	 * Set the color of the border of the rectangle
	 * @param borderColor
	 */
	public void setBorderColor(int borderColor) {
		mRectPaint.setColor(borderColor);
	}

	/**
	 * Sets callback for up
	 * 
	 * @param callback
	 *            {@link OnUpCallback}
	 */
	public void setOnUpCallback(OnUpCallback callback) {
	    mCallback = callback;
	}

	/**
	 * Initializes internal data
	 */
	private void init() {
	    mRectPaint = new Paint();
	    mRectPaint.setColor(Color.GREEN);
	    mRectPaint.setStyle(Paint.Style.STROKE);
	    mRectPaint.setStrokeWidth(5); 

	    mTextPaint = new TextPaint();
	    mTextPaint.setColor(Color.MAGENTA);
	    mTextPaint.setTextSize(20);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if(isCropping) {
		    switch (event.getAction()) {
		    case MotionEvent.ACTION_DOWN:
		        mDrawRect = false;
		        mStartX = (int) event.getX();
		        mStartY = (int) event.getY();
		        invalidate();
		        break;
	
		    case MotionEvent.ACTION_MOVE:
		        final int x = (int) event.getX();
		        final int y = (int) event.getY();
		        if (!mDrawRect || Math.abs(x - mEndX) > 5
		                || Math.abs(y - mEndY) > 5) {
		            mEndX = x;
		            mEndY = y;
		            invalidate();
		        }
		        mDrawRect = true;
		        break;
	
		    case MotionEvent.ACTION_UP:
		        if (mCallback != null) {
		        	Log.i("DragRectView", "onTouchEvent: before onRectFinished");
		            mCallback.onRectFinished(new Rect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY), Math.max(mEndX, mStartX),  Math.max(mEndY, mStartX)));
		            Log.i("DragRectView", "onTouchEvent: after onRectFinished");
		        }
		        invalidate();
		        break;
	
		    default:
		        break;
		    }
		}
	    return true;
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		if(isCropping) {
		    super.onDraw(canvas);
		    if (mDrawRect) {
		        canvas.drawRect(Math.min(mStartX, mEndX), Math.min(mStartY, mEndY),
		                Math.max(mEndX, mStartX), Math.max(mEndY, mStartY),
		                mRectPaint);
		        canvas.drawText(
		                "  (" + Math.abs(mStartX - mEndX) + ", "
		                        + Math.abs(mStartY - mEndY) + ")",
		                Math.max(mEndX, mStartX), Math.max(mEndY, mStartY),
		                mTextPaint);
		    }
		}
	}
}
