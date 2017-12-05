package com.imotom.dm.model.imp;
/*
 * Created by HZP on 2017/12/4.
 */


import com.imotom.dm.model.OnSubmitListener;

public interface ISetWifiStaSettingsModel {
    void submit(String ssid, String ssidPassword, String url, OnSubmitListener onSubmitListener);
    void cancleTasks();
}
