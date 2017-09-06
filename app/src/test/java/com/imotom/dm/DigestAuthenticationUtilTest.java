package com.imotom.dm;
/*
 * Created by ZhiPeng Huang on 2017-05-24.
 */


import com.imotom.dm.utils.DigestAuthenticationUtil;

import org.junit.Assert;
import org.junit.Test;

public class DigestAuthenticationUtilTest {

    private String myUerName = "admin";
    private String myPassword = "admin";

    @Test
    public void startDigestGetTest(){
        String header = "Digest realm=\"ezbox\",qop=\"auth\",nonce=\"182600005484\",algorithm=\"MD5\",opaque=\"01234567876543210\"";
        String action = "/";
        String authorizationHaderValue = DigestAuthenticationUtil.startDigestGet(header,myUerName,myPassword, action);
        String value = "Digest username=\"admin\", realm=\"ezbox\", nonce=\"182600005484\", uri=\"/\", algorithm=MD5, response=\"b5a5865768b8aac915168e0e002b2aef\", opaque=\"01234567876543210\", qop=auth, nc=00000002, cnonce=\"6d9a4895d16b3021\"";
        Assert.assertEquals(value,authorizationHaderValue);
    }

    @Test
    public void startDigestPostTest(){
        String postHeader = "Digest realm=\"ezbox\",qop=\"auth\",nonce=\"885100008256\",algorithm=\"MD5\",opaque=\"01234567876543210\"";
        String postAction = "/wifi_pwd_retrieve";
        String authorizationHaderValue = DigestAuthenticationUtil.startDigestPost(postHeader,myUerName,myPassword, postAction);
        String postValue = "Digest username=\"admin\", realm=\"ezbox\", nonce=\"885100008256\", uri=\"/wifi_pwd_retrieve\", algorithm=MD5, response=\"dac81e048cdebc445075d3eab40e1376\", opaque=\"01234567876543210\", qop=auth, nc=00000002, cnonce=\"6d9a4895d16b3021\"";
        Assert.assertEquals(postValue,authorizationHaderValue);
    }
}
