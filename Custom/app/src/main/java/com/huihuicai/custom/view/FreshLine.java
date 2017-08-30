package com.huihuicai.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by ybm on 2017/8/29.
 */

public class FreshLine extends View {
    private final int MODE_ONE_DIRECTION = 1;
    private final int MODE_TWO_DIRECTION = 2;
    private int mCurrentMode = MODE_ONE_DIRECTION;
    private int mHeight, mWidth;
    private float mProgress, mOffset;
    private int[] mColor = {0xffef3467, 0xff769823, 0xff234523};

    private Paint mPaint;
    private ValueAnimator mAnimator;

    public FreshLine(Context context) {
        this(context, null);
    }

    public FreshLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FreshLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mHeight == 0 ? 8 : mHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        if (modeH == MeasureSpec.AT_MOST || modeH == MeasureSpec.UNSPECIFIED) {
            sizeH = mHeight == 0 ? 8 : mHeight;
        }
        setMeasuredDimension(sizeW, sizeH);
    }

    public void setProgress(float progress, boolean release) {
        mProgress = progress * 1.2f;
        if (!release) {
            invalidate();
        } else {
            if (progress == 0 && mAnimator != null) {
                mAnimator.cancel();
                invalidate();
                return;
            }
            float from, to;
            if (mCurrentMode == MODE_ONE_DIRECTION) {
                if (mProgress < mWidth) {
                    from = mProgress;
                    to = 0;
                } else {
                    from = 0;
                    to = mWidth;
                }
            } else {
                if (mProgress < mWidth) {
                    from = 0.5f * mProgress;
                    to = 0;
                } else {
                    from = 0;
                    to = 0.5f * mWidth;
                }
            }
            startAnimation(from, to);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("onSizeChanged", "h:" + h);
        mPaint.setStrokeWidth(h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrentMode == MODE_ONE_DIRECTION) {
            float length;
            if (mProgress <= mWidth) {
                length = mProgress / mColor.length;
                for (int i = 0; i < mColor.length; i++) {
                    mPaint.setColor(mColor[i]);
                    canvas.drawLine(i * length, 0, (i + 1) * length, 0, mPaint);
                }
            } else {
                length = mWidth / mColor.length;
                for (int i = 0, len = mColor.length; i < 2 * len; i++) {
                    mPaint.setColor(mColor[i % len]);
                    canvas.drawLine(i * length - mWidth + mOffset, 0, (i + 1) * length - mWidth + mOffset, 0, mPaint);
                }
            }
        } else if (mCurrentMode == MODE_TWO_DIRECTION) {
            float halfLength;
            if (mProgress < mWidth) {
                halfLength = 0.5f * mProgress / mColor.length;
                for (int i = 0, len = mColor.length; i < len; i++) {
                    mPaint.setColor(mColor[i]);
                    canvas.drawLine(0.5f * mProgress - (len - i) * halfLength, 0, 0.5f * mProgress - (len - i + 1) * halfLength, 0, mPaint);
                    canvas.drawLine(0.5f * mProgress + (len - i - 1) * halfLength, 0, 0.5f * mProgress + (len - i) * halfLength, 0, mPaint);
                }
            } else {
                halfLength = 0.5f * mWidth / mColor.length;
                for (int i = 0, len = mColor.length; i < len; i++) {
                    mPaint.setColor(mColor[i]);
                    canvas.drawLine(-mOffset + 0.5f * mWidth - (len - i) * halfLength, 0, -mOffset + 0.5f * mWidth - (len - i + 1) * halfLength, 0, mPaint);
                    canvas.drawLine(mOffset + 0.5f * mWidth + (len - i - 1) * halfLength, 0, mOffset + 0.5f * mWidth + (len - i) * halfLength, 0, mPaint);
                }
                if (mOffset < halfLength) {
                    mPaint.setColor(mColor[0]);
                    canvas.drawLine(0.5f * mWidth - mOffset, 0, 0.5f * mWidth, 0, mPaint);
                    canvas.drawLine(0.5f * mWidth, 0, 0.5f * mWidth + mOffset, 0, mPaint);
                } else if (mOffset < 2 * halfLength) {
                    mPaint.setColor(mColor[0]);
                    canvas.drawLine(0.5f * mWidth - mOffset, 0, 0.5f * mWidth - mOffset + halfLength, 0, mPaint);
                    canvas.drawLine(0.5f * mWidth + mOffset - halfLength, 0, 0.5f * mWidth + mOffset, 0, mPaint);
                    mPaint.setColor(mColor[1]);
                    canvas.drawLine(0.5f * mWidth - mOffset + halfLength, 0, 0.5f * mWidth, 0, mPaint);
                    canvas.drawLine(0.5f * mWidth, 0, 0.5f * mWidth + mOffset - halfLength, 0, mPaint);
                } else {
                    mPaint.setColor(mColor[0]);
                    canvas.drawLine(0.5f * mWidth - mOffset, 0, 0.5f * mWidth - mOffset + halfLength, mHeight, mPaint);
                    canvas.drawLine(0.5f * mWidth + mOffset - halfLength, 0, 0.5f * mWidth + mOffset, mHeight, mPaint);
                    mPaint.setColor(mColor[1]);
                    canvas.drawLine(0.5f * mWidth - mOffset + halfLength, 0, 0.5f * mWidth - mOffset + 2 * halfLength, mHeight, mPaint);
                    canvas.drawLine(0.5f * mWidth + mOffset - 2 * halfLength, 0, 0.5f * mWidth + mOffset, mHeight, mPaint);
                    mPaint.setColor(mColor[2]);
                    canvas.drawLine(0.5f * mWidth - mOffset + 2 * halfLength, 0, 0.5f * mWidth, mHeight, mPaint);
                    canvas.drawLine(0.5f * mWidth, 0, 0.5f * mWidth + mOffset - 2 * halfLength, mHeight, mPaint);
                }
            }
        }
    }

    private void startAnimation(float from, float to) {
        mAnimator = ValueAnimator.ofFloat(from, to);
        mAnimator.setDuration(300);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.start();
    }
}
