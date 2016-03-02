package com.fjwangjia.android.yochart;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by flb on 16/2/26.
 */
public class LineChart extends Chart {

    CoordinateSystemsShader coordinateSystemsShader;
    LineChartShader lineChartShader;

    GestureDetector gestureDetector;

    float fTotalChartLength = 0;
    float fScreenCurrentPosition = -1;
    float fScreenWidth = 0;
    int mScreenCountX_Axis = 5;
    int mScreenCountY_Axis = 6;
    int mTotalCountX = 0;
    String[] mContentX_Axis;
    String[] mContentY_Axis;

    List<Line> mLines = new ArrayList<>();

    static public class Line{

        List<Float> mRateY_Axis = new ArrayList<>();
        int mColor;
        public Line(int color){
            mColor = color;
        }

        public void add(float rate){
            mRateY_Axis.add(rate);
        }
        public void setRate(float[] rates){
            for (Float rate:rates){
                mRateY_Axis.add(rate);
            }
        }
        public void clear(){
            mRateY_Axis.clear();
        }

        public float getRateYAxis(int index){
            return mRateY_Axis.get(index);
        }

        public int getColor(){
            return mColor;
        }
    }

    public LineChart(Context context) {
        super(context);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public LineChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    void init(Context context){
        lineChartShader = new LineChartShader(Utils.dpTopxF(getContext(), 1.5f),Utils.dpTopxF(getContext(),3f));
        gestureDetector = new GestureDetector(context,simpleOnGestureListener);
        coordinateSystemsShader = new CoordinateSystemsShader(Utils.dpTopx(context,10),Utils.dpTopx(context,14),Utils.dpTopx(context,10));
    }

    public void createCoordinationSystem(int screenCountX_Axis,String[] contentX_Axis,int screenCountY_Axis,String[] contentY_Axis){
        mScreenCountX_Axis = screenCountX_Axis;
        mScreenCountY_Axis = screenCountY_Axis;
        mTotalCountX = contentX_Axis.length;
        mContentX_Axis = contentX_Axis;
        mContentY_Axis = contentY_Axis;
    }

    public void setLines(List<Line> lines){
        mLines = lines;
    }

    public void addLine(Line line){
        mLines.add(line);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        coordinateSystemsShader.layout(new RectF(0,0,mWidth,mHeight));
        lineChartShader.layout(coordinateSystemsShader.calculateChartCanvasRect());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lineChartShader == null)return;
        RectF screenChartRect = coordinateSystemsShader.calculateChartCanvasRect();
        float screenWidth = screenChartRect.width();
        float screenHeight = screenChartRect.height();
        fScreenWidth = screenWidth;

        float fUnitXLength = screenWidth/(mScreenCountX_Axis-1);

        fTotalChartLength = fUnitXLength*(mTotalCountX-1);
        if(fScreenCurrentPosition == -1){
            fScreenCurrentPosition = fTotalChartLength;
        }

        float fScreenCurrentEndPos = fScreenCurrentPosition;
        float fScreenCurrentStartPos = fScreenCurrentPosition - screenWidth;

        float startRate = fScreenCurrentStartPos / fUnitXLength;
        int startIndex = (int) Math.floor(fScreenCurrentStartPos / fUnitXLength);
        float offsetX = startIndex*fUnitXLength - fScreenCurrentStartPos;
        coordinateSystemsShader.setContents(mContentX_Axis,mContentY_Axis);
        coordinateSystemsShader.drawData(startIndex,mScreenCountX_Axis , offsetX);
        coordinateSystemsShader.shader(canvas);

        int endIndex = startIndex+mScreenCountX_Axis;
        if(endIndex<mTotalCountX){
            endIndex++;
        }


        //计算在当前需要显示的坐标
        List<PointF[]> pointFList = new ArrayList<>();
        List<Integer>  colorList = new ArrayList<>();
        for (Line line : mLines){
            PointF[] pointFs = new PointF[endIndex-startIndex];
            for (int i=startIndex;i<endIndex;i++){
                PointF pointF = new PointF();
                pointF.y = screenHeight*(1-line.getRateYAxis(i));
                pointF.x = (i-startIndex)*fUnitXLength + offsetX;
                pointFs[i-startIndex] = pointF;
            }
            pointFList.add(pointFs);
            colorList.add(line.getColor());
        }


        lineChartShader.setPoint(pointFList,colorList);
        lineChartShader.shader(canvas);
    }

    float lastX;
    float fScreenLastPosition = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                fScreenLastPosition = fScreenCurrentPosition;

                break;
            case MotionEvent.ACTION_MOVE:
                float dx = lastX - event.getX();
//                float sqrt = (float)Math.sqrt(Math.abs(dx));
//                dx = dx>0 ? sqrt : -sqrt;
                if(fScreenCurrentPosition + dx < fScreenWidth){
                    fScreenCurrentPosition = fScreenWidth;
                }else  if(fScreenCurrentPosition + dx > fTotalChartLength){
                    fScreenCurrentPosition = fTotalChartLength;
                }
                else {
                    fScreenCurrentPosition = fScreenCurrentPosition + dx;
                }
                if(fScreenLastPosition != fScreenCurrentPosition){
                    invalidate();
                    fScreenLastPosition = fScreenCurrentPosition;
                }
                lastX = event.getX();
                break;
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    Timer timer;
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        float runTime = 0;
        int frameTime = 20;
        float totalTime = 0;
        float a = 0.5f;
        float startPosition;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(timer != null){
                timer.cancel();
            }
            if(velocityX > 4000){
                velocityX = 4000;
            }else
            if(velocityX < -4000){
                velocityX = -4000;
            }
            final float v = -velocityX/10;
            runTime = 0;
            totalTime = Math.abs(v/a);
            startPosition = fScreenCurrentPosition;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(v>0){
                                a = -a;
                            }else {

                            }
                            if(runTime + frameTime > totalTime){
                                fScreenCurrentPosition = startPosition + Utils.decelerate(v,a,totalTime/1000);
                                timer.cancel();
                            }else {
                                runTime += frameTime;
                                fScreenCurrentPosition = startPosition + Utils.decelerate(v,a,runTime/1000);
                            }

                            if(fScreenCurrentPosition  < fScreenWidth){
                                fScreenCurrentPosition = fScreenWidth;
                                timer.cancel();
                            }else  if(fScreenCurrentPosition > fTotalChartLength){
                                fScreenCurrentPosition = fTotalChartLength;
                                timer.cancel();
                             }
                            invalidate();
                        }
                    });
                }
            },frameTime,frameTime);
            invalidate();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };
}
