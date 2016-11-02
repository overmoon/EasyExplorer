package fun.my.easyexplorer.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
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
        // 获取属性
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCircleView, defStyleAttr, defStyleRes);
        // 设置属性
        mShape = SHAPE.values()[(typedArray.getInt(R.styleable.CustomCircleView_shape, 0))];
        mOuterRadius = typedArray.getDimension(R.styleable.CustomCircleView_outerRadius, DensityUtils.dip2px(context, 30));
        mRingWidth = typedArray.getDimension(R.styleable.CustomCircleView_ringWidth, DensityUtils.dip2px(context, 3));
        // 混合属性分别判断
        mInnerCircleColor = typedArray.getColor(R.styleable.CustomCircleView_innerCircleColor, 0);
        if (mInnerCircleColor == 0) {
            mInnerCircleColor = typedArray.getResourceId(R.styleable.CustomCircleView_innerCircleColor, R.color.transparent);
        }

        mOuterRingColor = typedArray.getColor(R.styleable.CustomCircleView_outerRingColor, 0);
        if (mOuterRingColor == 0) {
            mOuterRingColor = typedArray.getResourceId(R.styleable.CustomCircleView_outerRingColor, R.color.colorPrimaryDark);
        }

        mOuterRingBackgroundColor = typedArray.getColor(R.styleable.CustomCircleView_outerRingBackgroundColor, 0);
        if (mOuterRingBackgroundColor == 0) {
            mOuterRingBackgroundColor = typedArray.getResourceId(R.styleable.CustomCircleView_outerRingBackgroundColor, R.color.transparent);
        }

        mTextColor = typedArray.getColor(R.styleable.CustomCircleView_textColor, 0);
        if (mTextColor == 0) {
            mTextColor = typedArray.getResourceId(R.styleable.CustomCircleView_textColor, R.color.colorBlack);
        }

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
        mDuration = typedArray.getInt(R.styleable.CustomCircleView_duration, 0);
        typedArray.recycle();

        mPaint = new Paint();
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
        //外圈背景
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        if (mOuterRingBackgroundColor != Color.TRANSPARENT  && mOuterRingBackgroundColor != mInnerCircleColor) {
            mPaint.setColor(mOuterRingBackgroundColor);
            canvas.drawArc(rectF, 180, 90, false, mPaint);
        }
        mPaint.setColor(mOuterRingColor);
        //根据形状
        switch (mShape) {
            case RING:
                canvas.drawArc(rectF, -90, 270, false, mPaint);
                break;
            case SECTOR:
                canvas.drawArc(rectF, -90, 270, true, mPaint);
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
}
