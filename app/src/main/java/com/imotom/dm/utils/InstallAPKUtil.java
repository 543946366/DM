package com.imotom.dm.utils;
/*
 * Created by ZhiPeng Huang on 2017-08-29.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class InstallAPKUtil {
    public static void installAPK(String basePath, String downloadAppName,Context context) {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setDataAndType(Uri.parse("file://" + basePath + "/" +downloadAppName + ".apk"), "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
        } else {
            File file = new File(basePath, downloadAppName + ".apk");
            if (file.exists()) {
                openFile(file, context);
            }
        }
    }

    /**
     *重点在这里
     */
    private static void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Uri uriForFile = FileProvider.getUriForFile(var1, var1.getApplicationContext().getPackageName() + ".provider", var0);
            var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        }else{
            var2.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }
        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }
    private static String getMIMEType(File var0) {
        String var1;
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }
}
