/*
 * Copyright (C) 2015 Doug Melton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imotom.dm.upnp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.ui.DengLuActivity;
import com.imotom.dm.ui.GuanLiActivity;
import com.imotom.dm.utils.DigestAuthenticationUtil;
import com.imotom.dm.utils.PasswordHelp;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.utils.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.imotom.dm.Consts.Consts.CHONG_QI_TEXT;


public class UPnPDeviceAdapter extends RecyclerView.Adapter<UPnPDeviceAdapter.ViewHolder> {

    /*public interface ItemClickListener {
        void onClick(UPnPDevice item, int position);
    }*/

    private Comparator<UPnPDevice> mComparator = new UPnPDeviceComparator();

    private ArrayList<UPnPDevice> mItems;
    //private ItemClickListener mListener;

    private String myURL;
    private String displayFriendlyName;
    private String displaySerialNumber;
    private String displayModelNumber;
    private Context myContext;
    private String myUerName;
    private String myPassword;

    private Handler myHandler;
    //网页请求URL
    private OkHttpClient myOkHttpClient = new OkHttpClient();

    public UPnPDeviceAdapter(Context context,Handler handler) {
        super();
        myContext = context;
        myHandler = handler;
        //LayoutInflater inflater = LayoutInflater.from(context);
        Picasso picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(false);
        mItems = new ArrayList<>();
        setHasStableIds(false);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private UPnPDevice getItem(int position) {
        return mItems.get(position);
    }

    public void clear() {
        int count = mItems.size();
        mItems.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void add(UPnPDevice item) {
        int index = Collections.binarySearch(mItems, item, mComparator);
        if (index < 0) {
            int position = -index - 1;
            mItems.add(position, item);
            notifyItemInserted(position);
        } else {
            mItems.set(index, item);
            notifyItemChanged(index);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int p) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upnp_device, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        if (viewHolder.ll_upnp_dev_item != null) {
            viewHolder.ll_upnp_dev_item.setOnClickListener(v -> {
                int position = viewHolder.getAdapterPosition();
                /*if (mListener != null) {
                    mListener.onClick(mItems.get(position), position);
                    notifyItemChanged(position);
                }*/
                UPnPDevice upnpDev = mItems.get(position);

                myURL = upnpDev.getPresentationURL();
                //myURL = "http://192.168.63.9:8099";
                displayFriendlyName = upnpDev.getScrubbedFriendlyName();
                displaySerialNumber = upnpDev.getSerialNumber();
                displayModelNumber = upnpDev.getModelNumber();

                Log.d("TAG", upnpDev.getRawXml() + "-----------------" + upnpDev.getServer());

                showDeviceXinxiDialog(upnpDev);
            });
        }
        /*viewHolder.ll_upnp_dev_item.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
				int position = viewHolder.getAdapterPosition();
				if (mListener != null) {
					mListener.onClick(mItems.get(position), position);
					notifyItemChanged(position);
				}
				UPnPDevice upnpDev = mItems.get(position);

				myURL = upnpDev.getPresentationURL();
				//myURL = "http://192.168.63.9:8099";
					//	"" + dd.getDevice().getDetails().getPresentationURI();
				displayFriendlyName = upnpDev.getFriendlyName();
						//"" + dd.getDevice().getDetails().getFriendlyName();
				displaySerialNumber = upnpDev.getSerialNumber();

						//dd.getDevice().getDetails().getSerialNumber();
				L.e(upnpDev.getLocation().getAuthority()+"----------"+upnpDev.getLocation().getHost()+"--------"+upnpDev.getLocation().getPath()+""+upnpDev.getRawUPnP());
				Log.d("TAG",upnpDev.getRawXml()+"-----------------"+upnpDev.getServer());
				Toast.makeText(v.getContext(),myURL,Toast.LENGTH_LONG).show();
				//Toast.makeText(v.getContext(),myURL+"----"+upnpDev.getSerialNumber()+"---"+upnpDev.getRawXml(),Toast.LENGTH_SHORT).show();

				dianJiGuanLi();
			}
		});*/

        return viewHolder;
    }

    /**
     * 弹窗显示设备信息
     *
     * @param deviceDisplay UPnP发现的设备
     */
    private void showDeviceXinxiDialog(UPnPDevice deviceDisplay) {
        final AlertDialog dialog = new AlertDialog.Builder(myContext).create();
        dialog.setTitle("设备信息");
        dialog.setMessage(deviceDisplay.getMyDetailsMsg());
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "管理",
                (authorizationHaderValue, i) -> dianJiGuanLi());
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                (dialogInterface, i) -> {
                });
        dialog.show();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UPnPDevice item = getItem(position);
        if (holder.friendlyName != null && holder.icon != null) {
            String devName = item.getDevName();
            if (TextUtils.isEmpty(devName)) {
                devName = "[unnamed]";
            }
            holder.friendlyName.setText(devName);
            //新的根据型号区分设备种类
            if (item.getModelNumber() == null || item.getModelNumber().equals("")) {
                holder.icon.setImageResource(R.mipmap.cheji_icon);
            } else {
                if (item.getModelNumber().equals(Consts.MT_guoKe_model_number)) {
                    //设置 item图片
                    holder.icon.setImageResource(R.mipmap.camera_icon);
                } else {
                    holder.icon.setImageResource(R.mipmap.cheji_icon);
                }
            }

        }

        //读取upnp设备图标
		/*if (holder.icon != null) {
			if (!TextUtils.isEmpty(item.getIconUrl())) {
				int iconSize = (int) holder.icon.getContext().getResources().getDimension(R.dimen.icon_size);
				picasso.load(item.getIconUrl())
					.error(R.drawable.ic_server_network)
					.resize(iconSize, iconSize)
					.centerInside()
					.into(holder.icon);
			}
			else {
				holder.icon.setImageResource(R.drawable.ic_server_network);
			}
		}*/
    }

	/*private void linkify(TextView view, CharSequence str, String url) {
		if (TextUtils.isEmpty(str) && TextUtils.isEmpty(url)) {
			view.setVisibility(View.GONE);
			return;
		}

		view.setVisibility(View.VISIBLE);
		if (TextUtils.isEmpty(url)) {
			view.setText(str);
			return;
		}

		if (TextUtils.isEmpty(str)) {
			str = url;
		}

		SpannableBuilder builder = new SpannableBuilder(view.getContext());
		builder.append(str, new URLSpan(url));

		view.setText(builder.build());
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}*/

    class ViewHolder extends RecyclerView.ViewHolder {
        //@BindView(R.id.icon)
        @Nullable
        ImageView icon;

        //@BindView(R.id.friendly_name)
        @Nullable
        TextView friendlyName;

        @Nullable
        LinearLayout ll_upnp_dev_item;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            icon = (ImageView) view.findViewById(R.id.icon);
            friendlyName = (TextView) view.findViewById(R.id.friendly_name);
            ll_upnp_dev_item = (LinearLayout) view.findViewById(R.id.ll_upnp_dev_item);
        }

        /*@OnClick(R.id.root)
        void click(View view) {
            int position = getAdapterPosition();
            if (mListener != null) {
                mListener.onClick(mItems.get(position), position);
                notifyItemChanged(position);
            }
        }*/
    }

    /**
     * 点击弹窗的管理按钮
     */
    private void dianJiGuanLi() {
        //提取保存的用户名和用户密码
        myUerName = PasswordHelp.readPassword(myContext)[0];
        myPassword = PasswordHelp.readPassword(myContext)[1];

        //如果之前是非记住密码保存，则跳转到登录界面
        if (myUerName.equals("nullCaoNiMa") ||
                myPassword.equals("null")) {
            Intent meiJiLuTiaoZhuan = new Intent(myContext, DengLuActivity.class);
            meiJiLuTiaoZhuan.putExtra(Consts.INTENT_deviceURL, myURL);
            //获取设备名
            meiJiLuTiaoZhuan.putExtra(Consts.INTENT_display_friendly_name, displayFriendlyName);
            //获取设备序列号
            meiJiLuTiaoZhuan.putExtra(Consts.INTENT_display_serial_number, displaySerialNumber);
            //新的需要传递的数据modelNumber
            meiJiLuTiaoZhuan.putExtra(Consts.INTENT_display_model_number, displayModelNumber);

            myContext.startActivity(meiJiLuTiaoZhuan);
        } else {
            //尝试Http验证登录，
            newHttpDigest();
        }
    }

    private void newHttpDigest() {
        final String url = myURL;
        new Thread(() -> {

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = myOkHttpClient.newCall(request).execute();
                //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                L.e("311:" + response.code() + "------------" + response.message());
                if (response.code() == 401) {
                    L.e("header:" + response.header("WWW-Authenticate"));
                    String authorizationHaderValue = DigestAuthenticationUtil.startDigestGet(response.header("WWW-Authenticate"), myUerName, myPassword, "/");

					/*Map<String, String> maps = getMapByKeyArray(response.header("WWW-Authenticate").split(","));

					maps.put("username", myUerName);
					maps.put("password", myPassword);
					maps.put("nc", "00000002");
					maps.put("cnonce", "6d9a4895d16b3021");
					maps.put("uri", "/");
					maps.put("response", getResponse(maps));

					// 开始拼凑Authorization 头信息
					//旧的StringBuffer，java8 提示更换为StringBuilder（待测试看有无效）
					// StringBuffer authorizationHaderValue = new StringBuffer();
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
							.append(maps.get("cnonce")).append("\"");*/

                    L.e("value:" + authorizationHaderValue);

                    myOkHttpClient = new OkHttpClient();
//创建一个Request
                    request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization",
                                    authorizationHaderValue)
                            .build();
                    response = myOkHttpClient.newCall(request).execute();
                    // 打印响应码
                    System.out.println(response.code());
                    System.out.println("错误：" + response.message());


                    Intent intent = new Intent();
                    if (response.code() == 200) {
                        //mTv.setText("登录成功");
                        intent = new Intent(myContext, GuanLiActivity.class);
                        intent.putExtra(Consts.INTENT_deviceURL, myURL);
                        intent.putExtra(Consts.INTENT_display_friendly_name, displayFriendlyName);
                        intent.putExtra(Consts.INTENT_display_serial_number, displaySerialNumber);
                        intent.putExtra(Consts.INTENT_display_model_number, displayModelNumber);

                    } else {
                        intent = new Intent(myContext, DengLuActivity.class);
                        intent.putExtra(Consts.INTENT_deviceURL, myURL);
                        intent.putExtra("extra_error", "请重新登录");
                        intent.putExtra(Consts.INTENT_display_friendly_name, displayFriendlyName);
                        intent.putExtra(Consts.INTENT_display_serial_number, displaySerialNumber);
                        intent.putExtra(Consts.INTENT_display_model_number, displayModelNumber);
                    }
                    myContext.startActivity(intent);
                    // 打印响应的信息
                    L.e(response.body().toString());

                } else if (response.code() == 200) {
                    //如果上次升级时，没重启成功，则会返回200，并会显示要求重启界面，此时应该执行重启操作
                    L.e(response.body().string());
                    L.e(url);
                    Message message = new Message();
                    message.what = CHONG_QI_TEXT;
                    message.obj = url + "reboot";

                    myHandler.sendMessage(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
