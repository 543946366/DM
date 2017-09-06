package com.imotom.dm.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.bean.GetSystemInfoJson;
import com.imotom.dm.ui.NewGuanLiActivity;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;

import okhttp3.Call;

public class GetSystemInfoHandler extends Handler implements Consts{
    private WeakReference<NewGuanLiActivity> myActivity;
    private String displayFriendlyName;
    private String displayModelNumber;
    private String displaySerialNumber;
    private String displayIP;

    public GetSystemInfoHandler(NewGuanLiActivity myActivity,String displayFriendlyName,String displayModelNumber,String displaySerialNumber, String displayIP) {
        this.myActivity = new WeakReference<>(myActivity);
        this.displayFriendlyName = displayFriendlyName;
        this.displayModelNumber = displayModelNumber;
        this.displaySerialNumber = displaySerialNumber;
        this.displayIP = displayIP;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = myActivity.get();
        switch (msg.what) {
            case OK_TEXT:
                // 在这里可以进行UI操作


                try {
                    Logger.e(msg.obj.toString());
                    DeviceOffLine deviceOffLine = new DeviceOffLine();
                    StringBuilder systemInfo = new StringBuilder();

                    int jsonSize = msg.obj.toString().indexOf("{");
                    String jsonContent ;
                    if(jsonSize == 0){
                        jsonContent = msg.obj.toString();
                    }else {
                        jsonContent = msg.obj.toString().substring(jsonSize);
                    }

                    GetSystemInfoJson getSystemInfoJson = new GsonBuilder().create().fromJson(jsonContent, GetSystemInfoJson.class);
                    if (!getSystemInfoJson.getSn().isEmpty()) {
                        systemInfo.append("序列号：").append(getSystemInfoJson.getSn()).append("\n");
                        deviceOffLine.setDevice_serial_number(getSystemInfoJson.getSn());
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

                        if(displayModelNumber.equals(MT_guoKe_model_number)){
                            guoKeRuanJianBaoShengJiChaXun(getSystemInfoJson.getSwid());
                        }else if(displayModelNumber.equals(MT_cheJi_model_number)){
                            cheJiRuanJianBaoChaXun();
                        }

                    }
                    if (getSystemInfoJson.getStm32_ver() != null && !getSystemInfoJson.getStm32_ver().isEmpty()) {
                        systemInfo.append("单片机版本：").append(getSystemInfoJson.getStm32_ver()).append("\n");
                        deviceOffLine.setDevice_cheji_stm32ver(getSystemInfoJson.getStm32_ver());
                    }

                    Logger.d(systemInfo.toString());
                    if(!systemInfo.toString().isEmpty()) {
                        activity.tvNewGuanLiDevInfo.setText(systemInfo.toString());
                    }

                    deviceOffLine.setDevice_friendly_name(displayFriendlyName);
                    deviceOffLine.setDevice_model_number(displayModelNumber);
                    deviceOffLine.setDevice_url(displayIP);
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

    //国科升级软件包查询
    private void guoKeRuanJianBaoShengJiChaXun(final String dangQianBanBenName) {
        NewGuanLiActivity activity = myActivity.get();
        //String url = "http://120.27.94.20:10080/vendor/imotom/MT1828/upgrade.txt";
        OkHttpUtils
                .get()
                .url(URL_inquire_guoKe_dev_update_doc)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("TAG", e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("TAG", response);
                        //String a = "AAA6";
                        for (String str : response.split(";")) {
                            Log.d("TAG", "当前坂本为：" + str.split(":")[0].trim());
                            if (str.split(":")[0].trim().equals(dangQianBanBenName)) {
                                Log.d("TAG", "下个升级的坂本为：" + str.split(":")[1]);
                                if (str.split(":")[1].equals("new")) {
                                    activity.tvNewGuanLiDevVersion.setText("当前已是最新版本，无需升级！");
                                } else {
                                    activity.tvNewGuanLiDevVersion.setText("有新版本：" + str.split(":")[1] + "\n请尽快联系4S店或者厂家升级！");
                                }
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                activity.tvNewGuanLiDevVersion.setText("当前已是最新版本，无需升级！");
                            }
                        }

                    }
                });
    }


    //车机升级软件包查询
    private void cheJiRuanJianBaoChaXun() {
        NewGuanLiActivity activity = myActivity.get();
        //String url = "http://120.27.94.20:10080/vendor/imotom/MT1845/upgrade.txt";
        OkHttpUtils
                .get()
                .url(URL_inquire_cheJi_dev_update_doc)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("TAG", e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("TAG", response);
                        activity.tvNewGuanLiDevVersion.setText(String.valueOf("最新固件包日期为："+response + "\n如需要更新，请联系4S店或者厂家！"));

                    }
                });
    }
}
