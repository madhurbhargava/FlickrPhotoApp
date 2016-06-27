package com.demo.flickrphotoapplication.tasks;

import android.os.AsyncTask;

import com.demo.flickrphotoapplication.Helpers.FlickrHelper;
import com.demo.flickrphotoapplication.event.Events.IPhotoListReadyListener;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import java.util.Collections;
import java.util.List;

/**
 * Created by madhur on 24/06/16.
 */
public class SearchPhotosTask extends AsyncTask<Void, Void, List<Photo>> {



    private final IPhotoListReadyListener mListener;
    private final int mPage;
    private final String mSearchTerm;
    private String mUserId;
    private Exception mException;

    public SearchPhotosTask(IPhotoListReadyListener listener,
                            String searchTerm, int page) {
        mListener = listener;
        mPage = page;
        mSearchTerm = searchTerm;
    }

    public SearchPhotosTask(IPhotoListReadyListener listener,
                            String searchTerm, int page, String userId) {
        this(listener, searchTerm, page);
        mUserId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Photo> doInBackground(Void... params) {

        SearchParameters sp = new SearchParameters();
        sp.setExtras(FlickrHelper.EXTRAS);
        sp.setText(mSearchTerm);
        if (mUserId != null) {
            sp.setUserId(mUserId);
        }
        sp.setSort(SearchParameters.RELEVANCE);

        {

            try {
                return FlickrHelper.getInstance().getPhotosInterface()
                        .search(sp, FlickrHelper.FETCH_PER_PAGE, mPage);
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Photo> result) {
        if (result == null) {

            result = Collections.EMPTY_LIST;
        }
        mListener.onPhotosReady(result, mException);
    }


}