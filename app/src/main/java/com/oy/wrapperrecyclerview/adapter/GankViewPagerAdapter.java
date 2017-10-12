package com.oy.wrapperrecyclerview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.oy.wrapperrecyclerview.GankFragment;

import java.util.List;

/**
 * Author   : xiaoyu
 * Date     : 2017/9/28 11:19
 * Describe :
 */

public class GankViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> mTitleList;

    public GankViewPagerAdapter(FragmentManager fm, List<String> titleList) {
        super(fm);
        mTitleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return GankFragment.newInstance(mTitleList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mTitleList.size();
    }
}
