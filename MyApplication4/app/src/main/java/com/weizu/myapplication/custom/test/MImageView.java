package com.weizu.myapplication.custom.test;

import android.content.Context;
import android.util.AttributeSet;

public class MImageView extends androidx.appcompat.widget.AppCompatImageView {

    public MImageView(Context context) {
        super(context);
    }

    public MImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
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
}
