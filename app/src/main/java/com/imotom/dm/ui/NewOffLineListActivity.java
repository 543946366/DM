package com.imotom.dm.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.imotom.dm.R;
import com.imotom.dm.adapter.SupportAppAdapter;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.bean.GetSupportAppJson;
import com.imotom.dm.utils.FileUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.imotom.dm.Consts.Consts.DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER;
import static com.imotom.dm.Consts.Consts.INTENT_display_model_number_add_serial_number;
import static com.imotom.dm.Consts.Consts.INTENT_download_app_URL;
import static com.imotom.dm.Consts.Consts.INTENT_download_app_introduction;
import static com.imotom.dm.Consts.Consts.INTENT_download_app_name;
import static com.imotom.dm.Consts.Consts.MT_cheJi_model_number;
import static com.imotom.dm.Consts.Consts.MT_guoKe_model_number;
import static com.imotom.dm.Consts.Consts.URL_check_dev_supportAPP_baseURL;
import static com.imotom.dm.Consts.Consts.URL_inquire_cheJi_dev_update_doc;
import static com.imotom.dm.Consts.Consts.URL_inquire_guoKe_dev_update_doc;

public class NewOffLineListActivity extends AppCompatActivity {

    @BindView(R.id.tv_new_offLineList_dev_info)
    TextView tvNewOffLineListDevInfo;
    @BindView(R.id.tv_new_offLineList_dev_newVersionInfo)
    TextView tvNewOffLineListDevNewVersionInfo;
    @BindView(R.id.rv_newOffLineList)
    RecyclerView rvNewOffLineList;

    private String display_model_number_add_serial_number;
    private String displayIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_off_line_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
    }

    private void init() {
        display_model_number_add_serial_number = getIntent().getStringExtra(INTENT_display_model_number_add_serial_number);
        List<DeviceOffLine> deviceOffLineList = DataSupport
                .where(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER + "=?",
                        display_model_number_add_serial_number).find(DeviceOffLine.class);
        DeviceOffLine deviceOffLine = deviceOffLineList.get(0);
        StringBuilder systemInfo = new StringBuilder();
        if (!deviceOffLine.getDevice_serial_number().isEmpty()) {
            systemInfo.append("序列号：").append(deviceOffLine.getDevice_serial_number()).append("\n");
        }
        if (!deviceOffLine.getDevice_hwid().isEmpty()) {
            systemInfo.append("硬件号：").append(deviceOffLine.getDevice_hwid()).append("\n");
        }

        if (!deviceOffLine.getDevice_Mac().isEmpty()) {
            systemInfo.append("Mac地址：").append(deviceOffLine.getDevice_Mac()).append("\n");
        }
        if (!deviceOffLine.getDevice_swid().isEmpty()) {
            systemInfo.append("软件号：").append(deviceOffLine.getDevice_swid()).append("\n");

            if(deviceOffLine.getDevice_model_number() != null && !deviceOffLine.getDevice_model_number().isEmpty()) {
                if (deviceOffLine.getDevice_model_number().equals(MT_guoKe_model_number)){
                    guoKeRuanJianBaoShengJiChaXun(deviceOffLine.getDevice_swid());
                }else if (deviceOffLine.getDevice_model_number().equals(MT_cheJi_model_number)) {
                    cheJiRuanJianBaoChaXun();
                }
            }

        }
        if (deviceOffLine.getDevice_cheji_stm32ver() != null && !deviceOffLine.getDevice_cheji_stm32ver().isEmpty()) {
            systemInfo.append("单片机版本：").append(deviceOffLine.getDevice_cheji_stm32ver()).append("\n");
        }
        if (!deviceOffLine.getDevice_url().isEmpty()) {
            displayIP = deviceOffLine.getDevice_url();
        }

        Logger.d(systemInfo.toString());
        if (!systemInfo.toString().isEmpty()) {
            tvNewOffLineListDevInfo.setText(systemInfo.toString());
        }

        checkSupportApp(deviceOffLine.getDevice_model_number(),deviceOffLine.getDevice_swid());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void checkSupportApp(String displayModelNumber ,String displaySwid) {
        Logger.e("start" + display_model_number_add_serial_number);
        Logger.e(URL_check_dev_supportAPP_baseURL + displayModelNumber + "/" + displaySwid + "AppList.txt");
        OkHttpUtils.get().url(URL_check_dev_supportAPP_baseURL + displayModelNumber + "/" + displaySwid + "AppList.txt").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.e(e.getMessage());

            }

            @Override
            public void onResponse(String response, int id) {
                GetSupportAppJson getSupportAppJson = new GsonBuilder().create().fromJson(response, GetSupportAppJson.class);
                List<GetSupportAppJson.SupportAppBean> supportAppBeanList = getSupportAppJson.getSupport_app();
                SupportAppAdapter adapter = new SupportAppAdapter(supportAppBeanList);
                adapter.setOnItemClickListener((adapter1, view, position) -> {
                    Logger.e(supportAppBeanList.get(position).getName());

                    clickApp(supportAppBeanList.get(position));
                });
                rvNewOffLineList.setLayoutManager(new LinearLayoutManager(NewOffLineListActivity.this));
                rvNewOffLineList.setAdapter(adapter);

            }
        });

        Logger.e("stop");
    }

    private void clickApp(GetSupportAppJson.SupportAppBean supportAppBean) {
        FileUtils.writeTxtToFile(displayIP, Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLine.txt");
        FileUtils.writeTxtToFile(display_model_number_add_serial_number, Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLineNumber.txt");

        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(supportAppBean.getPackageX());
            //传送设备ip并标记 101
            intent.setFlags(101);
            intent.putExtra("device_ip", displayIP);
            intent.putExtra(INTENT_display_model_number_add_serial_number,display_model_number_add_serial_number);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "没有安装", Toast.LENGTH_LONG).show();
            Logger.e(supportAppBean.getIntroduction());
            showNavigationErrorDialog(supportAppBean.getAndroid_download_url(), supportAppBean.getName(), supportAppBean.getIntroduction());
        }
    }

    private void showNavigationErrorDialog(String downloadAppURL, String downloadAppName, String introduction) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage(downloadAppName + "启动失败，或者还没下载，请重新下载后再试！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "进入下载",
                (dialogInterface, i) -> startActivity(new Intent(this, DownloadAppActivity.class)
                        .putExtra(INTENT_download_app_URL,downloadAppURL)
                        .putExtra(INTENT_download_app_name, downloadAppName)
                        .putExtra(INTENT_download_app_introduction,introduction)));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (dialogInterface, i) -> {
        });
        dialog.show();
    }

    //国科升级软件包查询
    private void guoKeRuanJianBaoShengJiChaXun(final String dangQianBanBenName) {
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
                                    tvNewOffLineListDevNewVersionInfo.setText("当前已是最新版本，无需升级！");
                                } else {
                                    tvNewOffLineListDevNewVersionInfo.setText("有新版本：" + str.split(":")[1] + "\n请尽快联系4S店或者厂家升级！");
                                }
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                tvNewOffLineListDevNewVersionInfo.setText("当前已是最新版本，无需升级！");
                            }
                        }

                    }
                });
    }


    //车机升级软件包查询
    private void cheJiRuanJianBaoChaXun() {
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
                        tvNewOffLineListDevNewVersionInfo.setText(String.valueOf("最新固件包日期为："+response + "\n如需要更新，请联系4S店或者厂家！"));

                    }
                });
    }
}
