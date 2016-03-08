package com.fjwangjia.android.yochart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

/**
 * Created by flb on 16/2/26.
 */
public class LineChartShader implements ChartShader {

    RectF drawRect;
    Paint mPaint = new Paint();
    List<PointF[]> mPoints;
    List<Integer> mColors;


    int mFillColor = Color.WHITE;

    float iSpace = 10f;
    float mRadius = 5f;
    float mPaintWidth = 0f;
    public LineChartShader(float paintWidth,float radius){
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(paintWidth);
        mRadius = radius;
        mPaintWidth = paintWidth;
        iSpace = radius + paintWidth/2;
    }

    @Override
    public void layout(RectF rect) {
        drawRect = new RectF(rect.left-iSpace,rect.top-iSpace,rect.right+iSpace,rect.bottom+iSpace);
    }

    public void setPoint(List<PointF[]> points,List<Integer> colors){
        mPoints = points;
        for (PointF[] pointFs : mPoints){
            for (PointF pointF : pointFs){
                pointF.x = pointF.x + iSpace;
                pointF.y = pointF.y + iSpace;
            }
        }
        mColors = colors;
    }

    public float testLoaction(int line_index,int index,PointF pointF){
        PointF point = mPoints.get(line_index)[index];
        PointF fixedPoint = new PointF();
        fixedPoint.x = point.x+drawRect.left;
        fixedPoint.y = point.y+drawRect.top;
        return (float)Utils.distance(fixedPoint,pointF);
    }


    public PointF getFixedPoint(int lineIndex,int index){
        PointF point = mPoints.get(lineIndex)[index];
        return new PointF(point.x+drawRect.left,point.y+drawRect.top);
    }

    @Override
    public void shader(Canvas canvas) {

        Bitmap tmpBitmap = Bitmap.createBitmap((int)drawRect.width(),(int)drawRect.height(), Bitmap.Config.ARGB_8888);

        Canvas chartCanvas = new Canvas(tmpBitmap);
        for (int i=0;i<mPoints.size();i++){
            PointF[] pointFs = mPoints.get(i);
            mPaint.setColor(mColors.get(i));
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);

            for (int k=0;k<pointFs.length;k++){
                if(k==0){
                    path.moveTo(pointFs[k].x,pointFs[k].y);
                }else {
                    path.lineTo(pointFs[k].x,pointFs[k].y);
                }
                chartCanvas.drawCircle(pointFs[k].x, pointFs[k].y,mRadius, mPaint);
            }

            chartCanvas.drawPath(path, mPaint);

            for (int k=0;k<pointFs.length;k++){
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(mFillColor);
                paint.setStyle(Paint.Style.FILL);
                chartCanvas.drawCircle(pointFs[k].x,pointFs[k].y,mRadius - mPaintWidth/2,paint);
            }
        }
        canvas.drawBitmap(tmpBitmap, drawRect.left, drawRect.top,null);
        tmpBitmap.recycle();

    }
}
