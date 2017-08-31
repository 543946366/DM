package com.imotom.dm.handler;
/*
 * Created by ZhiPeng Huang on 2017-08-24.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.adapter.SupportAppAdapter;
import com.imotom.dm.bean.CapabilityJson;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.bean.DeviceSupportAppData;
import com.imotom.dm.bean.GetSupportAppJson;
import com.imotom.dm.ui.DownloadAppActivity;
import com.imotom.dm.ui.NewGuanLiActivity;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.FileUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;

public class GetCapabilityHandler extends Handler implements Consts{

    private WeakReference<NewGuanLiActivity> myActivity;
    private String displayModelNumber;
    private String displaySerialNumber;
    private String deviceIP;
    private GetSupportAppJson getSupportAppJson;

    public GetCapabilityHandler(NewGuanLiActivity myActivity,String displayModelNumber,String displaySerialNumber, String deviceIP) {
        this.myActivity = new WeakReference<>(myActivity);
        this.displayModelNumber = displayModelNumber;
        this.displaySerialNumber = displaySerialNumber;
        this.deviceIP = deviceIP;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        NewGuanLiActivity activity = myActivity.get();
        switch (msg.what) {
            case OK_TEXT:
                // 在这里可以进行UI操作
                // new DownloadTask().execute();
                try {
                    String fuWuList = "";

                    CapabilityJson capabilityJson = new GsonBuilder().create().fromJson(String.valueOf(msg.obj.toString().substring(4)), CapabilityJson.class);
                    for (String t : capabilityJson.getCapability()) {
                        Log.d("TAG", t);
                        fuWuList = fuWuList.concat(t);
                    }

                    Logger.d(fuWuList);
                    if (fuWuList.contains("get_version")) {
                        activity.cvNewGuanLiDevInfo.setVisibility(View.VISIBLE);
                        Logger.d("=========");
                        DigestAuthenticationUtil.startDigest("http://192.168.43.1:8199/get_system_info", activity.getSystemInfoHandler, "/get_system_info");
                        Logger.d("=========");
                    }
                    if (fuWuList.contains("wifi_pwd_retrieve")) {
                        activity.cvNewGuanLiWifiPassword.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> DigestAuthenticationUtil.startDigest("http://192.168.43.1:8199/wifi_pwd_retrieve",activity.getWifiPasswordHandler,"/wifi_pwd_retrieve"),1000);
                        }
                    if (fuWuList.contains("wifi_pwd_update")) {
                        activity.cvNewGuanLiChangePassword.setVisibility(View.VISIBLE);
                    }
                    if (fuWuList.contains("update_time")) {
                        //TODO 国科设备专用的服务，如果有则提供修改设备时间界面
                    }
                    checkSupportApp(activity);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                break;

            case NO_TEXT:
                break;

            default:
                break;
        }
    }

    private void checkSupportApp(NewGuanLiActivity activity) {
        Logger.e("start" + displayModelNumber + displaySerialNumber);
        List<DeviceOffLine> deviceOffLineList = DataSupport
                .where(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER + "=?",
                        displayModelNumber + displaySerialNumber).find(DeviceOffLine.class);
        DeviceOffLine deviceOffLine = deviceOffLineList.get(0);
        Logger.e(deviceOffLine.getDevice_model_number_add_serial_number());
        Logger.d(deviceOffLineList.size() + "_______________"
                + deviceOffLine.getDevice_swid() + "____"
                + displaySerialNumber + displaySerialNumber);

        Logger.e(URL_check_dev_supportAPP_baseURL + displayModelNumber + "/" + "CAA9AppList.txt");
        //OkHttpUtils.get().url(URL_check_dev_supportAPP_baseURL + displayModelNumber + "/" + "CAA9AppList.txt").build().execute(new StringCallback() {
        OkHttpUtils.get().url(URL_check_dev_supportAPP_baseURL + displayModelNumber + "/" + deviceOffLine.getDevice_swid() + "AppList.txt").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.e(e.getMessage());

            }

            @Override
            public void onResponse(String response, int id) {
               /* String re = "{\n" +
                        "    \"support_app\": [\n" +
                        "        {\n" +
                        "            \"name\": \"GKDVR\",\n" +
                        "            \"android_download_url\": \"http://shouji.360tpcdn.com/170208/c876925ccfa83020f6a8bb4703b93a02/zxc.com.gkdvr_21.apk\",\n" +
                        "            \"package\": \"zxc.com.gkdvr\",\n" +
                        "            \"ios_download_url\": \"https://itunes.apple.com/cn/app/gkdvr/id1127795700?mt=8\",\n" +
                        "            \"url_schema\": \"QQ41e4efec://\"\n" +
                        "        },\n" +
                        "{\n" +
                        "\"name\":\"aaa\"\n" +
                        "},\n" +
                        "{\n" +
                        "\"name\":\"bbb\"\n" +
                        "},\n" +
                        "{\n" +
                        "\"name\":\"ccc\"\n" +
                        "},\n" +
                        "{\n" +
                        "\"name\":\"ddd\"\n" +
                        "},\n" +
                        "{\n" +
                        "\"name\":\"eee\"\n" +
                        "},\n" +
                        "{\n" +
                        "\"name\":\"fff\"\n" +
                        "}\n" +
                        "    ]\n" +
                        "}";*/

                getSupportAppJson = new GsonBuilder().create().fromJson(response, GetSupportAppJson.class);
                List<GetSupportAppJson.SupportAppBean> supportAppBeanList = getSupportAppJson.getSupport_app();
                /*for (GetSupportAppJson.SupportAppBean bean : supportAppBeanList) {
                    bean.getName();
                }*/
                SupportAppAdapter adapter = new SupportAppAdapter(supportAppBeanList);
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Logger.e(supportAppBeanList.get(position).getName());

                        clickApp(supportAppBeanList.get(position),activity);
                    }
                });
                activity.rvNewGuanLi.setLayoutManager(new LinearLayoutManager(activity));
                activity.rvNewGuanLi.setAdapter(adapter);

                /*GetSupportAppJson.SupportAppBean supportAppBean = supportAppBeanList.get(0);
                activity.tvNewGuanLiDevSupportAppCheck.setText(supportAppBean.getName());
                if (supportAppBeanList.size() >= 2) {
                    activity.cvNewGuanLiDevSupportAppCheckTest.setVisibility(View.VISIBLE);
                    activity.tvNewGuanLiDevSupportAppCheckTest.setText(supportAppBeanList.get(1).getName());
                }*/

                DeviceSupportAppData deviceSupportAppData = new DeviceSupportAppData();
                deviceSupportAppData.setDevice_model_number_add_serial_number(displayModelNumber + displaySerialNumber);
                deviceSupportAppData.setDevice_support_app_infoResponse(response);
                deviceSupportAppData.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",displayModelNumber + displaySerialNumber);
            }
        });

        Logger.e("stop");
    }

    private void clickApp(GetSupportAppJson.SupportAppBean supportAppBean, Context context) {
        FileUtils.writeTxtToFile(deviceIP, Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLine.txt");

        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(supportAppBean.getPackageX());
            //传送设备ip并标记 101
            intent.setFlags(101);
            intent.putExtra("device_ip", deviceIP);
            intent.putExtra(INTENT_display_model_number_add_serial_number,displayModelNumber + displaySerialNumber);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "没有安装", Toast.LENGTH_LONG).show();
            showNavigationErrorDialog(context,supportAppBean.getAndroid_download_url(),supportAppBean.getName());
        }
    }

    private void showNavigationErrorDialog(Context context, String downloadAppURL, String downloadAppName) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("提示");
        dialog.setMessage("NavigationAPP启动失败，或者还没下载，请重新下载后再试！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "进入下载",
                (dialogInterface, i) -> context.startActivity(new Intent(context, DownloadAppActivity.class)
                        .putExtra(INTENT_download_app_URL,downloadAppURL)
                        .putExtra(INTENT_download_app_name, downloadAppName)));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (dialogInterface, i) -> {
        });
        dialog.show();
    }
}
