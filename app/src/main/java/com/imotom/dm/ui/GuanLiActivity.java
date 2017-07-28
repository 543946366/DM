package com.imotom.dm.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.bean.CapabilityBean;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.FileUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuanLiActivity extends AppCompatActivity implements View.OnClickListener, Consts {

    @BindView(R.id.ll_checkDevVersion_guanLi)
    LinearLayout ll_checkDevVersion_guanLi;
    @BindView(R.id.ll_huoQuWIFIMiMa_guanLi)
    LinearLayout ll_huoQuWIFIMiMa_guanLi;
    @BindView(R.id.ll_xiuGaiWIFIMiMa_guanLi)
    LinearLayout ll_xiuGaiWIFIMiMa_guanLi;
    @BindView(R.id.ll_GKDVRAPP_guanLi)
    LinearLayout ll_GKDVRAPP_guanLi;
    @BindView(R.id.ll_carDev_guanLi)
    LinearLayout ll_carDev_guanLi;

    //设备URL及设备名和设备序列号
    private String myBaseUrl;
    String displayFriendlyName;
    private String displaySerialNumber;
    private String displayModelNumber;
    /*//正则表达式获取网址IP
    private String reg = REG;*/

    static CapabilityBean capabilityBean;

    /*//常量
    public static final int OK_TEXT = 4;
    public static final int NO_TEXT = 5;*/
    private Handler logTextHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<GuanLiActivity> myActivity;

        private MyHandler(GuanLiActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GuanLiActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    // 在这里可以进行UI操作
                    // new DownloadTask().execute();
                    try {
                        String fuWuList = "";

                        capabilityBean = new GsonBuilder().create().fromJson(String.valueOf(msg.obj.toString().substring(4)), CapabilityBean.class);
                        for (String t : capabilityBean.getCapability()) {
                            Log.d("TAG", t);
                            fuWuList = fuWuList.concat(t);
                        }

                        Logger.d(fuWuList);
                        if (fuWuList.contains("get_version")) {
                            activity.ll_checkDevVersion_guanLi.setVisibility(View.VISIBLE);
                        }
                        if (fuWuList.contains("wifi_pwd_retrieve")) {
                            activity.ll_huoQuWIFIMiMa_guanLi.setVisibility(View.VISIBLE);
                        }
                        if (fuWuList.contains("wifi_pwd_update")) {
                            activity.ll_xiuGaiWIFIMiMa_guanLi.setVisibility(View.VISIBLE);
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
    }

    public String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guan_li);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
        initView();
    }

    private void init() {
        //获取上个界面传过来的intent
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(Consts.INTENT_deviceURL);
        displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);

        //新的业务逻辑
        if(displayModelNumber.equals(MT_guoKe_model_number)){
            ll_carDev_guanLi.setVisibility(View.GONE);
            ll_GKDVRAPP_guanLi.setVisibility(View.VISIBLE);
        }
        if(displayModelNumber.equals(MT_cheJi_model_number)){
            ll_carDev_guanLi.setVisibility(View.VISIBLE);
            ll_GKDVRAPP_guanLi.setVisibility(View.GONE);
        }


        getCapability();
    }

    private void initView() {
        ll_checkDevVersion_guanLi.setOnClickListener(this);
        ll_huoQuWIFIMiMa_guanLi.setOnClickListener(this);
        ll_xiuGaiWIFIMiMa_guanLi.setOnClickListener(this);
        ll_GKDVRAPP_guanLi.setOnClickListener(this);
        ll_carDev_guanLi.setOnClickListener(this);
    }

    private void getCapability() {
        //创建一个Request  "http://192.168.63.9:8199/get_capability"  正则表达式获取IP，再加端口号处理
        //String reg = ".*\\/\\/([^\\/\\:]*).*";
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_capability";
        DigestAuthenticationUtil.startDigest(url, logTextHandler, "/get_capability");

        /*new Thread(() -> {

            try {
                //创建okHttpClient对象
//创建一个Request  "http://192.168.63.9:8199/get_capability"  正则表达式获取IP，再加端口号处理
                //String reg = ".*\\/\\/([^\\/\\:]*).*";
                String myUrl = "http://" + myBaseUrl.replaceAll(reg, "$1") + ":8199/";
                String url = myUrl + "get_capability";
                Response response = OkHttpUtils
                        .post()
                        .url(url)
                        .build()
                        .execute();
                if (response.code() == 401) {
                    L.e("下面开始666WWW-Authenticate:" + response.header("WWW-Authenticate"));
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        L.e(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    Map<String, String> maps = getMapByKeyArray(response.header("WWW-Authenticate").split(","));

                    maps.put("username", "admin");
                    maps.put("nc", "00000002");
                    maps.put("cnonce", "6d9a4895d16b3021");
                    maps.put("uri", "/get_capability");

                    *//*
                     * POST请求要HA2 要修改为 HA2 = MD5Object.encrypt("POST:" + "/get_version");
                     *//*
                    maps.put("response", getPOSTResponse(maps));

                    // 开始拼凑Authorization 头信息
                    //StringBuffer authorizationHaderValue = new StringBuffer();
                    StringBuilder authorizationHaderValue = new StringBuilder();
                    authorizationHaderValue
                            .append("Digest username=\"")
                            .append(maps.get("username"))
                            .append("\", ")
                            .append("realm=\"")
                            .append(maps.get("realm"))
                            .append("\", ")
                            .append("nonce=\"").append(maps.get("nonce"))
                            .append("\", ").append("uri=\"").append(maps.get("uri"))
                            .append("\", ").append("algorithm=").append("MD5")
                            .append(", ").append("response=\"")
                            .append(maps.get("response")).append("\", ")
                            .append("opaque=\"").append(maps.get("opaque"))
                            .append("\", ").append("qop=").append(maps.get("qop"))
                            .append(", ").append("nc=").append(maps.get("nc"))
                            .append(", ").append("cnonce=\"")
                            .append(maps.get("cnonce")).append("\"");

                    System.out.println(authorizationHaderValue.toString());

//创建一个Request
                    OkHttpUtils
                            .post()
                            .url(url)
                            .addHeader("Authorization",
                                    authorizationHaderValue.toString())
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                }

                                @Override
                                public void onResponse(String response, int id) {

                                    Message message = new Message();
                                    message.what = OK_TEXT;
                                    //因为获取的json格式后问题，需要去除前面的4字节
                                    message.obj = response.substring(4);
                                    logTextHandler.sendMessage(message); // 将Message对象发送出去
                                }
                            });
                    // 打印响应码
                    System.out.println(response.code());
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

        }).start();*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_checkDevVersion_guanLi:
                Intent intent = null;
                if(displayModelNumber.equals(MT_guoKe_model_number)) {
                    intent = new Intent(this, CheckDevVersionActivity.class);

                }else if(displayModelNumber.equals(MT_cheJi_model_number)){
                    intent = new Intent(this,CheckDeviceInfoActivity.class);

                }
                if (intent != null) {
                    intent.putExtra(INTENT_deviceURL,myBaseUrl);
                    intent.putExtra(INTENT_display_serial_number, displaySerialNumber);
                    intent.putExtra(INTENT_display_friendly_name, displayFriendlyName);
                    intent.putExtra(INTENT_display_model_number, displayModelNumber);
                    startActivity(intent);
                }

                break;

            case R.id.ll_huoQuWIFIMiMa_guanLi:
                Intent intentHuoQu = new Intent(this, HuoQuWIFIMiMaActivity.class);
                intentHuoQu.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentHuoQu);
                break;

            case R.id.ll_xiuGaiWIFIMiMa_guanLi:
                Intent intentXiuGai = new Intent(this, XiuGaiMiMaActivity.class);
                intentXiuGai.putExtra(Consts.INTENT_deviceURL, myBaseUrl);
                startActivity(intentXiuGai);
                break;

            case R.id.ll_GKDVRAPP_guanLi:
                GKDVRAPP();
                break;

            case R.id.ll_carDev_guanLi:
                Navigation();
                break;
        }
    }

    /**
     * 跳转到导航地址共享APP，手机上已安装则直接打开，没有则进行下载
     */
    private void Navigation() {

        try {
            Intent intent = GuanLiActivity.this.getPackageManager().getLaunchIntentForPackage("com.imotom.navigation");
            //传送设备ip并标记 101
            intent.setFlags(101);
            intent.putExtra("device_ip", myBaseUrl.replaceAll(REG, "$1"));
            startActivity(intent);
            test = "startGKDVRActivity";
        } catch (Exception e) {
            Toast.makeText(GuanLiActivity.this, "没有安装", Toast.LENGTH_LONG).show();
            showNavigationErrorDialog();
        }
    }

    private void showNavigationErrorDialog() {

        //本地保存设备IP地址
        //String reg = ".*\\/\\/([^\\/\\:]*).*";
        FileUtils.writeTxtToFile(myBaseUrl.replaceAll(REG, "$1"), Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLine.txt");

        test = "showNavigationErrorDialog";
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage("NavigationAPP启动失败，或者还没下载，请重新下载后再试！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "进入下载",
                (dialogInterface, i) -> startActivity(new Intent(GuanLiActivity.this, DownloadNavigationAPPActivity.class)));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (dialogInterface, i) -> {
        });
        dialog.show();
    }

    /**
     * 跳转到国科app，手机上已安装则直接打开，没有则进行下载
     */
    private void GKDVRAPP() {

        try {
            Intent intent = GuanLiActivity.this.getPackageManager().getLaunchIntentForPackage("zxc.com.gkdvr");
            startActivity(intent);
            test = "startGKDVRActivity";
        } catch (Exception e) {
            Toast.makeText(GuanLiActivity.this, "没有安装", Toast.LENGTH_LONG).show();
            //startActivity(new Intent(GuanLiActivity.this,CActivity.class));
            showGKDVRErrorDialog();
        }
    }

    private void showGKDVRErrorDialog() {
        test = "showGKDVRErrorDialog";
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage("国科APP启动失败，或者还没下载，请重新下载后再试！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "进入下载",
                (dialogInterface, i) -> startActivity(new Intent(GuanLiActivity.this, DownAPPActivity.class)));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (dialogInterface, i) -> {
        });
        dialog.show();
    }

}