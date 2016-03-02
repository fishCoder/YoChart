package com.fjwangjia.android.yochart;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by flb on 16/2/26.
 */
public interface ChartShader {
    void layout(RectF rect);
    void shader(Canvas canvas);
}
