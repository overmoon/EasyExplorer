package fun.my.easyexplorer.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import fun.my.easyexplorer.R;
import my.fun.asyncload.imageloader.utils.DensityUtils;

/**
 * Created by admin on 2016/11/1.
 */
public class CustomCircleView extends View {
    //形状
    private enum SHAPE {
        RING, SECTOR
    }

    //text 样式
    private enum TEXT_STYLE {
        NORMAL, BOLD, ITALIC
    }

    //text 位置
    private enum TEXT_ALIGN {
        CENTER, LEFT, RIGHT
    }

    //形状，扇形 or 圆环
    private SHAPE mShape;
    private Paint mPaint;
    //内部文字
    private String mText;
    //文字颜色
    private int mTextColor;
    //是否显示text
    private boolean mTextDisplay;
    private boolean mPercentDisplay;
    private TEXT_ALIGN mTextAlign;
    private TEXT_STYLE mTextStyle;
    private float mTextSize;
    //内部圆颜色
    private int mInnerCircleColor;
    //外部进度圆环颜色
    private int mOuterRingColor;
    //外部圆环底色
    private int mOuterRingBackgroundColor;
    //外半径
    private float mOuterRadius;
    //环宽
    private float mRingWidth;
    //进度，百分比
    private float mMax;
    private float mProcess;
    private float mPercent;
    //动画完成时间
    private int mDuration;

    public CustomCircleView(Context context) {
        this(context, null);
    }

