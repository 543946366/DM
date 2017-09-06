package com.imotom.dm;
/*
 * Created by ZhiPeng Huang on 2017-09-04.
 */

import com.google.gson.GsonBuilder;
import com.imotom.dm.bean.GetCapabilityJson;
import com.imotom.dm.bean.GetSupportAppJson;
import com.imotom.dm.bean.GetSystemInfoJson;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class JsonUtilTest {

    @Test
    public void GetcapablilityJsonTest() {
        String testData = "68\n" +
                "{\"capability\":[\"get_version\",\"get_system_info\",\"set_system_info\",\"wifi_pwd_retrieve\",\"wifi_pwd_update\"]}";

        String capablilityList = "";
        int jsonSize = testData.indexOf("{");
        String jsonContent ;
        if(jsonSize == 0){
            jsonContent = testData;
        }else {
            jsonContent = testData.substring(jsonSize);
        }

        GetCapabilityJson getCapabilityJson = new GsonBuilder().create().fromJson(jsonContent, GetCapabilityJson.class);
        for (String t : getCapabilityJson.getCapability()) {
            capablilityList = capablilityList.concat(t);
        }

        Assert.assertEquals(true,capablilityList.contains("get_version"));
        Assert.assertEquals(true,capablilityList.contains("wifi_pwd_retrieve"));
        Assert.assertEquals(true,capablilityList.contains("wifi_pwd_update"));
        Assert.assertEquals(false,capablilityList.contains("update_time"));
        Assert.assertEquals(true,capablilityList.contains("get_system_info"));
        Assert.assertEquals(true,capablilityList.contains("set_system_info"));

    }

    @Test
    public void getSystemInfoJsonTest() {
        String testData = "65\n" +
                "{\"sn\":\"MT1845000005\",\"hwid\":\"MT1845V12\",\"swid\":\"1.0.0.1\",\"mac\":\"3c:33:00:00:00:05\",\"stm32_ver\":\"1.0\"}";

        int jsonSize = testData.indexOf("{");
        String jsonContent ;
        if(jsonSize == 0){
            jsonContent = testData;
        }else {
            jsonContent = testData.substring(jsonSize);
        }

        GetSystemInfoJson systemInfoBean = new GsonBuilder().create().fromJson(jsonContent, GetSystemInfoJson.class);
        Assert.assertEquals("MT1845000005",systemInfoBean.getSn());
        Assert.assertEquals("MT1845V12",systemInfoBean.getHwid());
        Assert.assertEquals("1.0.0.1",systemInfoBean.getSwid());
        Assert.assertEquals("3c:33:00:00:00:05",systemInfoBean.getMac());
        Assert.assertEquals("1.0",systemInfoBean.getStm32_ver());

    }

    @Test
    public void getSupportAppJsonTest() {
        String testData = "{\n" +
                "    \"support_app\": [\n" +
                "        {\n" +
                "            \"name\": \"GKDVR\",\n" +
                "            \"android_download_url\": \"http://shouji.360tpcdn.com/170208/c876925ccfa83020f6a8bb4703b93a02/zxc.com.gkdvr_21.apk\",\n" +
                "            \"package\": \"zxc.com.gkdvr\",\n" +
                "            \"ios_download_url\": \"https://itunes.apple.com/cn/app/gkdvr/id1127795700?mt=8\",\n" +
                "            \"url_schema\": \"QQ41e4efec://\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        GetSupportAppJson getSupportAppJson = new GsonBuilder().create().fromJson(testData, GetSupportAppJson.class);
        List<GetSupportAppJson.SupportAppBean> supportAppBeanList = getSupportAppJson.getSupport_app();
        GetSupportAppJson.SupportAppBean supportAppBean = supportAppBeanList.get(0);

        Assert.assertEquals("GKDVR",supportAppBean.getName());
        Assert.assertEquals("http://shouji.360tpcdn.com/170208/c876925ccfa83020f6a8bb4703b93a02/zxc.com.gkdvr_21.apk",supportAppBean.getAndroid_download_url());
        Assert.assertEquals("zxc.com.gkdvr",supportAppBean.getPackageX());
        Assert.assertEquals("https://itunes.apple.com/cn/app/gkdvr/id1127795700?mt=8",supportAppBean.getIos_download_url());
        Assert.assertEquals("QQ41e4efec://",supportAppBean.getUrl_schema());
    }
}
