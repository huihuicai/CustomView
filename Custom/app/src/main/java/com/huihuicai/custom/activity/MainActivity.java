package com.huihuicai.custom.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huihuicai.custom.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
