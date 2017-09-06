package com.imotom.dm;
/*
 * Created by ZhiPeng Huang on 2017-05-22.
 */


import com.imotom.dm.Consts.Consts;

import org.junit.Assert;
import org.junit.Test;

public class GetIPTest {
    @Test
    public void getIp(){
        String url = "http://192.168.63.9:8199/get_capability";
        String ip = url.replaceAll(Consts.REG,"$1");
        Assert.assertEquals("192.168.63.9",ip);
    }

}
