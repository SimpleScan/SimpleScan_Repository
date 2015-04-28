package com.SimpleScan.simplescan.Camera.Views;

import com.SimpleScan.simplescan.Tools.TouchGeometry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;

public class ZoomableImageView extends ImageView {
	
	private static final String TAG = "ZoomableImageView";
	
	public static final int DEFAULT_SCALE_FIT_INSIDE = 0;
    public static final int DEFAULT_SCALE_ORIGINAL = 1;
	
	private boolean isCropping;
	
    private Bitmap imgBitmap = null;
    private Bitmap scaled_imgBitmap = null;
   
    private int containerWidth;
    private int containerHeight;
       
    private Paint background;   
   
    //Matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
   
    private PointF start = new PointF();       
   
    private float currentScale;
    private float curX;
    private float curY;
   
    //We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
   
    //For animating stuff   
    private float targetX;
    private float targetY;
    private float targetScale;
    private float targetScaleX;
    private float targetScaleY;
    private float scaleChange;
    
    private boolean isAnimating = false;
   
    //For pinch and zoom
    private float oldDist = 1f;   
    private PointF mid = new PointF();
   
    private Handler mHandler = new Handler();       
   
    private float minScale;
    private float maxScale = 8.0f;
   
    private GestureDetector gestureDetector;
    private int defaultScale;
   
    public boolean isCroppingMode() {
    	return isCropping;
    }
    
    public void setCroppingMode (boolean newCroppingMode) {
    	isCropping = newCroppingMode;
    	setFocusable(!isCropping);
        setFocusableInTouchMode(!isCropping);
    }
    
    public int getDefaultScale() {
        return defaultScale;
    }

    public void setDefaultScale(int defaultScale) {
        this.defaultScale = defaultScale;
    }

    public ZoomableImageView(Context context) {
        super(context);
        
        isCropping = false;
        
        setFocusable(true);
        setFocusableInTouchMode(true);
                       
        initPaints();
        gestureDetector = new GestureDetector(new MyGestureDetector());  
    }
   
    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
       
        isCropping = false;
  
        initPaints();
        gestureDetector = new GestureDetector(new MyGestureDetector());
       
