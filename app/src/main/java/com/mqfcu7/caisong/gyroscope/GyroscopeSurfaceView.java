package com.mqfcu7.caisong.gyroscope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

public class GyroscopeSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int SECTION_LINE_WIDTH = 3;
    private static final int MAIN_COLOR = 0xFF5B7CBE;

    private Rect mBoardRect;
    private Gyroscope mGyroscope = new Gyroscope();

    private Paint mOuterCirclePaint = new Paint();
    private Paint mInnerCirclePaint = new Paint();
    private Paint mSectionLinePaint = new Paint();
    private Paint mArrowSubPaint = new Paint();
    private Paint mArrowFlagPaint = new Paint();

    private SurfaceHolder mSurfaceHolder;

    private int mTriggerOrientation = 1;

    private VelocityTracker mVelocity;
    private Thread mRotateThread;
    private Gyroscope.Line mTriggerLine = new Gyroscope.Line();
    private boolean mIsTouchArrow;
    private float mTriggerValue = 1;

    public GyroscopeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        mOuterCirclePaint.setColor(Color.WHITE);
        mOuterCirclePaint.setAntiAlias(true);

        mInnerCirclePaint.setColor(MAIN_COLOR);
        mInnerCirclePaint.setAntiAlias(true);

        mSectionLinePaint.setColor(MAIN_COLOR);
        mSectionLinePaint.setAntiAlias(true);
        mSectionLinePaint.setStrokeWidth(SECTION_LINE_WIDTH);

        mArrowSubPaint.setColor(Color.WHITE);
        mArrowSubPaint.setAntiAlias(true);
        mArrowSubPaint.setShadowLayer(20, 4, 4, 0x50000000);

        mArrowFlagPaint.setColor(Color.RED);
        mArrowFlagPaint.setAntiAlias(true);
        mArrowFlagPaint.setShadowLayer(20, 4, 4, 0x50000000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, widthSize);

        mBoardRect = new Rect(getPaddingLeft(), getPaddingLeft(),
                widthSize - getPaddingRight(),
                widthSize - getPaddingRight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGyroscope.init(new PointF(mBoardRect.centerX(), mBoardRect.centerY()), mBoardRect.width() / 2, 5);

        RadialGradient gradient = new RadialGradient(10, 10, mBoardRect.width(),
                new int[]{0xFF8EAEE4, MAIN_COLOR}, null, Shader.TileMode.CLAMP);
        mInnerCirclePaint.setShader(gradient);

        mArrowSubPaint.setStrokeWidth(mGyroscope.getArrowLineWidth());
        mArrowFlagPaint.setStrokeWidth(mGyroscope.getArrowLineWidth());

        onDrawBoard();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    private float rotationInterplate(float x) {
        return (float) (Math.sin(x * Math.PI / 2)) * mTriggerValue;
        //return (float) ((Math.tanh(x * Math.PI / 2 * 5)) * 0.699 * mTriggerValue);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRotateThread != null && mRotateThread.isAlive()) {
            return false;
        }

        PointF point = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(point);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event, point);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onActionUpAndCancel(point);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onActionDown(PointF point) {
        mVelocity = VelocityTracker.obtain();
        mTriggerLine.s.set(point);
        mIsTouchArrow = mGyroscope.isTouchArrow(point);
    }

    private void onActionMove(MotionEvent event, PointF point) {
        mVelocity.addMovement(event);
        mTriggerLine.e.set(point);
        if (mIsTouchArrow) {
            float angle = mGyroscope.calcPointAngle(point);
            mGyroscope.setStartAngle(angle);
            onDrawBoard();
        } else {
            if (mGyroscope.lineIntersect(mTriggerLine)) {
                mVelocity.computeCurrentVelocity(1000);
                onRotate(new PointF((float)mVelocity.getXVelocity(), (float)mVelocity.getYVelocity()));
            }
        }
    }

    private void onActionUpAndCancel(PointF point) {
        if (mIsTouchArrow) {
            mVelocity.computeCurrentVelocity(1000);
            onRotate(new PointF((float) mVelocity.getXVelocity(), (float) mVelocity.getYVelocity()));
        }
        mIsTouchArrow = false;
        mVelocity.clear();
        mVelocity.recycle();
    }

    private void onRotate(PointF accelerate) {
        if (mRotateThread != null && mRotateThread.isAlive()) {
            return;
        }

        final float factor = 10000;
        accelerate.x /= factor;
        accelerate.y /= factor;

        mTriggerValue = mGyroscope.calcForceValue(accelerate);
        mTriggerOrientation = mGyroscope.calcForceOrientation(accelerate);

        if (Math.abs(mTriggerValue) < mGyroscope.EXP) {
            return;
        }

        mRotateThread = new Thread(this);
        mRotateThread.start();
    }

    @Override
    public void run() {
        int animationValue = mTriggerOrientation;
        while (true) {
            int angle = (int)(rotationInterplate(animationValue * 3 / 360.0f) * 3600);
            if (angle == 0 || !mGyroscope.updateArrowAngle(angle)) {
                break;
            }
            onDrawBoard();
            animationValue += mTriggerOrientation;
        }
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

    private void onDrawing(Canvas canvas) {
        canvas.drawColor(MAIN_COLOR);
        Gyroscope.Circle outCircle = mGyroscope.getOutCircle();
        Gyroscope.Circle innerCircle = mGyroscope.getInnerCircle();
        Gyroscope.Circle innermostCircle = mGyroscope.getInnermostCircle();
        Gyroscope.Line[] sectionsLine = mGyroscope.getSectionsLine();
        Gyroscope.Line arrowSubLine = mGyroscope.getArrowSubLine();
        Gyroscope.Line arrowFlagLine = mGyroscope.getArrowFlagLine();

        canvas.drawCircle(outCircle.c.x, outCircle.c.y, outCircle.r, mOuterCirclePaint);
        for(int i = 0; i < mGyroscope.getSectionsNum(); ++ i) {
            canvas.drawLine(sectionsLine[i].s.x, sectionsLine[i].s.y,
                    sectionsLine[i].e.x, sectionsLine[i].e.y, mSectionLinePaint);
        }

        canvas.drawCircle(innerCircle.c.x, innerCircle.c.y, innerCircle.r, mInnerCirclePaint);
        canvas.drawLine(arrowSubLine.s.x, arrowSubLine.s.y, arrowSubLine.e.x, arrowSubLine.e.y, mArrowSubPaint);
        canvas.drawLine(arrowFlagLine.s.x, arrowFlagLine.s.y, arrowFlagLine.e.x, arrowFlagLine.e.y, mArrowFlagPaint);
        canvas.drawCircle(innermostCircle.c.x, innermostCircle.c.y, innermostCircle.r, mOuterCirclePaint);
    }
}
