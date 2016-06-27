package com.demo.flickrphotoapplication.fragment;

import android.os.Bundle;
import android.view.View;

import com.demo.flickrphotoapplication.tasks.SearchPhotosTask;

/**
 * Created by madhur on 24/06/16.
 */
public class SearchFragment extends PhotoGridFragment {


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
    private String mSearchterm;
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
        if(mSearchterm != null && mSearchterm.length() > 0) {
            startTask(mPage++, mSearchterm);
        }
        return mMorePages;
    }

    private void startTask(int page, String searchTerm) {
        super.startTask();
        new SearchPhotosTask(this, searchTerm, mPage++).execute();
    }

    //result from Activity
    public void onSearchReceived(String searchTerm){
        mSearchterm = searchTerm;
        if(mSearchterm != null && mSearchterm.length() > 0) {
            mActivity.getProgressBar().setVisibility(View.VISIBLE);
            mPhotos.clear();
            startTask(mPage++, searchTerm);
        }
    }
}
