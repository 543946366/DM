package com.imotom.dm.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.SPUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class XiuGaiMiMaActivity extends AppCompatActivity implements Consts {

    //确认修改WIFI密码按钮
    @BindView(R.id.btn_ok_xiuGaiMiMa)
    Button btn_ok;
    @BindView(R.id.et_newPassword_XiuGaiMiMa)
    EditText et_newPassword_XiuGaiMiMa;
    @BindView(R.id.et_again_XiuGaiMiMa)
    EditText et_again_XiuGaiMiMa;

    private String miMa_text;
    private String myBaseUrl;

    /*public static final int OK_TEXT = 1;
    public static final int NO_TEXT = 2;*/
    private Handler handler = new MyHandler(this);
            /*new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OK_TEXT:
// 在这里可以进行UI操作
                    L.d("4444444444");
                    //本地保存WIFI密码
                    if (SPUtils.contains(XiuGaiMiMaActivity.this, "WIFI密码")) {
                        SPUtils.remove(XiuGaiMiMaActivity.this, "WIFI密码");
                    }
                    SPUtils.put(XiuGaiMiMaActivity.this, "WIFI密码", miMa_text);
                    finish();
                    T.showLong(XiuGaiMiMaActivity.this, "修改成功！请使用新密码登录WIFI！");
                    break;

                case NO_TEXT:
                    T.showShort(XiuGaiMiMaActivity.this, "修改失败！请重试！");
                    break;
                default:
                    break;
            }
        }
    };*/

    private static class MyHandler extends Handler {

        private final WeakReference<XiuGaiMiMaActivity> myActivity;

        private MyHandler(XiuGaiMiMaActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            XiuGaiMiMaActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
// 在这里可以进行UI操作
                    Logger.d("4444444444");
                    //本地保存WIFI密码
                    if (SPUtils.contains(activity, "WIFI密码")) {
                        SPUtils.remove(activity, "WIFI密码");
                    }
                    SPUtils.put(activity, "WIFI密码", activity.miMa_text);
                    activity.finish();
                    Toast.makeText(activity, "修改成功！请使用新密码登录WIFI！", Toast.LENGTH_SHORT).show();
                    break;

                case NO_TEXT:
                    Toast.makeText(activity, "修改失败！请重试！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiu_gai_mi_ma);
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
        //获取到的URL
        Intent intent = getIntent();
        //原来的地址myBaseUrl = intent.getStringExtra(INTENT_deviceURL);
        myBaseUrl = "http://" + intent.getStringExtra(INTENT_deviceURL).replaceAll(REG, "$1") + ":8199/";
        Logger.d(myBaseUrl);
    }

    private void initView() {
        btn_ok.setOnClickListener(view -> {
            if (et_again_XiuGaiMiMa.getText().toString().isEmpty() || et_newPassword_XiuGaiMiMa.getText().toString().isEmpty()) {
                Toast.makeText(this, "输入的新密码不能为空！", Toast.LENGTH_SHORT).show();
            } else if (et_newPassword_XiuGaiMiMa.getText().toString().equals("12345678") || et_again_XiuGaiMiMa.getText().toString().equals("12345678")) {
                Toast.makeText(this, "出于安全考虑，新密码不能为初始密码12345678！", Toast.LENGTH_SHORT).show();
                et_newPassword_XiuGaiMiMa.setText("");
                et_again_XiuGaiMiMa.setText("");
            } else if (et_newPassword_XiuGaiMiMa.getText().toString().equals(et_again_XiuGaiMiMa.getText().toString())) {
                miMa_text = et_again_XiuGaiMiMa.getText().toString();
                showMyDialog();
            } else {
                Toast.makeText(this, "两次密码输入不一样，请重新输入！", Toast.LENGTH_SHORT).show();
                et_newPassword_XiuGaiMiMa.setText("");
                et_again_XiuGaiMiMa.setText("");
            }

        });
    }

    private void showMyDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage("修改WIFI密码会使当前网络断开，需使用新密码连接WIFI！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,"确定",(d,i)->{

            if (SPUtils.contains(XiuGaiMiMaActivity.this, "WIFI密码")) {
                SPUtils.remove(XiuGaiMiMaActivity.this, "WIFI密码");
            }
            miMa_text = et_again_XiuGaiMiMa.getText().toString();
            SPUtils.put(XiuGaiMiMaActivity.this, "WIFI密码", miMa_text);
            changeWIFIPassword();
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,"取消",(d,i) -> {});
        dialog.show();
    }

    /**
     * 更改WIFI密码
     */
    private void changeWIFIPassword() {
        //"http://192.168.63.9:8199/wifi_pwd_update"
        DigestAuthenticationUtil.startDigestPost(myBaseUrl+"wifi_pwd_update",handler,"/wifi_pwd_update","admin","admin","wifi_password",miMa_text);
        /*new Thread(() -> {

            try {

                //创建okHttpClient对象
//创建一个Request
                //"http://192.168.63.9:8199/wifi_pwd_update"
                Response response = OkHttpUtils
                        .post()
                        .addParams("wifi_password", miMa_text)
                        .url(myBaseUrl+"wifi_pwd_update")
                        .build()
                        .execute();
                //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                if (response.code() == 401) {
                    L.d("6");

                    L.d("下面开始666WWW-Authenticate:" + response.header("WWW-Authenticate"));
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        L.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    Map<String, String> maps = getMapByKeyArray(response.header("WWW-Authenticate").split(","));

                    maps.put("username", "admin");
                    maps.put("nc", "00000002");
                    maps.put("cnonce", "6d9a4895d16b3021");
                    maps.put("uri", "/wifi_pwd_update");
                    *//*
                     * POST请求要HA2 要修改为 HA2 = MD5Object.encrypt("POST:" + "/get_version");
                     *//*
                    maps.put("response", getPOSTResponse(maps));

                    // 开始拼凑Authorization 头信息
                    StringBuilder authorizationHaderValue = new StringBuilder();
                    //StringBuffer authorizationHaderValue = new StringBuffer();
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
                            .append(maps.get("cnonce")).append("\"");

                    System.out.println(authorizationHaderValue.toString());

//创建一个Request
                        *//*RequestBody bodyd = RequestBody.create(JSON, "Hello World");
                        request = new Request.Builder()
                                .url("http://192.168.63.9:8199/get_version")
                                .post(bodyd)
                                .addHeader("Authorization",
                                        authorizationHaderValue.toString())
                                .build();
                        response = mOkHttpClient.newCall(request).execute();*//*

                    //"http://192.168.63.9:8199/wifi_pwd_update"
                    OkHttpUtils
                            .post()
                            .url(myBaseUrl+"wifi_pwd_update")
                            .addHeader("Authorization",
                                    authorizationHaderValue.toString())
                            .addParams("wifi_password", miMa_text)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    L.d("失败：" + e.getMessage());
                                    Message message = new Message();
                                    message.what = NO_TEXT;
                                    handler.sendMessage(message); // 将Message对象发送出去
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    L.d("成功:" + response);
                                    Message message = new Message();
                                    message.what = OK_TEXT;
                                    handler.sendMessage(message); // 将Message对象发送出去
                                }
                            });
                    // 打印响应码
                    System.out.println(response.code());


                    Headers responseHeadersqq = response.headers();
                    for (int i = 0; i < responseHeadersqq.size(); i++) {
                        L.d(responseHeadersqq.name(i) + ": " + responseHeadersqq.value(i));
                    }
                    // 打印响应的信息
                    System.out.println(response.body());

                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }).start();*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
