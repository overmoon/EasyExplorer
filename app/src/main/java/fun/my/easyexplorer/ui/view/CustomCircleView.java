package fun.my.easyexplorer.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import fun.my.easyexplorer.R;

/**
 * Created by admin on 2016/11/1.
 */
public class CustomCircleView extends View {
    //形状
    private enum SHAPE{
        RING, SECTOR
    }
    //text 样式
    private enum TEXT_STYLE{
        NORMAL, BOLD, ITALIC
    }
    //text 位置
    private enum TEXT_ALIGN{
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
    private TEXT_ALIGN mTextAlign;
    private TEXT_STYLE mTextStyle;
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
        mOuterRadius = typedArray.getDimension(R.styleable.CustomCircleView_outerRadius, 30);
        mRingWidth = typedArray.getDimension(R.styleable.CustomCircleView_ringWidth, 3);
        // 混合属性分别判断
        mInnerCircleColor = typedArray.getColor(R.styleable.CustomCircleView_innerCircleColor, -1);
        if (mInnerCircleColor == -1){
            mInnerCircleColor = typedArray.getResourceId(R.styleable.CustomCircleView_innerCircleColor, R.color.transparent);
        }

        mOuterRingColor = typedArray.getColor(R.styleable.CustomCircleView_outerRingColor, -1);
        if (mOuterRingColor == -1){
            mOuterRingColor = typedArray.getResourceId(R.styleable.CustomCircleView_outerRingColor, R.color.colorPrimaryDark);
        }

        mOuterRingBackgroundColor = typedArray.getColor(R.styleable.CustomCircleView_outerRingBackgroundColor, -1);
        if (mOuterRingBackgroundColor == -1){
            mOuterRingBackgroundColor = typedArray.getResourceId(R.styleable.CustomCircleView_outerRingBackgroundColor, mInnerCircleColor);
        }

        mTextColor = typedArray.getColor(R.styleable.CustomCircleView_textColor, -1);
        if (mTextColor == -1){
            mTextColor = typedArray.getResourceId(R.styleable.CustomCircleView_textColor, R.color.colorBlack);
        }

        mText = typedArray.getString(R.styleable.CustomCircleView_text);
        // enum属性通过数组获取对应值
        mTextStyle = TEXT_STYLE.values()[(typedArray.getInt(R.styleable.CustomCircleView_textStyle, 0))];
        mTextAlign = TEXT_ALIGN.values()[(typedArray.getInt(R.styleable.CustomCircleView_textAlign, 0))];

        typedArray.recycle();

    }

}
