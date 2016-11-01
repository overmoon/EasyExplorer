package fun.my.easyexplorer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by admin on 2016/9/19.
 */
public class FileDividerItemDecoration extends RecyclerView.ItemDecoration {

    //画笔
    private Paint mPaint;
    //orientation
    private int mOrientation;
    //divider大小
    private int mItemSize = 1;

    public static int VERTICAL = LinearLayoutManager.VERTICAL;
    public static int HORIZONTAL = LinearLayoutManager.HORIZONTAL;


    public FileDividerItemDecoration(Context context, int orientation) {
        this.mOrientation = orientation;
        checkOrientation();
        initPaint();
    }

    public FileDividerItemDecoration(Context context, int orientation, int mItemSize) {
        this.mOrientation = orientation;
        checkOrientation();
        this.mItemSize = mItemSize;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void setColor(int color){
        mPaint.setColor(color);
    }

    public void setSize(int size){
        mItemSize = size;
    }

    public void checkOrientation() {
        if (mOrientation != LinearLayoutManager.VERTICAL && mOrientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("Wrong orientation arg passed in.");
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            drawHorizontal(c, parent);
        }


    }

    private void drawVertical(Canvas c, RecyclerView parent) {

    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight()-parent.getPaddingBottom();

        final int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mItemSize;
            Path path = new Path();
            path.moveTo(left, top);
            path.lineTo(right, (top+bottom)/2);
            path.lineTo(left, bottom );
            c.drawPath(path, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mItemSize);
        } else if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, mItemSize, 0);
        }
    }
}
