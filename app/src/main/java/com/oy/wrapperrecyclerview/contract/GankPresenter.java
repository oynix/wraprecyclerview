package com.oy.wrapperrecyclerview.contract;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.oy.wrapperrecyclerview.bean.GankNewsBean;
import com.oy.wrapperrecyclerview.bean.GankResponseBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 10:20
 * Describe :
 */

public class GankPresenter implements GankContract.Presenter {

    private final GankURLService mUrlService;
    private final GankContract.View mView;
    private List<GankNewsBean> mListData = new ArrayList<>();
    private String mType;
    private int mCurrentPage;

    public GankPresenter(GankContract.View view, String fragmentType) {
        // retrofit 创建API Service实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GankURLService.BASE_URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mUrlService = retrofit.create(GankURLService.class);
        mListData.clear();
        mView = view;
        mType = fragmentType;
    }

    @Override
    public void onViewCreate() {
        mView.setPageState(true);
        mCurrentPage = 1;
        loadData(0);
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        loadData(1);
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        loadData(2);
    }

    // 0 : 初始化请求
    // 1 : refresh请求
    // 2 : loadMore请求
    private void loadData(final int requestDataType) {
        mUrlService.requestData(mType, 20, mCurrentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankResponseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GankResponseBean value) {
                        switch (requestDataType) {
                            case 0: // init
                                mListData.clear();
                                mListData.addAll(value.getResults());
                                mView.setListData(mListData);
                                mView.setPageState(false);
                                break;
                            case 1: // refresh
                                mListData.clear();
                                mListData.addAll(value.getResults());
                                mView.onRefreshComplete();
                                break;
                            case 2: // load more
                                mListData.addAll(value.getResults());
                                mView.onLoadMoreComplete();
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.onInitLoadFailed();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
