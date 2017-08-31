package com.imotom.dm.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.bean.GetSystemInfoJson;
import com.imotom.dm.ui.NewGuanLiActivity;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class GetSystemInfoHandler extends Handler implements Consts{
    private WeakReference<NewGuanLiActivity> myActivity;
    private String displayFriendlyName;
    private String displayModelNumber;
    private String displaySerialNumber;

    public GetSystemInfoHandler(NewGuanLiActivity myActivity,String displayFriendlyName,String displayModelNumber,String displaySerialNumber) {
        this.myActivity = new WeakReference<>(myActivity);
        this.displayFriendlyName = displayFriendlyName;
        this.displayModelNumber = displayModelNumber;
        this.displaySerialNumber = displaySerialNumber;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = myActivity.get();
        switch (msg.what) {
            case OK_TEXT:
                // 在这里可以进行UI操作


                try {
                    DeviceOffLine deviceOffLine = new DeviceOffLine();
                    StringBuilder systemInfo = new StringBuilder();
                    GetSystemInfoJson getSystemInfoJson = new GsonBuilder().create().fromJson(String.valueOf(msg.obj.toString().substring(4)), GetSystemInfoJson.class);
                    if (!getSystemInfoJson.getSn().isEmpty()) {
                        systemInfo.append("序列号：").append(getSystemInfoJson.getSn()).append("\n");
                    }
                    if (!getSystemInfoJson.getHwid().isEmpty()) {
                        systemInfo.append("硬件号：").append(getSystemInfoJson.getHwid()).append("\n");
                        deviceOffLine.setDevice_hwid(getSystemInfoJson.getHwid());
                    }

                    if (!getSystemInfoJson.getMac().isEmpty()) {
                        systemInfo.append("Mac地址：").append(getSystemInfoJson.getMac()).append("\n");
                        deviceOffLine.setDevice_Mac(getSystemInfoJson.getMac());
                    }
                    if (!getSystemInfoJson.getSwid().isEmpty()) {
                        systemInfo.append("软件号：").append(getSystemInfoJson.getSwid()).append("\n");
                        deviceOffLine.setDevice_swid(getSystemInfoJson.getSwid());
                        Logger.d(getSystemInfoJson.getSwid());
                    }
                    if (!getSystemInfoJson.getStm32_ver().isEmpty()) {
                        systemInfo.append("单片机版本：").append(getSystemInfoJson.getStm32_ver()).append("\n");
                        deviceOffLine.setDevice_cheji_stm32ver(getSystemInfoJson.getStm32_ver());
                    }

                    Logger.d(systemInfo.toString());
                    if(!systemInfo.toString().isEmpty()) {
                        activity.tvNewGuanLiDevInfo.setText(systemInfo.toString());
                    }

                    deviceOffLine.setDevice_friendly_name(displayFriendlyName);
                    deviceOffLine.setDevice_model_number(displayModelNumber);
                    deviceOffLine.setDevice_model_number_add_serial_number(displayModelNumber+displaySerialNumber);
                    deviceOffLine.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",deviceOffLine.getDevice_model_number_add_serial_number());

                } catch (Exception e) {
                    Logger.d(e.getMessage());
                }

                break;

            case NO_TEXT:
                Toast.makeText(activity, "获取主板信息失败。", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }
}
