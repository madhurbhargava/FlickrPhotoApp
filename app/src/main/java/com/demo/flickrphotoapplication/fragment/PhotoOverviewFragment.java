package com.demo.flickrphotoapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.demo.flickrphotoapplication.R;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.tags.Tag;

import java.util.Collection;

/**
 * Created by madhur on 26/06/16.
 */
public final class PhotoOverviewFragment extends DialogFragment {

    private Photo mPhoto = new Photo();

    protected ViewGroup mLayout;

    public static PhotoOverviewFragment newInstance(Photo photo) {
        PhotoOverviewFragment photoFragment = new PhotoOverviewFragment();
        photoFragment.mPhoto = photo;
        return photoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayout = (ScrollView) inflater.inflate(
                R.layout.photo_overview_fragment, container, false);



        /* build the title textview */
        TextView titleTextView = (TextView)
                mLayout.findViewById(R.id.textViewTitle);
        String title = mPhoto.getTitle();
        if ("".equals(title)) {
            titleTextView.setText("‘" + getString(R.string.none) + "’");
        } else {
            titleTextView.setText("‘" + title + "’");
        }

        /* build the tags textview */
        TextView mTextTags = (TextView) mLayout.findViewById(R.id.textViewTags);
        StringBuilder tags = new StringBuilder();
        final Collection<Tag> allTags = mPhoto.getTags();
        int count = 0;
        for (Tag t : allTags) {
            tags.append(t.getValue());
            if (count < allTags.size()-1) {
                tags.append(" · ");
            }
            count++;
        }
        if ("".equals(tags.toString())) {
            mTextTags.setText("‘" + getString(R.string.none) + "’");
        } else {
            mTextTags.setText(tags.toString());

        }

        /* build the description textview */
        TextView textviewDescription = (TextView)
                mLayout.findViewById(R.id.textViewDescription);
        String description = mPhoto.getDescription();
        if ("".equals(description)) {
            textviewDescription.setText("‘" + getString(R.string.none) + "’");
        } else {
            textviewDescription.setText(Html.fromHtml(description));
        }

        setHasOptionsMenu(false);

        return mLayout;
    }

}
