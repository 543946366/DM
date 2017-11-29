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
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.bean.GetSystemInfoJson;
import com.imotom.dm.bean.SetWifiStaSettingsJson;
import com.imotom.dm.handler.GetCapabilityHandler;
import com.imotom.dm.handler.GetSystemInfoHandler;
import com.imotom.dm.handler.GetWifiPasswordHandler;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.SPUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

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
    @BindView(R.id.et_new_guanLi_ssid)
    EditText etNewGuanLiSsid;
    @BindView(R.id.et_new_guanLi_hotspotPassword)
    EditText etNewGuanLiHotspotPassword;
    @BindView(R.id.cv_new_guanLi_change_hotspot)
    CardView cvNewGuanLiChangeHotspot;

    //设备URL及设备名和设备序列号
    private String myBaseUrl;

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

                SetWifiStaSettingsJson setWifiStaSettingsJson = new SetWifiStaSettingsJson();
                setWifiStaSettingsJson.setSsid(etNewGuanLiSsid.getText().toString());
                setWifiStaSettingsJson.setPassword(etNewGuanLiHotspotPassword.getText().toString());
                String ss = new GsonBuilder().create().toJson(setWifiStaSettingsJson, SetWifiStaSettingsJson.class);
                //String ss = "{\"ssid\":\"Honor 6X\",\"password\":\"88888888\"}";
                Logger.e(ss);
                String url = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/set_wifi_sta_settings";

                new Thread(() -> {
                    try {
                        Response response;
                        response = OkHttpUtils
                                .postString()
                                .url(url)
                                .content(ss)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                .build()
                                .execute();
                        if (response.code() == 401) {
                            Headers responseHeaders = response.headers();
                            for (int i = 0; i < responseHeaders.size(); i++) {
                                Logger.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }
                            String authorizationHaderValue = DigestAuthenticationUtil
                                    .startDigestPost(response.header("WWW-Authenticate"), "admin", "admin", "/set_wifi_sta_settings");
                            OkHttpUtils
                                    .postString()
                                    .url(url)
                                    .content(ss)
                                    .addHeader("Authorization",
                                            authorizationHaderValue)
                                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            //Toast.makeText(NewGuanLiActivity.this, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Snackbar.make(v,"热点修改失败！请重试！",Snackbar.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            Log.e("TAG",response);
                                            //Toast.makeText(NewGuanLiActivity.this, "返回成功" + response, Toast.LENGTH_SHORT).show();
                                            Snackbar.make(v,"热点修改成功！",Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }).start();
                //TODO
                /*OkHttpUtils.postString()
                        .url(url)
                        .content(ss)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("TAG",e.getMessage());
                        Toast.makeText(NewGuanLiActivity.this, "错误" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("TAG",response);
                        Toast.makeText(NewGuanLiActivity.this, "返回成功" + response, Toast.LENGTH_SHORT).show();
                    }
                });*/
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
