package com.mqfcu7.jiangmeilan.gyroscope;

import android.graphics.Color;
import android.graphics.Paint;

public class PaintContainer {

    public static final int MAIN_COLOR = 0xFF5B7CBE;

    public Paint mOuterCirclePaint = new Paint();
    public Paint mInnerCirclePaint = new Paint();
    public Paint mSectionLinePaint = new Paint();
    public Paint mArrowSubPaint = new Paint();
    public Paint mArrowFlagPaint = new Paint();
    public Paint mSectionPaint = new Paint();

    public PaintContainer() {
        mOuterCirclePaint.setColor(Color.WHITE);
        mOuterCirclePaint.setAntiAlias(true);

        mInnerCirclePaint.setColor(MAIN_COLOR);
        mInnerCirclePaint.setAntiAlias(true);

        mSectionLinePaint.setColor(MAIN_COLOR);
        mSectionLinePaint.setAntiAlias(true);

        mArrowSubPaint.setColor(Color.WHITE);
        mArrowSubPaint.setAntiAlias(true);
        mArrowSubPaint.setShadowLayer(20, 4, 4, 0x50000000);

        mArrowFlagPaint.setColor(Color.RED);
        mArrowFlagPaint.setAntiAlias(true);
        mArrowFlagPaint.setShadowLayer(20, 4, 4, 0x50000000);

        mSectionPaint.setColor(0x15FF0000);
        mSectionPaint.setAntiAlias(true);
    }
}
