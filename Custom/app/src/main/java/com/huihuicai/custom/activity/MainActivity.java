package com.huihuicai.custom.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.huihuicai.custom.R;
import com.huihuicai.custom.weex.WeexActivity;

import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printProperties();
    }

    private void printProperties() {
        Properties properties = System.getProperties();
        if (properties != null) {
            for (Object o : properties.keySet()) {
                Log.e("property", o + "==" + properties.get(o));
            }
            String test = "asssss";
            boolean index = test.contains("a");
            if (index) {
                test.toCharArray();
                char position = test.charAt(0);
                test.indexOf(position, 1);
            }
        }
    }

    public void onShop(View view) {
        startActivity(new Intent(this, ShopActivity.class));
    }

    public void onWave(View view) {
        startActivity(new Intent(this, WaveActivity.class));
    }

    public void onPwd(View view) {
        startActivity(new Intent(this, PasswordActivity.class));
    }

    public void springAnim(View view) {
        startActivity(new Intent(this, SpringAnimActivity.class));
    }

    public void onCoordinate(View view) {
        startActivity(new Intent(this, CoordinatedActivity.class));
    }

    public void onWeex(View view) {
        startActivity(new Intent(this, WeexActivity.class));
    }
}
