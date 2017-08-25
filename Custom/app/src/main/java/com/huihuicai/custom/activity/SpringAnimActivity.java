package com.huihuicai.custom.activity;

import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.huihuicai.custom.R;

public class SpringAnimActivity extends AppCompatActivity {

    private SpringAnimation mAnimationX;//x方向
    private SpringAnimation mAnimationY;//y方向
    private ImageView ivDrag, ivDrag1, ivDrag2, ivDrag3;
    private int mViewWidth, mViewHeight;
    private float mWidthStart, mHeightStart;
    private int[] mViewPosition = new int[2];
    private boolean mCanDrag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_anim);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);
        ivDrag1 = (ImageView) findViewById(R.id.iv_drag_1);
        ivDrag2 = (ImageView) findViewById(R.id.iv_drag_2);
        ivDrag3 = (ImageView) findViewById(R.id.iv_drag_3);
        ivDrag.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ivDrag.getLocationInWindow(mViewPosition);
                        mViewWidth = ivDrag.getMeasuredWidth();
                        mViewHeight = ivDrag.getMeasuredHeight();
                        mWidthStart = ivDrag.getX();
                        mHeightStart = ivDrag.getY();
                        mAnimationX = createAnimation(ivDrag, SpringAnimation.TRANSLATION_X, 0);
                        mAnimationY = createAnimation(ivDrag, SpringAnimation.TRANSLATION_Y, 0);
                        ivDrag.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
        );
    }

    private SpringAnimation createAnimation(View view, DynamicAnimation.ViewProperty property, float finalPosition) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce force = new SpringForce(finalPosition);
        force.setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        force.setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);
        animation.setSpring(force);
        return animation;
    }

    private float mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getRawX();
                mLastY = event.getRawY();
                if (mViewPosition[0] + mViewWidth >= mLastX && mViewPosition[0] <= mLastX
                        && mViewPosition[1] <= mLastY && mViewPosition[1] + mViewHeight >= mLastY) {
                    mAnimationX.cancel();
                    mAnimationY.cancel();
                    mCanDrag = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCanDrag) {
                    float deltaX = event.getRawX() - mLastX + mWidthStart;
                    float deltaY = event.getRawY() - mLastY + mHeightStart;
                    if (Math.abs(deltaX) < 1 || Math.abs(deltaY) < 1) {
                        return false;
                    }
                    ivDrag.animate().x(deltaX).y(deltaY).setDuration(0).start();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCanDrag) {
                    mAnimationX.start();
                    mAnimationY.start();
                    mCanDrag = false;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
