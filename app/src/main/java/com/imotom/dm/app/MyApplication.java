package com.imotom.dm.app;
/*
 * Created by ZhiPeng Huang on 2017-07-17.
 */

import android.app.Application;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.litepal.LitePal;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        LitePal.initialize(this);
        //初始化日志框架
        Logger.addLogAdapter(new AndroidLogAdapter());
        //一下均为OKHttp需要
        ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

//        CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(cookieJar1)
                .hostnameVerifier((String hostname, SSLSession session) -> true)
				/*{
					*//*@Override
					public boolean verify(String hostname, SSLSession session)
					{
						return true;
					}*//*
				})*/
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);
        //以上均为OKHttp需要

        //初始化下载器
        FileDownloader.setup(getApplicationContext());
    }
}
