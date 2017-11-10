package com.imotom.dm.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.service.NewDownloadAppService;
import com.imotom.dm.utils.InstallAPKUtil;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadAppActivity extends AppCompatActivity implements Consts{

    @BindView(R.id.tv_downloadApp_name)
    TextView tvDownloadAppName;
    @BindView(R.id.tv_downloadApp_click)
    TextView tvDownloadAppClick;
    @BindView(R.id.tv_downloadApp_hint)
    TextView tvDownloadAppHint;
    @BindView(R.id.pb_downloadApp)
    ProgressBar pbDownloadApp;
    @BindView(R.id.tv_downloadAPp_size)
    TextView tvDownloadAPpSize;

    //BaseDownloadTask baseDownloadTask;
    private String downloadAppName;

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;
    private boolean isDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        init();
    }

    private void init() {
        String downloadAppURL = getIntent().getStringExtra(INTENT_download_app_URL);
        downloadAppName = getIntent().getStringExtra(INTENT_download_app_name);
        //downloadAppPackage = getIntent().getStringExtra(INTENT_download_app_package);
        tvDownloadAppHint.setText(String.valueOf("\u3000\u3000" + getIntent().getStringExtra(INTENT_download_app_introduction)));
        tvDownloadAppName.setText(downloadAppName);
        pbDownloadApp.setMax(100);


        // 激活Service
        Intent intent = new Intent(this, NewDownloadAppService.class)
                .putExtra(INTENT_download_app_URL, downloadAppURL)
                .putExtra(INTENT_download_app_name, downloadAppName);
        startService(intent);

        // 注册Service广播的接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        //接收
        filter.addAction(ACTION_SET_DOWNLOAD_STATE);
        //接收取消下载广播
        filter.addAction(ACTION_SET_PAUSE_STATE);
        //接收下载进度广播
        filter.addAction(ACTION_UPDATE_PROGRESS);
        //接收下载成功广播
        filter.addAction(ACTION_DOWNLOAD_SUCCEED);
        //接收下载失败广播
        filter.addAction(ACTION_DOWNLOAD_ERROR);
        registerReceiver(receiver, filter);
/*
 * 发送询问是否在下载状态的广播
 */
        sendBroadcast(new Intent().setAction(ACTION_IS_DOWNLOAD));

        //Logger.e(FileDownloader.getImpl().create(downloadAppURL).getStatus() + "");

        /*baseDownloadTask = FileDownloader.getImpl().create(downloadAppURL)
                .setPath(Environment.getExternalStorageDirectory().getPath() + "/imotom/test.apk")
                .setAutoRetryTimes(2)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Logger.e(task.getStatus()+"");

                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Logger.e("start");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        tvDownloadAPpSize.setText(bytes2kb(soFarBytes)+"/"+bytes2kb(totalBytes));
                        Logger.d(soFarBytes*100/totalBytes+"=============");
                        pbDownloadApp.setProgress(soFarBytes*100/totalBytes);

                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Logger.e("OK");
                        tvDownloadAPpSize.setText("已下载完成");
                        Logger.e(task.getStatus()+"");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                });
*/
    }

    @OnClick(R.id.cardView)
    public void onClick(View view) {
        if (view.getId() == R.id.cardView) {

            try {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/imotom", downloadAppName + ".apk");
                if (file.exists()) {
                    InstallAPKUtil.installAPK(Environment.getExternalStorageDirectory().getPath() + "/imotom", downloadAppName, this);
                }else {
                    sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE));
                }

            }catch (Exception e) {
                Logger.e(e.getMessage());

            }
        }
    }

    @Override
    protected void onDestroy() {
        //取消广播接收者
        unregisterReceiver(receiver);

        if(!isDownload){
            // 停止Service
            Intent intentStop = new Intent(DownloadAppActivity.this, NewDownloadAppService.class);
            stopService(intentStop);
        }
        super.onDestroy();

    }

    /**
     * Service广播的接收者
     */
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取Action
            String action = intent.getAction();
            // 判断Action
            //接收 更新进度的广播后的逻辑
            if (ACTION_UPDATE_PROGRESS.equals(action)) {
                // 更新播放进度
                long percent = intent.getLongExtra(EXTRA_PERCENT,0);
                String percentText = intent.getStringExtra(EXTRA_PERCENT_TEXT);
                Log.d("TAG", percent + "");
                pbDownloadApp.setProgress((int) percent);
                tvDownloadAPpSize.setText(percentText);
                //接收 固件下载成功的广播后的逻辑
            } else if (ACTION_DOWNLOAD_SUCCEED.equals(action)) {
                pbDownloadApp.setProgress(0);
                tvDownloadAPpSize.setText("下载成功");

                /*// 取消注册广播接收者
                unregisterReceiver(receiver);*/
                // 停止Service
                Intent intentStop = new Intent(DownloadAppActivity.this, NewDownloadAppService.class);
                stopService(intentStop);
                tvDownloadAppClick.setText("开始下载");

                isDownload = false;

                //自动安装apk
                //installAPK(Environment.getExternalStorageDirectory().getPath() + "/imotom/" + downloadappn, context);

                //接收 正在下载状态的广播 后的逻辑
            } else if (ACTION_SET_DOWNLOAD_STATE.equals(action)) {
                //Toast.makeText(context, "下载中", Toast.LENGTH_SHORT).show();
                tvDownloadAppClick.setText("取消下载");
                isDownload = true;
                //接收 取消下载状态的广播 后的逻辑
            } else if (ACTION_SET_PAUSE_STATE.equals(action)) {
                tvDownloadAppClick.setText("下载");
                isDownload = false;
                //接收 固件下载失败的广播 后的逻辑
            } else if (ACTION_DOWNLOAD_ERROR.equals(action)) {
                if(!isDownload) {
                    tvDownloadAppClick.setText("开始下载");
                    String error_text = intent.getStringExtra(EXTRA_DOWNLOAD_ERROR_TEXT);
                    Toast.makeText(context, "软件下载失败！请重试！\n失败信息:" + error_text, Toast.LENGTH_SHORT).show();
                }
                isDownload = false;
            }
        }
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
