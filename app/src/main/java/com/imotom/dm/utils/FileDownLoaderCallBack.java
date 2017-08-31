package com.imotom.dm.utils;
/*
 * Created by ZhiPeng Huang on 2017-08-28.
 */

import com.liulishuo.filedownloader.BaseDownloadTask;

public interface FileDownLoaderCallBack {
    void downLoadComplated(BaseDownloadTask task);
    void downLoadError(BaseDownloadTask task, Throwable e);
    void downLoadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes);
}
