package com.huihuicai.custom.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

/**
 * Created by ybm on 2017/8/18.
 */

public class DragView extends AppCompatImageView {

    private SpringAnimation mAnimX, mAnimY;
    private int mWidth, mHeight, mStartX, mStartY;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAnimX = createAnimation(SpringAnimation.TRANSLATION_X, getX());
                mAnimY = createAnimation(SpringAnimation.TRANSLATION_Y, getY());
                mStartX = (int) getX();
                mStartY = (int) getY();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    private SpringAnimation createAnimation(DynamicAnimation.ViewProperty property, float finalPosition) {
        SpringAnimation animation = new SpringAnimation(this, property);
        SpringForce force = new SpringForce(finalPosition);
        force.setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        force.setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);
        animation.setSpring(force);
        return animation;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    /**
     * 当前的view位置边界
     */
    public RectF getViewRect() {
        int[] location = new int[2];
        getLocationInWindow(location);
        RectF rect = new RectF();
        rect.left = location[0];
        rect.top = location[1];
        rect.right = location[0] + mWidth;
        rect.bottom = location[1] + mHeight;
        return rect;
    }

    /**
     * 开始的地方的坐标
     */
    public Point getPoitToParent() {
        return new Point(mStartX, mStartY);
    }

    /**
     * 移动到某一个位置
     */
    public void movePosition(float x, float y, long delay) {
        animate().x(x).y(y).setDuration(delay).start();
    }

    /**
     * 回弹到原始位置
     */
    public void springBack() {
        mAnimX.start();
        mAnimY.start();
    }
}
