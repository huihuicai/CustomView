package com.huihuicai.custom.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huihuicai.custom.R;

/**
 * Created by ybm on 2017/8/11.
 */

public class ShopView extends View {

    private final int STATUS_PACK = 0;
    private final int STATUS_EXPAND = 1;
    private final int STATUS_ANIMATION = 2;

    public static final int CLICK_LEFT = 0;
    public static final int CLICK_MIDDLE = 1;
    public static final int CLICK_RIGHT = 2;

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
    private int mNumber, mMaxNumber;
    private RectF mArcRect = new RectF();
    private Paint mPaint, mClearPaint;
    private TextPaint mTextPaint;
    private Xfermode mClearMode, mSrcMode;
    private String mText;
    private float mTouchSlop;
    private ValueAnimator mAnimation;
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
        //要关闭硬件加速，不然动画绘制的时候可能有重影，清屏也需要关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.ShopView);
        mCircleRadius = ta.getDimensionPixelOffset(R.styleable.ShopView_radius, 40);
        mBorderWidth = ta.getDimensionPixelSize(R.styleable.ShopView_circle_width, 4);
        mSymbolWidth = ta.getDimensionPixelSize(R.styleable.ShopView_symbol_width, 4);
        mTextWidth = ta.getDimensionPixelSize(R.styleable.ShopView_text_width, 200);
        mCircleColor = ta.getColor(R.styleable.ShopView_circle_color, Color.WHITE);
        mBorderColor = ta.getColor(R.styleable.ShopView_border_color, Color.GREEN);
        mSymbolColor = ta.getColor(R.styleable.ShopView_symbol_color, Color.DKGRAY);
        mTextColor = ta.getColor(R.styleable.ShopView_text_color, Color.BLACK);
        mBgTextView = ta.getColor(R.styleable.ShopView_text_background, Color.RED);
        mCurrentStatus = ta.getInt(R.styleable.ShopView_mode, STATUS_PACK);
        ta.recycle();
        mSymbolSize = mCircleRadius * 0.5f;
        mClearPaint = new Paint();
        mClearPaint.setColor(0xffffffff);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(34);
        mTextPaint.setColor(mTextColor);

        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mSrcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextBaseLine = (fm.ascent + fm.descent) / 2;

        mText = "0";
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    public void initData(int number, int maxNumber) {
        mNumber = number;
        mMaxNumber = maxNumber;
        mText = String.valueOf(number);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);
        switch (modeW) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                sizeW = getPaddingLeft() + getPaddingRight() + 2 * (mCircleRadius + mBorderWidth) + mTextWidth;
                if (mTextWidth == 0) {
                    mTextWidth = sizeW - getPaddingLeft() - getPaddingRight() - 4 * (mCircleRadius + mBorderWidth);
                }
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

    private void showAnimation() {
        if (isAnimation()) {
            return;
        }
        final int preStatus = mCurrentStatus;
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mCurrentStatus = STATUS_ANIMATION;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentStatus = preStatus == STATUS_PACK ? STATUS_EXPAND : STATUS_PACK;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float angle = (float) animation.getAnimatedValue();
                mDeltaX = (int) (mSymbolSize * Math.cos(angle * Math.PI / 180f));
                mDeltaY = (int) (mSymbolSize * Math.sin(angle * Math.PI / 180f));
                if (preStatus == STATUS_PACK) {
                    mLeftRadius.x = (int) (mRightRadius.x - angle / 720f * mCircleGap);
                } else if (preStatus == STATUS_EXPAND) {
                    mLeftRadius.x = (int) (mRightRadius.x - (1 - angle / 720f) * mCircleGap);
                }
                invalidate();
            }
        });
        mAnimation.setDuration(1000);
        mAnimation.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        mClearPaint.setXfermode(mClearMode);
