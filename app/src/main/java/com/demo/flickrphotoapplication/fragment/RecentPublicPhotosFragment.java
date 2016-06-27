package com.demo.flickrphotoapplication.fragment;

import android.os.Bundle;

import com.demo.flickrphotoapplication.tasks.LoadPublicPhotosTask;

/**
 * Created by madhur on 24/06/16.
 */
public class RecentPublicPhotosFragment extends PhotoGridFragment {

    public static RecentPublicPhotosFragment newInstance() {
        return new RecentPublicPhotosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowDetailsOverlay = false;
    }

    /**
     * Once the parent binds the adapter it will trigger cacheInBackground
     * for us as it will be empty when first bound.
     */
    @Override
    protected boolean cacheInBackground() {
        startTask(mPage++);
        return mMorePages;
    }

    private void startTask(int page) {
        super.startTask();
        new LoadPublicPhotosTask(this, page).execute();
    }
}
