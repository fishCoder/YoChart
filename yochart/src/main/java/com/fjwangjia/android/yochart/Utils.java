package com.fjwangjia.android.yochart;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by flb on 16/2/26.
 */
public class Utils {
    public  static int  dpTopx(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public  static float  dpTopxF(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static double distance(PointF p, PointF p1) {
        return Math.hypot(p.x-p1.x, p.y-p1.y);
    }
    public static float  decelerate(float v,float a,float t){
        return (v*t-a*t*t/2);
    }
}
