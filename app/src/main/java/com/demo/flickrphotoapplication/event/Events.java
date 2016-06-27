package com.demo.flickrphotoapplication.event;

import com.googlecode.flickrjandroid.photos.Photo;

import java.util.List;

/**
 * Created by madhur on 24/06/16.
 */
public class Events {

    public interface IPhotoListReadyListener {
        void onPhotosReady(List<Photo> photos, Exception e);
    }
}
