package com.mqfcu7.jiangmeilan.gyroscope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SpliterLineView extends View {

    public SpliterLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(0xAA000000);
        paint.setAntiAlias(true);
        //paint.setShadowLayer(3, 1, 1, 0xFF000000);
        canvas.drawLine(0, 0, getWidth(), 1, paint);
    }
}
