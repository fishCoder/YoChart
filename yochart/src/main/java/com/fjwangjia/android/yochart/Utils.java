package com.fjwangjia.android.yochart;

import android.content.Context;

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

    public static float  decelerate(float v,float a,float t){
        return (v*t-a*t*t/2);
    }
}
