package com.demo.flickrphotoapplication.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.demo.flickrphotoapplication.R;
import com.demo.flickrphotoapplication.fragment.RecentPublicPhotosFragment;
import com.demo.flickrphotoapplication.fragment.SearchFragment;

/**
 * Created by madhur on 24/06/16.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {


    public static final int RECENT_POSITION = 0;
    public static final int SEARCH_POSITION = 1;
    Context context;

    public DashboardPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case RECENT_POSITION:
                return new RecentPublicPhotosFragment();
            case SEARCH_POSITION:
                return new SearchFragment();
        }
        return null;
    }

    @Override
    public int getCount() {return 2;}


    private int[] strResId = {
            R.string.recent,
            R.string.search
    };

    //Set image in place of tab text
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence text = context.getResources().getString(strResId[position]);
        return text;
    }
}
