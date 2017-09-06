package com.imotom.dm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.bean.GetSystemInfoJson;
import com.imotom.dm.R;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.utils.DigestAuthenticationUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckDeviceInfoActivity extends AppCompatActivity implements Consts{
    @BindView(R.id.tv_TXZBXX_sn)
    TextView tv_TXZBXX_sn;

    @BindView(R.id.tv_TXZBXX_hwid)
    TextView tv_TXZBXX_hwid;

    @BindView(R.id.tv_TXZBXX_swid)
    TextView tv_TXZBXX_swid;

    @BindView(R.id.tv_TXZBXX_mac)
    TextView tv_TXZBXX_mac;

    @BindView(R.id.tv_TXZBXX_stm32)
    TextView tv_TXZBXX_stm32;

    private GetSystemInfoJson getSystemInfoJson;

    //设备序列号
    private String displaySerialNumber;
    private String displayFriendlyName;
    private String displayModelNumber;

    private Handler getSystemInfoHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<CheckDeviceInfoActivity> myActivity;

        private MyHandler(CheckDeviceInfoActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CheckDeviceInfoActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    // 在这里可以进行UI操作
                    // new DownloadTask().execute();
                    try {

                        int jsonSize = msg.obj.toString().indexOf("{");
                        String jsonContent ;
                        if(jsonSize == 0){
                            jsonContent = msg.obj.toString();
                        }else {
                            jsonContent = msg.obj.toString().substring(jsonSize);
                        }

                        activity.getSystemInfoJson = new GsonBuilder().create().fromJson(jsonContent, GetSystemInfoJson.class);
                        if (!activity.getSystemInfoJson.getSn().isEmpty()) {
                            activity.tv_TXZBXX_sn.setText(activity.getSystemInfoJson.getSn());
                        }
                        if (!activity.getSystemInfoJson.getHwid().isEmpty()) {
                            activity.tv_TXZBXX_hwid.setText(activity.getSystemInfoJson.getHwid());
                        }
                        if (!activity.getSystemInfoJson.getSwid().isEmpty()) {
                            activity.tv_TXZBXX_swid.setText(activity.getSystemInfoJson.getSwid());
                        }
                        if (!activity.getSystemInfoJson.getMac().isEmpty()) {
                            activity.tv_TXZBXX_mac.setText(activity.getSystemInfoJson.getMac());
                        }
                        if (!activity.getSystemInfoJson.getStm32_ver().isEmpty()) {
                            activity.tv_TXZBXX_stm32.setText(activity.getSystemInfoJson.getStm32_ver());
                        }

                    } catch (Exception e) {
                        //e.printStackTrace();
                    }

                    DeviceOffLine deviceOffLine = new DeviceOffLine();
                    deviceOffLine.setDevice_friendly_name(activity.displayFriendlyName);
                    deviceOffLine.setDevice_model_number(activity.displayModelNumber);
                    deviceOffLine.setDevice_model_number_add_serial_number(activity.displayModelNumber+activity.displaySerialNumber);
                    deviceOffLine.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",deviceOffLine.getDevice_model_number_add_serial_number());

                    break;

                case NO_TEXT:
                    Toast.makeText(activity, "获取主板信息失败。", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_device_info);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
    }

    private void init() {
        String url = "http://192.168.43.1:8199/get_system_info";
        DigestAuthenticationUtil.startDigest(url, getSystemInfoHandler, "/get_system_info");

        //获取上一个界面传来的URL
        Intent intent = getIntent();
        // 获取型号，再区分是国科设备还是车机设备
        displayFriendlyName = intent.getStringExtra(INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(INTENT_display_model_number);
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
}
