package com.github.saadfarooq;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import static java.lang.Math.min;

public class ScrollingTrailerLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "ScrollingTrailerLayout";
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;
    private int direction = DIRECTION_UP;
    private final NestedScrollingParentHelper parentHelper;
    private View heroChild;
    private View trailerChild;
    private int heroChildInitialHeight;
    private int lastDyUnconsumed;
    private int scrolled;


    public ScrollingTrailerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        parentHelper = new NestedScrollingParentHelper(this);
    }

    public ScrollingTrailerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        parentHelper = new NestedScrollingParentHelper(this);
    }

    public ScrollingTrailerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        parentHelper = new NestedScrollingParentHelper(this);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                heroChildInitialHeight = heroChild.getHeight();
                getChildAt(2).getLayoutParams().height = getHeight() - trailerChild.getHeight();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        heroChild = getChildAt(0);
        trailerChild = getChildAt(1);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        parentHelper.onStopNestedScroll(target);
        if (direction == DIRECTION_UP) {
            scrollBy(0, heroChildInitialHeight - scrolled);
            scrolled = heroChildInitialHeight;
        } else {
            scrollBy(0, -1 *scrolled);
            scrolled = 0;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0 && scrolled > 0) {
            int maxDelta = min(scrolled, -1 * dyUnconsumed);
            scrollBy(0, -1 * maxDelta);
            scrolled -= maxDelta;
            direction = DIRECTION_DOWN;
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (scrolled < heroChildInitialHeight && dy > 0) {
            int maxDelta = min(heroChildInitialHeight - scrolled, dy);
            scrollBy(0, maxDelta);
            scrolled += maxDelta;
            consumed[1] = dy;
            direction = DIRECTION_UP;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return parentHelper.getNestedScrollAxes();
    }

    private void debug(String logString, Object... args) {
        Log.d(TAG, String.format(logString, args));
    }
}
