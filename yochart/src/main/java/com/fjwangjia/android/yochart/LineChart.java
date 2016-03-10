package com.fjwangjia.android.yochart;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.Iterator;
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
    String[] mContentY;
    int mStartIndex = 0;
    List<Line> mLines = new ArrayList<>();

    long showToastTime = 0;

    List<Toast> toastList = new ArrayList<>();

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

        OnPointClickListener listener;

        public void setListener(OnPointClickListener listener) {
            this.listener = listener;
        }
    }

    public interface OnPointClickListener{
        /**
         *
         * @param which 哪条折线
         * @param index 第几个点
         */
        void onPointClick(int which,int index);
    }

    public LineChart(Context context) {
        super(context);
        init(context, null);
    }

    public void draw(){
        requestLayout();
        invalidate();
    }

    public void clear(){
        mLines.clear();
        fScreenCurrentPosition = -1;
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(21)
    public LineChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    void init(Context context,AttributeSet attrs){
        lineChartShader = new LineChartShader(Utils.dpTopxF(getContext(), 1.5f),Utils.dpTopxF(getContext(),3f));
        gestureDetector = new GestureDetector(context,simpleOnGestureListener);
        coordinateSystemsShader = new CoordinateSystemsShader(Utils.dpTopx(context,10),Utils.dpTopx(context,14),Utils.dpTopx(context,10));

        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.LineChart);
            int fillColor = typedArray.getColor(R.styleable.LineChart_fillColor, Color.WHITE);
            int lineColor = typedArray.getColor(R.styleable.LineChart_lineColor,Color.BLACK);
            int textColor = typedArray.getColor(R.styleable.LineChart_fontColor,Color.BLACK);
            lineChartShader.mFillColor =  fillColor;
            coordinateSystemsShader.mLineColor = lineColor;
            coordinateSystemsShader.setTextColor(textColor);
        }
    }

    public void createCoordinationSystem(int screenCountX_Axis,String[] contentX_Axis,int screenCountY_Axis,String[] contentY_Axis,String[] yContent){
        mScreenCountX_Axis = screenCountX_Axis;
        mScreenCountY_Axis = screenCountY_Axis;
        mTotalCountX = contentX_Axis.length;
        mContentX_Axis = contentX_Axis;
        mContentY_Axis = contentY_Axis;
        mContentY = yContent;

        if(mTotalCountX<screenCountX_Axis){
            mScreenCountX_Axis = mTotalCountX;
        }

        if(mScreenCountY_Axis>mContentY.length-1){
            mScreenCountY_Axis = mContentY.length-1;
        }
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
        if(lineChartShader == null || mLines.size()==0)return;
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
        mStartIndex = (int) Math.floor(fScreenCurrentStartPos / fUnitXLength);
        float offsetX = mStartIndex*fUnitXLength - fScreenCurrentStartPos;
        coordinateSystemsShader.setContents(mContentX_Axis,mContentY_Axis);
        coordinateSystemsShader.drawData(mStartIndex,mScreenCountX_Axis , offsetX);
        coordinateSystemsShader.shader(canvas);

        int endIndex = mStartIndex+mScreenCountX_Axis;
        if(endIndex<mTotalCountX){
            endIndex++;
        }


        //计算在当前需要显示的坐标
        List<PointF[]> pointFList = new ArrayList<>();
        List<Integer>  colorList = new ArrayList<>();
        for (Line line : mLines){
            PointF[] pointFs = new PointF[endIndex-mStartIndex];
            for (int i=mStartIndex;i<endIndex;i++){
                PointF pointF = new PointF();
                pointF.y = screenHeight*(1-line.getRateYAxis(i));
                pointF.x = (i-mStartIndex)*fUnitXLength + offsetX;
                pointFs[i-mStartIndex] = pointF;

            }
            pointFList.add(pointFs);
            colorList.add(line.getColor());
        }



        lineChartShader.setPoint(pointFList,colorList);
        lineChartShader.shader(canvas);

        for(Toast toast : toastList){
            if(toast.index-mStartIndex<0||toast.index-mStartIndex>=mScreenCountX_Axis)continue;
            PointF pointF = lineChartShader.getFixedPoint(toast.lineIndex,toast.index-mStartIndex);
            toast.draw(pointF,canvas);
        }
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(toastTimer != null){
            toastTimer.cancel();
        }
    }

    Timer timer;
    Timer toastTimer;
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        float runTime = 0;
        int frameTime = 15;
        float totalTime = 0;
        float a = 0.5f;
        float startPosition;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(timer != null){
                timer.cancel();
            }
            int threshold = 6000;
            if(velocityX > threshold){
                velocityX = threshold;
            }else
            if(velocityX < - threshold){
                velocityX = - threshold;
            }
            final float v = -velocityX/12;
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
                            if (v > 0) {
                                a = -a;
                            } else {

                            }
                            if (runTime + frameTime > totalTime) {
                                fScreenCurrentPosition = startPosition + Utils.decelerate(v, a, totalTime / 1000);
                                timer.cancel();
                            } else {
                                runTime += frameTime;
                                fScreenCurrentPosition = startPosition + Utils.decelerate(v, a, runTime / 1000);
                            }

                            if (fScreenCurrentPosition < fScreenWidth) {
                                fScreenCurrentPosition = fScreenWidth;
                                timer.cancel();
                            } else if (fScreenCurrentPosition > fTotalChartLength) {
                                fScreenCurrentPosition = fTotalChartLength;
                                timer.cancel();
                            }
                            invalidate();
                        }
                    });
                }
            }, frameTime, frameTime);
            return super.onFling(e1, e2, velocityX, velocityY);
        }



        //点击一下
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float iMinDistance = Utils.dpTopxF(getContext(),3f)*5;
            int lineIndex = -1;
            int index = -1;
            PointF pointF  = new PointF();
            for (int loop=0;loop<mLines.size();loop++){
                Line line = mLines.get(loop);
                if(line.listener != null){
                    for (int i=0;i<mScreenCountX_Axis ; i++){
                        PointF  returnPointF  = new PointF();
                        float distance = lineChartShader.testLoaction(loop,i,new PointF(e.getX(),e.getY()));
                        if(iMinDistance>=distance){
                            lineIndex = loop;
                            index = i+mStartIndex;
                            iMinDistance = distance;
                        }
                    }
                }
            }

            if(index!=-1 && lineIndex!=-1){
                mLines.get(lineIndex).listener.onPointClick(lineIndex,index);
                RectF rectF = coordinateSystemsShader.calculateChartCanvasRect();
                String content = mContentX_Axis[index]+":"+mContentY[index];
                Toast toast = new Toast(content,rectF,Utils.dpTopx(getContext(),13),Color.WHITE,0xFFEBD33A);
                toast.lineIndex = lineIndex;
                toast.index = index;

                toastList.add(toast);

                if(toastTimer == null){
                    toastTimer = new Timer();
                    toastTimer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            if (toastList.size() == 0){
                                return;
                            }

                            Iterator<Toast> iterator = toastList.iterator();
                            while (iterator.hasNext()){
                                Toast t = iterator.next();
                                if(System.currentTimeMillis() - t.startShowTime > 1000){
                                    iterator.remove();
                                }
                            }

                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    invalidate();
                                }
                            });
                        }
                    },50,50);
                }
            }
            return super.onSingleTapUp(e);
        }
    };
}
