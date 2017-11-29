package com.imotom.dm.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.imotom.dm.upnp.MainActivity;
import com.imotom.dm.R;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //占满整个界面，让SplashActivity真正占满全屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //SplashActivityPermissionsDispatcher.jumpNavWithCheck(SplashActivity.this);
        //SplashActivityPermissionsDispatcher.needPerWithCheck(this);
        SplashActivityPermissionsDispatcher.needPerWithCheck(this);

    }

    private void jump() {
        new Handler().postDelayed(() ->{
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
        },1500);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needPer() {
        jump();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /*@NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needPer() {
        jump();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }*/
}
