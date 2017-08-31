package com.imotom.dm.bean;
/*
 * Created by ZhiPeng Huang on 2017-08-30.
 */

import org.litepal.crud.DataSupport;

public class DeviceSupportAppData extends DataSupport {
    //设备的唯一标识为设备的model number + 序列号
    private String device_model_number_add_serial_number;
    //设备支持的app信息
    private String device_support_app_infoResponse;

    public String getDevice_model_number_add_serial_number() {
        return device_model_number_add_serial_number;
    }

    public void setDevice_model_number_add_serial_number(String device_model_number_add_serial_number) {
        this.device_model_number_add_serial_number = device_model_number_add_serial_number;
    }

    public String getDevice_support_app_infoResponse() {
        return device_support_app_infoResponse;
    }

    public void setDevice_support_app_infoResponse(String device_support_app_infoResponse) {
        this.device_support_app_infoResponse = device_support_app_infoResponse;
    }
}
