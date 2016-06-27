package com.demo.flickrphotoapplication.Helpers;

import android.content.Context;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by madhur on 24/06/16.
 */
public final class FlickrHelper {

    public static final String ERR_CODE_FLICKR_UNAVAILABLE = "105";
    public static final String API_KEY = "53fadf8502f8733e4fab358384f9eac0";
    public static final String API_SECRET = "4b69ddd7c31e8485";

    /* Attributes to fetch for a photo */
    public static final Set<String> EXTRAS = new HashSet<String>();
    static {
        EXTRAS.add("owner_name");
        EXTRAS.add("url_q");  /* large square 150x150 */
        EXTRAS.add("url_m");  /* small, 240 on longest side */
        EXTRAS.add("url_l");
        EXTRAS.add("views");
        EXTRAS.add("description");
        EXTRAS.add("tags");
    }

    /* Number of items to fetch per page for calls that support pagination */
    public static final int FETCH_PER_PAGE = 20;

    private static FlickrHelper instance = null;

    private FlickrHelper() {
    }

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }

        return instance;
    }

    public Flickr getFlickr() {
        try {
            return new Flickr(API_KEY, API_SECRET, new REST());
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public InterestingnessInterface getInterestingInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getInterestingnessInterface();
        } else {
            return null;
        }
    }

    public PhotosInterface getPhotosInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getPhotosInterface();
        } else {
            return null;
        }
    }

    /**
     * Check if exception e is the cause of Flickr being down and show some toast.
     * @param context
     * @return true if flickr is down
     */
    public boolean handleFlickrUnavailable(Context context, Exception e) {
        if (e != null && e instanceof FlickrException) {
            if (((FlickrException) e).getErrorCode().equals(
                    ERR_CODE_FLICKR_UNAVAILABLE)) {
                e.printStackTrace();
                Toast.makeText(context, "Flickr seems down at the moment",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }
}
