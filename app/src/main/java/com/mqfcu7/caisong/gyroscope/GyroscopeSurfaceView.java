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
import android.view.VelocityTracker;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.Random;

public class GyroscopeSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final float EXP = 0.000001f;
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

    private int mArrowLineWidth;
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

    private int mCurrentRotationAngle = ARROW_START_ANGLE;
    private int mStartRotationAngle = ARROW_START_ANGLE;
    private int mRotationOriention = 1;

    VelocityTracker mVelocity;
    private Line mTriggerLine = new Line();
    private boolean mIsTouchLine;
    private float mTriggerValue = 1;
    private boolean mTriggerRotate;

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

        mSectionLinePaint.setColor(MAIN_COLOR);
        mSectionLinePaint.setAntiAlias(true);
        mSectionLinePaint.setStrokeWidth(SECTION_LINE_WIDTH);
        mArrowPaint.setShadowLayer(3, 1, 1, 0xFF000000);

        mArrowPaint.setColor(0xFFFFFFFF);
        mArrowPaint.setAntiAlias(true);
        mArrowPaint.setStrokeWidth(ARROW_LINE_WIDTH);
        mArrowPaint.setShadowLayer(20, 4, 4, 0x50000000);

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

        float radian = (float) Math.toRadians(mStartRotationAngle + angle);
        mCurrentRotationAngle = mStartRotationAngle + angle;
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

        mArrowLineWidth = mBoardRect.width() / 30;
        mArrowPaint.setStrokeWidth(mArrowLineWidth);
        mArrowFlagPaint.setStrokeWidth(mArrowLineWidth);

        onDrawBoard();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    private float rotationInterplate(float x) {
        return (float) (Math.sin(x * Math.PI / 2)) * mTriggerValue;
        //return (float) ((Math.tanh(x * Math.PI / 2 * 5)) * 0.699 * mTriggerValue);
    }

    private float calcPointDistancePower(PointF p1, PointF p2) {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
    }

    private float calcPointToLineDistance(PointF point, Line line) {
        float sDist = (float) Math.sqrt(calcPointDistancePower(point, line.s));
        float eDist = (float) Math.sqrt(calcPointDistancePower(point, line.e));
        float length = (float) Math.sqrt(calcPointDistancePower(line.s, line.e));
        if (sDist < EXP || eDist < EXP) {
            return 0f;
        }

        // 点在直线上
        if (sDist * sDist >= length * length + eDist * eDist) {
            return eDist;
        }
        if (eDist * eDist >= length * length + sDist * sDist) {
            return sDist;
        }

        float p = (sDist + eDist + length) / 2;
        float s = (float) Math.sqrt(p * (p - sDist) * (p - eDist) * (p - length));  // 海伦公式求面积
        return 2 * s / length;  // 三角形面积公式求高
    }

    private boolean isInCircle(PointF center, float radius, PointF point) {
        return calcPointDistancePower(center, point) < radius * radius;
    }

    private boolean isTouchArrow(PointF point) {
        return calcPointToLineDistance(point, mArrowFlagLine) < mArrowLineWidth;
    }

    boolean onSegment(PointF p, PointF q, PointF r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;
        return false;
    }

    private int orientation(PointF p, PointF q, PointF r) {
        float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;
    }

    private boolean lineIntersect(Line l1, Line l2) {
        int o1 = orientation(l1.s, l1.e, l2.s);
        int o2 = orientation(l1.s, l1.e, l2.e);
        int o3 = orientation(l2.s, l2.e, l1.s);
        int o4 = orientation(l2.s, l2.e, l1.e);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        if (o1 == 0 && onSegment(l1.s, l2.s, l1.e)) return true;
        if (o2 == 0 && onSegment(l1.s, l2.e, l1.e)) return true;
        if (o3 == 0 && onSegment(l2.s, l1.s, l2.e)) return true;
        if (o4 == 0 && onSegment(l2.s, l1.e, l2.e)) return true;

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTriggerRotate = false;
                mVelocity = VelocityTracker.obtain();
                mTriggerLine.s.set(point);
                mIsTouchLine = isTouchArrow(point);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocity.addMovement(event);
                mTriggerLine.e.set(point);
                if (!mIsTouchLine && !mTriggerRotate && lineIntersect(mArrowFlagLine, mTriggerLine)) {
                    mVelocity.computeCurrentVelocity(1000);
                        onRotate(mTriggerLine,
                                new PointF((float)mVelocity.getXVelocity(), (float)mVelocity.getYVelocity()));
                        mTriggerRotate = true;
                }
                if (!mTriggerRotate && mIsTouchLine) {
                    int angle = calcPointAngle(new PointF(mBoardRect.centerX(), mBoardRect.centerY()), point);
                    mStartRotationAngle = angle;
                    Canvas canvas = null;
                    try {
                        canvas = mSurfaceHolder.lockCanvas();
                        calcArrow(0);
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
                    mCurrentRotationAngle = angle;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsTouchLine) {
                    mVelocity.computeCurrentVelocity(1000);
                    mTriggerLine.e.set(point);
                    onRotate(mTriggerLine,
                            new PointF((float)mVelocity.getXVelocity(), (float)mVelocity.getYVelocity()));
                    mTriggerRotate = true;
                }
                mIsTouchLine = false;
                mVelocity.clear();
                mVelocity.recycle();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void run() {
        mAnimationValue = mRotationOriention;
        Canvas canvas = null;
        while (mIsDrawing) {
            try {
                canvas = mSurfaceHolder.lockCanvas();
                int angle = (int)(rotationInterplate(mAnimationValue * 3 / 360.0f) * 3600);
                if (mRotationOriention > 0) {
                    if (angle + mStartRotationAngle <= mCurrentRotationAngle) {
                        mIsDrawing = false;
                    }
                } else {
                    if (angle + mStartRotationAngle >= mCurrentRotationAngle) {
                        mIsDrawing = false;
                    }
                }
                calcArrow(angle);
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
            mAnimationValue += mRotationOriention;
        }
        mStartRotationAngle = mCurrentRotationAngle;
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

    private int calcPointAngle(PointF center, PointF point) {
        if (center.x == point.x) {
            if (point.y > center.y) {
                return 90;
            } else {
                return 180;
            }
        }

        int angle = (int)Math.toDegrees(Math.atan((double) (point.y - center.y) / (point.x - center.x)));
        float dx = point.x - center.x;
        float dy = point.y - center.y;
        Log.d("TAG", String.valueOf(angle));
        if (dx > 0 && dy > 0) {
            return angle;
        } else if (dx > 0 && dy < 0) {
            return 360 + angle;
        } else if (dx < 0 && dy < 0) {
            return 180 + angle;
        } else {
            return 180 + angle;
        }
    }

    private float getVectorDot(PointF v1, PointF v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    private float getVectorNorm(PointF v) {
        return (float)Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public void onRotate(Line triggerLine, PointF accelerate) {
        final float factor = 10000;
        accelerate.x /= factor;
        accelerate.y /= factor;

        PointF lineVector = new PointF(mArrowFlagLine.e.x - mArrowFlagLine.s.x,
                mArrowFlagLine.e.y - mArrowFlagLine.s.y);
        float cosTheta = getVectorDot(lineVector, accelerate) / (getVectorNorm(lineVector) * getVectorNorm(accelerate));
        float sinTheta = (float)Math.sqrt(1 - cosTheta * cosTheta);
        float trigger = getVectorNorm(accelerate) * sinTheta;
        mTriggerValue = trigger;
        if (lineVector.x * accelerate.y - lineVector.y * accelerate.x > 0) {
            mRotationOriention = 1;
        } else {
            mRotationOriention = -1;
        }

        if (mIsDrawing) {
            return;
        }

        mIsDrawing = true;
        new Thread(this).start();
    }
}
