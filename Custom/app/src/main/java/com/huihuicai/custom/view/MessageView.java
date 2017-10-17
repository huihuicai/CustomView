package com.huihuicai.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.huihuicai.custom.R;

/**
 * Created by ybm on 2017/9/22.
 */

public class MessageView extends View {
    private Paint mPaint;
    private TextPaint mTextPaint;

    private int mWidth, mHeight;
    private int mResIcon, mResText;
    private String mText;


    public MessageView(Context context) {
        this(context, null);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        //通过xml获得属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MessageView);
        ta.recycle();
        //初始化paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
