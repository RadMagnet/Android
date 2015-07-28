package com.radmagnet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Nikhil on 5/22/15.
 *
 * Used in rad category
 *
 */
public class RadLinearLayout extends LinearLayout{
    private RectF rect;
    private Paint paint;

    public RadLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadLinearLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#7EB5D6"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rect, 20, 20, paint);
    }

}
