package com.huihuicai.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.huihuicai.custom.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ybm on 2017/8/16.
 */

/**
 * 1.需要定义几个框（总宽÷个数）
 * 2.在输入的时候，密码可见与不可见时的样式
 * 3.删除密码，样式变化
 */
public class PasswordView extends View {

    private int mWidth, mHeight;
    private InputMethodManager mInputManager;

    private float mBorderSize;
    private float mBorderGap;
    private float mBorderRadius;
    private int mBorderCount;

    private int mFillColor;
    private int mNullColor;
    private int mTextColor;
    private int mShowMode;
    private int mBorderMargin;
    private int mBorderStartH;

    private Paint mPaint;
    private TextPaint mTextPaint;
    private RectF mBorderRect;
    private float mTextBaseLine;
    private float mTextWidth;
    private List<String> mText;


    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOnKeyListener(new NumKeyListener());
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);
        mBorderRadius = ta.getDimension(R.styleable.PasswordView_border_radius, 6);
        mBorderSize = ta.getDimension(R.styleable.PasswordView_border_size, 100);
        mBorderCount = ta.getDimensionPixelOffset(R.styleable.PasswordView_border_count, 4);
        mBorderMargin = ta.getDimensionPixelOffset(R.styleable.PasswordView_border_margin, 20);
        mFillColor = ta.getColor(R.styleable.PasswordView_fill_color, Color.BLUE);
        mNullColor = ta.getColor(R.styleable.PasswordView_null_color, Color.GRAY);
        mTextColor = ta.getColor(R.styleable.PasswordView_text_color, Color.BLACK);
        mShowMode = ta.getInt(R.styleable.PasswordView_PWD, 0);
        ta.recycle();

        mText = new ArrayList<>(mBorderCount);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mNullColor);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(30);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextBaseLine = (fm.ascent + fm.descent) / 2;
        mTextWidth = mTextPaint.measureText("8");

        mBorderRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

        if (modeH == MeasureSpec.AT_MOST || modeH == MeasureSpec.UNSPECIFIED) {
            sizeH = (int) (getPaddingTop() + getPaddingBottom() + mBorderSize + 2 * mBorderMargin);
        }

        if (modeW == MeasureSpec.AT_MOST || modeW == MeasureSpec.UNSPECIFIED) {
            mBorderGap = (sizeW - mBorderSize * mBorderCount) / (mBorderCount + 1);
        }

        setMeasuredDimension(sizeW, sizeH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (mWidth < mBorderSize * mBorderCount) {
            mBorderGap = 0;
            mBorderSize = mWidth / mBorderCount;
        } else {
            mBorderGap = (w - mBorderSize * mBorderCount) / (mBorderCount + 1);
        }
        mBorderStartH = getPaddingTop() + mBorderMargin;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float middleH = mBorderStartH + mBorderSize / 2;
        float middleW;
        for (int i = 0; i < mBorderCount; i++) {
            middleW = (i + 1) * mBorderGap + i * mBorderSize / 2;
            mBorderRect.left = (i + 1) * mBorderGap + i * mBorderSize;
            mBorderRect.top = mBorderStartH;
            mBorderRect.right = (i + 1) * (mBorderGap + mBorderSize);
            mBorderRect.bottom = mBorderStartH + mBorderSize;
            mPaint.setColor(mText.size() > i && !TextUtils.isEmpty(mText.get(i)) ? mFillColor : mNullColor);
            canvas.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, mPaint);

            mBorderRect.left = (i + 1) * mBorderGap + i * mBorderSize + 2;
            mBorderRect.top = mBorderStartH + 2;
            mBorderRect.right = (i + 1) * (mBorderGap + mBorderSize) - 2;
            mBorderRect.bottom = mBorderStartH + mBorderSize - 2;
            mPaint.setColor(Color.WHITE);
            canvas.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, mPaint);
            if (mText.size() > i && !TextUtils.isEmpty(mText.get(i))) {
                canvas.drawText(mText.get(i), middleW - mTextWidth / 2, middleH - mTextBaseLine, mTextPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            requestFocus();
            showKeyBoard();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void showKeyBoard() {
        if (mInputManager == null) {
            mInputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mInputManager.showSoftInput(this, InputMethodManager.SHOW_FORCED);
    }

    private void hideKeyBoard() {
        if (mInputManager == null) {
            return;
        }
        mInputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        outAttrs.inputType = EditorInfo.TYPE_CLASS_NUMBER;
        return new NumInputConnection(this, false);
    }

    private class NumInputConnection extends BaseInputConnection {

        public NumInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    private class NumKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_0) {
                    if (mText.size() == mBorderCount) {
                        return true;
                    }
                    mText.add(String.valueOf(keyCode - 7));
                    invalidate();
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!mText.isEmpty()) {
                        mText.remove(mText.size() - 1);
                        invalidate();
                    }
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    //收键盘
                    hideKeyBoard();
                    return true;
                }
            }

            return false;
        }
    }
}
