package com.imotom.dm.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.imotom.dm.R;
import com.imotom.dm.utils.FileUtils;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.imotom.dm.Consts.Consts.INTENT_display_model_number_add_serial_number;

public class OffLineListActivity extends AppCompatActivity {

    @BindView(R.id.btn_offLineList_downloadGuoKeAPP)
    Button btn_guoKe;
    @BindView(R.id.btn_offLineList_downloadDaoHang)
    Button btn_daoHang;
    @BindView(R.id.btn_offLineList_downloadApp1845)
    Button btn_app1845;

    private String display_model_number_add_serial_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line_list);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        display_model_number_add_serial_number = getIntent().getStringExtra(INTENT_display_model_number_add_serial_number);

        if(getIntent().getFlags() == 1845){
            btn_daoHang.setVisibility(View.VISIBLE);
            btn_app1845.setVisibility(View.VISIBLE);

        }else  if (getIntent().getFlags() == 1828){
            btn_guoKe.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.btn_offLineList_checkDevVersion,R.id.btn_offLineList_downloadGuoKeAPP,R.id.btn_offLineList_downloadDaoHang,R.id.btn_offLineList_downloadApp1845})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_offLineList_checkDevVersion:
                //查看设备版本
                if(getIntent().getFlags() == 1845){
                    startActivity(new Intent(OffLineListActivity.this,OffLineCheckDevVersionActivity.class)
                            .putExtra(INTENT_display_model_number_add_serial_number,
                                    getIntent().getStringExtra(INTENT_display_model_number_add_serial_number))
                            .setFlags(1845));
                }else  if (getIntent().getFlags() == 1828){
                    startActivity(new Intent(OffLineListActivity.this,OffLineCheckDevVersionActivity.class)
                            .putExtra(INTENT_display_model_number_add_serial_number,
                                    getIntent().getStringExtra(INTENT_display_model_number_add_serial_number))
                    .setFlags(1828));
                }


                break;

            case R.id.btn_offLineList_downloadGuoKeAPP:
                startActivity(new Intent(OffLineListActivity.this,DownAPPActivity.class));
                break;

            case R.id.btn_offLineList_downloadDaoHang:
                startActivity(new Intent(OffLineListActivity.this,DownloadNavigationAPPActivity.class));
                break;

            case R.id.btn_offLineList_downloadApp1845:
                FileUtils.writeTxtToFile(display_model_number_add_serial_number, Environment.getExternalStorageDirectory().getPath() + "/imotom/", "DeviceOffLineNumber.txt");

                app1845();
                break;
        }
    }

    private void app1845() {

        try {
            Intent intent = this.getPackageManager().getLaunchIntentForPackage("com.imotom.location");
            //传送设备唯一识别码并标记 103
            intent.setFlags(103);
            intent.putExtra(INTENT_display_model_number_add_serial_number,display_model_number_add_serial_number);
            Logger.d(display_model_number_add_serial_number);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "没有安装该APP", Toast.LENGTH_LONG).show();
            showApp1845ErrorDialog();
        }
    }

    private void showApp1845ErrorDialog() {

        //本地保存设备IP地址
        //String reg = ".*\\/\\/([^\\/\\:]*).*";

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage("APP1845启动失败，或者还没下载，请重新下载后再试！");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "进入下载",
                //TODO
                (dialogInterface, i) -> startActivity(new Intent(this, DownloadNavigationAPPActivity.class)));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (dialogInterface, i) -> {
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
