package com.imotom.dm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.PasswordHelp;
import com.zhy.http.okhttp.utils.L;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DengLuActivity extends AppCompatActivity implements View.OnClickListener,Consts {

    //提示文本View
    @BindView(R.id.tv_dengLu_tishi)
    TextView tv_tiShi;
    //输入用户名文本框
    @BindView(R.id.et_dengLu_username)
    EditText et_username;
    //输入密码文本框
    @BindView(R.id.et_dengLu_password)
    EditText et_password;
    //登录按钮
    @BindView(R.id.btn_dengLu_dengLuButton)
    Button btn_dengLu;
    //记住登录按钮
    @BindView(R.id.cb_dengLu_checkBox)
    CheckBox isJiZhu;
    //从MainActivity获取的设备URL及设备名
    private String myBaseUrl;
    private String displayFriendlyName;
    //设备displaySerialNumber
    private String displaySerialNumber;

    private String displayModelNumber;
    //用户名
    private String myPassword;
    //用户密码
    private String myUername;

    /**
     * 提示用户输入框输入账号或者密码不能为空 -- 常量
    private static final int UPDATE_TEXT = 2;
    //登录失败常量
    private static final int SHI_BAI_TEXT = 3;
    //登录成功常量
    private static final int CHENG_GONG_TEXT = 4;*/

    private Handler handler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<DengLuActivity> myActivity;

        private MyHandler(DengLuActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DengLuActivity activity = myActivity.get();
            switch (msg.what) {
                case UPDATE_TEXT:
// 在这里可以进行UI操作
                    activity.tv_tiShi.setVisibility(View.VISIBLE);
                    activity.tv_tiShi.setText("账号或者密码不能为空");
                    break;

                case SHI_BAI_TEXT:
                    activity.tv_tiShi.setVisibility(View.VISIBLE);
                    activity.tv_tiShi.setText("账号或者密码输入错误，请重试");
                    break;

                case CHENG_GONG_TEXT:
                    //登录成功跳转到管理界面
                    activity.tv_tiShi.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(activity, NewGuanLiActivity.class);
                    //携带设备URL
                    intent.putExtra(Consts.INTENT_deviceURL, activity.myBaseUrl);
                    intent.putExtra(Consts.INTENT_display_friendly_name, activity.displayFriendlyName);
                    intent.putExtra(Consts.INTENT_display_serial_number,activity.displaySerialNumber);
                    intent.putExtra(Consts.INTENT_display_model_number,activity.displayModelNumber);

                    activity.startActivity(intent);
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deng_lu);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        initView();


    }

    private void init() {
        //获取从MainActivity携带的设备URL
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(Consts.INTENT_deviceURL);
        displayFriendlyName = intent.getStringExtra(Consts.INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(Consts.INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(Consts.INTENT_display_model_number);
    }

    private void initView() {
        btn_dengLu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dengLu_dengLuButton:

                dengLu();
                break;

        }
    }

    /**
     * 登录逻辑
     */
    private void dengLu() {

        //判断是否记住密码
        if (isJiZhu.isChecked()) {

            //如果记住密码则通过 3DES加密保存在本地
            PasswordHelp.savePassword(DengLuActivity.this, et_username.getText().toString(), et_password.getText().toString(), true);
        } else {
            //不记住密码则本地保存自定义账号密码
            PasswordHelp.savePassword(this, "nullCaoNiMa", "null", true);
        }
        myPassword = et_password.getText().toString();
        myUername = et_username.getText().toString();

        newHttpDigest();
    }

    /**
     * 进行Http摘要验证
     */
    private void newHttpDigest() {

        final String url = myBaseUrl;
        //final String url = "http://192.168.1.183:8080/Duang/protect/protect.jsp";
        new Thread(() -> {

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                OkHttpClient myOkHttpClient = new OkHttpClient();
                Response response = myOkHttpClient.newCall(request).execute();
                //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                if (response.code() == 401) {

                    /*Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        log(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }*/
                    if (myUername.isEmpty() || myPassword.isEmpty()) {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message); // 将Message对象发送出去
                    } else {
                        String authorizationHaderValue = DigestAuthenticationUtil.startDigestGet(response.header("WWW-Authenticate"),myUername,myPassword,"/");
                        /*Map<String, String> maps = getMapByKeyArray(response.header("WWW-Authenticate").split(","));

                        maps.put("username", myUername);
                        maps.put("password", myPassword);
                        maps.put("nc", "00000002");
                        maps.put("cnonce", "6d9a4895d16b3021");
                        maps.put("uri", "/");
                        maps.put("response", getResponse(maps));

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
                                // .append("nonce=\"").append(maps.get("nonceTime")).append(maps.get("nonce")).append("\", ")
                                .append("nonce=\"").append(maps.get("nonce"))
                                .append("\", ").append("uri=\"").append(maps.get("uri"))
                                .append("\", ").append("algorithm=").append("MD5")
                                .append(", ").append("response=\"")
                                .append(maps.get("response")).append("\", ")
                                .append("opaque=\"").append(maps.get("opaque"))
                                .append("\", ").append("qop=").append(maps.get("qop"))
                                .append(", ").append("nc=").append(maps.get("nc"))
                                .append(", ").append("cnonce=\"")
                                .append(maps.get("cnonce")).append("\"");*/
                        L.e(authorizationHaderValue);

                        myOkHttpClient = new OkHttpClient();
                        //创建一个Request
                        request = new Request.Builder()
                                .url(url)
                                .addHeader("Authorization",
                                        authorizationHaderValue)
                                .build();
                        response = myOkHttpClient.newCall(request).execute();
                        // 打印响应码
                        System.out.println(response.code());
                        L.e("WWW-Authenticate:" + response.header("WWW-Authenticate"));
                        if (response.code() == 200) {
                            Message message = new Message();
                            message.what = CHENG_GONG_TEXT;
                            handler.sendMessage(message); // 将Message对象发送出去

                        } else {
                            Message message = new Message();
                            message.what = SHI_BAI_TEXT;
                            handler.sendMessage(message); // 将Message对象发送出去

                        }
                        // 打印响应的信息
                        System.out.println(response.body());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
