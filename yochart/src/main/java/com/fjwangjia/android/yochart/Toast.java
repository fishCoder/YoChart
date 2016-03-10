package com.fjwangjia.android.yochart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by flb on 16/3/8.
 */
public class Toast {

    RectF drawRect;
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int mColor;
    int mBgColor;
    String mContent;

    int mWidth;
    int mHeight;

    //线下标
    int lineIndex;
    //
    int index;

    PointF drawPoint = new PointF();

    float fSpace = 10f;

    RectF rect;

    public Toast(String content,RectF rect,int textSize,int color,int bgColor){
        mColor = color;
        mBgColor = bgColor;
        mContent = content==null?"":content;

        Rect textRect = new Rect();
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(mContent, 0, mContent.length(), textRect);
        mPaint.setColor(color);

        mWidth = textRect.width();
        mHeight = textRect.height();

        startShowTime = System.currentTimeMillis();

        fSpace = textSize/4;
        this.rect = rect;
    }


    void draw(PointF point,Canvas canvas){

        if(point.x+mWidth/2>rect.right-2*fSpace){
            drawPoint.x = rect.right - mWidth/2-4*fSpace;
        }else if (point.x-mWidth/2<rect.left+fSpace){
            drawPoint.x = rect.left + 4*fSpace;
        }else {
            drawPoint.x = point.x - mWidth/2;
        }


        if(point.y-mHeight < rect.top + 2*fSpace){
            drawPoint.y = point.y + mHeight + fSpace;
        }else {
            drawPoint.y = point.y - mHeight - fSpace;
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mBgColor);
        paint.setStyle(Paint.Style.FILL);

//        canvas.drawRect(drawRect, paint);
        canvas.drawRoundRect(new RectF(
                            drawPoint.x-fSpace*3,
                            drawPoint.y-fSpace*5,
                            drawPoint.x+mWidth+fSpace*3,
                            drawPoint.y+mHeight-fSpace), 8f, 8f, paint);
        canvas.drawText(mContent,drawPoint.x,drawPoint.y,mPaint);
    }


    long startShowTime = 0l;

}
