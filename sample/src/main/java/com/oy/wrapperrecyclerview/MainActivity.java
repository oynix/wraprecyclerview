package com.oy.wrapperrecyclerview;

import android.os.Bundle;

import com.oy.wrapperrecyclerview.adapter.GankViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.gank_view_pager)
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

        ButterKnife.bind(this);

        mViewPager.setAdapter(new GankViewPagerAdapter(getSupportFragmentManager(), mFragmentType));
    }
}
