package com.imotom.dm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.handler.GetCapabilityHandler;
import com.imotom.dm.handler.GetSystemInfoHandler;
import com.imotom.dm.handler.GetWifiPasswordHandler;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.SPUtils;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewGuanLiActivity extends AppCompatActivity implements Consts {

    @BindView(R.id.tv_new_guanLi_dev_info)
    public
    TextView tvNewGuanLiDevInfo;
    @BindView(R.id.tv_new_guanLi_show_password)
    public
    TextView tvNewGuanLiShowPassword;
    @BindView(R.id.tv_new_guanLi_dev_newVersionInfo)
    TextView tvNewGuanLiDevVersion;
    @BindView(R.id.cv_new_guanLi_wifiPassword)
    public
    CardView cvNewGuanLiWifiPassword;
    @BindView(R.id.cv_new_guanLi_devInfo)
    public
    CardView cvNewGuanLiDevInfo;
    @BindView(R.id.cv_new_guanLi_change_password)
    public CardView cvNewGuanLiChangePassword;
    @BindView(R.id.cv_new_guanLi_dev_support_app_check)
    public CardView cvNewGuanLiDevSupportAppCheck;
    @BindView(R.id.cv_new_guanLi_dev_support_app_check_test)
    public CardView cvNewGuanLiDevSupportAppCheckTest;
    @BindView(R.id.tv_new_guanLi_dev_support_app_check)
    public TextView tvNewGuanLiDevSupportAppCheck;
    @BindView(R.id.tv_new_guanLi_dev_support_app_check_test)
    public TextView tvNewGuanLiDevSupportAppCheckTest;
    @BindView(R.id.rv_newGuanLi)
    public RecyclerView rvNewGuanLi;

    //设备URL及设备名和设备序列号
    private String myBaseUrl;
    private String displayFriendlyName;
    private String displaySerialNumber;
    private String displayModelNumber;

    /*private Handler logTextHandler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<NewGuanLiActivity> myActivity;

        private MyHandler(NewGuanLiActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
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

                        capabilityJson = new GsonBuilder().create().fromJson(String.valueOf(msg.obj.toString().substring(4)), CapabilityJson.class);
                        for (String t : capabilityJson.getCapability()) {
                            Log.d("TAG", t);
                            fuWuList = fuWuList.concat(t);
                        }

                        Logger.d(fuWuList);
                        if (fuWuList.contains("get_version")) {
                            activity.cvNewGuanLiDevInfo.setVisibility(View.VISIBLE);
                            Logger.d("=========");
                            DigestAuthenticationUtil.startDigest("http://192.168.43.1:8199/get_system_info", getSystemInfoHandler, "/get_system_info");
                            Logger.d("=========");
                        }
                        if (fuWuList.contains("wifi_pwd_retrieve")) {
                            activity.cvNewGuanLiWifiPassword.setVisibility(View.VISIBLE);
                        }
                        if (fuWuList.contains("wifi_pwd_update")) {
                            activity.cvNewGuanLiChangePassword.setVisibility(View.VISIBLE);
                        }
                        if (fuWuList.contains("update_time")) {
                            //TODO 国科设备专用的服务，如果有则提供修改设备时间界面
                        }

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
    }*/

    public GetSystemInfoHandler getSystemInfoHandler;
    private GetCapabilityHandler getCapabilityHandler;
    public GetWifiPasswordHandler getWifiPasswordHandler = new GetWifiPasswordHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_guan_li);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
    }

    /**
     * 返回界面获取新的WIFI密码
     */
    @Override
    protected void onResume() {
        Logger.d("hahaahah");
        if (SPUtils.contains(this, "WIFI密码")) {
            String WIFIMiMa = String.valueOf(SPUtils.get(NewGuanLiActivity.this, "WIFI密码", ""));
            Logger.d(WIFIMiMa);
            tvNewGuanLiShowPassword.setText(WIFIMiMa);
            Logger.d("获取WIFI密码界面");
        } else {
            Logger.d("没有本地保存密码");
        }
        super.onResume();
    }

    private void init() {
        //获取上个界面传过来的intent
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(Consts.INTENT_deviceURL);
        String deviceIP = myBaseUrl.replaceAll(REG, "$1");
        displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);

        getSystemInfoHandler = new GetSystemInfoHandler(this, displayFriendlyName, displayModelNumber, displaySerialNumber);
        getCapabilityHandler = new GetCapabilityHandler(this, displayModelNumber, displaySerialNumber ,deviceIP);

        Logger.e(myBaseUrl + "===" + displayFriendlyName + "==" + displaySerialNumber + "==" + displayModelNumber);

        getCapability();
    }

    @OnClick({R.id.cv_new_guanLi_change_password, R.id.cv_new_guanLi_dev_support_app_check})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_new_guanLi_change_password:
                Intent intentXiuGai = new Intent(this, XiuGaiMiMaActivity.class);
                intentXiuGai.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentXiuGai);
                break;

            case R.id.cv_new_guanLi_dev_support_app_check:


                break;
        }
    }

    private void getCapability() {
        //创建一个Request  "http://192.168.63.9:8199/get_capability"  正则表达式获取IP，再加端口号处理
        //String reg = ".*\\/\\/([^\\/\\:]*).*";
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_capability";
        DigestAuthenticationUtil.startDigest(url, getCapabilityHandler, "/get_capability");

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
