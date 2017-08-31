package com.imotom.dm.service;
/*
 * Created by ZhiPeng Huang on 2017-08-29.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.utils.InstallAPKUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.orhanobut.logger.Logger;

import java.math.BigDecimal;

public class NewDownloadService extends Service implements Consts {

    private long Progress;

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver;
    /**
     * 提交下载进度的线程的循环条件
     */
    private boolean isRunning;
    /**
     * 当前是否为下载状态
     */
    private boolean isDownload;

    private BaseDownloadTask baseDownloadTask;
    /**
     * 下载软件网址
     */
    private String downloadAppURL;
    private String downloadAppName;

    @Override
    public void onCreate() {
        // 注册接收activity的广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DOWNLOAD_OR_PAUSE);
        filter.addAction(ACTION_IS_DOWNLOAD);
        registerReceiver(receiver, filter);



    }

    /**
     * 广播接收者，接收activity后进行的操作
     */
    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            // 获取广播中的Intent的Action
            String action = intent.getAction();
            // 判断Action
            if (ACTION_DOWNLOAD_OR_PAUSE.equals(action)) {
                Logger.e(baseDownloadTask.getStatus()+"");
                if(baseDownloadTask.getStatus() == FileDownloadStatus.started || baseDownloadTask.getStatus() == FileDownloadStatus.connected
                        || baseDownloadTask.getStatus() == FileDownloadStatus.pending
                        || baseDownloadTask.getStatus() == FileDownloadStatus.progress) {
                    // 发送广播，告知Activity已经暂停
                    Intent intentStop = new Intent();
                    intentStop.setAction(ACTION_SET_PAUSE_STATE);
                    sendBroadcast(intentStop);
//取消下载的网络请求
                    Logger.d("pause");
                    baseDownloadTask.pause();
                }else if(baseDownloadTask.getStatus() == FileDownloadStatus.paused){
                    baseDownloadTask.reuse();
                    baseDownloadTask.start();
                }else if(baseDownloadTask.getStatus() == FileDownloadStatus.INVALID_STATUS){
                    Logger.d("start");
                    baseDownloadTask.start();

                }
            }else if(ACTION_IS_DOWNLOAD.equals(action)){ //回应activity询问当前是否在下载状态的请求
                if (isDownload){
                    sendBroadcast(new Intent().setAction(ACTION_SET_DOWNLOAD_STATE));
                }else {
                    sendBroadcast(new Intent().setAction(ACTION_SET_PAUSE_STATE));
                }
            }
        }

    }


    @Override
    public void onDestroy() {
        // 停止更新进度的线程
        //stopUpdateThread();
        // 取消注册广播接收者
        unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadAppName = intent.getStringExtra(INTENT_download_app_name);
        downloadAppURL = intent.getStringExtra(INTENT_download_app_URL);
        Logger.d(downloadAppName + " ===========" + downloadAppURL);
        baseDownloadTask = FileDownloader.getImpl().create(downloadAppURL)
                .setPath(Environment.getExternalStorageDirectory().getPath() + "/imotom/" + downloadAppName + ".apk")
                .setAutoRetryTimes(2)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
// 发送广播，告知Activity正在下载
                        sendBroadcast(new Intent().setAction(ACTION_SET_DOWNLOAD_STATE));

                        isDownload = true;
                        // 开启更新进度的线程
                        //startUpdateThread();
                        Logger.d("go");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Progress = (long)soFarBytes*100/(long)totalBytes;
                        Logger.d(soFarBytes + "\n" + totalBytes + "\n" + Progress);
                        String progressText = bytes2kb(soFarBytes) + "/" + bytes2kb(totalBytes);
                        //发送下载进度百分比的广播
                        sendBroadcast(new Intent().setAction(ACTION_UPDATE_PROGRESS)
                                .putExtra(EXTRA_PERCENT, Progress)
                                .putExtra(EXTRA_PERCENT_TEXT,progressText));
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //stopUpdateThread();
                        sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_SUCCEED));
                        Logger.d("下载完成");

                        InstallAPKUtil.installAPK(Environment.getExternalStorageDirectory().getPath() + "/imotom",downloadAppName,NewDownloadService.this);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
// 发送广播，告知Activity已经暂停
                        Intent intentStop = new Intent();
                        intentStop.setAction(ACTION_SET_PAUSE_STATE);
                        sendBroadcast(intentStop);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        // 停止更新进度的线程
                        //stopUpdateThread();
                        sendBroadcast(new Intent().setAction(ACTION_DOWNLOAD_ERROR).putExtra(EXTRA_DOWNLOAD_ERROR_TEXT,e.getMessage()));
                        Logger.e(e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Logger.e("warn");
                        task.reuse();
                        task.pause();
// 发送广播，告知Activity已经暂停
                        Intent intentStop = new Intent();
                        intentStop.setAction(ACTION_SET_PAUSE_STATE);
                        sendBroadcast(intentStop);
                    }
                });

        Logger.e(baseDownloadTask.getStatus() +"");
        return START_NOT_STICKY;
    }

    private String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }
}
