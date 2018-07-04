package com.mqfcu7.caisong.gyroscope;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.Random;

public class GyroscopeSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int SECTION_START_ANGLE = 270;
    private static final int ARROW_START_ANGLE = SECTION_START_ANGLE + 20;
    private static final int ARROW_LINE_WIDTH = 35;
    private static final int ARROW_FLAG_MARGIN = 35;
    private static final double INNER_RADIUS_RATIO = 1.0 / 4;
    private static final int SECTION_LINE_WIDTH = 3;
    private static final int MAIN_COLOR = 0xFF5B7CBE;

    private class Line {
        public PointF s = new PointF();
        public PointF e = new PointF();
    }

    private Rect mBoardRect;
    private int mSectionsNum = 5;
    private Line[] mSections = new Line[mSectionsNum];
    private Line mArrowLine = new Line();
    private Line mArrowFlagLine = new Line();
    private Random mRandom = new Random();

    private Paint mOuterCirclePaint = new Paint();
    private Paint mInnerCirclePaint = new Paint();
    private Paint mInnerCircleGradient = new Paint();
    private Paint mSectionLinePaint = new Paint();
    private Paint mArrowPaint = new Paint();
    private Paint mArrowFlagPaint = new Paint();

    private int mAnimationValue = 0;
    AccelerateDecelerateInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private SurfaceHolder mSurfaceHolder;
    private boolean mIsDrawing;

    public GyroscopeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        mOuterCirclePaint.setColor(0xFFFFFFFF);
        mOuterCirclePaint.setAntiAlias(true);

        mInnerCirclePaint.setColor(MAIN_COLOR);
        mInnerCirclePaint.setAntiAlias(true);
        //mInnerCirclePaint.setShadowLayer(5, 1, 1, 0xFF000000);

        mSectionLinePaint.setColor(MAIN_COLOR);
        mSectionLinePaint.setAntiAlias(true);
        mSectionLinePaint.setStrokeWidth(SECTION_LINE_WIDTH);
        mArrowPaint.setShadowLayer(3, 1, 1, 0xFF000000);

        mArrowPaint.setColor(0xFFFFFFFF);
        mArrowPaint.setAntiAlias(true);
        mArrowPaint.setStrokeWidth(ARROW_LINE_WIDTH);
        mArrowPaint.setShadowLayer(20, 4, 4, 0x50000000);

        //mArrowFlagPaint.setColor(0xFFDB504A);
        mArrowFlagPaint.setColor(0xFFFF0000);
        mArrowFlagPaint.setAntiAlias(true);
        mArrowFlagPaint.setStrokeWidth(ARROW_LINE_WIDTH);
        mArrowFlagPaint.setShadowLayer(20, 4, 4, 0x50000000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(wSpecSize, wSpecSize);

        mBoardRect = new Rect(getPaddingLeft(), getPaddingLeft(),
                wSpecSize - getPaddingRight(),
                wSpecSize - getPaddingRight());
    }

    private void calcSection() {
        PointF center = new PointF(mBoardRect.centerX(), mBoardRect.centerY());

        double step = 360.0 / mSectionsNum;
        for (int i = 0; i < mSectionsNum; ++ i) {
            mSections[i] = new Line();
            mSections[i].s.set(center);
            double radian = Math.toRadians(step * i + SECTION_START_ANGLE);
            mSections[i].e.set(
                    (float)(Math.cos(radian) * mBoardRect.width() / 2 + center.x),
                    (float)(Math.sin(radian) * mBoardRect.height() / 2 + center.y));
        }
    }

    private void calcArrow(int angle) {
        int width = mBoardRect.width();
        int height = mBoardRect.height();
        PointF center = new PointF(mBoardRect.centerX(), mBoardRect.centerY());

        float radian = (float) Math.toRadians(ARROW_START_ANGLE + angle);
        mArrowLine.s.set(center);
        mArrowLine.e.set(
                (float)(Math.cos(radian) * width * INNER_RADIUS_RATIO + center.x),
                (float)(Math.sin(radian) * height * INNER_RADIUS_RATIO + center.y));

        mArrowFlagLine.s.set(
                (float)(Math.cos(radian) * width * INNER_RADIUS_RATIO + center.x),
                (float)(Math.sin(radian) * height * INNER_RADIUS_RATIO + center.y));
        mArrowFlagLine.e.set(
                (float)(Math.cos(radian) * (width / 2  - ARROW_FLAG_MARGIN) + center.x),
                (float)(Math.sin(radian) * (height / 2 - ARROW_FLAG_MARGIN) + center.y));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        calcSection();
        calcArrow(0);

        RadialGradient gradient = new RadialGradient(10, 10, mBoardRect.width(),
                new int[]{0xFF8EAEE4, MAIN_COLOR}, null, Shader.TileMode.CLAMP);
        mInnerCirclePaint.setShader(gradient);

        onDrawBoard();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void run() {
        mAnimationValue = 0;
        Canvas canvas = null;
        while (mIsDrawing) {
            try {
                canvas = mSurfaceHolder.lockCanvas();
                calcArrow((int)(mInterpolator.getInterpolation(mAnimationValue / 3600.0f) * 3600));
                onDrawing(canvas);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            mAnimationValue += 20;
            if (mAnimationValue >= 3600) {
                mIsDrawing = false;
            }
        }
    }

    private void onDrawing(Canvas canvas) {
        canvas.drawColor(MAIN_COLOR);
        canvas.drawCircle(mBoardRect.centerX(), mBoardRect.centerY(), mBoardRect.width() / 2, mOuterCirclePaint);
        for(int i = 0; i < mSectionsNum; ++ i) {
            canvas.drawLine(mSections[i].s.x, mSections[i].s.y, mSections[i].e.x, mSections[i].e.y, mSectionLinePaint);
        }

        canvas.drawCircle(mBoardRect.centerX(), mBoardRect.centerY(), (int)(mBoardRect.width() * INNER_RADIUS_RATIO), mInnerCirclePaint);
        canvas.drawLine(mArrowFlagLine.s.x, mArrowFlagLine.s.y, mArrowFlagLine.e.x, mArrowFlagLine.e.y, mArrowFlagPaint);
        canvas.drawLine(mArrowLine.s.x, mArrowLine.s.y, mArrowLine.e.x, mArrowLine.e.y, mArrowPaint);
        canvas.drawCircle(mBoardRect.centerX(), mBoardRect.centerY(), mBoardRect.width() / 17, mOuterCirclePaint);
    }

    private void onDrawBoard() {
        Canvas canvas = null;
        try {
            canvas = mSurfaceHolder.lockCanvas();
            onDrawing(canvas);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void onRotate() {
        if (mIsDrawing) {
            return;
        }

        mIsDrawing = true;
        new Thread(this).start();
    }
}
