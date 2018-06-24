package com.mqfcu7.caisong.gyroscope;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Random;

public class GyroscopeView extends View {

    private static final int SECTION_START_ANGLE = 270;
    private static final int ARROW_START_ANGLE = SECTION_START_ANGLE + 20;
    private static final int ARROW_LINE_WIDTH = 30;
    private static final int ARROW_FLAG_MARGIN = 20;
    private static final double INNER_RADIUS_RATIO = 1.0 / 4;

    private Rect mBoardRect;
    private PointF[][] mSection;
    private int mSectionNum = 5;
    private PointF[] mArrowLine;
    private PointF[] mArrowFlagLine;
    private int mArrowAnimationAngle;
    private Random mRandom = new Random();

    private Paint mOuterCirclePaint;
    private Paint mInnerCirclePaint;
    private Paint mSectionLinePaint;
    private Paint mArrowPaint;
    private Paint mArrowFlagPaint;

    private ValueAnimator mArrowAnimator;


    public GyroscopeView(Context context) {this(context, null);}

    public GyroscopeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mOuterCirclePaint = new Paint();
        mInnerCirclePaint = new Paint();
        mSectionLinePaint = new Paint();
        mArrowPaint = new Paint();
        mArrowFlagPaint = new Paint();

        //RadialGradient innerLg = new RadialGradient(0.2f, 0.2f, 0.1f, 0xFFFFFFFF, 0xFF202C50, Shader.TileMode.MIRROR);

        mOuterCirclePaint.setColor(0xFFFFFFFF);
        mOuterCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setColor(0xFF202C50);
        mInnerCirclePaint.setAntiAlias(true);
        //mInnerCirclePaint.setShader(innerLg);
        mSectionLinePaint.setColor(0xFF1D2744);
        mSectionLinePaint.setAntiAlias(true);
        mSectionLinePaint.setStrokeWidth(3);
        mArrowPaint.setColor(0xFFFFFFFF);
        mArrowPaint.setAntiAlias(true);
        mArrowPaint.setStrokeWidth(ARROW_LINE_WIDTH);
        mArrowFlagPaint.setColor(0xFFFF0000);
        mArrowFlagPaint.setAntiAlias(true);
        mArrowFlagPaint.setStrokeWidth(ARROW_LINE_WIDTH);

        mArrowAnimationAngle = 0;

        mArrowAnimator = new ValueAnimator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSize;
        int height = width;

        setMeasuredDimension(width, height);

        mBoardRect = new Rect(getPaddingLeft(), getPaddingLeft(),
                width - getPaddingRight(),
                width - getPaddingRight());
        Log.d("TAG", mBoardRect.toString());
    }

    private void calcArrow(int angle) {
        Point center = new Point(mBoardRect.centerX(), mBoardRect.centerY());
        mArrowLine[0] = new PointF(center.x, center.y);
        mArrowLine[1] = new PointF(
                (float) (Math.cos(Math.toRadians(ARROW_START_ANGLE + angle)) * mBoardRect.width() / 2 + center.x),
                (float) (Math.sin(Math.toRadians(ARROW_START_ANGLE + angle)) * mBoardRect.width() / 2 + center.y));

        mArrowFlagLine[0] = new PointF(
                (float) (Math.cos(Math.toRadians(ARROW_START_ANGLE + angle)) * mBoardRect.width() * INNER_RADIUS_RATIO + center.x),
                (float) (Math.sin(Math.toRadians(ARROW_START_ANGLE + angle)) * mBoardRect.width() * INNER_RADIUS_RATIO + center.y));
        mArrowFlagLine[1] = new PointF(
                (float) (Math.cos(Math.toRadians(ARROW_START_ANGLE + angle)) * (mBoardRect.width() / 2 - ARROW_FLAG_MARGIN) + center.x),
                (float) (Math.sin(Math.toRadians(ARROW_START_ANGLE + angle)) * (mBoardRect.width() / 2 - ARROW_FLAG_MARGIN) + center.y));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Point center = new Point(mBoardRect.centerX(), mBoardRect.centerY());
        mSection = new PointF[mSectionNum][2];

        double step = 360.0 / mSectionNum;
        for (int i = 0; i < mSectionNum; ++ i) {
            mSection[i][0] = new PointF(center.x, center.y);
            double angle = Math.toRadians(step * i + SECTION_START_ANGLE);
            mSection[i][1] = new PointF(
                    (float) (Math.cos(angle) * mBoardRect.width() / 2 + center.x),
                    (float) (Math.sin(angle) * mBoardRect.width() / 2 + center.y));
        }

        mArrowLine = new PointF[2];
        mArrowFlagLine = new PointF[2];
        calcArrow(0);
    }

    private void drawSection(Canvas canvas) {
        for (int i = 0; i < mSectionNum; ++ i) {
            canvas.drawLine(mSection[i][0].x, mSection[i][0].y,
                    mSection[i][1].x, mSection[i][1].y, mSectionLinePaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mBoardRect.centerX(), mBoardRect.centerY(), mBoardRect.width() / 2, mOuterCirclePaint);
        drawSection(canvas);
        canvas.drawCircle(mBoardRect.centerX(), mBoardRect.centerY(), mBoardRect.width() / 4, mInnerCirclePaint);
        canvas.drawLine(mArrowLine[0].x, mArrowLine[0].y, mArrowLine[1].x, mArrowLine[1].y, mArrowPaint);
        canvas.drawLine(mArrowFlagLine[0].x, mArrowFlagLine[0].y, mArrowFlagLine[1].x, mArrowFlagLine[1].y, mArrowFlagPaint);
        canvas.drawCircle(mBoardRect.centerX(), mBoardRect.centerY(), mBoardRect.width() / 20, mOuterCirclePaint);
    }

    public void onRotateArrow() {
        if (mArrowAnimator.isRunning()) {
            return;
        }

        mArrowAnimator.setDuration(10000);
        mArrowAnimator.setIntValues(0, 360 * 10 + mRandom.nextInt(360));
        mArrowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mArrowAnimationAngle = (int)animation.getAnimatedValue();
                calcArrow(mArrowAnimationAngle);
                invalidate();
            }
        });
        mArrowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mArrowAnimator.start();
    }
}
