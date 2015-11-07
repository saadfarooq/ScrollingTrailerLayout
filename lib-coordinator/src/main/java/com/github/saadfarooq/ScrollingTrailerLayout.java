package com.github.saadfarooq;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import static android.animation.ValueAnimator.ofInt;
import static java.lang.Math.min;

public class ScrollingTrailerLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "ScrollingTrailerLayout";
    private static final int DURATION_ANIMATION_SCROLL = 100;
    private static final int DIRECTION_UNKNOWN = 0;
    private static final int DIRECTION_UP = 1;
    private static final int DIRECTION_DOWN = 2;
    private int direction = 0;
    private final NestedScrollingParentHelper parentHelper;
    private View heroChild;
    private View trailerChild;
    private int maxScroll;
    private int currentScroll = 0;
    private boolean isAnimating = false;


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
                maxScroll = heroChild.getHeight();
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
        switch (direction) {
            case DIRECTION_UP:
                animatedScrollBy(maxScroll - currentScroll);
                currentScroll = maxScroll;
                break;
            case DIRECTION_DOWN:
                animatedScrollBy(-1 * currentScroll);
                currentScroll = 0;
                break;
        }
        direction = DIRECTION_UNKNOWN;
    }

    private void animatedScrollBy(int dy) {
        int currentScrollY = getScrollY();
        debug("animated scroll by: %d, %d, %d", dy, currentScrollY, currentScrollY + dy);
        ValueAnimator animator = ofInt(currentScrollY, currentScrollY + dy);
        animator.setDuration(DURATION_ANIMATION_SCROLL);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                setScrollY((Integer) animation.getAnimatedValue());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override public void onAnimationCancel(Animator animation) {
                isAnimating = false;
            }

            @Override public void onAnimationRepeat(Animator animation) {
                isAnimating = true;
            }
        });
        animator.start();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0 && currentScroll > 0 && !isAnimating) {
            int maxDelta = min(currentScroll, -1 * dyUnconsumed);
            scrollBy(0, -1 * maxDelta);
            currentScroll -= maxDelta;
            direction = DIRECTION_DOWN;
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (currentScroll < maxScroll && dy > 0 && !isAnimating) {
            int maxDelta = min(maxScroll - currentScroll, dy);
            scrollBy(0, maxDelta);
            currentScroll += maxDelta;
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
