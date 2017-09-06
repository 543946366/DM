package com.imotom.dm;
/*
 * Created by ZhiPeng Huang on 2017-09-04.
 */

import org.junit.Assert;
import org.junit.Test;

public class SetWiFiPasswordTest {

    @Test
    public void checkWiFiPasswordTest() {
        String newPasswordTest1 = "88888888";
        String againPasswordTest1 = "88888888";

        String newPasswordTest2 = "";
        String againPasswordTest2 = "";

        String newPasswordTest3 = "12345678";
        String againPasswordTest3 = "12345678";

        String newPasswordTest4 = "8888888";
        String againPasswordTest4 = "8888888";

        String newPasswordTest5 = "18888888";
        String againPasswordTest5 = "88888888";

        String newPasswordTest6 = "";
        String againPasswordTest6 = "88888888";

        String newPasswordTest7 = "123";
        String againPasswordTest7 = "88888888";

        Assert.assertEquals("密码符合规范",checkWiFiPassword(newPasswordTest1,againPasswordTest1));
        Assert.assertEquals("输入的新密码不能为空！",checkWiFiPassword(newPasswordTest2,againPasswordTest2));
        Assert.assertEquals("出于安全考虑，新密码不能为初始密码12345678！",checkWiFiPassword(newPasswordTest3,againPasswordTest3));
        Assert.assertEquals("新密码少于8位数，请重新设置！",checkWiFiPassword(newPasswordTest4,againPasswordTest4));
        Assert.assertEquals("两次密码输入不一样，请重新输入！",checkWiFiPassword(newPasswordTest5,againPasswordTest5));
        Assert.assertEquals("输入的新密码不能为空！",checkWiFiPassword(newPasswordTest6,againPasswordTest6));
        Assert.assertEquals("两次密码输入不一样，请重新输入！",checkWiFiPassword(newPasswordTest7,againPasswordTest7));

    }

    private String checkWiFiPassword(String newPassword, String againPassword) {
        if (newPassword.isEmpty() || newPassword.isEmpty()) {
            return "输入的新密码不能为空！";
        } else if (newPassword.equals("12345678") || againPassword.equals("12345678")) {
            return "出于安全考虑，新密码不能为初始密码12345678！";
        } else if (newPassword.equals(againPassword)) {
            // wifi密码限制长度至少8位数以上
            if (againPassword.length() >= 8) {
                return "密码符合规范";
            } else {
                return "新密码少于8位数，请重新设置！";
            }

        } else {
            return "两次密码输入不一样，请重新输入！";
        }
    }
}
