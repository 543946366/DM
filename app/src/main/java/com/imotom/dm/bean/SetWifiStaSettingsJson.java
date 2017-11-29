package com.imotom.dm.bean;
/*
 * Created by HZP on 2017/11/22.
 */

public class SetWifiStaSettingsJson {

    /**
     * ssid : IMOTOM_502
     * password : 56+78=134
     */

    private String ssid;
    private String password;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
