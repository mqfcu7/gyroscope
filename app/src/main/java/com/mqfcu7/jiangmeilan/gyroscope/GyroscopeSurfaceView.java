package com.mqfcu7.jiangmeilan.gyroscope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

public class GyroscopeSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int SECTION_LINE_WIDTH = 3;
    private static final int MAIN_COLOR = 0xFF5B7CBE;

    private Rect mBoardRect;
    private Gyroscope mGyroscope = new Gyroscope();
    private Database mDatabase;
    private Database.GyroscopeData mGyroscopeData;

    private PaintContainer mPaints = new PaintContainer();
    private SurfaceHolder mSurfaceHolder;

    private int mTriggerOrientation = 1;

    private VelocityTracker mVelocity;
    private Thread mRotateThread;
    private Gyroscope.Line mTriggerLine = new Gyroscope.Line();
    private boolean mIsTouchArrow;
    private float mTriggerValue = 1;
    private boolean mArrowEnable = true;
    private boolean mRecordEnable = true;

    private GameFragment mGameFragment;
    private ControlPadFragment mControlPad;

    public GyroscopeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        mDatabase = new Database(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size = widthSize;
        if (heightMode == MeasureSpec.EXACTLY) {
            size = heightSize;
        }

        setMeasuredDimension(size, size);

        mBoardRect = new Rect(getPaddingLeft(), getPaddingLeft(),
                size - getPaddingRight(),
                size - getPaddingRight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mGyroscopeData == null) {
            mGyroscopeData = mDatabase.getSettingData();
        }
        mGyroscope.init(new PointF(mBoardRect.centerX(), mBoardRect.centerY()), mBoardRect.width() / 2,
                mGyroscopeData.sectionsNum, mGyroscopeData.sectionsAngle, mGyroscopeData.arrowAngle);
        if (mGyroscopeData.selectedSection != Gyroscope.INVALID_SELECTED_SECTION) {
            mGyroscope.setSelectedSection(mGyroscopeData.selectedSection);
        }

        RadialGradient gradient = new RadialGradient(10, 10, mBoardRect.width(),
                new int[]{0xFF8EAEE4, MAIN_COLOR}, null, Shader.TileMode.CLAMP);
        mPaints.mInnerCirclePaint.setShader(gradient);

        mPaints.mSectionLinePaint.setStrokeWidth(SECTION_LINE_WIDTH);
        mPaints.mArrowSubPaint.setStrokeWidth(mGyroscope.getArrowLineWidth());
        mPaints.mArrowFlagPaint.setStrokeWidth(mGyroscope.getArrowLineWidth());
        onDrawBoard();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void setControlPad(ControlPadFragment controlPad) {
        mControlPad = controlPad;
    }

    public void setGyroscopeData(Database.GyroscopeData data) {
        mGyroscopeData = data;
    }

    public void setArrowEnable(boolean enable) {
        mArrowEnable = enable;
    }

    public void setRecordEnable(boolean enable) {
        mRecordEnable = enable;
    }

    public int getSectionsNum() {
        return mGyroscope.getSectionsNum();
    }

    public void setSectionsNum(int num) {
        if (num == mGyroscope.getSectionsNum()) {
            return;
        }

        mGyroscope.setSectionsNum(num);
        mDatabase.updateSettingData(num, mGyroscope.getSectionsAngle(), Integer.MAX_VALUE);

        onDrawBoard();
    }

    public void setGameFragment(GameFragment gameFragment) {
        mGameFragment = gameFragment;
    }

    public boolean isRotating() {
        return mRotateThread != null && mRotateThread.isAlive();
    }

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
            if (mRecordEnable) {
                mDatabase.updateSettingData(Integer.MAX_VALUE, null, mGyroscope.getArrowCurrentAngle());
            } else {
                mDatabase.updateGameData(mGyroscope.getArrowCurrentAngle(), Integer.MAX_VALUE);
            }
            mVelocity.computeCurrentVelocity(1000);
            onRotate(new PointF((float) mVelocity.getXVelocity(), (float) mVelocity.getYVelocity()));
        }
        mIsTouchArrow = false;
        mVelocity.clear();
        mVelocity.recycle();
    }

    public void onRotate(PointF accelerate) {
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

    public void onRotate(float force) {
        mTriggerValue = force;
        mTriggerOrientation = 1;
        if (Math.abs(mTriggerValue) < mGyroscope.EXP) {
            return;
        }

        if (mRotateThread != null && mRotateThread.isAlive()) {
            return;
        }
        mRotateThread = new Thread(this);
        mRotateThread.start();
    }

    @Override
    public void run() {
        mControlPad.setEnable(false);
        int animationValue = mTriggerOrientation;
        boolean running = true;
        while (running) {
            int angle = (int)(rotationInterplate(animationValue * 3 / 360.0f) * 3600);
            if (angle == 0 || !mGyroscope.updateArrowAngle(angle)) {
                running = false;
            }
            onDrawBoard();
            animationValue += mTriggerOrientation;
        }

        if (mRecordEnable) {
            mDatabase.updateSettingData(Integer.MAX_VALUE, null, mGyroscope.getArrowCurrentAngle());

            Database.GyroscopeData data = new Database.GyroscopeData();
            data.sectionsNum = mGyroscope.getSectionsNum();
            data.sectionsAngle = mGyroscope.getSectionsAngle();
            data.selectedSection = mGyroscope.getSelectedSection();
            data.arrowAngle = (int) mGyroscope.getArrowCurrentAngle();
            data.time = System.currentTimeMillis();
            data.location = "";
            mDatabase.saveGyroscope(data);
        } else {
            Database.GameData gameData = mDatabase.getGameData();
            float[] sectionsAngle = mGyroscope.getSectionsAngle();
            int cash = Math.min(gameData.score, 100);
            if (mGyroscope.getSelectedSection() != Gyroscope.INVALID_SELECTED_SECTION) {
                int factor = (int)(60.f / sectionsAngle[mGyroscope.getSelectedSection()]);
                factor = factor > 2 ? factor : 0;
                gameData.score = gameData.score - cash + (cash * factor);
                if (gameData.score == 0) gameData.score = 1000;
            } else {
                gameData.score = Integer.MAX_VALUE;
            }
            mDatabase.updateGameData(mGyroscope.getArrowCurrentAngle(), gameData.score);
            if (mGameFragment != null) {
                Message msg = new Message();
                msg.what = GameFragment.UPDATE_TEXT;
                msg.arg1 = gameData.score;
                msg.arg2 = Math.min(gameData.score, 100);
                mGameFragment.handler.sendMessage(msg);
            }
        }
        mControlPad.setEnable(true);
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
        int selectedSection = mGyroscope.getSelectedSection();
        float[] sectionsAngle = mGyroscope.getSectionsAngle();

        canvas.drawCircle(outCircle.c.x, outCircle.c.y, outCircle.r, mPaints.mOuterCirclePaint);
        for (int i = 0; i < mGyroscope.getSectionsNum(); ++ i) {
            canvas.drawLine(sectionsLine[i].s.x, sectionsLine[i].s.y,
                    sectionsLine[i].e.x, sectionsLine[i].e.y, mPaints.mSectionLinePaint);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (selectedSection != Gyroscope.INVALID_SELECTED_SECTION) {
                float start = 90 - sectionsAngle[0] / 2;
                for (int i = 1; i <= selectedSection; ++i) {
                    start = (start + sectionsAngle[i - 1]) % 360;
                }
                canvas.drawArc(mBoardRect.left, mBoardRect.top, mBoardRect.right, mBoardRect.bottom,
                        start, sectionsAngle[selectedSection], true, mPaints.mSectionPaint);
            }
        }
        drawSectionsName(canvas);
        canvas.drawCircle(innerCircle.c.x, innerCircle.c.y, innerCircle.r, mPaints.mInnerCirclePaint);
        if (mArrowEnable) {
            canvas.drawLine(arrowSubLine.s.x, arrowSubLine.s.y, arrowSubLine.e.x, arrowSubLine.e.y, mPaints.mArrowSubPaint);
            canvas.drawLine(arrowFlagLine.s.x, arrowFlagLine.s.y, arrowFlagLine.e.x, arrowFlagLine.e.y, mPaints.mArrowFlagPaint);
            canvas.drawCircle(innermostCircle.c.x, innermostCircle.c.y, innermostCircle.r, mPaints.mOuterCirclePaint);
        }

    }

    private void drawSectionsName(Canvas canvas) {
        if (mGyroscopeData.sectionsName == null || mGyroscopeData.sectionsName.length != mGyroscopeData.sectionsNum) {
            return;
        }

        final float diff = 2;
        float density = getContext().getResources().getDisplayMetrics().density;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize((int) (12*density+0.5));
        paint.setStrokeWidth(SECTION_LINE_WIDTH);
        paint.setShadowLayer(5, 2, 2, 0x50000000);
        float startAngle = 90 + diff;
        float total = 0;
        for (int i = 0; i < mGyroscopeData.sectionsNum; ++ i) {
            canvas.rotate(startAngle, mBoardRect.centerX(), mBoardRect.centerY());
            total += startAngle;
            if (mGyroscopeData.sectionsName[i].compareTo("0") == 0) {
                paint.setColor(Color.BLACK);
            } else {
                paint.setColor(Color.BLUE);
            }
            canvas.drawText(mGyroscopeData.sectionsName[i], mBoardRect.centerX() + mBoardRect.width() / 4 + 40, mBoardRect.centerY(), paint);
            if (i < mGyroscopeData.sectionsNum - 1) {
                startAngle = mGyroscopeData.sectionsAngle[i] / 2 + mGyroscopeData.sectionsAngle[i+1] / 2;
            }
        }
        canvas.rotate(-total, mBoardRect.centerX(), mBoardRect.centerY());
    }
}
