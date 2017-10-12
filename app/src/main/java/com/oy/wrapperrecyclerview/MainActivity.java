package com.oy.wrapperrecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.oy.wrapperrecyclerview.adapter.GankViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.gank_view_pager)
    ViewPager mViewPager;

    private List<String> mFragmentType = new ArrayList<String>(){
        {
            add("Android");
            add("福利");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mViewPager.setAdapter(new GankViewPagerAdapter(getSupportFragmentManager(), mFragmentType));
    }
}
