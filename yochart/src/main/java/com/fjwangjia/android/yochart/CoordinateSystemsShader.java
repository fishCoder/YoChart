package com.fjwangjia.android.yochart;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by flb on 16/2/26.
 */
public class CoordinateSystemsShader implements ChartShader {

    RectF drawRect;
    Paint mPaintX = new Paint();
    Paint mPaintY = new Paint();
    int iSpace;
    int iStartIndex;
    String[] mContentsY;
    String[] mContentsX;

    float mOffsetX;
    float mOffsetY;
    int mLineColor;
    int mTextColor;
    int iScreenTotalX = 4;
    public CoordinateSystemsShader(int sizeX,int sizeY,int space){
        mPaintY.setTextSize(sizeY);
        mPaintX.setTextSize(sizeX);

        mPaintY.setColor(Color.BLACK);
        mPaintX.setColor(Color.BLACK);

        mPaintY.setAntiAlias(true);
        mPaintX.setAntiAlias(true);
        iSpace = space;
    }

    public void setContents(String[] contentsX,String[] contentsY){
        mContentsY = contentsY;
        mContentsX = contentsX;

    }


    public void setTextColor(int textColor){
        mTextColor = textColor;
        mPaintY.setColor(mTextColor);
        mPaintX.setColor(mTextColor);
    }
    /**
     *
     * @param startIndex  起始坐标
     * @param screenTotalX 最大显示坐标个数
     * @param offsetX   起始坐标距Y轴的偏移量 0或小于0
     */
    public void drawData(int startIndex,int screenTotalX,float offsetX){
        mOffsetX = offsetX;
        iStartIndex = startIndex;
        iScreenTotalX = screenTotalX;
    }

    @Override
    public void layout(RectF rect) {
        drawRect = rect;
    }

    public RectF calculateChartCanvasRect(){


        Rect rectX = new Rect();
        String content = "8888年上半年";
        mPaintX.getTextBounds(content,0,content.length(),rectX);

        RectF rectF = new RectF(drawRect.left+iSpace+rectX.width()/2,
                                drawRect.top+iSpace,
                                drawRect.right-iSpace-rectX.width()/2,
                                drawRect.bottom-3*iSpace-rectX.height());
        return rectF;
    }

    @Override
    public void shader(Canvas canvas) {
        if(mContentsY == null && mContentsX ==null){return;}

        RectF screenRect = calculateChartCanvasRect();

        int ylinesCount = mContentsY.length;
        int xlinesCount = mContentsX.length;

        float perYlength = screenRect.height()/(ylinesCount-1);
        float perXlength = screenRect.width()/(iScreenTotalX-1);

        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(mLineColor);
        for (int i=0;i<ylinesCount;i++){
            float baseLineY = screenRect.bottom - perYlength * i;
            Path path = new Path();
            path.moveTo(screenRect.left, baseLineY);
            path.lineTo(screenRect.right, baseLineY);
            canvas.drawPath(path,linePaint);

            Rect fontRect = new Rect();
            mPaintY.getTextBounds(mContentsY[i],0,mContentsY[i].length(),fontRect);
            canvas.drawText(mContentsY[i],
                    screenRect.left - fontRect.width() - iSpace,
                    baseLineY  + fontRect.height()/2 ,
                    mPaintY);

            if(i==0){
                PathEffect effects = new DashPathEffect(new float[] { 5,10 }, 1);
                linePaint.setPathEffect(effects);
            }

        }

        int endIndex = iStartIndex+iScreenTotalX;
        if(endIndex<mContentsX.length){
            endIndex++;
        }
        for (int i=iStartIndex;i<endIndex;i++){
            Rect fontRect = new Rect();
            mPaintX.getTextBounds(mContentsX[i],0,mContentsX[i].length(),fontRect);
            canvas.drawText(mContentsX[i],
                    screenRect.left-fontRect.width()/2 + perXlength * (i-iStartIndex)+mOffsetX,
                    screenRect.bottom+fontRect.height()+iSpace,
                    mPaintX);
        }
    }
}
