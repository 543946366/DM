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

import com.imotom.dm.R;
import com.imotom.dm.bean.DeviceOffLine;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;

import static com.imotom.dm.Consts.Consts.DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER;
import static com.imotom.dm.Consts.Consts.INTENT_display_model_number_add_serial_number;
import static com.imotom.dm.Consts.Consts.NEXT_BANBENHAO_TEXT;
import static com.imotom.dm.Consts.Consts.URL_inquire_cheJi_dev_update_doc;
import static com.imotom.dm.Consts.Consts.URL_inquire_guoKe_dev_update_doc;

public class OffLineCheckDevVersionActivity extends AppCompatActivity {

    TextView tv_log;

    //网络上新的版本号
    private String newBanBen;
    private String dangQianBanBen;

    private Handler logTextHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<OffLineCheckDevVersionActivity> myActivity;

        private MyHandler(OffLineCheckDevVersionActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OffLineCheckDevVersionActivity activity = myActivity.get();
            switch (msg.what) {
                //下个版本信息
                case NEXT_BANBENHAO_TEXT:
                    if (String.valueOf(msg.obj).equals("当前已是最新版本，无需升级！")) {
                        activity.tv_log.setText("当前"+activity.dangQianBanBen+"已是最新版本，无需升级！");

                    } else {
                        activity.tv_log.setText(String.valueOf("有新版本：" + activity.newBanBen + "\n" + "请尽快联系4S店或者厂家升级！"));

                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line_check_dev_version);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tv_log = (TextView)findViewById(R.id.tv_off_line_check_dev_version_log);

        if(getIntent().getFlags() == 1828){
            initGuoKe();
        }else if(getIntent().getFlags() == 1845){
            initCheJi();
        }
    }

    private void initCheJi() {
        xiaBanBenShengJi();
    }

    /**
     * 根据国科提供的版本号再查询网络看下个升级的版本是什么
     *
     *
     */
    private void xiaBanBenShengJi() {

        //String url = "http://120.27.94.20:10080/vendor/imotom/MT1845/upgrade.txt";
        OkHttpUtils
                .get()
                .url(URL_inquire_cheJi_dev_update_doc)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("TAG", e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("TAG", response);
                        tv_log.setText(String.valueOf("最新固件包日期为："+response + "\n如需要更新，请联系4S店或者厂家！"));

                    }
                });
    }

    private void initGuoKe() {
        Intent intentGetFlags = getIntent();
        //新的业务逻辑获取本地保存的版本号

        List<DeviceOffLine> deviceOffLineList = DataSupport
                .where(DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER + "=?",
                        intentGetFlags.getStringExtra(INTENT_display_model_number_add_serial_number)).find(DeviceOffLine.class);
        DeviceOffLine deviceOffLine = deviceOffLineList.get(0);
        Logger.e("" + deviceOffLine.getDevice_guoke_version_number());
        Logger.e(deviceOffLine.getDevice_model_number_add_serial_number());
        Logger.d(deviceOffLineList.size() + "_______180_________"
                + deviceOffLineList.get(0).getDevice_guoke_version_number() + "____"
                + intentGetFlags.getStringExtra(INTENT_display_model_number_add_serial_number));
        dangQianBanBen = deviceOffLine.getDevice_guoke_version_number();
        tv_log.setText(String.valueOf("当前版本：" + dangQianBanBen));

        xiaBanBenShengJi(dangQianBanBen);
    }

    /**
     * 根据国科提供的版本号再查询网络看下个升级的版本是什么
     *
     * @param dangQianBanBen 当前国科设备的版本号
     */
    private void xiaBanBenShengJi(final String dangQianBanBen) {
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
                            if (str.split(":")[0].trim().equals(dangQianBanBen)) {
                                Log.d("TAG", "下个升级的坂本为：" + str.split(":")[1]);
                                if (str.split(":")[1].equals("new")) {
                                    newBanBen = "当前已是最新版本，无需升级！";
                                } else {
                                    newBanBen = str.split(":")[1] + ".bin";
                                }
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = newBanBen;
                                logTextHandler.sendMessage(message); // 将Message对象发送出去
                                return;
                            } else {
                                //当网络上无此版本号时，提示无需升级
                                newBanBen = "当前已是最新版本，无需升级！";
                                Message message = new Message();
                                message.what = NEXT_BANBENHAO_TEXT;
                                message.obj = newBanBen;
                                logTextHandler.sendMessage(message); // 将Message对象发送出去
                            }
                        }

                    }
                });
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
