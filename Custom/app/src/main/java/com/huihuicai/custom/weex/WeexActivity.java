package com.huihuicai.custom.weex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huihuicai.custom.R;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXFileUtils;

public class WeexActivity extends AppCompatActivity implements IWXRenderListener {

    private WXSDKInstance wxsdkInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实例化一个instance，并监听到接口
        wxsdkInstance = new WXSDKInstance(this);
        wxsdkInstance.registerRenderListener(this);
        //添加要渲染的js文件
        wxsdkInstance.render("WXSample", WXFileUtils.loadAsset("index.js", this), null, null, WXRenderStrategy.APPEND_ASYNC);
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        setContentView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wxsdkInstance != null) {
            wxsdkInstance.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wxsdkInstance != null) {
            wxsdkInstance.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wxsdkInstance != null) {
            wxsdkInstance.onActivityStop();
        }
    }
}
