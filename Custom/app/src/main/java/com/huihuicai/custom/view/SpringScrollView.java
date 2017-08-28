package com.huihuicai.custom.view;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by ybm on 2017/8/28.
 */

public class SpringScrollView extends ScrollView {

    private ViewGroup mChild;
    private int mTouchSlop;
    private int[] mChildLocation = new int[2];
    private int mChildBottom;
    private float mChildW, mChildH;
    private float mStartW, mStartH;
    private float mClickX, mClickY;
    private boolean mCanDrag, mCanMove;
    private SpringAnimation mAnimationY;//y方向

    public SpringScrollView(Context context) {
        this(context, null);
    }

    public SpringScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count == 1) {
            mChild = (ViewGroup) getChildAt(0);
        }

        if (mChild == null) {
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mChild = new LinearLayout(getContext());
            mChild.setLayoutParams(params);
            addView(mChild, 0);
        }

        mChild.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mChild.getLocationInWindow(mChildLocation);
                mChildW = mChild.getWidth();
                mChildH = mChild.getHeight();
                mStartW = mChild.getX();
                mStartH = mChild.getY();
                mChildBottom = getHeight();
                mAnimationY = createAnimation(mChild, SpringAnimation.TRANSLATION_Y, 0);
                mChild.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private SpringAnimation createAnimation(View view, DynamicAnimation.ViewProperty property, float finalPosition) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce force = new SpringForce(finalPosition);
        force.setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        force.setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);
        animation.setSpring(force);
        return animation;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 如果点击的区域是在当前的child区域，那么就可以视为需要拖拽了
                mClickX = ev.getRawX();
                mClickY = ev.getRawY();
                if ((getScrollY() <= 0 || getScrollY() >= mChildH - mChildBottom)
                        && mClickX >= mChildLocation[0] + mStartW
                        && mClickX <= mChildLocation[0] + mStartW + mChildW
                        && mClickY > mChildLocation[1] + mStartH
                        && mClickY <= mChildLocation[1] + mStartH + mChildH) {
                    mCanDrag = true;
                    mAnimationY.cancel();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getRawY() - mClickY;
                if (mCanDrag && dy > mTouchSlop && getScrollY() == 0) {
                    //向下拖拽的时候
                    mCanMove = true;
                    float deltaY = dy + mStartH;
                    if (Math.abs(deltaY) < 5) {
                        return false;
                    }
                    mChild.animate().y(deltaY).setDuration(0).start();
                    return true;
                } else if (mCanDrag && dy < -mTouchSlop && getScrollY() == mChildH - mChildBottom) {
                    //向上拖拽的时候
                    mCanMove = true;
                    float deltaY = dy + mStartH;
                    if (Math.abs(deltaY) < 5) {
                        return false;
                    }
                    mChild.animate().y(deltaY).setDuration(0).start();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCanDrag && mCanMove) {
                    mAnimationY.start();
                }
                mCanMove = false;
                mCanDrag = false;
                break;
        }
        return super.onTouchEvent(ev);
    }
}
