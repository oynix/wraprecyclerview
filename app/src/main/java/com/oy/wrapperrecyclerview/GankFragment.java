package com.oy.wrapperrecyclerview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oy.wrapperrecyclerview.adapter.AdapterWrapper;
import com.oy.wrapperrecyclerview.adapter.GankNewsAdapter;
import com.oy.wrapperrecyclerview.bean.GankNewsBean;
import com.oy.wrapperrecyclerview.contract.GankContract;
import com.oy.wrapperrecyclerview.contract.GankPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/27 21:43
 * Describe :
 */

public class GankFragment extends Fragment implements GankContract.View, SwipeRefreshLayout.OnRefreshListener, SwipeToLoadHelper.LoadMoreListener {

    private static final String GANK_DATA_KEY = "gank_fragment_data_key";
    private AdapterWrapper mAdapterWrapper;
    private SwipeToLoadHelper mLoadMoreHelper;

    /** 创建instance */
    public static GankFragment newInstance(@NonNull String type) {
        Bundle bundle = new Bundle();
        bundle.putString(GANK_DATA_KEY, type);
        GankFragment gankFragment = new GankFragment();
        gankFragment.setArguments(bundle);
        return gankFragment;
    }

    private Context mContext;
    private GankPresenter mPresenter;

    private String mFragmentType;

    @BindView(R.id.gank_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.gank_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.gank_loading)
    ProgressBar mProgressBar;

    @BindView(R.id.gank_load_failed_tv)
    TextView mTvLoadFailed;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentType = getArguments().getString(GANK_DATA_KEY);
        mPresenter = new GankPresenter(this, mFragmentType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gank, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (mFragmentType.equals("福利")) {
            mLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        } else {
            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        mPresenter.onViewCreate();
    }

    @Override
    public void setPageState(boolean isLoading) {
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mSwipeRefreshLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    // 初始页面完成后调用;或Refresh完成后调用
    @Override
    public void setListData(List<GankNewsBean> listData) {
        mAdapterWrapper = new AdapterWrapper(new GankNewsAdapter(this, listData, mFragmentType));
        mLoadMoreHelper = new SwipeToLoadHelper(mRecyclerView, mAdapterWrapper);
        mLoadMoreHelper.setLoadMoreListener(this);

        mRecyclerView.setAdapter(mAdapterWrapper);
    }

    // 刷新也就是重新初始化该页面
    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
        // 刷新时禁用上拉加载更多
        mLoadMoreHelper.setSwipeToLoadEnabled(false);
    }

    @Override
    public void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        // 刷新完成是解禁上拉加载更多
        mLoadMoreHelper.setSwipeToLoadEnabled(true);
        mAdapterWrapper.notifyDataSetChanged();
    }

    // 请求更多数据 并且禁用SwipeRefresh功能
    @Override
    public void onLoad() {
        mSwipeRefreshLayout.setEnabled(false);
        mPresenter.onLoadMore();
    }

    // 刷新界面显示 并且解禁SwipeRefresh功能
    @Override
    public void onLoadMoreComplete() {
        mSwipeRefreshLayout.setEnabled(true);
        mLoadMoreHelper.setLoadMoreFinish();
        mAdapterWrapper.notifyDataSetChanged();
    }

    @Override
    public void onInitLoadFailed() {
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mTvLoadFailed.setVisibility(View.VISIBLE);
    }
}
