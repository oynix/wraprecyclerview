package com.oy.wrapperrecyclerview.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oy.wrapperrecyclerview.R;
import com.oy.wrapperrecyclerview.bean.GankNewsBean;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 15:06
 * Describe :
 */

public class GankNewsAdapter extends RecyclerView.Adapter<GankNewsAdapter.MyViewHolder> {

    private final List<GankNewsBean> mListData;
    // Glide加载图片使用
    private final Fragment mFragment;
    // 是否是 福利
    private boolean mIsFuli = false;

    public GankNewsAdapter(Fragment fragment, List<GankNewsBean> data, String type) {
        mFragment = fragment;
        mListData = data;
        mIsFuli = "福利".equals(type);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        if (mIsFuli) {
            return new MyViewHolder(View.inflate(context, R.layout.item_gank_news_fuli, null));
        } else {
            return new MyViewHolder(View.inflate(context, R.layout.item_gank_news, null));
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GankNewsBean item = mListData.get(position);
        if (!mIsFuli) {
            holder.title.setText(item.getDesc());
            holder.subtitle.setText(item.getPublishedAt().toLocaleString());
            final List<String> images = item.getImages();
            if (images != null && images.size() > 0) {
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(mFragment).load(images.get(0)).asBitmap().into(holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            Glide.with(mFragment).load(item.getUrl()).asBitmap().into(holder.imageFuli);
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Optional
        @InjectView(R.id.gank_item_title)
        TextView title;

        @Optional
        @InjectView(R.id.gank_item_subtitle)
        TextView subtitle;

        @Optional
        @InjectView(R.id.gank_item_image)
        ImageView image;

        // FULI fragment
        @Optional
        @InjectView(R.id.gank_item_image_fuli)
        ImageView imageFuli;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
