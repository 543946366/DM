package com.imotom.dm.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.SPUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HuoQuWIFIMiMaActivity extends AppCompatActivity implements Consts {

    @BindView(R.id.tv_WIFI_miMa)
    TextView tv_WIFIMiMa;
    @BindView(R.id.btn_fuZhi_huoQuWIFIMiMa)
    Button btn_huoQu;
    private ClipboardManager myClipboard;
    ClipData myClip;
    String WIFIMiMa;
    private String myBaseUrl;
    private Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private final WeakReference<HuoQuWIFIMiMaActivity> myActivity;

        private MyHandler(HuoQuWIFIMiMaActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HuoQuWIFIMiMaActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
// 在这里可以进行UI操作
                    String WIFIpassword = msg.obj.toString().split(":")[1].substring(1, msg.obj.toString().split(":")[1].length() - 2);

                    activity.tv_WIFIMiMa.setText(String.valueOf(WIFIpassword));
                    if (SPUtils.contains(activity, "WIFI密码")) {
                        SPUtils.remove(activity, "WIFI密码");
                    }
                    SPUtils.put(activity, "WIFI密码", WIFIpassword);
                    Logger.d("4444444444");
                    break;

                case NO_TEXT:
                    activity.tv_WIFIMiMa.setText("获取失败，请重试！");
                    Logger.d("获取失败，请重试！");
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huo_qu_wifimi_ma);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_huoQuWIFIMiMa);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setDisplayHomeAsUpEnabled(true);
        }

        init();
        initView();
        huoQuWIFIPassword();
    }

    private void init() {
//获取到的URL
        Intent intent = getIntent();
        //intent.getStringExtra(Consts.INTENT_deviceURL).replaceAll(reg,"$1");
        myBaseUrl = "http://" + intent.getStringExtra(Consts.INTENT_deviceURL).replaceAll(Consts.REG, "$1") + ":8199/";

        Logger.d(myBaseUrl);
         /*
         * 赋值粘贴
         */
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    private void initView() {
        btn_huoQu.setOnClickListener(view -> copy());
    }

    /**
     * 返回界面获取新的WIFI密码
     */
    @Override
    protected void onResume() {
        Logger.d("hahaahah");
        if (SPUtils.contains(this, "WIFI密码")) {
            WIFIMiMa = String.valueOf(SPUtils.get(HuoQuWIFIMiMaActivity.this, "WIFI密码", ""));
            Logger.d(WIFIMiMa);
            tv_WIFIMiMa.setText(WIFIMiMa);
            Logger.d("获取WIFI密码界面");
        } else {
            Logger.d("没有本地保存密码");
        }
        super.onResume();
    }

    private void copy() {
        String text = tv_WIFIMiMa.getText().toString();
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(this, "复制成功，可直接粘贴！",
                Toast.LENGTH_SHORT).show();
    }

    private void huoQuWIFIPassword() {
        //创建一个Request  "http://192.168.63.9:8199/wifi_pwd_retrieve"
        String url = myBaseUrl + "wifi_pwd_retrieve";
        DigestAuthenticationUtil.startDigestPost(url,handler,"/wifi_pwd_retrieve","admin","admin");

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
