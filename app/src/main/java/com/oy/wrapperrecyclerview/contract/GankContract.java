package com.oy.wrapperrecyclerview.contract;

import com.oy.wrapperrecyclerview.bean.GankNewsBean;

import java.util.List;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 9:31
 * Describe :
 */

public interface GankContract {
    interface View {
        void setPageState(boolean isLoading);
        void setListData(List<GankNewsBean> listData);
        void onRefreshComplete();
        void onLoadMoreComplete();
        void onInitLoadFailed();
    }
    interface Presenter {
        void onViewCreate();
        void onRefresh();
        void onLoadMore();
    }
}
