package com.imotom.dm.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.service.DownloadAPPService;

import java.io.File;

public class DownAPPActivity extends AppCompatActivity implements View.OnClickListener,Consts {


    private ProgressBar progress1;
    Button btn;
    Button btn_quXiao;
    //权限申请常量
    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;

    private boolean isDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_app);
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
        // 激活Service
        Intent intent = new Intent(this, DownloadAPPService.class);
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
    }

    private void initView() {
        btn = (Button) findViewById(R.id.DownAPP_btn_down);
        btn.setOnClickListener(this);
        progress1 = (ProgressBar)findViewById(R.id.DownAPP_pb);
        progress1.setMax(100);
        btn_quXiao = (Button)findViewById(R.id.DownAPP_btn_cancel);
        btn_quXiao.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.DownAPP_btn_down:
                progress1.setVisibility(View.VISIBLE);
                //downloadFile();
                //btn_xiaZai.setVisibility(View.INVISIBLE);
                /*
                 * 向Service发送点击了下载按钮的广播
                 */
                sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_OR_PAUSE).putExtra(EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT, URL_downloadGuoKeAPPurl));

                break;
        }
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
                int percent = intent.getIntExtra(EXTRA_PERCENT, 0);
                Log.d("TAG", percent + "");
                progress1.setProgress(percent);
                //接收 固件下载成功的广播后的逻辑
            } else if (ACTION_DOWNLOAD_SUCCEED.equals(action)) {
                progress1.setProgress(0);
                Toast.makeText(DownAPPActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                /*// 取消注册广播接收者
                unregisterReceiver(receiver);*/
                // 停止Service
                Intent intentStop = new Intent(DownAPPActivity.this, DownloadAPPService.class);
                stopService(intentStop);
                btn.setText("开始下载");

                progress1.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.INVISIBLE);

                isDownload = false;

                //自动安装apk
                installAPK(Environment.getExternalStorageDirectory().getPath() + "/imotom/" + "zxc.com.gkdvr_21.apk", context);

                //接收 正在下载状态的广播 后的逻辑
            } else if (ACTION_SET_DOWNLOAD_STATE.equals(action)) {
                btn.setVisibility(View.VISIBLE);
                progress1.setVisibility(View.VISIBLE);
                Toast.makeText(context, "下载中", Toast.LENGTH_SHORT).show();
                btn.setText("取消下载");
                isDownload = true;
                //接收 取消下载状态的广播 后的逻辑
            } else if (ACTION_SET_PAUSE_STATE.equals(action)) {
                btn.setText("下载");
                progress1.setProgress(0);
                progress1.setVisibility(View.INVISIBLE);
                isDownload = false;
                //接收 固件下载失败的广播 后的逻辑
            } else if (ACTION_DOWNLOAD_ERROR.equals(action)) {
                if(!isDownload) {
                    String error_text = intent.getStringExtra(EXTRA_DOWNLOAD_ERROR_TEXT);
                    Toast.makeText(context, "软件下载失败！请重试！\n失败信息:" + error_text, Toast.LENGTH_SHORT).show();
                }
                isDownload = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //取消广播接收者
        unregisterReceiver(receiver);

        if(!isDownload){
            // 停止Service
            Intent intentStop = new Intent(DownAPPActivity.this, DownloadAPPService.class);
            stopService(intentStop);
        }
        super.onDestroy();
    }

    private void installAPK(String path, Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intents);
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/imotom", "zxc.com.gkdvr_21.apk");
            if (file.exists()) {
                openFile(file, context);
            }
        }
    }

    /**
     *重点在这里
     */
    private void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Uri uriForFile = FileProvider.getUriForFile(var1, var1.getApplicationContext().getPackageName() + ".provider", var0);
            var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        }else{
            var2.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }
        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }
    private String getMIMEType(File var0) {
        String var1;
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
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
