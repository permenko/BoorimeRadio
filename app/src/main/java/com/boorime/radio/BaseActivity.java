package com.boorime.radio;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.astuetz.PagerSlidingTabStrip;
import com.boorime.radio.adapters.CustomPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.pager)
    ViewPager pager;

    private int firstPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        initViewPager();
    }

    private void initViewPager() {
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        CustomPagerAdapter adapter = new CustomPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(firstPage);
    }
}
