package com.imotom.dm.presenter;
/*
 * Created by HZP on 2017/12/4.
 */

import android.os.Handler;
import android.text.TextUtils;

import com.imotom.dm.model.OnSubmitListener;
import com.imotom.dm.model.SetWifiStaSettingsModel;
import com.imotom.dm.model.imp.ISetWifiStaSettingsModel;
import com.imotom.dm.view.imp.ISetWifiStaSettingsView;


public class SetWifiStaSettingsPresenter {
    private ISetWifiStaSettingsModel iSetWifiStaSettingsModel;
    private ISetWifiStaSettingsView iSetWifiStaSettingsView;
    private Handler handler = new Handler();

    public SetWifiStaSettingsPresenter(ISetWifiStaSettingsView view){
        this.iSetWifiStaSettingsView = view;
        this.iSetWifiStaSettingsModel = new SetWifiStaSettingsModel();
    }

    public void submit(){
        iSetWifiStaSettingsView.showLoading();
        if(TextUtils.isEmpty(iSetWifiStaSettingsView.getSsid()) || TextUtils.isEmpty(iSetWifiStaSettingsView.getSsidPassword())){
            iSetWifiStaSettingsView.closeLoading();
            iSetWifiStaSettingsView.showFailed();
        }else{
            iSetWifiStaSettingsModel.submit(iSetWifiStaSettingsView.getSsid(),
                    iSetWifiStaSettingsView.getSsidPassword(),
                    iSetWifiStaSettingsView.getDevIP(),
                    new OnSubmitListener() {
                        @Override
                        public void submitSuccess() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    iSetWifiStaSettingsView.closeLoading();
                                    iSetWifiStaSettingsView.showSuccess();
                                }
                            });
                        }

                        @Override
                        public void submitFailed() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    iSetWifiStaSettingsView.closeLoading();
                                    iSetWifiStaSettingsView.showFailed();
                                }
                            });

                        }
                    });
        }
    }

    public void destroy() {
        iSetWifiStaSettingsView = null;
        if(iSetWifiStaSettingsModel != null) {
            iSetWifiStaSettingsModel.cancleTasks();
            iSetWifiStaSettingsModel = null;
        }
    }
}