//        canvas.drawPaint(mClearPaint);
//        mClearPaint.setXfermode(mSrcMode);
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
        canvas.drawLine(mRightRadius.x - mSymbolSize, mRightRadius.y,
                mRightRadius.x + mSymbolSize, mRightRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x, mRightRadius.y - mSymbolSize,
                mRightRadius.x, mRightRadius.y + mSymbolSize, mPaint);
    }

    //绘制左右环
    private void drawCircle(Canvas canvas) {
        mPaint.setColor(mBorderColor);
        mArcRect.left = mLeftRadius.x - mCircleRadius - mBorderWidth;
        mArcRect.top = mLeftRadius.y - mCircleRadius - mBorderWidth;
        mArcRect.right = mRightRadius.x + mCircleRadius + mBorderWidth;
        mArcRect.bottom = mRightRadius.y + mCircleRadius + mBorderWidth;
        canvas.drawRoundRect(mArcRect, 90, 90, mPaint);
    }

    //文字背景
    private void drawBgText(Canvas canvas) {
        mPaint.setColor(mBgTextView);
        if (mRightRadius.x - mLeftRadius.x > mCircleRadius) {
            canvas.drawRect(mLeftRadius.x + mCircleRadius, mLeftRadius.y - mCircleRadius,
                    mRightRadius.x - mCircleRadius, mRightRadius.y + mCircleRadius, mPaint);
        }
    }

    //绘制左右圆的部分
    private void drawShapeCircle(Canvas canvas) {
        mPaint.setColor(mCircleColor);
        canvas.drawCircle(mRightRadius.x, mRightRadius.y, mCircleRadius, mPaint);
        canvas.drawCircle(mLeftRadius.x, mLeftRadius.y, mCircleRadius, mPaint);
        if (mRightRadius.x - mLeftRadius.x > mCircleRadius) {
            canvas.drawRect(mLeftRadius.x, mLeftRadius.y - mCircleRadius,
                    mLeftRadius.x + mCircleRadius, mLeftRadius.y + mCircleRadius, mPaint);
            canvas.drawRect(mRightRadius.x - mCircleRadius, mRightRadius.y - mCircleRadius,
                    mRightRadius.x, mRightRadius.y + mCircleRadius, mPaint);
        }
    }

    //绘制文字
    private void drawText(Canvas canvas) {
        float textLength = mTextPaint.measureText(mText);
        if (mRightRadius.x - mLeftRadius.x - 2 * mCircleRadius >= textLength) {
            canvas.drawText(mText, (mLeftRadius.x + mRightRadius.x - textLength) / 2, mHeight / 2 - mTextBaseLine, mTextPaint);
        }
    }

    private void drawStatusExpand(Canvas canvas) {
        //绘制左右环
        drawCircle(canvas);
        //绘制中间文字背景
        drawBgText(canvas);
        //绘制文字
        drawText(canvas);
        //绘制左右圆的部分
        drawShapeCircle(canvas);
        //绘制"+","-"号
        mPaint.setColor(mSymbolColor);
        mPaint.setStrokeWidth(mSymbolWidth);
        canvas.drawLine(mLeftRadius.x - mSymbolSize, mLeftRadius.y,
                mLeftRadius.x + mSymbolSize, mLeftRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x - mSymbolSize, mRightRadius.y,
                mRightRadius.x + mSymbolSize, mRightRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x, mRightRadius.y - mSymbolSize,
                mRightRadius.x, mRightRadius.y + mSymbolSize, mPaint);

    }

    private void drawStatusAnimation(Canvas canvas) {
        //绘制左右环
        drawCircle(canvas);
        //绘制中间文字背景
        drawBgText(canvas);
        //绘制文字
        drawText(canvas);
        //绘制左右圆的部分
        drawShapeCircle(canvas);
        //绘制"+","-"号
        mPaint.setColor(mSymbolColor);
        mPaint.setStrokeWidth(mSymbolWidth);
        canvas.drawLine(mLeftRadius.x - mDeltaX, mLeftRadius.y - mDeltaY,
                mLeftRadius.x + mDeltaX, mLeftRadius.y + mDeltaY, mPaint);
        canvas.drawLine(mRightRadius.x - mSymbolSize, mRightRadius.y,
                mRightRadius.x + mSymbolSize, mRightRadius.y, mPaint);
        canvas.drawLine(mRightRadius.x, mRightRadius.y - mSymbolSize,
                mRightRadius.x, mRightRadius.y + mSymbolSize, mPaint);
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
                //左边点击
                if (mCurrentStatus == STATUS_EXPAND && !isAnimation()
                        && mLastX >= mLeftRadius.x - mCircleRadius && mLastX <= mLeftRadius.x + mCircleRadius
                        && mLastY > mLeftRadius.y - mCircleRadius && mLastY <= mLeftRadius.y + mCircleRadius) {
                    if (mClickListener != null) {
                        mClickListener.onClick(CLICK_LEFT);
                    }
                    if (mNumber == 0) {
                        showAnimation();
                    } else {
                        mNumber--;
                        invalidate();
                    }
                    mText = String.valueOf(mNumber);
                }
                //右边点击
                if (mLastX >= mRightRadius.x - mCircleRadius && mLastX <= mRightRadius.x + mCircleRadius
                        && mLastY > mRightRadius.y - mCircleRadius && mLastY <= mRightRadius.y + mCircleRadius) {
                    if (mClickListener != null) {
                        mClickListener.onClick(CLICK_RIGHT);
                    }
                    if (mNumber == 0 || mCurrentStatus == STATUS_PACK) {
                        mNumber++;
                        showAnimation();
                    } else {
                        mNumber++;
                        invalidate();
                    }
                    mText = String.valueOf(mNumber);
                }
                //中间点击
                if (mCurrentStatus == STATUS_EXPAND && !isAnimation()
                        && mLastX >= mLeftRadius.x + mCircleRadius && mLastX <= mRightRadius.x - mCircleRadius
                        && mLastY > mRightRadius.y - mCircleRadius && mLastY <= mRightRadius.y + mCircleRadius) {
                    if (mClickListener != null) {
                        mClickListener.onClick(CLICK_MIDDLE);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isAnimation() {
        if (mAnimation == null) {
            mAnimation = ValueAnimator.ofFloat(0, 720f);
        }
        return mAnimation.isRunning();
    }

    public Point getPoint() {
        int[] start = new int[2];
        getLocationInWindow(start);
        Point startPoint = new Point();
        startPoint.x = start[0] + getPaddingLeft() + mRightRadius.x - mCircleRadius;
        startPoint.y = start[1] + getPaddingTop() + mRightRadius.y - mCircleRadius;
        return startPoint;
    }

    public interface ClickListener {
        void onClick(int position);
    }
}
