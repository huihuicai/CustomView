package com.huihuicai.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.animation.SpringAnimation;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by ybm on 2017/8/15.
 */

public class DragFloatingButton extends FloatingActionButton {

    private int screenWidth;
    private int screenHeight;
    private Context mContext;
    private int lastX, lastY;
    private int left, top;
    private ViewGroup.MarginLayoutParams layoutParams;
    private int startX;
    private int endX;
    private boolean isMoved = false;
    private onDragViewClickListener mLister;

    public interface onDragViewClickListener {
        void onDragViewClick();
    }

    public void setOnDragViewClickListener(onDragViewClickListener listener) {
        this.mLister = listener;
    }

    public DragFloatingButton(Context context) {
        this(context, null);
    }

    public DragFloatingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels - getStatusBarHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        post(new Runnable() {
            @Override
            public void run() {
                layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                layoutParams.topMargin = screenHeight - getHeight();
                layoutParams.leftMargin = screenWidth - getWidth();
                setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                startX = lastX;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                left = this.getLeft() + dx;
                top = this.getTop() + dy;
                int right = this.getRight() + dx;
                int bottom = this.getBottom() + dy;
                // 设置不能出界
                if (left < 0) {
                    left = 0;
                    right = left + this.getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - this.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + this.getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - this.getHeight();
                }
                this.layout(left, top, right, bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //只有滑动改变上边距时，抬起才进行设置
                if (isMoved) {
                    layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                    layoutParams.topMargin = top;
                    setLayoutParams(layoutParams);
                }
                endX = (int) event.getRawX();
                //滑动距离比较小，当作点击事件处理
                if (Math.abs(startX - endX) < 6) {
                    return false;
                }
                if (left + this.getWidth() / 2 < screenWidth / 2) {
                    startScroll(left, screenWidth / 2, true);
                } else {
                    startScroll(left, screenWidth / 2, false);
                }
                break;
        }
        return true;
    }

    //在此处理点击事件
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mLister.onDragViewClick();
//        return super.onTouchEvent(event);
//    }
    public void startScroll(final int start, int end, final boolean isLeft) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end).setDuration(800);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isLeft) {
                    layoutParams.leftMargin = (int) (start * (1 - animation.getAnimatedFraction()));
                } else {
                    layoutParams.leftMargin = (int) (start + (screenWidth - start - getWidth()) * (animation.getAnimatedFraction()));
                }
                setLayoutParams(layoutParams);
            }
        });
        valueAnimator.start();
    }

    /**
     * 获取状态栏的高度
     *
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
