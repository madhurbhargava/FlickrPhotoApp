package com.demo.flickrphotoapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.flickrphotoapplication.PhotoActivity;
import com.demo.flickrphotoapplication.R;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.Size;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by madhur on 26/06/16.
 */
public final class PhotoViewerFragment extends Fragment {

    protected ViewGroup mLayout;
    private Photo mBasePhoto;
    private TextView mTextViewTitle;
    private TextView mTextViewAuthor;
    private ImageView mImageView;
    private ProgressBar mProgress;

    protected PhotoActivity mActivity;

    private int mNum;



    public static PhotoViewerFragment newInstance(Photo photo, boolean fetchExtraInfo, int num) {


        PhotoViewerFragment photoFragment = new PhotoViewerFragment();
        photoFragment.mBasePhoto = photo;

        Bundle args = new Bundle();
        args.putInt("num", num);
        photoFragment.setArguments(args);

        return photoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (PhotoActivity)getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (PhotoActivity)getActivity();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.photo_menu, menu);
        MenuItem shareActionItem = menu.findItem(R.id.menu_share);

        ShareActionProvider shareActionProvider = new ShareActionProvider(mActivity);
        shareActionProvider.setShareHistoryFileName(
                ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        shareActionProvider.setShareIntent(createShareIntent());
        MenuItemCompat.setActionProvider(shareActionItem, shareActionProvider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                setPhotoInfoFragmentVisibility(mBasePhoto,true, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setPhotoInfoFragmentVisibility(Photo photo, boolean show, boolean animate) {
        if (photo == null) {

            return;
        }
        {
            if (show) {
                PhotoOverviewFragment photoInfoFrag = PhotoOverviewFragment.newInstance(photo);
                photoInfoFrag.show(getActivity().getSupportFragmentManager(), "PhotoOverviewFragment");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLayout = (RelativeLayout) inflater.inflate(
                R.layout.photoviewer_fragment, container, false);

        mImageView = (ImageView) mLayout.findViewById(R.id.image);
        mTextViewTitle = (TextView) mLayout.findViewById(R.id.textViewTitle);
        mTextViewAuthor = (TextView) mLayout.findViewById(R.id.textViewAuthor);
        mProgress = (ProgressBar) mLayout.findViewById(R.id.progress);

        /* If this fragment is new as part of a set, update it's overlay
         * visibility based on the state of the actionbar */
        setOverlayVisibility(true);

        displayImage();

        return mLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("mNum", mNum);
        savedInstanceState.putSerializable(mNum + "_basePhoto", mBasePhoto);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @SuppressLint("NewApi")
    public void setOverlayVisibility(final boolean on) {
        if (on) {
            mTextViewTitle.setVisibility(View.VISIBLE);
            mTextViewAuthor.setVisibility(View.VISIBLE);
            mLayout.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        } else {
            mTextViewTitle.setVisibility(View.INVISIBLE);
            mTextViewAuthor.setVisibility(View.INVISIBLE);
            mLayout.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    /**
     * Creates a sharing {@link Intent}.
     *
     * @return The sharing intent.
     */
    private Intent createShareIntent() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String text = "";
        try {
            text = String.format("\"%s\" %s %s: %s", mBasePhoto.getTitle(),
                    mActivity.getString(R.string.by),
                    mBasePhoto.getOwner().getUsername(),
                    mBasePhoto.getUrl());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }

    /**
     * Return the largest size available for a given photo.
     * <p/>
     * All should have medium, but not all have large.
     */
    private String getLargestUrlAvailable(Photo photo) {
        Size size = photo.getLargeSize();
        if (size != null) {
            return photo.getLargeUrl();
        } else {
            /* No large size available, fall back to medium */
            return photo.getMediumUrl();
        }
    }

    private void displayImage() {

        /* Fetch the main image */
        if (mBasePhoto != null) {
            String urlToFetch = getLargestUrlAvailable(mBasePhoto);
            Picasso.with(mActivity).load(urlToFetch).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    mProgress.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError() {

                }
            });

            /* Set the photo title and author text. */
            String photoTitle = mBasePhoto.getTitle();
            if (photoTitle == null || photoTitle.length() == 0) {
                photoTitle = mActivity.getString(R.string.untitled);
            }
            String authorText = String.format("%s %s",
                    mActivity.getString(R.string.by),
                    mBasePhoto.getOwner().getUsername());
            mTextViewTitle.setText(photoTitle);
            mTextViewAuthor.setText(authorText);

        }

    }
}

