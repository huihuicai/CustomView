package com.huihuicai.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.huihuicai.custom.R;

/**
 * Created by ybm on 2017/8/11.
 */

public class ShopView extends View {

    private final int STATUS_PACK = 0;
    private final int STATUS_EXPAND = 1;
    private final int STATUS_ANIMATION = 2;

    private int mWidth, mHeight;
    private int mCircleRadius;
    private int mBorderWidth;
    private float mSymbolSize;
    private int mSymbolWidth;
    private int mTextWidth;
    private int mCircleColor;
    private int mBorderColor;
    private int mSymbolColor;
    private int mTextColor;
    private int mBgTextView;
    private Point mLeftRadius;
    private Point mRightRadius;

    private float mTextBaseLine;
    private float mDeltaX, mDeltaY;
    private int mCircleGap;
    private int mCurrentStatus;
    private RectF mArcRect = new RectF();
    private Paint mPaint;
    private TextPaint mTextPaint;
    private float mTouchSlop;
    private boolean mClickable;
    private ClickListener mClickListener;

    public ShopView(Context context) {
        this(context, null);
    }

    public ShopView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.ShopView);
        mCircleRadius = ta.getDimensionPixelOffset(R.styleable.ShopView_radius, 40);
        mBorderWidth = ta.getDimensionPixelSize(R.styleable.ShopView_circle_width, 1);
        mSymbolWidth = ta.getDimensionPixelSize(R.styleable.ShopView_symbol_width, 4);
        mTextWidth = ta.getDimensionPixelSize(R.styleable.ShopView_text_width, 200);
        mCircleColor = ta.getColor(R.styleable.ShopView_circle_color, Color.WHITE);
        mBorderColor = ta.getColor(R.styleable.ShopView_border_color, Color.GRAY);
        mSymbolColor = ta.getColor(R.styleable.ShopView_symbol_color, Color.DKGRAY);
        mTextColor = ta.getColor(R.styleable.ShopView_text_color, Color.BLACK);
        mBgTextView = ta.getColor(R.styleable.ShopView_text_background, Color.rgb(238, 238, 238));
        ta.recycle();
        mSymbolSize = mCircleRadius * 0.5f;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(34);
        mTextPaint.setColor(mTextColor);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextBaseLine = (fm.ascent + fm.descent) / 2;

        mCurrentStatus = STATUS_PACK;
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        switch (modeW) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                sizeW = getPaddingLeft() + getPaddingRight() + 2 * (mCircleRadius + mBorderWidth) + mTextWidth;
                break;
            case MeasureSpec.EXACTLY:
                mTextWidth = sizeW - getPaddingLeft() - getPaddingRight() - 4 * (mCircleRadius + mBorderWidth);
                break;
        }
        switch (modeH) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                sizeH = getPaddingTop() + getPaddingBottom() + 2 * (mCircleRadius + mBorderWidth);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        setMeasuredDimension(sizeW, sizeH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mLeftRadius = new Point(getPaddingLeft() + mBorderWidth + mCircleRadius, h / 2);
        mRightRadius = new Point(mWidth - getPaddingRight() - mBorderWidth - mCircleRadius, h / 2);
        mCircleGap = mRightRadius.x - mLeftRadius.x;
    }

    private void expandAnimation() {
        ValueAnimator expandAnim = ValueAnimator.ofFloat(0, 720f);
        expandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                mDeltaX = (int) (mSymbolSize * Math.cos(angle * Math.PI / 180f));
                mDeltaY = (int) (mSymbolSize * Math.sin(angle * Math.PI / 180f));
                mLeftRadius.x = (int) (mRightRadius.x + angle / 720f * mCircleGap);
                invalidate();
            }
        });
        expandAnim.setDuration(1000);
        expandAnim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mCurrentStatus) {
            case STATUS_PACK:
                drawStatusPack(canvas);
                break;
            case STATUS_EXPAND:
                drawStatusExpand(canvas);
                break;
            case STATUS_ANIMATION:
                drawStatusAnimation(canvas);
                break;
        }
    }

    private void drawStatusPack(Canvas canvas) {
        mPaint.setColor(mBorderColor);
        canvas.drawCircle(mRightRadius.x, mRightRadius.y, mCircleRadius + mBorderWidth, mPaint);
        mPaint.setColor(mCircleColor);
        canvas.drawCircle(mRightRadius.x, mRightRadius.y, mCircleRadius, mPaint);
        mPaint.setColor(mSymbolColor);
        mPaint.setStrokeWidth(mSymbolWidth);
        //＋号
        canvas.drawLine(mRightRadius.x - mSymbolSize, mRightRadius.y,
                mRightRadius.x + mSymbolSize, mRightRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x, mRightRadius.y - mSymbolSize,
                mRightRadius.x, mRightRadius.y + mSymbolSize, mPaint);
    }

    private void drawStatusExpand(Canvas canvas) {
        //绘制左右环
        mPaint.setColor(mBorderColor);
        mArcRect.left = mLeftRadius.x - mCircleRadius - mBorderWidth;
        mArcRect.top = mLeftRadius.y - mCircleRadius - mBorderWidth;
        mArcRect.right = mRightRadius.x + mCircleRadius + mBorderWidth;
        mArcRect.bottom = mRightRadius.y + mCircleRadius + mBorderWidth;
        canvas.drawRoundRect(mArcRect, 90, 90, mPaint);
        //绘制中间文字背景
        mPaint.setColor(mBgTextView);
        canvas.drawRect(mLeftRadius.x + mCircleRadius, mLeftRadius.y - mCircleRadius,
                mRightRadius.x - mCircleRadius, mRightRadius.y + mCircleRadius, mPaint);
        //绘制左右圆的部分
        mPaint.setColor(mCircleColor);
        canvas.drawCircle(mRightRadius.x, mRightRadius.y, mCircleRadius, mPaint);
        canvas.drawCircle(mLeftRadius.x, mLeftRadius.y, mCircleRadius, mPaint);
        canvas.drawRect(mLeftRadius.x, mLeftRadius.y - mCircleRadius,
                mLeftRadius.x + mCircleRadius, mLeftRadius.y + mCircleRadius, mPaint);
        canvas.drawRect(mRightRadius.x - mCircleRadius, mRightRadius.y - mCircleRadius,
                mRightRadius.x, mRightRadius.y + mCircleRadius, mPaint);
        //绘制"+","-"号
        mPaint.setColor(mSymbolColor);
        mPaint.setStrokeWidth(mSymbolWidth);
        canvas.drawLine(mLeftRadius.x - mSymbolSize, mLeftRadius.y,
                mLeftRadius.x + mSymbolSize, mLeftRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x - mSymbolSize, mRightRadius.y,
                mRightRadius.x + mSymbolSize, mRightRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x, mRightRadius.y - mSymbolSize,
                mRightRadius.x, mRightRadius.y + mSymbolSize, mPaint);
        //绘制文字
        float textLength = mTextPaint.measureText("0");
        canvas.drawText("0", (mWidth - textLength) / 2, mHeight / 2 - mTextBaseLine, mTextPaint);
    }

    private void drawStatusAnimation(Canvas canvas) {
        //绘制左右环
        mPaint.setColor(mBorderColor);
        mArcRect.left = mLeftRadius.x - mCircleRadius - mBorderWidth;
        mArcRect.top = mLeftRadius.y - mCircleRadius - mBorderWidth;
        mArcRect.right = mRightRadius.x + mCircleRadius + mBorderWidth;
        mArcRect.bottom = mRightRadius.y + mCircleRadius + mBorderWidth;
        canvas.drawRoundRect(mArcRect, 90, 90, mPaint);
        //绘制中间文字背景
        mPaint.setColor(mBgTextView);
        canvas.drawRect(mLeftRadius.x + mCircleRadius, mLeftRadius.y - mCircleRadius,
                mRightRadius.x - mCircleRadius, mRightRadius.y + mCircleRadius, mPaint);
        //绘制左右圆的部分
        mPaint.setColor(mCircleColor);
        canvas.drawCircle(mRightRadius.x, mRightRadius.y, mCircleRadius, mPaint);
        canvas.drawCircle(mLeftRadius.x, mLeftRadius.y, mCircleRadius, mPaint);
        canvas.drawRect(mLeftRadius.x, mLeftRadius.y - mCircleRadius,
                mLeftRadius.x + mCircleRadius, mLeftRadius.y + mCircleRadius, mPaint);
        canvas.drawRect(mRightRadius.x - mCircleRadius, mRightRadius.y - mCircleRadius,
                mRightRadius.x, mRightRadius.y + mCircleRadius, mPaint);
        //绘制"+","-"号
        mPaint.setColor(mSymbolColor);
        mPaint.setStrokeWidth(mSymbolWidth);
        canvas.drawLine(mLeftRadius.x - mSymbolSize + mDeltaX, mLeftRadius.y - mDeltaY,
                mLeftRadius.x + mSymbolSize - mDeltaX, mLeftRadius.y + mDeltaY, mPaint);
        canvas.drawLine(mRightRadius.x - mSymbolSize, mRightRadius.y,
                mRightRadius.x + mSymbolSize, mRightRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x, mRightRadius.y - mSymbolSize,
                mRightRadius.x, mRightRadius.y + mSymbolSize, mPaint);
        //绘制文字
        float textLength = mTextPaint.measureText("0");
        canvas.drawText("0", (mWidth - textLength) / 2, mHeight / 2 - mTextBaseLine, mTextPaint);
    }

    private float mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mLastX - event.getX()) >= mTouchSlop
                        || Math.abs(mLastY - event.getY()) >= mTouchSlop) {
                    return false;
                }
                Log.e("event","up==========");
                //左边点击
                if (mLastX >= mLeftRadius.x - mCircleRadius && mLastX <= mLeftRadius.x + mCircleRadius
                        && mLastY > mLeftRadius.y - mCircleRadius && mLastY <= mLeftRadius.y + mCircleRadius) {
                    if (mClickListener != null) {
                        mClickListener.onClick(0);
                    }
                    Log.e("event","左边点击");
                }
                //右边点击
                if (mLastX >= mRightRadius.x - mCircleRadius && mLastX <= mRightRadius.x + mCircleRadius
                        && mLastY > mRightRadius.y - mCircleRadius && mLastY <= mRightRadius.y + mCircleRadius) {
                    if (mClickListener != null) {
                        mClickListener.onClick(2);
                    }
                    expandAnimation();
                    Log.e("event","右边点击");
                }
                //中间点击
                if (mLastX >= mLeftRadius.x + mCircleRadius && mLastX <= mRightRadius.x - mCircleRadius
                        && mLastY > mRightRadius.y - mCircleRadius && mLastY <= mRightRadius.y + mCircleRadius) {
                    if (mClickListener != null) {
                        mClickListener.onClick(1);
                    }
                    Log.e("event","中间点击");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface ClickListener {
        void onClick(int position);
    }
}
