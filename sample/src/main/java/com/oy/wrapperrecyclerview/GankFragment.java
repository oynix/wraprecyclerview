package com.oy.wrapperrecyclerview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oy.wrapperrecyclerview.adapter.GankNewsAdapter;
import com.oy.wrapperrecyclerview.bean.GankNewsBean;
import com.oy.wrapperrecyclerview.contract.GankContract;
import com.oy.wrapperrecyclerview.contract.GankPresenter;
import com.oynix.xrecyclerview.xRecyclerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/27 21:43
 * Describe :
 */

public class GankFragment extends Fragment implements GankContract.View {

    private static final String TAG = "GankFragment";

    private static final String GANK_DATA_KEY = "gank_fragment_data_key";

    /** 创建instance */
    public static GankFragment newInstance(@NonNull String type) {
        Bundle bundle = new Bundle();
        bundle.putString(GANK_DATA_KEY, type);
        GankFragment gankFragment = new GankFragment();
        gankFragment.setArguments(bundle);
        return gankFragment;
    }

    private GankNewsAdapter mAdapter;
    private GankPresenter mPresenter;

    @BindView(R.id.gank_recycler_view)
    xRecyclerView mRecyclerView;

    @BindView(R.id.gank_load_failed_tv)
    TextView mTvLoadFailed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new GankPresenter(this, getArguments().getString(GANK_DATA_KEY));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gank, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mRecyclerView.setListener(new xRecyclerView.xAdapterListener() {
            @Override
            public void startRefresh() {
                Log.e("fragment", "start refresh");
                mPresenter.startRefresh();
            }

            @Override
            public void startLoadMore() {
                Log.e("fragment", "start load more");
                mPresenter.startLoadMore();
            }
        });
        mRecyclerView.startRefreshing();
        mPresenter.onViewCreate();
    }

    @Override
    public void setListData(List<GankNewsBean> listData, String type) {
        Log.i(TAG, "notify data set changed");
        if (mAdapter == null) {
            mAdapter = new GankNewsAdapter(listData, type);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onInitLoadFailed() {
        mRecyclerView.setVisibility(View.GONE);
        mTvLoadFailed.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopRefresh() {
        mRecyclerView.stopRefreshing();
    }

    @Override
    public void stopLoadMore() {
        mRecyclerView.stopLoadingMore();
    }
}
