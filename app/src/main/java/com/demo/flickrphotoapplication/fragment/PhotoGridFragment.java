package com.demo.flickrphotoapplication.fragment;

/**
 * Created by madhur on 24/06/16.
 */

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.demo.flickrphotoapplication.Helpers.FlickrHelper;
import com.demo.flickrphotoapplication.PhotoActivity;
import com.demo.flickrphotoapplication.R;
import com.demo.flickrphotoapplication.event.Events.IPhotoListReadyListener;
import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Base Fragment that contains a GridView of photos.
 */
public abstract class PhotoGridFragment extends BaseFragment
        implements IPhotoListReadyListener
         {

    protected GridView mGridView;
    private EndlessGridAdapter mAdapter;

    protected final List<Photo> mPhotos = new ArrayList<Photo>();
    private List<Photo> mNewPhotos = new ArrayList<Photo>();
    protected int mPage = 1;
    protected boolean mMorePages = true;
    protected boolean mShowDetailsOverlay = true;

    protected int mGridChoiceMode = ListView.CHOICE_MODE_SINGLE;

    private ViewGroup mNoConnectionLayout;

    private SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLayout = (RelativeLayout) inflater.inflate(R.layout.gridview_fragment, container,
                false);

        mSwipeLayout = (SwipeRefreshLayout) mLayout.findViewById(R.id.swipe_container);
        mSwipeLayout.setColorScheme(R.color.flickr_pink, R.color.flickr_blue, R.color.flickr_pink,
                R.color.flickr_blue);

        mNoConnectionLayout = (ViewGroup) mLayout.findViewById(R.id.no_connection_layout);

        initGridView();

        return mLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mPhotos.isEmpty()) {
            GridView gridView = (GridView) mLayout.findViewById(R.id.gridview);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPhotosReady(List<Photo> photos, Exception e) {

        mActivity.getProgressBar().setVisibility(View.GONE);
        mSwipeLayout.setRefreshing(false);

        if (FlickrHelper.getInstance().handleFlickrUnavailable(mActivity, e)) {
            return;
        }

        if (photos == null) {
            mNoConnectionLayout.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            return;
        }
        if (photos.isEmpty()) {
            Toast.makeText(getContext(), "No data received", Toast.LENGTH_LONG).show();
            mMorePages = false;
        }
        mNoConnectionLayout.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);

        mPhotos.addAll(photos);
        mAdapter.onDataReady();
    }



    @Override
    protected void refresh() {
        mPage = 1;
        mMorePages = true;
        if (mPhotos.size() > 0) {
            mPhotos.clear();
            cacheInBackground();
        }
    }

    protected int getGridChoiceMode() {
        return mGridChoiceMode;
    }

    protected void initGridView() {
        mAdapter = new EndlessGridAdapter(mPhotos);
        mAdapter.setRunInBackground(false);
        mGridView = (GridView) mLayout.findViewById(R.id.gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setChoiceMode(getGridChoiceMode());
        mGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                PhotoActivity.startPhotoViewer(mActivity, mPhotos, position);
                mGridView.invalidateViews();
            }
        });
        mGridView.setVisibility(View.INVISIBLE);
    }

    /**
     * Return false by default to indicate to the EndlessAdapter that there's
     * no more data to load.
     * <p/>
     * Subclasses that support pagination should override this.
     */
    protected boolean cacheInBackground() {
        return false;
    }

    public EndlessAdapter getAdapter() {
        return mAdapter;
    }

    class EndlessGridAdapter extends EndlessAdapter {

        public EndlessGridAdapter(List<Photo> list) {
            super(new GridAdapter(list));
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            return PhotoGridFragment.this.cacheInBackground();
        }

        @Override
        protected void appendCachedData() {
        }

        @Override
        protected View getPendingView(ViewGroup parent) {
            return new View(mActivity);
        }
    }

    class GridAdapter extends ArrayAdapter<Photo> {

        private final boolean mHighQualityThumbnails;

        public GridAdapter(List<Photo> items) {
            super(mActivity, R.layout.gridview_item, android.R.id.text1,
                    items);
            mHighQualityThumbnails = false;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(
                        R.layout.gridview_item, null);
                holder = new ViewHolder();

                holder.image = (ImageView) convertView.findViewById(
                        R.id.image_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Photo photo = getItem(position);



            /* Fetch the main photo */
            String thumbnailUrl = photo.getLargeSquareUrl();
            if (mHighQualityThumbnails) {
                thumbnailUrl = photo.getMediumUrl();
            }

            Picasso.with(mActivity).load(thumbnailUrl).into(holder.image);

            return convertView;
        }
    }

    public static class ViewHolder {

        public ImageView image;

    }
}
