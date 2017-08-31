package com.imotom.dm.bean;

import org.litepal.crud.DataSupport;

/*
 * Created by ZhiPeng Huang on 2017-05-13.
 */

public class DeviceOffLine extends DataSupport{
    //设备的唯一标识为设备的model number + 序列号
    private String device_model_number_add_serial_number;
    //设备序列号
    private String device_serial_number;
    //区分不同种类设备的标识
    private String device_model_number;
    //设备别名
    private String device_friendly_name;
    //设备硬件号
    private String device_hwid;
    //设备MAc地址
    private String device_Mac;
    //设备软件版本
    private String device_swid;
    //设备国科设备当前版本名
    private String device_guoke_version_number;
    //车机设备系统文件版本号
    private String device_cheji_Swid;
    //车机设备单片机版本号
    private String device_cheji_stm32ver;
    //车机设备URL
    private String device_url;

    public String getDevice_serial_number() {
        return device_serial_number;
    }

    public void setDevice_serial_number(String device_serial_number) {
        this.device_serial_number = device_serial_number;
    }

    public String getDevice_url() {
        return device_url;
    }

    public void setDevice_url(String device_url) {
        this.device_url = device_url;
    }

    public String getDevice_cheji_Swid() {
        return device_cheji_Swid;
    }

    public void setDevice_cheji_Swid(String device_cheji_Swid) {
        this.device_cheji_Swid = device_cheji_Swid;
    }

    public String getDevice_cheji_stm32ver() {
        return device_cheji_stm32ver;
    }

    public void setDevice_cheji_stm32ver(String device_cheji_stm32ver) {
        this.device_cheji_stm32ver = device_cheji_stm32ver;
    }

    public String getDevice_guoke_version_number() {
        return device_guoke_version_number;
    }

    public void setDevice_guoke_version_number(String device_guoke_version_number) {
        this.device_guoke_version_number = device_guoke_version_number;
    }

    public String getDevice_model_number_add_serial_number() {
        return device_model_number_add_serial_number;
    }

    public void setDevice_model_number_add_serial_number(String device_model_number_add_serial_number) {
        this.device_model_number_add_serial_number = device_model_number_add_serial_number;
    }

    public String getDevice_model_number() {
        return device_model_number;
    }

    public void setDevice_model_number(String device_model_number) {
        this.device_model_number = device_model_number;
    }

    public String getDevice_friendly_name() {
        return device_friendly_name;
    }

    public void setDevice_friendly_name(String device_friendly_name) {
        this.device_friendly_name = device_friendly_name;
    }


    public String getDevice_hwid() {
        return device_hwid;
    }

    public void setDevice_hwid(String device_hwid) {
        this.device_hwid = device_hwid;
    }

    public String getDevice_Mac() {
        return device_Mac;
    }

    public void setDevice_Mac(String device_Mac) {
        this.device_Mac = device_Mac;
    }

    public String getDevice_swid() {
        return device_swid;
    }

    public void setDevice_swid(String device_swid) {
        this.device_swid = device_swid;
    }
}
