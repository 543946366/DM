package com.imotom.dm.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.os.Handler;
import android.os.Message;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.ui.NewGuanLiActivity;
import com.imotom.dm.utils.SPUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class GetWifiPasswordHandler extends Handler implements Consts{

    private WeakReference<NewGuanLiActivity> myActivity;

    public GetWifiPasswordHandler(NewGuanLiActivity myActivity) {
        this.myActivity = new WeakReference<>(myActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = myActivity.get();
        switch (msg.what) {
            case OK_TEXT:
// 在这里可以进行UI操作
                String WIFIpassword = msg.obj.toString().split(":")[1].substring(1, msg.obj.toString().split(":")[1].length() - 2);

                activity.tvNewGuanLiShowPassword.setText(String.valueOf(WIFIpassword));
                if (SPUtils.contains(activity, "WIFI密码")) {
                    SPUtils.remove(activity, "WIFI密码");
                }
                SPUtils.put(activity, "WIFI密码", WIFIpassword);
                Logger.d("4444444444");
                break;

            case NO_TEXT:
                activity.tvNewGuanLiShowPassword.setText("获取失败，请重试！");
                Logger.d("获取失败，请重试！");
                break;
            default:
                break;
        }

    }
}
