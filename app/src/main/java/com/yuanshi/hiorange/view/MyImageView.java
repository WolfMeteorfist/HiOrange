package com.yuanshi.hiorange.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by ZYc on 2018/2/6.
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView {
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        path.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStrokeWidth(2);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor( Color.parseColor("#FFFFFF"));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2)-1, circlePaint);

    }
}
