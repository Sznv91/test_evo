package ru.softvillage.fiscalizer.tabs.left_menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ELinearLayout extends LinearLayout implements IExpanded {
    private float ratio = 1f;

    public ELinearLayout(Context context) {
        super(context);
    }

    public ELinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ELinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ELinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        setMeasuredDimension(width, (int) (height * ratio));
    }

    @Override
    public void expand(float ratio) {
        this.ratio = ratio;
        requestLayout();
    }
}
