package com.imotom.dm.view.imp;
/*
 * Created by HZP on 2017/12/4.
 */

public interface ISetWifiStaSettingsView {
    void showLoading();
    void closeLoading();
    void showSuccess();
    void showFailed();
    String getSsid();
    String getSsidPassword();
    String getDevIP();
}
