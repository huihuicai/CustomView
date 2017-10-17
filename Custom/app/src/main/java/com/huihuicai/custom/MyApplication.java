package com.huihuicai.custom;

import android.app.Application;

import com.huihuicai.custom.weex.WXImageAdapter;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;

/**
 * Created by ybm on 2017/9/13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InitConfig config = new InitConfig.Builder().setImgAdapter(new WXImageAdapter(this)).build();
        WXSDKEngine.initialize(this, config);
    }
}
