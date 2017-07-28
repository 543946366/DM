package com.imotom.dm.utils;
/*
 * Created by ZhiPeng Huang on 2017-05-19.
 */

import android.os.Handler;
import android.os.Message;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import static com.orhanobut.logger.Logger.d;
import static com.zhy.http.okhttp.OkHttpUtils.post;

public class DigestAuthenticationUtil {

    //常量
    private static final int OK_TEXT = 4;
    private static final int NO_TEXT = 5;

    public static void startDigest(String url, Handler handler, String action) {
        startDigestPost(url, handler, action, "admin", "admin");

    }

    /**
     * @param url      网址
     * @param handler  handler
     * @param userName 用户名
     * @param password 密码
     */
    public static void startDigestPost(String url, Handler handler, String action, String userName, String password) {

        new Thread(() -> {
            try {

                //创建okHttpClient对象
//创建一个Request  "http://192.168.63.9:8199/wifi_pwd_retrieve"
                //String url = myBaseUrl + "wifi_pwd_retrieve";
                Response response;
                response =
                        post()
                                .url(url)
                                .build()
                                .execute();

                if (response.code() == 401) {
                    d("下面开始666WWW-Authenticate:" + response.header("WWW-Authenticate"));
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    String authorizationHaderValue = startDigestPost(response.header("WWW-Authenticate"), userName, password, action);
                    /*Map<String, String> maps = getMapByKeyArray(response.header("WWW-Authenticate").split(","));

                    maps.put("username", userName);
                    maps.put("password", password);
                    maps.put("nc", "00000002");
                    maps.put("cnonce", "6d9a4895d16b3021");
                    maps.put("uri", action);
                    *//*
                     *  POST请求要HA2 要修改为 HA2 = MD5Object.encrypt("POST:" + "/get_version");
                     *//*
                    maps.put("response", getPOSTResponse(maps));

                    // 开始拼凑Authorization 头信息
                    StringBuilder authorizationHaderValue = new StringBuilder();
                    authorizationHaderValue
                            .append("Digest username=\"")
                            .append(maps.get("username"))
                            .append("\", ")
                            .append("realm=\"")
                            .append(maps.get("realm"))
                            .append("\", ")
                            // .append("nonce=\"").append(maps.get("nonceTime")).append(maps.get("nonce")).append("\", ")
                            .append("nonce=\"").append(maps.get("nonce"))
                            .append("\", ").append("uri=\"").append(maps.get("uri"))
                            .append("\", ").append("algorithm=").append("MD5")
                            .append(", ").append("response=\"")
                            .append(maps.get("response")).append("\", ")
                            .append("opaque=\"").append(maps.get("opaque"))
                            .append("\", ").append("qop=").append(maps.get("qop"))
                            .append(", ").append("nc=").append(maps.get("nc"))
                            .append(", ").append("cnonce=\"")
                            .append(maps.get("cnonce")).append("\"");
*/
                    L.e(authorizationHaderValue);

                    post()
                            .url(url)
                            .addHeader("Authorization",
                                    authorizationHaderValue)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Logger.d("失败：" + e.getMessage());
                                    Message message = new Message();
                                    message.what = NO_TEXT;
                                    message.obj = e.getMessage();
                                    handler.sendMessage(message); // 将Message对象发送出去
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Logger.d("成功:" + response);

                                    Message message = new Message();
                                    message.what = OK_TEXT;
                                    //message.obj = response.split(":")[1].substring(1, response.split(":")[1].length() - 2);
                                    message.obj = response;
                                    handler.sendMessage(message); // 将Message对象发送出去
                                }
                            });
                    // 打印响应码
                    Logger.d(response.code() + "");
                    Headers responseHeadersqq = response.headers();
                    for (int i = 0; i < responseHeadersqq.size(); i++) {
                        Logger.d(responseHeadersqq.name(i) + ": " + responseHeadersqq.value(i));
                    }
                    // 打印响应的信息
                    L.e(response.body().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
        /**
         * @param url      网址
         * @param handler  handler
         * @param userName 用户名
         * @param password 密码
         */

    public static void startDigestPost(String url, Handler handler, String action, String userName, String password, String key,String val) {

        new Thread(() -> {
            try {

                //创建okHttpClient对象
//创建一个Request  "http://192.168.63.9:8199/wifi_pwd_retrieve"
                //String url = myBaseUrl + "wifi_pwd_retrieve";
                Response response;
                response = OkHttpUtils
                        .post()
                        .addParams(key, val)
                        .url(url)
                        .build()
                        .execute();

                if (response.code() == 401) {
                    Logger.d("下面开始666WWW-Authenticate:" + response.header("WWW-Authenticate"));
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Logger.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    String authorizationHaderValue = startDigestPost(response.header("WWW-Authenticate"), userName, password, action);
                    /*Map<String, String> maps = getMapByKeyArray(response.header("WWW-Authenticate").split(","));

                    maps.put("username", userName);
                    maps.put("password", password);
                    maps.put("nc", "00000002");
                    maps.put("cnonce", "6d9a4895d16b3021");
                    maps.put("uri", action);
                    *//*
                     *  POST请求要HA2 要修改为 HA2 = MD5Object.encrypt("POST:" + "/get_version");
                     *//*
                    maps.put("response", getPOSTResponse(maps));

                    // 开始拼凑Authorization 头信息
                    StringBuilder authorizationHaderValue = new StringBuilder();
                    authorizationHaderValue
                            .append("Digest username=\"")
                            .append(maps.get("username"))
                            .append("\", ")
                            .append("realm=\"")
                            .append(maps.get("realm"))
                            .append("\", ")
                            // .append("nonce=\"").append(maps.get("nonceTime")).append(maps.get("nonce")).append("\", ")
                            .append("nonce=\"").append(maps.get("nonce"))
                            .append("\", ").append("uri=\"").append(maps.get("uri"))
                            .append("\", ").append("algorithm=").append("MD5")
                            .append(", ").append("response=\"")
                            .append(maps.get("response")).append("\", ")
                            .append("opaque=\"").append(maps.get("opaque"))
                            .append("\", ").append("qop=").append(maps.get("qop"))
                            .append(", ").append("nc=").append(maps.get("nc"))
                            .append(", ").append("cnonce=\"")
                            .append(maps.get("cnonce")).append("\"");
*/
                    L.e(authorizationHaderValue);

                    OkHttpUtils
                            .post()
                            .url(url)
                            .addHeader("Authorization",
                                    authorizationHaderValue)
                            .addParams(key, val)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Logger.d("失败：" + e.getMessage());
                                    Message message = new Message();
                                    message.what = NO_TEXT;
                                    message.obj = e.getMessage();
                                    handler.sendMessage(message); // 将Message对象发送出去
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Logger.d("成功:" + response);

                                    Message message = new Message();
                                    message.what = OK_TEXT;
                                    //message.obj = response.split(":")[1].substring(1, response.split(":")[1].length() - 2);
                                    message.obj = response;
                                    handler.sendMessage(message); // 将Message对象发送出去
                                }
                            });
                    // 打印响应码
                    Logger.d(response.code() + "");
                    Headers responseHeadersqq = response.headers();
                    for (int i = 0; i < responseHeadersqq.size(); i++) {
                        Logger.d(responseHeadersqq.name(i) + ": " + responseHeadersqq.value(i));
                    }
                    // 打印响应的信息
                    L.e(response.body().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public static String startDigestPost(String header, String userName, String password, String action) {
        //创建okHttpClient对象
        StringBuilder authorizationHaderValue = new StringBuilder();
        try {
            Map<String, String> maps = getMapByKeyArray(header.split(","));

            maps.put("username", userName);
            maps.put("password", password);
            maps.put("nc", "00000002");
            maps.put("cnonce", "6d9a4895d16b3021");
            maps.put("uri", action);
                    /*
                     *  POST请求要HA2 要修改为 HA2 = MD5Object.encrypt("POST:" + "/get_version");
                     */
            maps.put("response", getPOSTResponse(maps));

            // 开始拼凑Authorization 头信息
            authorizationHaderValue
                    .append("Digest username=\"")
                    .append(maps.get("username"))
                    .append("\", ")
                    .append("realm=\"")
                    .append(maps.get("realm"))
                    .append("\", ")
                    // .append("nonce=\"").append(maps.get("nonceTime")).append(maps.get("nonce")).append("\", ")
                    .append("nonce=\"").append(maps.get("nonce"))
                    .append("\", ").append("uri=\"").append(maps.get("uri"))
                    .append("\", ").append("algorithm=").append("MD5")
                    .append(", ").append("response=\"")
                    .append(maps.get("response")).append("\", ")
                    .append("opaque=\"").append(maps.get("opaque"))
                    .append("\", ").append("qop=").append(maps.get("qop"))
                    .append(", ").append("nc=").append(maps.get("nc"))
                    .append(", ").append("cnonce=\"")
                    .append(maps.get("cnonce")).append("\"");

            L.e(authorizationHaderValue.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorizationHaderValue.toString();
    }

    public static String startDigestGet(String header, String userName, String password, String action) {
        //创建okHttpClient对象
//创建一个Request  "http://192.168.63.9:8199/wifi_pwd_retrieve"
        //String url = myBaseUrl + "wifi_pwd_retrieve";
        StringBuilder authorizationHaderValue = new StringBuilder();
        try {
            Map<String, String> maps = getMapByKeyArray(header.split(","));

            maps.put("username", userName);
            maps.put("password", password);
            maps.put("nc", "00000002");
            maps.put("cnonce", "6d9a4895d16b3021");
            maps.put("uri", action);
                    /*
                     *  POST请求要HA2 要修改为 HA2 = MD5Object.encrypt("POST:" + "/get_version");
                     */
            maps.put("response", getResponse(maps));

            // 开始拼凑Authorization 头信息
            authorizationHaderValue
                    .append("Digest username=\"")
                    .append(maps.get("username"))
                    .append("\", ")
                    .append("realm=\"")
                    .append(maps.get("realm"))
                    .append("\", ")
                    // .append("nonce=\"").append(maps.get("nonceTime")).append(maps.get("nonce")).append("\", ")
                    .append("nonce=\"").append(maps.get("nonce"))
                    .append("\", ").append("uri=\"").append(maps.get("uri"))
                    .append("\", ").append("algorithm=").append("MD5")
                    .append(", ").append("response=\"")
                    .append(maps.get("response")).append("\", ")
                    .append("opaque=\"").append(maps.get("opaque"))
                    .append("\", ").append("qop=").append(maps.get("qop"))
                    .append(", ").append("nc=").append(maps.get("nc"))
                    .append(", ").append("cnonce=\"")
                    .append(maps.get("cnonce")).append("\"");

            L.e(authorizationHaderValue.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorizationHaderValue.toString();
    }


    /**
     * 通过HTTP 摘要认证的算法得出response
     *
     * @return String
     */
    private static String getPOSTResponse(Map<String, String> maps) throws Exception {
        String HA1 = MD5Object.encrypt(maps.get("username") + ":"
                + maps.get("realm") + ":" + maps.get("password"));
        System.out.println("HA1:" + HA1);

        String HA2 = MD5Object.encrypt("POST:" + maps.get("uri"));
        //String HA2 = "7e7338873cffb8a3456bc199943348e0";
        System.out.println("HA2:" + HA2);

        String response = MD5Object.encrypt(HA1 + ":" + maps.get("nonce") + ":"
                + maps.get("nc") + ":" + maps.get("cnonce") + ":"
                + maps.get("qop") + ":" + HA2);
        System.out.println(response);
        return response;
    }

    /**
     * 通过HTTP 摘要认证的算法得出response
     *
     * @return String
     */
    private static String getResponse(Map<String, String> maps) throws Exception {
        //HA1的公式为 HA1=MD5(username:realm:password)
        String HA1 = MD5Object.encrypt(maps.get("username") + ":"
                + maps.get("realm") + ":" + maps.get("password"));
        System.out.println("HA1:" + HA1);

        //HA2的公式为 HA2=MD5(method:uri)
        String HA2 = MD5Object.encrypt("GET:" + maps.get("uri"));
        System.out.println("HA2:" + HA2);

        //response的公式为 response=(HA1:nonce:nc:cnonce:qop:HA2)
        String response = MD5Object.encrypt(HA1 + ":" + maps.get("nonce") + ":"
                + maps.get("nc") + ":" + maps.get("cnonce") + ":"
                + maps.get("qop") + ":" + HA2);
        System.out.println(response);
        return response;
    }


    private static String getValueByName(String resourceStr) {
        return resourceStr.substring(resourceStr.indexOf("\"") + 1,
                resourceStr.lastIndexOf("\""));

    }

    private static Map<String, String> getMapByKeyArray(String[] resourceStr) {
        Map<String, String> maps = new HashMap<>(8);
        for (String str : resourceStr) {
            if (str.contains("realm")) {
                maps.put("realm", getValueByName(str));
            } else if (str.contains("qop")) {
                maps.put("qop", getValueByName(str));
            } else if (str.contains("nonce")) {
                maps.put("nonce", getValueByName(str));
                // maps.put("nonce", getValueByName(str, "nonce"));
                // maps.put("nonceTime", getValueByName(str, "nonceTime") + ":");
            } else if (str.contains("opaque")) {
                maps.put("opaque", getValueByName(str));
            }
        }

        return maps;
    }

}
