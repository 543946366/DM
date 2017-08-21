package com.imotom.dm.Consts;

/*
 * Created by Administrator on 2017-02-11.
 */

public interface Consts {
    /**
     * Activity发出的广播：开始下载文件或者取消
     */
    String ACTION_DOWNLOAD_OR_PAUSE = "com.imotom.intent.action.DOWNLOAD_OR_PAUSE";
    /**
     * Activity发出的广播：询问后台是否在下载
     */
    String ACTION_IS_DOWNLOAD = "com.imotom.intent.action.IS_DOWNLOAD";
    /**
     * Service发出的广播：下载成功
     */
    String ACTION_DOWNLOAD_SUCCEED = "com.imotom.intent.action.DOWNLOAD_SUCCEED";
    /**
     * Service发出的广播：下载失败
     */
    String ACTION_DOWNLOAD_ERROR = "com.imotom.intent.action.DOWNLOAD_ERROR";

    /**
     * Service发出的广播：更新下载进度
     */
    String ACTION_UPDATE_PROGRESS = "com.imotom.intent.action.UPDATE_PROGRESS";
    /**
     * Service发出的广播：设置界面为下载状态
     */
    String ACTION_SET_DOWNLOAD_STATE = "com.imotom.intent.action.SET_DOWNLOAD_STATE";
    /**
     * Service发出的广播：设置界面为取消状态
     */
    String ACTION_SET_PAUSE_STATE = "com.imotom.intent.action.SET_PAUSE_STATE";
    /**
     * Extra：文件下载到的进度的百分比
     */
    String EXTRA_PERCENT = "com.imotom.intent.extra.PERCENT";
    /**
     * Extra：文件下载失败的信息
     */
    String EXTRA_DOWNLOAD_ERROR_TEXT = "com.imotom.intent.extra.DOWNLOAD_ERROR_TEXT";
    /**
     * Extra：文件下载版本名字的信息
     */
    String EXTRA_DOWNLOAD_GUJIAN_NAME_TEXT = "com.imotom.intent.extra.DOWNLOAD_GUJIAN_NAME_TEXT";

    /**
     * ------------------------------------------------------------------------------------
     * 网址
     */
    //导航APP下载网址
    //String URL_downloadNavAPPUrl = "http://120.27.94.20:10080/vendor/imotom/APP/Android/navigation_v1.0.5.apk";
    String URL_downloadNavAPPUrl = "http://120.27.94.20:10080/vendor/imotom/APP/Android/" + Consts.NAVIGATION_APP_NAME;
    //国科设备升级文档查询
    String URL_inquire_guoKe_dev_update_doc = "http://120.27.94.20:10080/vendor/imotom/MT1828/upgrade.txt";
    //车机设备升级文档查询
    String URL_inquire_cheJi_dev_update_doc = "http://120.27.94.20:10080/vendor/imotom/MT1845/upgrade.txt";
    //国科APP下载网址
    String URL_downloadGuoKeAPPurl = "http://shouji.360tpcdn.com/170208/c876925ccfa83020f6a8bb4703b93a02/zxc.com.gkdvr_21.apk";

    /**
     * intent中携带的数据
     * ———————————————————————————————————————————————————————————————————————————————————————————————————————————
     */
    //设备的访问网址
    String INTENT_deviceURL = "device_url";
    //设备名
    String INTENT_display_friendly_name = "display_friendly_name";
    //设备序列号
    String INTENT_display_serial_number = "display_serial_number";
    //设备
    String INTENT_display_model_number = "display_model_number";

    String INTENT_display_model_number_add_serial_number = "display_model_number_add_serial_number";


    /**
     *  设备  设备序列号（暂定）
     * ------------------------------------------------------------------------------------
     */

    //国科设备modelNumber（区分不同种类设备）
    String MT_guoKe_model_number = "MT1828";
    //车机设备
    String MT_cheJi_model_number = "MT1845";

    /*
     * String
     *  --------------------------------------------------------------------------
     */
    String DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER = "device_model_number_add_serial_number";
    /**
     *  正则表达式
     *  --------------------------------------------------------------------------
     */
    //提取网址IP的正则表达式
    String REG = ".*\\/\\/([^\\/\\:]*).*";

    /**
     * 常量
     * ---------------------------------------------------------------------------
     */
    //传送常量
    int CHUANG_SONG = 3;
    //下载常量
    int XIA_ZAI = 2;
    //常量
    int OK_TEXT = 4;
    int NO_TEXT = 5;
    //下个版本号
    int NEXT_BANBENHAO_TEXT = 6;
    /**
     * 提示用户输入框输入账号或者密码不能为空 -- 常量
     */
    int UPDATE_TEXT = 7;
    //登录失败常量
    int SHI_BAI_TEXT = 8;
    //登录成功常量
    int CHENG_GONG_TEXT = 9;
    //重启
    int CHONG_QI_TEXT = 10;
    /**
     * appName
     * ---------------------------------------------------------------------------------------
     */
    String NAVIGATION_APP_NAME= "navigation.apk";
}
