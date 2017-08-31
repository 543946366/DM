package com.imotom.dm.bean;
/*
 * Created by ZhiPeng Huang on 2017-06-06.
 */

public class GetSystemInfoJson {

    /**
     * sn : MT1845000003
     * hwid : MT1845V11
     * swid : 1.0.0.1
     * mac : 3c:33:00:00:00:03
     * stm32_ver : 2.0
     */

    private String sn;
    private String hwid;
    private String swid;
    private String mac;
    private String stm32_ver;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    public String getSwid() {
        return swid;
    }

    public void setSwid(String swid) {
        this.swid = swid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStm32_ver() {
        return stm32_ver;
    }

    public void setStm32_ver(String stm32_ver) {
        this.stm32_ver = stm32_ver;
    }
}
