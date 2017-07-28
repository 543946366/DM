package com.imotom.dm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imotom.dm.Consts.Consts;
import com.imotom.dm.R;
import com.imotom.dm.bean.DeviceOffLine;

import java.util.List;

public class UPnPDeviceOffLineAdapter extends RecyclerView.Adapter<UPnPDeviceOffLineAdapter.MyViewHolder> {

    private List<DeviceOffLine> mDatas;
    private LayoutInflater mInflater;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public UPnPDeviceOffLineAdapter(Context context, List<DeviceOffLine> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(
                R.layout.item_upnp_device, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DeviceOffLine item = getItem(position);
        if (holder.friendlyName != null && holder.icon != null) {
            String devName = item.getDevice_friendly_name();
            if (TextUtils.isEmpty(devName)) {
                devName = "[unnamed]";
            }
            holder.friendlyName.setText(devName + "（离线）\n(" + item.getDevice_model_number_add_serial_number() + ")");
            if (item.getDevice_model_number() == null || item.getDevice_model_number().equals("")) {
                holder.icon.setImageResource(R.mipmap.cheji_off_icon);
            } else {
                if (item.getDevice_model_number().contains(Consts.MT_guoKe_model_number)) {
                    //设置 item图片
                    holder.icon.setImageResource(R.mipmap.camera_off_icon);
                } else {
                    holder.icon.setImageResource(R.mipmap.cheji_off_icon);
                }
            }

        }

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.ll_upnp_dev_item.setOnClickListener(v ->
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(holder.ll_upnp_dev_item, pos);
                    }
            );

            holder.ll_upnp_dev_item.setOnLongClickListener(v ->
            {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemLongClick(holder.ll_upnp_dev_item, pos);
                //removeData(pos);
                return false;
            });

        }
    }

    private DeviceOffLine getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void addData(int position, DeviceOffLine deviceOffLine) {
        mDatas.add(position, deviceOffLine);
        notifyItemInserted(position);
    }


    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int count = mDatas.size();
        mDatas.clear();
        notifyItemRangeRemoved(0, count);
    }

    class MyViewHolder extends ViewHolder {

        TextView friendlyName;
        ImageView icon;
        LinearLayout ll_upnp_dev_item;

        MyViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.icon);
            friendlyName = (TextView) view.findViewById(R.id.friendly_name);
            ll_upnp_dev_item = (LinearLayout) view.findViewById(R.id.ll_upnp_dev_item);


        }
    }
}