        defaultScale = ZoomableImageView.DEFAULT_SCALE_FIT_INSIDE;
    }
   
    private void initPaints() {
        background = new Paint();
    }
   
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
           
        //Reset the width and height. Will draw bitmap and change
        containerWidth = width;
        containerHeight = height;
       
        if(imgBitmap != null) {
            int imgHeight = imgBitmap.getHeight();
            int imgWidth = imgBitmap.getWidth();
           
            float scale;
            int initX = 0;
            int initY = 0;           
           
            if(defaultScale == ZoomableImageView.DEFAULT_SCALE_FIT_INSIDE) {               
                if(imgWidth > containerWidth) {           
                    scale = (float)containerWidth / imgWidth;           
                    float newHeight = imgHeight * scale;           
                    initY = (containerHeight - (int)newHeight)/2;
                   
                    matrix.setScale(scale, scale);
                    matrix.postTranslate(0, initY);
                } else {           
                    scale = (float)containerHeight / imgHeight;
                    float newWidth = imgWidth * scale;
                    initX = (containerWidth - (int)newWidth)/2;
                   
                    matrix.setScale(scale, scale);
                    matrix.postTranslate(initX, 0);
                }
               
                curX = initX;
                curY = initY;
               
                currentScale = scale;
                minScale = scale;
            } else {
                if(imgWidth > containerWidth) {                                   
                    initY = (containerHeight - (int)imgHeight)/2;                   
                    matrix.postTranslate(0, initY);
                } else {                               
                    initX = (containerWidth - (int)imgWidth)/2;                   
                    matrix.postTranslate(initX, 0);
                }
               
                curX = initX;
                curY = initY;
               
                currentScale = 1.0f;
                minScale = 1.0f;               
            }
            
            invalidate();           
        }
    }
   
    @Override
    protected void onDraw(Canvas canvas) {               
        if(imgBitmap != null && canvas != null) canvas.drawBitmap(imgBitmap, matrix, background);                                                   
    }
   
    //Checks and sets the target image x and y co-ordinates if out of bounds
    private void checkImageConstraints() {
        if(imgBitmap == null) return;
       
        float[] mvals = new float[9];
        matrix.getValues(mvals);
       
        currentScale = mvals[0];
               
        if(currentScale < minScale) {                               
            float deltaScale = minScale / currentScale;                   
            float px = containerWidth/2;
            float py = containerHeight/2;           
            matrix.postScale(deltaScale, deltaScale, px, py);
            invalidate();
        }       
       
        matrix.getValues(mvals);
        currentScale = mvals[0];
        curX = mvals[2];
        curY = mvals[5];
               
        int rangeLimitX = containerWidth - (int)(imgBitmap.getWidth() * currentScale);
        int rangeLimitY = containerHeight - (int)(imgBitmap.getHeight() * currentScale);       
       
        boolean toMoveX = false;
        boolean toMoveY = false;   
       
        if(rangeLimitX < 0) {
            if(curX > 0) {
                targetX = 0;
                toMoveX = true;
            } else if(curX < rangeLimitX) {
                targetX = rangeLimitX;
                toMoveX = true;
            }
        } else {
            targetX = rangeLimitX / 2;
            toMoveX = true;
        }
       
        if(rangeLimitY < 0) {
            if(curY > 0) {
                targetY = 0;
                toMoveY = true;
            } else if(curY < rangeLimitY) {
                targetY = rangeLimitY;
                toMoveY = true;
            }
        } else {
            targetY = rangeLimitY / 2;
            toMoveY = true;
        }
       
        if(toMoveX == true || toMoveY == true) {
            if(toMoveY == false) targetY = curY;
            if(toMoveX == false) targetX = curX;
           
            //Disable touch event actions
            isAnimating = true;
            //Initialize timer           
            mHandler.removeCallbacks(mUpdateImagePositionTask);
            mHandler.postDelayed(mUpdateImagePositionTask, 100);
        }
    }       
   
   
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(!isCropping) {
	        if(gestureDetector.onTouchEvent(event)) return true;
	        if(isAnimating == true) return true;
	       
	        //Handle touch events here       
	        float[] mvals = new float[9];
	        switch(event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: // contains initial starting location
	            if(isAnimating == false) {
	                savedMatrix.set(matrix);
	                start.set(event.getX(), event.getY());           
	                mode = DRAG;               
	            }
	        break;
	       
	        case MotionEvent.ACTION_POINTER_DOWN: //contains index of the non-primary pointer that will change
	            oldDist = TouchGeometry.spacing(event);           
	            if(oldDist > 10f) { // if the initial locations of the two touches are at least 10f apart 
	                savedMatrix.set(matrix);
	                midPoint(mid, event);
	                mode = ZOOM;
	            }
	        break;
	       
	        case MotionEvent.ACTION_UP:
	        case MotionEvent.ACTION_POINTER_UP:
	            mode = NONE;
	           
	            matrix.getValues(mvals); //copy 9 values from matrix into mvals
	            curX = mvals[2];
	            curY = mvals[5];
	            currentScale = mvals[0];
	           
	            if(isAnimating == false) checkImageConstraints();
	        break;
	           
	        case MotionEvent.ACTION_MOVE:           
	            if(mode == DRAG && isAnimating == false) {
	                matrix.set(savedMatrix);
	                float diffX = event.getX() - start.x;
	                float diffY = event.getY() - start.y;
	               
	                matrix.postTranslate(diffX, diffY);
	                               
	                matrix.getValues(mvals);
	                curX = mvals[2];
	                curY = mvals[5];
	                currentScale = mvals[0];
	                
	                System.out.println("mode==DRAG: " + "diffX="+diffX + " diffY="+diffY + " curX="+curX + " curY="+curY + " currentScale="+currentScale);
	                
	            } else if(mode == ZOOM && isAnimating == false) {
	                float newDist = TouchGeometry.spacing(event);               
	                if(newDist > 10f) {
	                    matrix.set(savedMatrix);
	                    float scale = newDist / oldDist; //scale = change in distance                  
	                    matrix.getValues(mvals);
	                    currentScale = mvals[0];
	                                       
	                    if(currentScale * scale <= minScale) scale = minScale/currentScale;
	                    else if(currentScale * scale >= maxScale) scale = maxScale/currentScale;
	                    matrix.postScale(scale, scale, mid.x, mid.y);                    
	               	                    
	                    System.out.println("mode==ZOOM:" + " oldDist="+oldDist + " curX="+curX + " curY="+curY + " currentScale="+currentScale);
	                    
	                    matrix.getValues(mvals);
	                    curX = mvals[2];
	                    curY = mvals[5];
	                    currentScale = mvals[0];
	                    
	                    System.out.println("scale="+scale + " newDist="+newDist + " new_curX="+curX + " new_curY="+curY + " new_cuurentScale="+currentScale);	                     
	                }	                	                
	            }          
	        break;                               
	        }
	        setScaledImgBitmap(imgBitmap, curX, curY, matrix);
	        
	        //Calculate the transformations and then invalidate
	        invalidate();    
    	}
        return true;
    }
   
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x/2, y/2);
    }
   
    public void setImageBitmap(Bitmap b) {       
        if(b != null) {
            imgBitmap = b;
            scaled_imgBitmap = imgBitmap;
           
            containerWidth = getWidth();
            containerHeight = getHeight();
                       
            int imgHeight = imgBitmap.getHeight();
            int imgWidth = imgBitmap.getWidth();
           
            float scale;
            int initX = 0;
            int initY = 0;
           
            matrix.reset();
           
            if(defaultScale == ZoomableImageView.DEFAULT_SCALE_FIT_INSIDE) {               
                if(imgWidth > containerWidth) {           
                    scale = (float)containerWidth / imgWidth;           
                    float newHeight = imgHeight * scale;           
                    initY = (containerHeight - (int)newHeight)/2;
                   
                    matrix.setScale(scale, scale);
                    matrix.postTranslate(0, initY);
                } else {           
                    scale = (float)containerHeight / imgHeight;
                    float newWidth = imgWidth * scale;
                    initX = (containerWidth - (int)newWidth)/2;
                   
                    matrix.setScale(scale, scale);
                    matrix.postTranslate(initX, 0);
                }
               
                curX = initX;
                curY = initY;
               
                currentScale = scale;
                minScale = scale;
            } else {
                if(imgWidth > containerWidth) {
                    initX = 0;
                    if(imgHeight > containerHeight) initY = 0;
                    else initY = (containerHeight - (int)imgHeight)/2;
                                       
                    matrix.postTranslate(0, initY);
                } else {                               
                    initX = (containerWidth - (int)imgWidth)/2;
                    if(imgHeight > containerHeight) initY = 0;
                    else initY = (containerHeight - (int)imgHeight)/2;

                    matrix.postTranslate(initX, 0);
                }
               
                curX = initX;
                curY = initY;
               
                currentScale = 1.0f;
                minScale = 1.0f;               
            }   
            invalidate();           
        } else  Log.d(TAG, "bitmap is null");
    }
    
    private void setScaledImgBitmap(Bitmap srcImgBitmap, float curX, float curY, Matrix matrix) {
    	if(srcImgBitmap!=null) {
        	float imgWidth = srcImgBitmap.getWidth()*currentScale;
        	float imgHeight = srcImgBitmap.getHeight()*currentScale;
        	
        	float cropX=0;
        	float cropY=0;
        	float cropWidth=srcImgBitmap.getWidth();
        	float cropHeight=srcImgBitmap.getHeight();
        	
            if(curX >= 0) {
            	cropX = 0;
            	if(imgWidth+curX < containerWidth) cropWidth = imgWidth;
            	else  cropWidth = imgWidth  - curX;
            } else {
            	cropX = -curX;
            	if(imgWidth+curX < containerWidth) cropWidth = imgWidth + curX;
            	else cropWidth = containerWidth;
            }
            if(curY >= 0) {
            	cropY = 0;
            	if(imgHeight+curY < containerHeight) cropHeight = imgHeight;
            	else cropHeight = imgHeight - curY;		
            } else {
            	cropY = -curY;
            	if(imgHeight+curY < containerHeight) cropHeight = imgHeight + curY;
            	else cropHeight = containerHeight;
            }        
            
        	if(cropX < 0) cropX = 0;
            else if(cropX > imgWidth) cropX = imgWidth;
            if(cropY < 0) cropY = 0;
            else if(cropY > imgHeight) cropY = imgHeight;
        	
            Bitmap resized_imgBitmap = Bitmap.createScaledBitmap(srcImgBitmap, (int) imgWidth, (int) imgHeight, false);
    		scaled_imgBitmap = Bitmap.createBitmap(resized_imgBitmap, (int) cropX, (int) cropY, (int) cropWidth, (int) cropHeight);
        }
    }
   
    public Bitmap getPhotoBitmap() {       
        return scaled_imgBitmap;
    }
   
    private Runnable mUpdateImagePositionTask = new Runnable() {
        public void run() {       
            float[] mvals;
           
            if(Math.abs(targetX - curX) < 5 && Math.abs(targetY - curY) < 5) {
                isAnimating = false;
                mHandler.removeCallbacks(mUpdateImagePositionTask);
               
                mvals = new float[9];
                matrix.getValues(mvals);
               
                currentScale = mvals[0];
                curX = mvals[2];
                curY = mvals[5];
               
                //Set the image parameters and invalidate display
                float diffX = (targetX - curX);
                float diffY = (targetY - curY);
                               
                matrix.postTranslate(diffX, diffY);
            } else {
                isAnimating = true;
                mvals = new float[9];
                matrix.getValues(mvals);
               
                currentScale = mvals[0];
                curX = mvals[2];
                curY = mvals[5];
               
                //Set the image parameters and invalidate display
                float diffX = (targetX - curX) * 0.3f;
                float diffY = (targetY - curY) * 0.3f;
                               
                matrix.postTranslate(diffX, diffY);               
                mHandler.postDelayed(this, 25);               
            }     
            invalidate();           
        }
    };
   
    private Runnable mUpdateImageScale = new Runnable() {
        public void run() {           
            float transitionalRatio = targetScale / currentScale;           
            float dx;
            if(Math.abs(transitionalRatio - 1) > 0.05) {
                isAnimating = true;               
                if(targetScale > currentScale) {                                       
                    dx = transitionalRatio - 1;
                    scaleChange = 1 + dx * 0.2f;
                   
                    currentScale *= scaleChange;
                   
                    if(currentScale > targetScale) {
                        currentScale = currentScale / scaleChange;
                        scaleChange = 1;
                    }
                } else {                                   
                    dx = 1 - transitionalRatio;                   
                    scaleChange = 1 - dx * 0.5f;
                    currentScale *= scaleChange;
                   
                    if(currentScale < targetScale) {
                        currentScale = currentScale / scaleChange;
                        scaleChange = 1;
                    }
                }                                       
               
                if(scaleChange != 1) {
                    matrix.postScale(scaleChange, scaleChange, targetScaleX, targetScaleY);               
                    mHandler.postDelayed(mUpdateImageScale, 15);
                    invalidate();
                } else {
                    isAnimating = false;
                    scaleChange = 1;                   
                    matrix.postScale(targetScale/currentScale, targetScale/currentScale, targetScaleX, targetScaleY);
                    currentScale = targetScale;
                    mHandler.removeCallbacks(mUpdateImageScale);
                    invalidate();
                    checkImageConstraints();
                }               
            } else {
                isAnimating = false;
                scaleChange = 1;               
                matrix.postScale(targetScale/currentScale, targetScale/currentScale, targetScaleX, targetScaleY);
                currentScale = targetScale;
                mHandler.removeCallbacks(mUpdateImageScale);
                invalidate();
                checkImageConstraints();
            }                               
        }
    };

   class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent event) {           
            if(!isCropping) {
	        	if(isAnimating == true) return true;
	           
	            scaleChange = 1;
	            isAnimating = true;
	            targetScaleX = event.getX();
	            targetScaleY = event.getY();
	           
	            if(Math.abs(currentScale - maxScale) > 0.1) targetScale = maxScale;
	            else targetScale = minScale;

	            mHandler.removeCallbacks(mUpdateImageScale);
	            mHandler.post(mUpdateImageScale);  
            }
            return true;
        }
       
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    		if(!isCropping) return super.onFling(e1, e2, velocityX, velocityY);
    		else return true;
        }
       
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    }
}
