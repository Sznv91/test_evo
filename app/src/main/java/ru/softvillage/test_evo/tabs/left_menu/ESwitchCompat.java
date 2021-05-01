package ru.softvillage.test_evo.tabs.left_menu;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;

public class ESwitchCompat extends SwitchCompat implements IExpanded {
    private float ratio = 1f;

    public ESwitchCompat(Context context) {
        super(context);
    }

    public ESwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ESwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        setMeasuredDimension(width, (int) (height * ratio));
    }

    @Override
    public void expand(float ratio) {
        this.ratio = ratio;
//        requestLayout();
        try {
            super.requestLayout();
        } catch (Exception ex){

        }
    }
}
