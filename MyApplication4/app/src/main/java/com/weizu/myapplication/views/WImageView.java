package com.weizu.myapplication.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.weizu.myapplication.R;
import com.weizu.myapplication.views.interfaces.WOnClickListener;

public class WImageView extends View{

    private Drawable mDrawable;
    private Paint mPaint;
    private int borderWidth;
    private int borderColor;
    private WOnClickListener onClickListener;

    public WImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WImageView);
        // attrs文件中定义的CameraImageView中的src属性
        mDrawable = typedArray.getDrawable(R.styleable.WImageView_src);
        borderWidth = typedArray.getInt(R.styleable.WImageView_border, 0);
        borderColor = typedArray.getColor(R.styleable.WImageView_borderColor,
                getResources().getColor(R.color.gray, null));
        typedArray.recycle(); // TypedArray是共享资源，使用完毕必须回收
        mPaint = new Paint();
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(borderColor);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(widthMeasureSpec, 200);
        int height = getSize(heightMeasureSpec, 200);
        setMeasuredDimension(width, height);
    }

    private int getSize(int widthMeasureSpec, int defultSize) {
        int realSize = defultSize;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                realSize = defultSize;
                break;
            case MeasureSpec.AT_MOST:
                realSize = size;
                break;
            case MeasureSpec.EXACTLY:
                realSize = size;
                break;
        }
        return realSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawable.setBounds(getLeft(), getTop(), getWidth(), getHeight());
        mDrawable.draw(canvas);
        canvas.drawRect(getLeft() + borderWidth, getTop() + borderWidth,
                getWidth() - 2 * borderWidth, getHeight() - 2 * borderWidth, mPaint);
    }
}
