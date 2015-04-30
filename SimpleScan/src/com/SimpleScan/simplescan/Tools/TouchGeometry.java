package com.SimpleScan.simplescan.Tools;

import android.util.FloatMath;
import android.view.MotionEvent;

public class TouchGeometry {

	public static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
	
}
