package com.imotom.dm.adapter;
/*
 * Created by ZhiPeng Huang on 2017-08-26.
 */

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imotom.dm.R;
import com.imotom.dm.bean.GetSupportAppJson;

import java.util.List;

public class SupportAppAdapter extends BaseQuickAdapter<GetSupportAppJson.SupportAppBean, BaseViewHolder> {


    public SupportAppAdapter(List<GetSupportAppJson.SupportAppBean> supportAppBeanList) {
        super(R.layout.item_new_guan_li_support_app,supportAppBeanList);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, GetSupportAppJson.SupportAppBean item) {
        viewHolder.setText(R.id.tv_item_newGuanLi, item.getName());

    }
}
