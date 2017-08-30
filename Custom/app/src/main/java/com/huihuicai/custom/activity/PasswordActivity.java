package com.huihuicai.custom.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.huihuicai.custom.R;
import com.huihuicai.custom.view.FreshLine;

public class PasswordActivity extends AppCompatActivity {

    private FreshLine flLine;
    private float mLastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        flLine = (FreshLine) findViewById(R.id.fl_line);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                flLine.setProgress(0, true);
                Log.e("onTouchEvent", "ACTION_DOWN========");
                return true;
            case MotionEvent.ACTION_MOVE:
                flLine.setProgress(event.getY() - mLastY, false);
                Log.e("onTouchEvent", "ACTION_MOVE========");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                flLine.setProgress(event.getY() - mLastY, true);
                Log.e("onTouchEvent", "ACTION_CANCEL========");
                break;
        }
        return super.onTouchEvent(event);
    }
}
