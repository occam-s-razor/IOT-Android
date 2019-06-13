package com.edu.sicnu.cs.zzy.iot_android.CustomView;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Me-262-SM on 2019/6/10.
 * Email：zzylikegirls@163.com
 * Version：v1.0
 */
public class DashBoard extends View {
    Paint mPaint;//画笔
    RectF rectF;
    int SWEEPANGLE = 200; //弧形的角度
    int RADIUS = 300; //半径
    int INDICATOR = 200; //指针的长度
    int width ;
    int height ;

    public DashBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        drawArc(canvas);
        drawDegree(canvas);
        drawIndicator(canvas);
    }

    private void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);//画线模式
        mPaint.setStrokeWidth(10);//线宽度
        mPaint.setColor(Color.BLACK);
    }

    private void drawArc(Canvas canvas){
        rectF = new RectF(width / 2 - RADIUS, height / 2 - RADIUS,
                width / 2 + RADIUS, height / 2 + RADIUS);
        canvas.drawArc(rectF,90+(360-SWEEPANGLE)/2,SWEEPANGLE,false,mPaint);
    }

    private void drawDegree(Canvas canvas){
        canvas.translate(width/2,height/2);
        int temp = 90-((360-SWEEPANGLE)/2);
        canvas.rotate(90-((360-SWEEPANGLE)/2));
        for(int i=0;i<20;i++){
            //向上提高画笔，加负值，即-mPaint.getStrokeWidth()/2
            canvas.drawLine(RADIUS-20,-mPaint.getStrokeWidth()/2,RADIUS,-mPaint.getStrokeWidth()/2,mPaint);
            canvas.rotate(-SWEEPANGLE/20);
        }

        //最后一条线，因坐标系已经旋转，向上提高，加整值，即mPaint.getStrokeWidth()/2
        canvas.drawLine(RADIUS-20,mPaint.getStrokeWidth()/2,RADIUS,mPaint.getStrokeWidth()/2,mPaint);
        canvas.rotate(SWEEPANGLE-temp);//旋转回去的角度
        canvas.translate(-width/2,-height/2);
    }

    private void drawIndicator(Canvas canvas){
        int currentAngle = 0;
        canvas.translate(width/2,height/2);
        canvas.drawLine(0,0,
                (float) Math.cos(Math.toRadians(currentAngle))*INDICATOR,
                (float)Math.sin(Math.toRadians(currentAngle))*INDICATOR,
                mPaint);
        canvas.translate(getWidth()/2,getHeight()/2);
    }




















    private int px2dp(float px){
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int)(px/scale+0.5f);
    }
}
