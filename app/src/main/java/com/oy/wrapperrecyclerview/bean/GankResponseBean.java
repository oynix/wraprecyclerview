package com.oy.wrapperrecyclerview.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 10:08
 * Describe :
 */

public class GankResponseBean implements Serializable{

    @SerializedName("results")
    private List<GankNewsBean> mResults;

    public List<GankNewsBean> getResults() {
        return mResults;
    }

}
