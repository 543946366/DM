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

    private Handler handler = new MyHandler(this);

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
                // wifi密码限制长度至少8位数以上
                if(et_again_XiuGaiMiMa.getText().toString().length() >= 8){
                    miMa_text = et_again_XiuGaiMiMa.getText().toString();
                    showMyDialog();
                }else{
                    Toast.makeText(this, "新密码少于8位数，请重新设置！", Toast.LENGTH_LONG).show();
                }

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