    public CustomCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        Resources.Theme theme = context.getTheme();
        // 获取属性
        TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.CustomCircleView, defStyleAttr, defStyleRes);
        // 设置属性
        mShape = SHAPE.values()[(typedArray.getInt(R.styleable.CustomCircleView_shape, 0))];
        mOuterRadius = typedArray.getDimension(R.styleable.CustomCircleView_outerRadius, DensityUtils.dip2px(context, 30));
        mRingWidth = typedArray.getDimension(R.styleable.CustomCircleView_ringWidth, DensityUtils.dip2px(context, 3));
        // 混合属性判断
        mInnerCircleColor = getColor(R.styleable.CustomCircleView_innerCircleColor, typedArray, context, R.attr.myColorPrimaryLight);
        mOuterRingColor = getColor(R.styleable.CustomCircleView_outerRingColor, typedArray, context, R.attr.myColorPrimaryDark);
        mOuterRingBackgroundColor = getColor(R.styleable.CustomCircleView_outerRingColor, typedArray, context, 0);
        mTextColor = getColor(R.styleable.CustomCircleView_outerRingColor, typedArray, context, R.attr.myColorText);

        mPercentDisplay = typedArray.getBoolean(R.styleable.CustomCircleView_percentDisplay, true);
        mTextDisplay = typedArray.getBoolean(R.styleable.CustomCircleView_textDisplay, false);

        mTextSize = typedArray.getDimension(R.styleable.CustomCircleView_textSize, DensityUtils.dip2px(context, 25));
        mText = typedArray.getString(R.styleable.CustomCircleView_text);
        // enum属性通过数组获取对应值
        mTextStyle = TEXT_STYLE.values()[(typedArray.getInt(R.styleable.CustomCircleView_textStyle, 0))];
        mTextAlign = TEXT_ALIGN.values()[(typedArray.getInt(R.styleable.CustomCircleView_textAlign, 0))];
        //进度相关属性
        mMax = typedArray.getFloat(R.styleable.CustomCircleView_max, 0);
        mProcess = typedArray.getFloat(R.styleable.CustomCircleView_process, 0);
        mPercent = typedArray.getFloat(R.styleable.CustomCircleView_percent, 0);
        mDuration = typedArray.getInt(R.styleable.CustomCircleView_duration, 1000);
        typedArray.recycle();

        mPaint = new Paint();
    }

    /**
     * Get color from reference or color
     * index: styleable index, ex, R.styleable.yourAttr
     */
    private int getColor(@StyleableRes int index, TypedArray typedArray, Context context, @AttrRes int defaultAttrColor) {
        Resources.Theme theme = context.getTheme();
        Resources resources = context.getResources();
        int color = 0;
        ColorStateList list = typedArray.getColorStateList(index);
        if (list == null && defaultAttrColor !=0 ) {
            TypedValue value = new TypedValue();
            theme.resolveAttribute(defaultAttrColor, value, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = resources.getColor(value.resourceId, theme);
            } else {
                color = resources.getColor(value.resourceId);
            }
        }
        if(list!=null){
            color = list.getDefaultColor();
        }
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        float center_x = width / 2.0f;
        float center_y = height / 2.0f;
        if (mMax != 0) {
            if (mProcess != 0 && mPercent == 0) {
                mPercent = mProcess / mMax;
            }
        }

        float innerRadius = mOuterRadius - mRingWidth;
        //消除锯齿
        mPaint.setAntiAlias(true);

        //圆弧的大小和界限
        float realOuterRadius = mOuterRadius - mRingWidth / 2;
        // 画底圈
        mPaint.setColor(mInnerCircleColor);
        mPaint.setStrokeWidth(innerRadius);
        canvas.drawCircle(center_x, center_y, mOuterRadius, mPaint);

        RectF rectF = new RectF(center_x - realOuterRadius, center_y - realOuterRadius, center_x + realOuterRadius, center_y + realOuterRadius);
        if (mPercent > 1 || mPercent < 0)
            throw new IllegalArgumentException("Percent can not be > 1 or < 0");
        float angle = mPercent * 360;
        float leftStartAngle = -90 + angle;
        float sweepAngle = 360 - angle;
        //外圈背景
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        if (mOuterRingBackgroundColor != Color.TRANSPARENT && mOuterRingBackgroundColor != mInnerCircleColor) {
            mPaint.setColor(mOuterRingBackgroundColor);
            canvas.drawArc(rectF, leftStartAngle, sweepAngle, false, mPaint);
        }
        mPaint.setColor(mOuterRingColor);
        //根据形状
        switch (mShape) {
            case RING:
                canvas.drawArc(rectF, -90, angle, false, mPaint);
                break;
            case SECTOR:
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawArc(rectF, -90, angle, true, mPaint);
                break;
        }

        if (mPercentDisplay) {
            mText = (int) (mPercent * 100) + "%";
        }

        if (mPercentDisplay || mTextDisplay && mText != null) {
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);
            mPaint.setStrokeWidth(0);
            mPaint.setTypeface(Typeface.DEFAULT);
            Rect bounds = new Rect();
            mPaint.getTextBounds(mText, 0, mText.length(), bounds);
            canvas.drawText(mText, center_x - bounds.width() / 2, center_y + bounds.height() / 2, mPaint);
        }
        mPaint.reset();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        Rect bounds = new Rect();
        if (mText != null) {
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds(mText, 0, mText.length(), bounds);
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            float textWidth = bounds.width();
            float length = mOuterRadius * 2 > textWidth ? mOuterRadius * 2 : textWidth;
            width = (int) (getPaddingLeft() + length + getPaddingRight());
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = bounds.height();
            float length = mOuterRadius * 2 > textHeight ? mOuterRadius * 2 : textHeight;
            height = (int) (getPaddingTop() + length + getPaddingBottom());
        }

        setMeasuredDimension(width, height);
    }

    public synchronized void setmPercent(float percent) {
        if (percent > 1 || percent < 0) {
            throw new IllegalArgumentException("Percent can not be > 1 or < 0");
        }
        //动画
        percentChange(mPercent, percent, mDuration);
    }

    public void percentChange(final float prePercent, final float currentPercent, final int duration) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //计算每1%耗时 毫秒
                int percent = (int) ((currentPercent - prePercent) * 100);
                int sleepTime = 0;
                if (percent != 0) {
                    sleepTime = duration / Math.abs(percent);
                    sleepTime = sleepTime > 15 ? 15 : sleepTime;
                }
                float increment = percent > 0 ? 0.01f : -0.01f;

                for (float i = percent; mPercent * increment < currentPercent * increment; i += increment) {
                    mPercent = mPercent + increment;
                    postInvalidate();
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
