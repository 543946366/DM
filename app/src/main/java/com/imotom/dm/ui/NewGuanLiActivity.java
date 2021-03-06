package com.imotom.dm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.bean.SetWifiStaSettingsJson;
import com.imotom.dm.handler.GetCapabilityHandler;
import com.imotom.dm.handler.GetSystemInfoHandler;
import com.imotom.dm.handler.GetWifiPasswordHandler;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.SPUtils;
import com.imotom.dm.view.SetWifiStaSettingsActivity;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;

public class NewGuanLiActivity extends AppCompatActivity implements Consts {

    @BindView(R.id.tv_new_guanLi_dev_info)
    public
    TextView tvNewGuanLiDevInfo;
    @BindView(R.id.tv_new_guanLi_show_password)
    public
    TextView tvNewGuanLiShowPassword;
    @BindView(R.id.tv_new_guanLi_dev_newVersionInfo)
    public TextView tvNewGuanLiDevVersion;
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
    @BindView(R.id.cv_new_guanLi_change_hotspot)
    CardView cvNewGuanLiChangeHotspot;
    @BindView(R.id.tv_new_guanLi_show_ssid)
    TextView tvNewGuanLiShowSsid;

    //设备URL及设备名和设备序列号
    private String myBaseUrl;

    public GetSystemInfoHandler getSystemInfoHandler;
    private GetCapabilityHandler getCapabilityHandler;
    public GetWifiPasswordHandler getWifiPasswordHandler = new GetWifiPasswordHandler(this);
    private String deviceIP;

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
        deviceIP = myBaseUrl.replaceAll(REG, "$1");
        String displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        String displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        String displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);

        getSystemInfoHandler = new GetSystemInfoHandler(this, displayFriendlyName, displayModelNumber, displaySerialNumber, deviceIP);
        getCapabilityHandler = new GetCapabilityHandler(this, displayModelNumber, displaySerialNumber, deviceIP);

        Logger.e(myBaseUrl + "===" + displayFriendlyName + "==" + displaySerialNumber + "==" + displayModelNumber);

        getCapability();
    }

    @OnClick({R.id.cv_new_guanLi_change_password, R.id.cv_new_guanLi_dev_support_app_check,R.id.cv_new_guanLi_change_hotspot})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_new_guanLi_change_password:
                Intent intentXiuGai = new Intent(this, XiuGaiMiMaActivity.class);
                intentXiuGai.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentXiuGai);
                break;

            case R.id.cv_new_guanLi_change_hotspot:
                startActivity(new Intent(this, SetWifiStaSettingsActivity.class).putExtra(Consts.INTENT_deviceURL, deviceIP));

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
