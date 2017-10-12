package com.oy.wrapperrecyclerview.contract;


import com.oy.wrapperrecyclerview.bean.GankResponseBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 9:43
 * Describe :
 */

public interface GankURLService {

    String BASE_URL = "http://gank.io/api/";

    @GET("data/{category}/{pagecount}/{page}")
    Observable<GankResponseBean> requestData(
            @Path("category") String category,
            @Path("pagecount") int countPerPage,
            @Path("page") int page);
}
