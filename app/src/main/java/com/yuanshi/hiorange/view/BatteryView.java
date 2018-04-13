package com.yuanshi.hiorange.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.yuanshi.hiorange.R;

/**
 * Created by Administrator on 2018/2/7.
 */

public class BatteryView extends View {

    private String mTitle = getContext().getString(R.string.box_info_power);
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPercentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLightingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int GAP = 40;

    private int mCircleRadius;
    private int mWidth;
    private int mHeight;

    //初始值，改的话要一起改
    private String mPercent = getContext().getString(R.string.connect);
    private int mSweep = 360;

    private int DEFALUT_WIDTH = 250;
    private RectF mArcRectF;
    private Rect mTitleRect;
    private Rect mPercentRect;
    private float HDPI = 1.5f;
    private float XHDPI = 2.0f;
    private float XXHDPI = 3.0f;

    private int mPercentHeight;

    public BatteryView(Context context) {
        super(context);
        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        mCirclePaint.setColor(Color.parseColor(context.getString(R.string.circlePaintColor)));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(9);

        mLinePaint.setColor(Color.parseColor(context.getString(R.string.circleLinePaintColor)));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeWidth(18);

        mTitlePaint.setTextSize(30);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setColor(Color.parseColor(context.getString(R.string.titlePaintColor)));

        mPercentPaint.setTextSize(60);
        mPercentPaint.setTextAlign(Paint.Align.CENTER);
        mPercentPaint.setColor(Color.parseColor(context.getString(R.string.titlePaintColor)));

        mLightingPaint.setStrokeCap(Paint.Cap.ROUND);
        mLightingPaint.setStyle(Paint.Style.STROKE);
        mLightingPaint.setStrokeWidth(40);
        initDefault();

    }

    private void initDefault() {
        // 获取屏幕密度（方法3）
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        float density = displayMetrics.density;
        if (density == HDPI) {
            DEFALUT_WIDTH = 200;
            mCirclePaint.setStrokeWidth(5);
            mLinePaint.setStrokeWidth(12);
            mTitlePaint.setTextSize(20);
            mPercentPaint.setTextSize(40);
            mLightingPaint.setStrokeWidth(54);
        } else if (density == XXHDPI) {
            DEFALUT_WIDTH = 400;
            mCirclePaint.setStrokeWidth(10);
            mLinePaint.setStrokeWidth(18);
            mTitlePaint.setTextSize(40);
            mPercentPaint.setTextSize(70);
            mLightingPaint.setStrokeWidth(70);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = measureSpc(widthMeasureSpec);
        int height = measureSpc(heightMeasureSpec);

        mCircleRadius = Math.max(width, height) / 2;

        mWidth = Math.max(width, height) + GAP * 2;
        mHeight = mWidth;


        mArcRectF = new RectF(GAP, GAP, GAP + mCircleRadius * 2, GAP + mCircleRadius * 2);
        mTitleRect = new Rect();
        mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleRect);
        mPercentRect = new Rect();
        mPercentPaint.getTextBounds(mPercent, 0, mPercent.length(), mPercentRect);

        mPercentHeight = mPercentRect.height();

        //这个效果待定
        mLightingPaint.setShader(new RadialGradient(mWidth / 2, mHeight / 2, (mCircleRadius / 2) + 30, 0x331AE9EB, 0x001A202E, Shader.TileMode.MIRROR));


        setMeasuredDimension(mWidth, mHeight);
    }

    private int measureSpc(int measureSpec) {

        int result = DEFALUT_WIDTH;

        int mode = MeasureSpec.getMode(measureSpec);
        if (mode != MeasureSpec.AT_MOST) {
            result = MeasureSpec.getSize(measureSpec);
        }

        return result;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!"连接".equals(mPercent)) {
            canvas.drawText(mTitle, mWidth / 2, mHeight / 2 - mPercentHeight, mTitlePaint);
            canvas.drawArc(mArcRectF, 90, -mSweep, false, mLinePaint);
            canvas.drawArc(mArcRectF, 90, -mSweep, false, mLightingPaint);
        }
        canvas.drawCircle(mWidth / 2, mHeight / 2, mCircleRadius, mCirclePaint);
        canvas.drawText(mPercent, mWidth / 2, mHeight / 2 + mPercentHeight / 2, mPercentPaint);

    }

    public void setPercent(@Nullable String percent) {
        if (percent != null) {
            int intPercent = Integer.valueOf(percent);
            mSweep = intPercent * 360 / 100;

            mPercent = intPercent + "%";
        } else {
            mPercent = getContext().getString(R.string.Connect);
        }

        postInvalidate();
    }

}
