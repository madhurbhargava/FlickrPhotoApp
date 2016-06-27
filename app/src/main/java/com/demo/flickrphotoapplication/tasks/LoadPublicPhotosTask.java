package com.demo.flickrphotoapplication.tasks;

import android.os.AsyncTask;

import com.demo.flickrphotoapplication.Helpers.FlickrHelper;
import com.demo.flickrphotoapplication.event.Events.IPhotoListReadyListener;
import com.googlecode.flickrjandroid.photos.Photo;

import java.util.Date;
import java.util.List;

/**
 * Created by madhur on 24/06/16.
 */
public class LoadPublicPhotosTask extends AsyncTask<Void, Void, List<Photo>> {

    private final IPhotoListReadyListener mListener;
    private final int mPage;
    private Exception mException;

    public LoadPublicPhotosTask(IPhotoListReadyListener listener, int page) {
        mListener = listener;
        mPage = page;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Photo> doInBackground(Void... arg0) {


        /* A specific date to return interesting photos for. */
        Date day = null;
        try {
            //noinspection ConstantConditions
            return FlickrHelper.getInstance().getInterestingInterface()
                    .getList(day, FlickrHelper.EXTRAS, FlickrHelper.FETCH_PER_PAGE,
                            mPage);
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(final List<Photo> result) {
        if (result == null) {

        }
        mListener.onPhotosReady(result, mException);
    }


}
