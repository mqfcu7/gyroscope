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

import java.util.ArrayList;
import java.util.Random;

public class GyroscopeView extends View {

    private static final int SECTION_LINE_WIDTH = 3;

    Gyroscope mGyroscope = new Gyroscope();
    Database.GyroscopeData mGyroscopeData;
    PaintContainer mPaints = new PaintContainer();
    private Rect mBoardRect;
    private ArrayList<Integer> mColors = new ArrayList<>();
    private Random mRandom = new Random();
    private int mColorIndex;

    public GyroscopeView(Context context) {this(context, null);}

    public GyroscopeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mColors.add(0xAACAE7B9);
        mColors.add(0xAAF3DE8A);
        mColors.add(0xAA5D5179);
        mColors.add(0xAAA2FAA3);
        mColors.add(0xAADE6449);
        mColors.add(0xAAE3B505);
        mColorIndex = mRandom.nextInt(mColors.size());
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

    public void setGyroscopeData(Database.GyroscopeData data) {
        mGyroscopeData = data;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mGyroscope.init(new PointF(mBoardRect.centerX(), mBoardRect.centerY()), mBoardRect.width() / 2,
                mGyroscopeData.sectionsNum, mGyroscopeData.sectionsAngle, mGyroscopeData.arrowAngle);
        mGyroscope.setSelectedSection(mGyroscopeData.selectedSection);

        RadialGradient gradient = new RadialGradient(10, 10, mBoardRect.width(),
                new int[]{0xFF8EAEE4, PaintContainer.MAIN_COLOR}, null, Shader.TileMode.CLAMP);
        mPaints.mInnerCirclePaint.setShader(gradient);

        mPaints.mSectionLinePaint.setStrokeWidth(SECTION_LINE_WIDTH);
        mPaints.mArrowSubPaint.setStrokeWidth(mGyroscope.getArrowLineWidth());
        mPaints.mArrowFlagPaint.setStrokeWidth(mGyroscope.getArrowLineWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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

        if (selectedSection != Gyroscope.INVALID_SELECTED_SECTION) {
            float start = 90 - sectionsAngle[0] / 2;
            for (int i = 1; i <= selectedSection; ++ i) {
                start = (start + sectionsAngle[i-1]) % 360;
            }
            mPaints.mSectionPaint.setColor(mColors.get(mColorIndex));
            canvas.drawArc(mBoardRect.left, mBoardRect.top, mBoardRect.right, mBoardRect.bottom,
                    start, sectionsAngle[selectedSection], true, mPaints.mSectionPaint);
        }
        canvas.drawCircle(innerCircle.c.x, innerCircle.c.y, innerCircle.r, mPaints.mInnerCirclePaint);
        /*
        canvas.drawLine(arrowSubLine.s.x, arrowSubLine.s.y, arrowSubLine.e.x, arrowSubLine.e.y, mPaints.mArrowSubPaint);
        canvas.drawLine(arrowFlagLine.s.x, arrowFlagLine.s.y, arrowFlagLine.e.x, arrowFlagLine.e.y, mPaints.mArrowFlagPaint);
        canvas.drawCircle(innermostCircle.c.x, innermostCircle.c.y, innermostCircle.r, mPaints.mOuterCirclePaint);
        */
    }
}
