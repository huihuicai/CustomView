package com.huihuicai.custom.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by ybm on 2017/8/29.
 */

public class FabBehavior extends CoordinatorLayout.Behavior<View> {

    private int mMoveHeight;
    private int mCurPosition;
    private boolean isStopAnim = true;
    private ViewPropertyAnimator mAnimator;

    public FabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * fling或者scroll都会执行
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        if (child != null) {
            mMoveHeight = coordinatorLayout.getMeasuredHeight();
            mCurPosition = child.getTop();
        }
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (dy > 10) {
            handleAnim(child, true);
        } else if (dy < -10) {
            handleAnim(child, false);
        }
    }

    private void handleAnim(View view, boolean isHide) {
        if (!isStopAnim) return;
        int finalPosition = isHide ? mMoveHeight : mCurPosition;
        mAnimator = view.animate().y(finalPosition).setDuration(500);
        mAnimator.start();
        mAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isStopAnim = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isStopAnim = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
