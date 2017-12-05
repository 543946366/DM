package com.imotom.dm.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.presenter.SetWifiStaSettingsPresenter;
import com.imotom.dm.view.imp.ISetWifiStaSettingsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetWifiStaSettingsActivity extends AppCompatActivity implements ISetWifiStaSettingsView, Consts {

    @BindView(R.id.et_setWifiSta_ssid)
    EditText etSetWifiStaSsid;
    @BindView(R.id.et_setWifiSta_password)
    EditText etSetWifiStaPassword;
    @BindView(R.id.tv_setWifiSta_hint)
    TextView tvSetWifiStaHint;
    @BindView(R.id.btn_setWifiSta_ok)
    Button btnSetWifiStaOk;
    private MaterialDialog materialDialog;
    private SetWifiStaSettingsPresenter presenter = new SetWifiStaSettingsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wifi_sta_settings);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_setWifiSta_ok)
    public void OnClick(){
        presenter.submit();
    }

    @Override
    public void showLoading() {
        materialDialog = new MaterialDialog.Builder(this).title("提交中...").progress(true,0).show();
    }

    @Override
    public void closeLoading() {
        materialDialog.dismiss();
    }

    @Override
    public void showSuccess() {
        Snackbar.make(btnSetWifiStaOk,"提交成功！",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showFailed() {
        Snackbar.make(btnSetWifiStaOk,"提交失败！",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public String getSsid() {
        return etSetWifiStaSsid.getText().toString();
    }

    @Override
    public String getSsidPassword() {
        return etSetWifiStaPassword.getText().toString();
    }

    @Override
    public String getDevIP() {
        return getIntent().getStringExtra(INTENT_deviceURL);
    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
