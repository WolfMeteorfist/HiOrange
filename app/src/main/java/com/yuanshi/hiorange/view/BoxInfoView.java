package com.yuanshi.hiorange.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.yuanshi.hiorange.R;

/**
 * @author ZYc
 * @date 2018/2/6
 */

public class BoxInfoView extends View {

    private Bitmap mBitmap;
    private String value = "--";
    private final String GETHEIGHT = "1";
    private String UNIT = "";
    private String DESCRIBE = "";
    private Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint descriptionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int width;
    private int height;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mDesWidth;
    private int mDesHeight;
    private int mValWidth;
    private int mValHeight;
    private int mUnitHeight;
    private int mMinWidth;
    private int mUnitWidth;
    private Bitmap mTemBitmap;


    private float ICON_SCALE = 1.0f;

    private final int BOTTOM_GAP = 10;
    private final int DES_MARGINTOP_GAP = 10;
    private final int ICON_MARGINTOP_GAP = 20;
    private final int VALUE_MARGINTOP_GAP = 5;
    private final int UNIT_MARGINTOP_GAP = 3;
    private RectF mRectF;

    private ColorMatrixColorFilter mColorfulFilter;
    private ColorMatrix mMatrix;
    private ColorMatrixColorFilter mColorlessFilter;

    private static float HDPI = 1.5f;
    private static float XHDPI = 2.0f;
    private static float XXHDPI = 3.0f;
    private float mDensity;

    public BoxInfoView(Context context) {
        super(context);
    }

    public BoxInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BoxInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public BoxInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        //为了value!
        valuePaint.setColor(Color.parseColor(context.getString(R.string.valuePaintColor)));
        valuePaint.setTextSize(60);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        //为了unit单位
        unitPaint.setColor(Color.parseColor(context.getString(R.string.unitPaintColor)));
        unitPaint.setTextSize(15);
        unitPaint.setTextAlign(Paint.Align.CENTER);

        //为了描述description
        descriptionPaint.setColor(Color.parseColor(context.getString(R.string.desPaintColor)));
        descriptionPaint.setTextSize(20);
        descriptionPaint.setTextAlign(Paint.Align.CENTER);

        //获取xml中的数据
        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.BoxInfoView);
        UNIT = ta.getString(R.styleable.BoxInfoView_unit);
        DESCRIBE = ta.getString(R.styleable.BoxInfoView_description);
        Drawable drawable = ta.getDrawable(R.styleable.BoxInfoView_drawable);
        mBitmap = drawable != null ? ((BitmapDrawable) drawable).getBitmap() : null;
        ta.recycle();

        mMatrix = new ColorMatrix();
        mMatrix.setSaturation(1);
        mColorfulFilter = new ColorMatrixColorFilter(mMatrix);
        mMatrix.setSaturation(0);
        mColorlessFilter = new ColorMatrixColorFilter(mMatrix);
        initDefault();

    }


    private void initDefault() {
        // 获取屏幕密度（方法3）
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        mDensity = displayMetrics.density;
        if (mDensity == HDPI) {
            valuePaint.setTextSize(30);
            unitPaint.setTextSize(14);
            descriptionPaint.setTextSize(11);
        } else if (mDensity == XHDPI) {
            valuePaint.setTextSize(50);
            unitPaint.setTextSize(30);
            descriptionPaint.setTextSize(20);
        }else if (mDensity == XXHDPI) {
            valuePaint.setTextSize(60);
            unitPaint.setTextSize(40);
            descriptionPaint.setTextSize(28);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Rect rectDescription = new Rect();
        Rect rectValue = new Rect();
        Rect rectUnit = new Rect();
        descriptionPaint.getTextBounds("开合状态", 0, "开合状态".length(), rectDescription);
        valuePaint.getTextBounds("开启", 0, "开启".length(), rectValue);
        unitPaint.getTextBounds("kg", 0, "kg".length(), rectUnit);

        mDesWidth = rectDescription.width();
        mDesHeight = rectDescription.height();

        mValWidth = rectValue.width();
        mValHeight = rectValue.height();

        mUnitWidth = rectUnit.width();
        mUnitHeight = rectUnit.height();

        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        mMinWidth = Math.max(mBitmapWidth, mDesWidth);
        mMinWidth = Math.max(mMinWidth, mValWidth);

        //补了个间隙值15
        int minHeight = mBitmapHeight + mDesHeight + mValHeight + mUnitHeight + 15;

        int totalGap = BOTTOM_GAP + DES_MARGINTOP_GAP + ICON_MARGINTOP_GAP + VALUE_MARGINTOP_GAP + UNIT_MARGINTOP_GAP;
        width = measureResult(widthMeasureSpec, mMinWidth);
        height = measureResult(heightMeasureSpec, minHeight) + totalGap;
//
//        mRectF = new RectF((width - mBitmapWidth) / 2,
//                height - (mBitmapHeight + mDesHeight + BOTTOM_GAP + DES_MARGINTOP_GAP),
//                (width + mBitmapWidth) / 2,
//                height - (BOTTOM_GAP + DES_MARGINTOP_GAP + mDesHeight));


        mRectF = new RectF((width - mBitmapWidth) / 2,
                height - (mBitmapHeight + mDesHeight + BOTTOM_GAP + DES_MARGINTOP_GAP),
                (width + mBitmapWidth) / 2,
                height - (BOTTOM_GAP + DES_MARGINTOP_GAP + mDesHeight));

        setMeasuredDimension(width, height);
    }


    private int measureResult(int measurespec, int minValue) {

        int mode = MeasureSpec.getMode(measurespec);
        int result;
        if (mode == MeasureSpec.AT_MOST) {
            result = minValue;
        } else {
            if (MeasureSpec.getSize(measurespec) < minValue) {
                result = minValue;
            } else {
                result = MeasureSpec.getSize(measurespec);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if ("--".equals(value)) {
            //30是一个神秘的数字
            canvas.drawText(value, width / 2, VALUE_MARGINTOP_GAP + 30, valuePaint);
            bitmapPaint.setColorFilter(mColorlessFilter);
        } else {
            canvas.drawText(value, width / 2, VALUE_MARGINTOP_GAP + mValHeight, valuePaint);
            bitmapPaint.setColorFilter(mColorfulFilter);
            canvas.drawText(UNIT, width / 2, UNIT_MARGINTOP_GAP + mValHeight + VALUE_MARGINTOP_GAP + mUnitHeight, unitPaint);
        }

        canvas.drawBitmap(mBitmap, null, mRectF, bitmapPaint);

        canvas.drawText(DESCRIBE, width / 2, height - BOTTOM_GAP, descriptionPaint);

    }

    public void setValue(@Nullable String value) {

        this.value = value == null ? "--" : value;
        requestLayout();
        postInvalidate();
    }


}
