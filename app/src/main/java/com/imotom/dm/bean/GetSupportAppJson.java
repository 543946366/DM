package com.imotom.dm.bean;
/*
 * Created by ZhiPeng Huang on 2017-08-22.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSupportAppJson {

    private List<SupportAppBean> support_app;

    public List<SupportAppBean> getSupport_app() {
        return support_app;
    }

    public void setSupport_app(List<SupportAppBean> support_app) {
        this.support_app = support_app;
    }

    public static class SupportAppBean {
        /**
         * name : GKDVR
         * android_download_url : http://shouji.360tpcdn.com/170208/c876925ccfa83020f6a8bb4703b93a02/zxc.com.gkdvr_21.apk
         * package : zxc.com.gkdvr
         * introduction : 应用介绍
         * ios_download_url : https://itunes.apple.com/cn/app/gkdvr/id1127795700?mt=8
         * url_schema : QQ41e4efec://
         */

        private String name;
        private String android_download_url;
        @SerializedName("package")
        private String packageX;
        private String introduction;
        private String ios_download_url;
        private String url_schema;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAndroid_download_url() {
            return android_download_url;
        }

        public void setAndroid_download_url(String android_download_url) {
            this.android_download_url = android_download_url;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getIos_download_url() {
            return ios_download_url;
        }

        public void setIos_download_url(String ios_download_url) {
            this.ios_download_url = ios_download_url;
        }

        public String getUrl_schema() {
            return url_schema;
        }

        public void setUrl_schema(String url_schema) {
            this.url_schema = url_schema;
        }
    }
}
