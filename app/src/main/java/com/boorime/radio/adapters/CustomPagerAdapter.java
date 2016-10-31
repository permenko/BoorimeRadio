package com.boorime.radio.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.boorime.radio.R;
import com.boorime.radio.fragments.AboutFragment;
import com.boorime.radio.fragments.FavoriteFragment;
import com.boorime.radio.fragments.PlayerFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles;

    public CustomPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mTitles = context.getResources().getStringArray(R.array.fragments_titles);;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PlayerFragment();
            case 1:
                return new FavoriteFragment();
            case 2:
                return new AboutFragment();
            default:
                return null;
        }
    }
}