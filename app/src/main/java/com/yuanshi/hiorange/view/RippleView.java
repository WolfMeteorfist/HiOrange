package com.yuanshi.hiorange.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.yuanshi.hiorange.R;

/**
 * @author Zyc
 * @date 2018/3/6
 */

public class RippleView extends View {

    private Paint circlePaint;
    private Paint bitmapPaint;
    private int radius;
    private int width;
    private int height;
    private int startColor;
    private int endColor;
    private int expendRadius;
    private Bitmap mBitmap;

    private final int DEFAULT_EXPEND = 200;
    private final int ANIM_TIME = 1500;

    private boolean isRunning = false;

    private ObjectAnimator mEndColorAnim = ObjectAnimator.ofArgb(this, "endColor", 0xFF22DEF4, 0xEE22DEF4,0xEE22DEF4,0x0022DEF4);
    private ObjectAnimator mStartColorAnim = ObjectAnimator.ofArgb(this, "startColor", 0xFF22DEF4, 0XEE22DEF4,0XEE22DEF4,0XEE22DEF4,0x0022DEF4);
    private ObjectAnimator mRadiusAnim;


    public RippleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        Drawable drawable = ta.getDrawable(R.styleable.RippleView_centerDrawable);
        if (drawable == null) {
            drawable = getResources().getDrawable(R.mipmap.ic_launcher, null);
        }
        mBitmap = ((BitmapDrawable) drawable).getBitmap();
        expendRadius = ta.getInteger(R.styleable.RippleView_expendRadius, 0);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        mStartColorAnim.setDuration(ANIM_TIME);
        mStartColorAnim.setInterpolator(new FastOutSlowInInterpolator());
        mStartColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        mStartColorAnim.setRepeatMode(ValueAnimator.RESTART);
        mEndColorAnim.setDuration(ANIM_TIME);
        mEndColorAnim.setInterpolator(new FastOutSlowInInterpolator());
        mEndColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        mEndColorAnim.setRepeatMode(ValueAnimator.RESTART);
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
        postInvalidate();
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (expendRadius == 0) {
            expendRadius = DEFAULT_EXPEND;
        }
        radius = expendRadius + mBitmap.getWidth() / 2;

        width = radius * 2;
        height = radius * 2;

        mRadiusAnim = ObjectAnimator.ofInt(this, "radius", 1, radius);
        mRadiusAnim.setDuration(ANIM_TIME);
        mRadiusAnim.setInterpolator(new LinearInterpolator());
        mRadiusAnim.setRepeatCount(ValueAnimator.INFINITE);
        mRadiusAnim.setRepeatMode(ValueAnimator.RESTART);

        setMeasuredDimension(width, height);
    }

    public void startAnim() {
        mStartColorAnim.start();
        mEndColorAnim.start();
        mRadiusAnim.start();
        isRunning = true;
    }

    public void stopAnim() {
        if (mStartColorAnim != null && mStartColorAnim.isRunning()) {
            mStartColorAnim.end();
        }
        if (mEndColorAnim != null && mEndColorAnim.isRunning()) {
            mEndColorAnim.end();
        }
        if (mRadiusAnim != null && mRadiusAnim.isRunning()) {
            mRadiusAnim.end();
        }
        isRunning =false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isRunning) {
            startAnim();
        }
        circlePaint.setShader(new RadialGradient(width / 2, height / 2, radius, startColor, endColor, Shader.TileMode.CLAMP));

        canvas.save();
        Path path = new Path();
        path.addRect(0,0,width,height, Path.Direction.CW);
        path.addCircle(width / 2, height / 2, radius - expendRadius, Path.Direction.CW);
        path.setFillType(Path.FillType.EVEN_ODD);
        canvas.clipPath(path);
        canvas.drawCircle(width / 2, height / 2, radius, circlePaint);

        canvas.restore();
        canvas.drawBitmap(mBitmap, expendRadius, expendRadius, bitmapPaint);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isRunning) {
            stopAnim();
        }
    }
}
