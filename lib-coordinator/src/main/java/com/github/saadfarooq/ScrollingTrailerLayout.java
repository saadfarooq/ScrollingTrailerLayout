package com.github.saadfarooq.library;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ScrollingTrailerLayout extends LinearLayout implements NestedScrollingParent {
    public ScrollingTrailerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingTrailerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScrollingTrailerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
