package com.huihuicai.custom.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.huihuicai.custom.R;
import com.huihuicai.custom.view.ShopView;

public class ShopActivity extends AppCompatActivity {

    private ShopView shopView;
    private ImageView ivEnd, ivDot;
    private Point mEndPoint;
    private Drawable mDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        shopView = (ShopView) findViewById(R.id.shop_view);
        ivEnd = (ImageView) findViewById(R.id.iv_end);
        ivDot = (ImageView) findViewById(R.id.iv_dot);
        mDrawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
        shopView.initData(1, 100);
        shopView.setClickListener(new ShopView.ClickListener() {
            @Override
            public void onClick(int position) {
                if (position > 2) {
                    return;
                }
                switch (position) {
                    case ShopView.CLICK_LEFT:
                        Toast.makeText(ShopActivity.this, "点击了左边的按钮", Toast.LENGTH_SHORT).show();
                        break;
                    case ShopView.CLICK_MIDDLE:
                        Toast.makeText(ShopActivity.this, "点击了中间", Toast.LENGTH_SHORT).show();
                        break;
                    case ShopView.CLICK_RIGHT:
                        Toast.makeText(ShopActivity.this, "点击了右边的按钮", Toast.LENGTH_SHORT).show();
                        showAnim(shopView.getPoint(), mEndPoint);
                        break;
                }
            }
        });

        ivEnd.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] end = new int[2];
                ivEnd.getLocationInWindow(end);
                mEndPoint = new Point(end[0] - ivEnd.getMeasuredWidth(), end[1] - ivEnd.getMeasuredHeight());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ivEnd.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void showAnim(final Point startPoint, Point endPoint) {
        Path path = new Path();
        path.lineTo(startPoint.x, startPoint.y);
        path.quadTo((startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.x) / 2, endPoint.x, endPoint.y);

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final float[] imgPosition = new float[2];
        ValueAnimator animator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ivDot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ivDot.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                ivDot.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(value, imgPosition, null);
                ivDot.setTranslationX(imgPosition[0]);
                ivDot.setTranslationY(imgPosition[1]);
            }
        });
        animator.setDuration(2000);
        animator.start();
    }

}
