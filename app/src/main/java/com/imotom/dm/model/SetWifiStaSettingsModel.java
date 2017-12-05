package com.imotom.dm.model;
/*
 * Created by HZP on 2017/12/4.
 */

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.imotom.dm.bean.SetWifiStaSettingsJson;
import com.imotom.dm.model.imp.ISetWifiStaSettingsModel;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Response;

public class SetWifiStaSettingsModel implements ISetWifiStaSettingsModel {
    @Override
    public void submit(String ssid, String ssidPassword, String ip, OnSubmitListener onSubmitListener) {
        SetWifiStaSettingsJson setWifiStaSettingsJson = new SetWifiStaSettingsJson();
        setWifiStaSettingsJson.setSsid(ssid);
        setWifiStaSettingsJson.setPassword(ssidPassword);
        String ss = new GsonBuilder().create().toJson(setWifiStaSettingsJson, SetWifiStaSettingsJson.class);

        String url = "http://" + ip + ":8199/set_wifi_sta_settings";
        Observable.create((ObservableOnSubscribe<Boolean>) faShe -> {
            try {
                Response response;
                response = OkHttpUtils
                        .postString()
                        .url(url)
                        .content(ss)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute();
                if (response.code() == 401) {
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Logger.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    String authorizationHaderValue = DigestAuthenticationUtil
                            .startDigestPost(response.header("WWW-Authenticate"), "admin", "admin", "/set_wifi_sta_settings");
                    OkHttpUtils
                            .postString()
                            .url(url)
                            .content(ss)
                            .addHeader("Authorization",
                                    authorizationHaderValue)
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    //Snackbar.make(v,"热点修改失败！请重试！",Snackbar.LENGTH_LONG).show();
                                    faShe.onNext(false);
                                    faShe.onComplete();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e("TAG", response);
                                    //Snackbar.make(v,"热点修改成功！",Snackbar.LENGTH_LONG).show();
                                    faShe.onNext(true);
                                    faShe.onComplete();
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
                faShe.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean o) {
                if (o) {
                    onSubmitListener.submitSuccess();
                } else {
                    onSubmitListener.submitFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                onSubmitListener.submitFailed();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void cancleTasks() {

    }
}
