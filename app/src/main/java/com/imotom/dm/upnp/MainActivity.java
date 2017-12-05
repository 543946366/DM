package com.imotom.dm.upnp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.adapter.UPnPDeviceOffLineAdapter;
import com.imotom.dm.bean.DeviceOffLine;
import com.imotom.dm.ui.NewOffLineListActivity;
import com.imotom.dm.utils.PasswordHelp;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Consts {

    //UPnp协议的适配器
    UPnPDeviceAdapter mAdapter;
    //离线用的adapter
    UPnPDeviceOffLineAdapter uPnPDeviceOffLineAdapter;
    @BindView(R.id.recycler)
    RecyclerView vRecycler;
    //离线用的RecyclerVIew
    @BindView(R.id.rv_main_offLine)
    RecyclerView offLineRecyclerView;

    //离线设备数据
    List<DeviceOffLine> deviceOffLineList = new ArrayList<>();

    //处理离线设备的添加和移除
    private Handler removeDataHandler = new MyMainHandler(MainActivity.this);

    private static class MyMainHandler extends Handler {
        private final WeakReference<MainActivity> myActivity;

        private MyMainHandler(MainActivity myActivity) {
            this.myActivity = new WeakReference<>(myActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = myActivity.get();
            switch (msg.what) {
                case OK_TEXT:
                    // 在这里可以进行UI操作
                    activity.uPnPDeviceOffLineAdapter.removeData((Integer) msg.obj);
                    break;

                case NO_TEXT:
                    //activity.uPnPDeviceOffLineAdapter.addData(msg.arg1, (DeviceOffLine) msg.obj);
                    break;

                case CHONG_QI_TEXT:
                    //http://192.168.43.1:8099/reboot
                    String u = (String) msg.obj;
                    L.e("107-----------" + u);
                    new MaterialDialog.Builder(activity)
                            .title("登录失败")
                            .content("上次升级还未重启系统文件，需要马上重启系统文件！是否马上重启?")
                            .positiveText("重启")
                            .onPositive(
                                    (dialog, which) -> OkHttpUtils//
                                            .post()//
                                            .url(u)//
                                            .build()//
                                            .execute(new StringCallback() {
                                                @Override
                                                public void onError(Call call, Exception e, int id) {
                                                    Toast.makeText(activity, "重启失败！\n" + "失败信息："
                                                            + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }

                                                @Override
                                                public void onResponse(String response, int id) {
                                                    Toast.makeText(activity, "重启设备成功！\n" + response, Toast.LENGTH_SHORT).show();

                                                }

                                            })
                            )
                            .show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ButterKnife.bind(this);

        //设置标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //设置标题栏左上角菜单图标的动画效果
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //同步动画
        toggle.syncState();

        //设置侧边栏的选项点击
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
    }

    private void init() {
        //初始化在线设备显示的adapter
        mAdapter = new UPnPDeviceAdapter(this, removeDataHandler);
        vRecycler.setLayoutManager(new LinearLayoutManager(this));
        vRecycler.setVisibility(View.INVISIBLE);
        vRecycler.setAdapter(mAdapter);

        liXianDev();
    }

    /**
     * 设置离线设备选项
     */
    private void liXianDev() {
        //新的业务逻辑为离线设备添加跳转
        //uPnPDeviceOffLineAdapter.clear();
        deviceOffLineList = DataSupport.findAll(DeviceOffLine.class);
        uPnPDeviceOffLineAdapter = new UPnPDeviceOffLineAdapter(MainActivity.this, deviceOffLineList);
        offLineRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offLineRecyclerView.setVisibility(View.VISIBLE);
        offLineRecyclerView.setAdapter(uPnPDeviceOffLineAdapter);
        //设置rv动画
        offLineRecyclerView.getItemAnimator().setAddDuration(0);
        offLineRecyclerView.getItemAnimator().setChangeDuration(0);
        offLineRecyclerView.getItemAnimator().setMoveDuration(0);
        offLineRecyclerView.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) offLineRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        uPnPDeviceOffLineAdapter.setOnItemClickLitener(new UPnPDeviceOffLineAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(MainActivity.this, NewOffLineListActivity.class)
                        .putExtra(INTENT_display_model_number_add_serial_number,
                                deviceOffLineList.get(position).getDevice_model_number_add_serial_number()));
            }

            @Override
            public void onItemLongClick(View view, int position) {

                new MaterialDialog.Builder(MainActivity.this)
                        .title("删除设备")
                        .content("是否删除离线设备:\n" + deviceOffLineList.get(position).getDevice_model_number_add_serial_number())
                        .positiveText("删除")
                        .onPositive(
                                (dialog, which) -> DataSupport.deleteAll(DeviceOffLine.class,
                                        DEVICE_MODEL_NUMBER_ADD_SERIAL_NUMBER + "=?",
                                        deviceOffLineList.get(position).getDevice_model_number_add_serial_number())
                        )
                        .negativeText("取消")
                        .onNegative(
                                (dialog, which) -> dialog.dismiss()
                        )
                        .show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        searchUpnpDev();
    }

    private void searchUpnpDev() {
        // 根据热点是否开启，执行通过IP地址获取设备XML文档里的UPNP协议
        /*if (isWifiApEnabled()) {
            Observable.create(new ObservableOnSubscribe<UPnPDevice>() {
                @Override
                public void subscribe(ObservableEmitter<UPnPDevice> e) throws Exception {
                    for (String ip : getConnectedIP()) {
                        String receivedString = "NOTIFY * HTTP/1.1\r\n" +
                                "HOST: 239.255.255.250:1900\r\n" +
                                "CACHE-CONTROL: max-age=1800\r\n" +
                                "LOCATION: http://" + ip + ":8099/JiahuaDevice.xml\r\n" +
                                "NT: upnp:rootdevice\r\n" +
                                "NTS: ssdp:alive\r\n" +
                                "USN: uuid:20170823-1538-0025-1987-6001944a9b3d::upnp:rootdevice";
                        UPnPDevice device = UPnPDevice.getInstance(receivedString);
                        if (device != null) {
                            e.onNext(device);
                        }
                    }
                }
        }*/
//创建一个上游 Observable：
        Observable<UPnPDevice> observable;
        //TODO 根据热点是否开启，执行通过IP地址获取设备XML文档里的UPNP协议
        if (isWifiApEnabled()) {
            observable = Observable.create(e -> {
                for (String ip : getConnectedIP()) {
                    Logger.i(ip);
                    String receivedString =
                            "LOCATION: http://" + ip + ":8099/JiahuaDevice.xml";
                    UPnPDevice device = UPnPDevice.getInstance(receivedString);
                    if (device != null) {
                        e.onNext(device);
                    }
                }
            });
        } else {
            observable = new UPnPDeviceFinder().observe();
        }
        observable
                .filter(device -> {
                    try {

                        Logger.d("=========");
                        device.downloadSpecs();
                    } catch (Exception e) {
                        // Ignore errors。
                        Logger.d("Error: " + e);
                    }
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(device -> {
                    // This is the first device found.
                    if (mAdapter.getItemCount() == 0) {
                        vRecycler.setAlpha(0f);
                        vRecycler.setVisibility(View.VISIBLE);
                        vRecycler.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setStartDelay(100)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                    }

                    if (TextUtils.isEmpty(device.getRawXml())) {
                        Logger.e(device.getRawXml());
                        return;
                    }
                    try {
                        //搜索到的Upnp设备含有车机或者国科设备序列号才显示在线
                        if (device.getManufacturer().equals(Manufacturer_Imotom) || device.getManufacturer().equals(Manufacturer_Jiahua)) {
                            mAdapter.add(device);
                            //offLineAdapter.remove(deviceOffLine);
                            //如果upnp设备在线，则移除离线设备的显示
                            if (deviceOffLineList.size() > -1) {
                                offLineRecyclerView.setVisibility(View.VISIBLE);
                                for (DeviceOffLine deviceOffLine : deviceOffLineList) {
                                    int t = -1;
                                    if (deviceOffLine.getDevice_model_number_add_serial_number()
                                            .equals(device.getModelNumber() + device.getSerialNumber())) {
                                        Logger.v(deviceOffLine.getDevice_model_number_add_serial_number());
                                        t = deviceOffLineList.indexOf(deviceOffLine);

                                        if (t != -1) {
                                            Message message = new Message();
                                            message.what = OK_TEXT;
                                            message.obj = t;
                                            removeDataHandler.sendMessage(message);
                                        }
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignore errors
                        Logger.d("Error: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //设置右上的点击响应功能
        if (id == R.id.action_search) {
            //startActivity(new Intent(MainActivity.this, DengLuActivity.class));
            mAdapter.clear();
            uPnPDeviceOffLineAdapter.clear();
            liXianDev();

            //uPnPDeviceOffLineAdapter.removeData(deviceOffLineList.indexOf(deviceOffLine));
            searchUpnpDev();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quXiaoDengLu) {
            PasswordHelp.savePassword(MainActivity.this, "nullCaoNiMa", "null", true);
        } else if (id == R.id.nav_ruJianBanBen) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 检查是否开启Wifi热点
     *
     * @return 返回true表示热点已开启，返回false表示热点已关闭
     */
    public boolean isWifiApEnabled() {
        try {
            WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert mWifiManager != null;
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开热点手机获得其他连接手机IP的方法
     *
     * @return 其他手机IP 数组列表
     */
    public ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIp = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String ip = splitted[0];
                    if (!ip.equalsIgnoreCase("ip")) {
                        connectedIp.add(ip);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connectedIp;
    }
}
