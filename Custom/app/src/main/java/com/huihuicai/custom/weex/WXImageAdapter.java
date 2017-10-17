package com.huihuicai.custom.weex;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

/**
 * Created by ybm on 2017/10/13.
 */

public class WXImageAdapter implements IWXImgLoaderAdapter {

    private Context mContext;

    public WXImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void setImage(final String url, final ImageView view, WXImageQuality quality, WXImageStrategy strategy) {
        //实现imageLoader接口,注意一定要添加网络请求的权限
        if (!TextUtils.isEmpty(url) && view != null) {
            WXSDKManager.getInstance().postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (view.getLayoutParams().width < 0
                            || view.getLayoutParams().height < 0) {
                        return;
                    }
                    Glide.with(mContext).load(url).into(view);
                }
            }, 0);
        }
    }
}
