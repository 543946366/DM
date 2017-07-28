package com.imotom.dm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.SPUtils;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CheckDevVersionActivity extends AppCompatActivity implements Consts {

    @BindView(R.id.tv_shengJi_log)
    TextView tv_log;
    //设备URL
    private String myBaseUrl;
    //设备序列号
    private String displaySerialNumber;
    private String displayFriendlyName;
    private String displayModelNumber;
    //固件版本号
    private String guJianName;
    //当前版本号 -- 通过访问车机设备获取
    private String dangQianBanBenName;

    private Handler logTextHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<CheckDevVersionActivity> myActivity;

        private MyHandler(CheckDevVersionActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CheckDevVersionActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    //json测试，版本号显示，现在格式暂时自定
                    if(activity.displayModelNumber.equals(MT_guoKe_model_number)){
                        activity.dangQianBanBenName = String.valueOf(msg.obj).split(":")[2].substring(0, ((String) msg.obj).split(":")[2].length() - 2);

                    }else if(activity.displayModelNumber.equals(MT_cheJi_model_number)){
                        activity.dangQianBanBenName = String.valueOf(msg.obj).split(":")[2].substring(1, ((String) msg.obj).split(":")[2].length() - 2);

                    }
                    activity.tv_log.setText(String.valueOf("当前版本为:" + activity.dangQianBanBenName));

                    DeviceOffLine deviceOffLine = new DeviceOffLine();
                    deviceOffLine.setDevice_friendly_name(activity.displayFriendlyName);
                    deviceOffLine.setDevice_model_number(activity.displayModelNumber);
                    deviceOffLine.setDevice_model_number_add_serial_number(activity.displayModelNumber+activity.displaySerialNumber);
                    deviceOffLine.setDevice_guoke_version_number(activity.dangQianBanBenName);
                    deviceOffLine.saveOrUpdate(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER+"=?",deviceOffLine.getDevice_model_number_add_serial_number());

                    //这里处理升级逻辑
                    activity.xiaBanBenShengJi(activity.dangQianBanBenName);
                    break;

                case NO_TEXT:
                    activity.tv_log.setText("失败");
                    break;

                //下个版本信息
                case NEXT_BANBENHAO_TEXT:

                    //如果之前已保存过，则删除
                    if (SPUtils.contains(activity, "升级版本号")) {
                        SPUtils.remove(activity, "升级版本号");
                    }

                    if (String.valueOf(msg.obj).equals("当前已是最新版本，无需升级！")) {
                        activity.tv_log.setText("当前版本为:" + activity.dangQianBanBenName + "\n" + "当前已是最新版本，无需升级！");
                        SPUtils.put(activity, "升级版本号", "已是最新");
                    } else {
                        activity.tv_log.setText("当前版本为:" + activity.dangQianBanBenName + "\n" + "有新版本：" + activity.guJianName + "\n" + "请尽快联系4S店或者厂家升级！");
                        //TODO
                        //activity.btn_shengJi.setVisibility(View.VISIBLE);

                        //本地保存下个固件升级版本号

                        SPUtils.put(activity, "升级版本号", activity.guJianName);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 根据国科提供的版本号再查询网络看下个升级的版本是什么
     *
     * @param dangQianBanBenName 当前国科版本号
     */
    private void xiaBanBenShengJi(final String dangQianBanBenName) {

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
                                    guJianName = "当前已是最新版本，无需升级！";
                                } else {
                                    guJianName = str.split(":")[1] + ".bin";
                                }
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = guJianName;
                                logTextHandler.sendMessage(message); // 将Message对象发送出去
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                guJianName = "当前已是最新版本，无需升级！";
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = guJianName;
                                logTextHandler.sendMessage(message); // 将Message对象发送出去
                            }
                        }

                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_dev_version);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        initView();
    }

    private void init() {
        //获取上一个界面传来的URL
        Intent intent = getIntent();
        myBaseUrl = intent.getStringExtra(INTENT_deviceURL);
        // 获取型号，再区分是国科设备还是车机设备
        displayFriendlyName = intent.getStringExtra(INTENT_display_friendly_name);
        displaySerialNumber = intent.getStringExtra(INTENT_display_serial_number);
        displayModelNumber = intent.getStringExtra(INTENT_display_model_number);
        Logger.d("DengLu:" + myBaseUrl+displayFriendlyName+displayModelNumber);

        banBenXinxi();

    }

    private void initView() {
    }

    private void banBenXinxi() {
        //创建一个Request  "http://192.168.63.9:8199/get_version"  正则表达式获取IP，再加端口号处理
        //myBaseUrl.replaceAll(reg, "$1");
        String myUrl = "http://" + myBaseUrl.replaceAll(REG, "$1") + ":8199/";
        String url = myUrl + "get_version";
        DigestAuthenticationUtil.startDigest(url,logTextHandler,"/get_version");
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
