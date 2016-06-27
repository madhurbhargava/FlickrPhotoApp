package com.demo.flickrphotoapplication.fragment;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.demo.flickrphotoapplication.DashboardActivity;


/**
 * Created by madhur on 24/06/16.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    /**
     * It's useful to keep a reference to the parent activity in our fragments.
     */
    protected DashboardActivity mActivity;

    protected ActionBar mActionBar;
    protected ViewGroup mLayout;
    protected SharedPreferences mDefaultSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = getActivity();
        if(activity instanceof DashboardActivity ) {
            mActivity = (DashboardActivity) getActivity();
        }
        mActionBar = mActivity.getActionBar();

        mDefaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        setRetainInstance(shouldRetainInstance());
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Update our reference to the activity as it may have changed */
        mActivity = (DashboardActivity) getActivity();
        mActionBar = mActivity.getActionBar();
    }

    protected void startTask() {
        Log.d(getLogTag(), "starttask");
    }

    protected void refresh() {
        Log.d(getLogTag(), "refresh");
    }

    protected boolean shouldRetainInstance() {
        return true;
    }

    protected String getLogTag() {
        return TAG;
    }
}

