package com.huihuicai.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.sax.RootElement;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.huihuicai.custom.R;

/**
 * Created by ybm on 2017/8/15.
 */

public class WaveView extends View {
    private final int STYLE_SINGLE = 0;
    private final int STYLE_DOUBLE = 1;
    private final int STYLE_BALL = 2;
    private int mWidth, mHeight;
    private int mWaveHeight;
    private int mLightColor;
    private int mDarkColor;
    private int mStyle;
    private int mSpeed;

    private int mWaveOffset;
    private float mRealHeight;
    private float mWaveBottom;
    private float mBallHeight;
    private Bitmap mBitmapBall;
    private Paint mPaint, mDarkPaint;
    private Path mPath, mDarkPath;
    private ValueAnimator mAnimator, mBallAnim;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mWaveHeight = ta.getInt(R.styleable.WaveView_wave_height, 60);
        mLightColor = ta.getColor(R.styleable.WaveView_wave_light_color, Color.argb(88, 190, 190, 190));
        mDarkColor = ta.getColor(R.styleable.WaveView_wave_dark_color, Color.argb(255, 30, 144, 255));
        mStyle = ta.getInt(R.styleable.WaveView_model, 1);
        mSpeed = ta.getInt(R.styleable.WaveView_speed, 4000);
        ta.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mLightColor);

        mDarkPaint = new Paint();
        mDarkPaint.setAntiAlias(true);
        mDarkPaint.setStyle(Paint.Style.FILL);
        mDarkPaint.setColor(mDarkColor);

        mPath = new Path();
        mDarkPath = new Path();

        setBall(null);
    }

    public void setBall(Bitmap bitmap) {
        if (mStyle != STYLE_BALL) {
            return;
        }
        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round, options);
        }
        mBitmapBall = bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

        if (modeH == MeasureSpec.AT_MOST) {
            sizeH = 6 * mWaveHeight;
        }
        setMeasuredDimension(sizeW, sizeH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRealHeight = mHeight / 4f;
        mWaveBottom = mHeight / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setDarkPath();
        canvas.drawPath(mDarkPath, mDarkPaint);
        if (mStyle == STYLE_DOUBLE) {
            setLightPath();
            canvas.drawPath(mPath, mPaint);
        }
        if (mStyle == STYLE_BALL) {
            canvas.drawBitmap(mBitmapBall, (mWidth - mBitmapBall.getWidth()) * 0.5f, mBallHeight - mBitmapBall.getHeight(), mPaint);
        }
        repeatDraw();
    }

    private void setLightPath() {
        mPath.reset();
        mPath.moveTo(-mWidth + mWaveOffset, mWaveBottom + mRealHeight / 2f);
        for (int i = 0; i < 2; i++) {
            mPath.quadTo(-mWidth * 3f / 4 + mWaveOffset + i * mWidth, mRealHeight + mWaveBottom, -mWidth / 2f + mWaveOffset + i * mWidth, mWaveBottom + mRealHeight / 2f);
            mPath.quadTo(-mWidth / 4f + mWaveOffset + i * mWidth, mWaveBottom, mWaveOffset + i * mWidth, mWaveBottom + mRealHeight / 2f);
        }
        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
    }

    private void setDarkPath() {
        mDarkPath.reset();
        mDarkPath.moveTo(-mWidth + mWaveOffset, mWaveBottom + mRealHeight / 2f);
        for (int i = 0; i < 2; i++) {
            mDarkPath.quadTo(-mWidth * 3f / 4 + mWaveOffset + i * mWidth, mWaveBottom, -mWidth / 2f + mWaveOffset + i * mWidth, mWaveBottom + mRealHeight / 2f);
            mDarkPath.quadTo(-mWidth / 4f + mWaveOffset + i * mWidth, mRealHeight + mWaveBottom, mWaveOffset + i * mWidth, mWaveBottom + mRealHeight / 2f);
        }
        mDarkPath.lineTo(mWidth, mHeight);
        mDarkPath.lineTo(0, mHeight);
        mDarkPath.close();
    }

    private void repeatDraw() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, mWidth);
        } else {
            return;
        }
        mAnimator.setDuration(mSpeed);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float factor = 0.5f * mWidth;
            float rate = 0.5f * mWaveBottom / mWidth;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveOffset = (int) animation.getAnimatedValue();
                if (mWaveOffset <= factor) {
                    mBallHeight = mWaveBottom - mWaveOffset * rate;
                } else {
                    mBallHeight = mWaveBottom - (mWidth - mWaveOffset) * rate;
                }
                postInvalidate();
            }
        });
        mAnimator.start();
    }
}
