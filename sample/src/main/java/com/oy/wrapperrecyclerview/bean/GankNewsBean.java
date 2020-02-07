package com.oy.wrapperrecyclerview.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 9:58
 * Describe :
 */

public class GankNewsBean implements Serializable {

    @SerializedName("desc")
    private String mDesc;

    @SerializedName("images")
    private List<String> mImages;

    @SerializedName("publishedAt")
    private Date mPublishedAt;

    @SerializedName("url")
    private String mUrl;

    public String getDesc() {
        return mDesc;
    }

    public List<String> getImages() {
        return mImages;
    }

    public Date getPublishedAt() {
        return mPublishedAt;
    }

    public String getUrl() {
        return mUrl;
    }
}
