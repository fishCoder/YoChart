package com.fjwangjia.android.chart;

import android.graphics.Color;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fjwangjia.android.yochart.LineChart;
import com.fjwangjia.android.yochart.Toast;

public class MainActivity extends AppCompatActivity  {

    LineChart lineChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineChart = (LineChart) findViewById(R.id.line_chart);

        lineChart.postDelayed(new Runnable() {
            @Override
            public void run() {
                lineChart.createCoordinationSystem(5, x, 5, y,x);
                LineChart.Line line1 = new LineChart.Line(Color.BLACK);
                line1.setRate(point);
                LineChart.Line line2 = new LineChart.Line(Color.RED);
                line2.setRate(point1);
                LineChart.Line line3 = new LineChart.Line(Color.GREEN);
                line3.setRate(point2);
                line3.setListener(new LineChart.OnPointClickListener() {
                    @Override
                    public String onPointClick(int which, int index) {
                        Log.d("onPointClick",String.format("which:%d,index:%d",which,index));
                        return "";
                    }
                });
                lineChart.addLine(line1);
                lineChart.addLine(line2);
                lineChart.addLine(line3);
                lineChart.draw();
            }
        },1000);

    }

    String[] x = new String[]{"001","002","003","004","005","006","007","008","009","010","011","012","013","014","015","8888年上半年"};
    String[] y = new String[]{"0","20","40","60","80","100"};
    float[] point = new float[]{0.10f,0.20f,0.30f,0.40f,0.50f,0.60f,0.70f,0.80f,0.60f,0.50f,0.45f,0.70f,0.8f,0.9f,0.8f,0.8f};
    float[] point1 = new float[]{0.6f,0.4f,0.1f,0f,0.3f,0.2f,0.6f,0.7f,0.8f,0.55f,0.6f,0.7f,0.7f,0.7f,0.6f,0.6f};
    float[] point2 = new float[]{0.15f,0.25f,0.4f,0.5f,0.8f,0.6f,0.5f,0.4f,0.3f,0.5f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f};


}
