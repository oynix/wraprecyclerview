package com.oy.wrapperrecyclerview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oy.wrapperrecyclerview.R;
import com.oy.wrapperrecyclerview.bean.GankNewsBean;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.oynix.library.xRecyclerView;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 15:06
 * Describe :
 */

public class GankNewsAdapter extends xRecyclerView.xAdapter<GankNewsAdapter.MyViewHolder> {

    private final List<GankNewsBean> mListData;
    // 是否是 福利
    private boolean mIsFuli = false;

    public GankNewsAdapter(List<GankNewsBean> data, String type) {
        mListData = data;
        mIsFuli = "福利".equals(type);
    }

    @Override
    protected int getxItemCount() {
        return mListData.size();
    }

    @Override
    protected MyViewHolder onCreatexViewHolder(ViewGroup parent, int itemType) {
        Context context = parent.getContext().getApplicationContext();
        if (mIsFuli) {
            return new MyViewHolder(View.inflate(context, R.layout.item_gank_news_fuli, null));
        } else {
            return new MyViewHolder(View.inflate(context, R.layout.item_gank_news, null));
        }
    }

    @Override
    protected void onBindxViewHolder(MyViewHolder holder, int position) {
        final GankNewsBean item = mListData.get(position);
        if (!mIsFuli) {
            holder.title.setText(item.getDesc());
            holder.subtitle.setText(item.getPublishedAt().toLocaleString());
            final List<String> images = item.getImages();
            if (images != null && images.size() > 0) {
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView).asBitmap().load(images.get(0)).into(holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            Glide.with(holder.itemView).asBitmap().load(item.getUrl()).into(holder.imageFuli);
        }
    }

    @Override
    protected xRecyclerView.LoadMoreViewHolder createLoadMoreViewHolder(ViewGroup viewGroup) {
        return new xRecyclerView.LoadMoreViewHolder() {
            @Override
            public View createLoadMoreView() {
                return View.inflate(viewGroup.getContext(), R.layout.custome_load_more_item, null);
            }

            @Override
            public void changeLoadMoreViewState(boolean loading) {
                View bar = mLoadMoreView.findViewById(R.id.lm_pb);
                bar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.gank_item_title)
        TextView title;

        @Nullable
        @BindView(R.id.gank_item_subtitle)
        TextView subtitle;

        @Nullable
        @BindView(R.id.gank_item_image)
        ImageView image;

        // FULI fragment
        @Nullable
        @BindView(R.id.gank_item_image_fuli)
        ImageView imageFuli;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
