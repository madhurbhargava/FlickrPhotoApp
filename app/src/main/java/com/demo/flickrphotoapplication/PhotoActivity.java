package com.demo.flickrphotoapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.demo.flickrphotoapplication.Helpers.GsonHelper;
import com.demo.flickrphotoapplication.event.Events;
import com.demo.flickrphotoapplication.fragment.PhotoOverviewFragment;
import com.demo.flickrphotoapplication.fragment.PhotoViewerFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.flickrjandroid.photos.Photo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

public class PhotoActivity extends AppCompatActivity implements Events.IPhotoListReadyListener {

    public static final String KEY_START_INDEX =
            "com.demo.flickrphotoapplication.PhotoActivity.KEY_START_INDEX";
    public static final String KEY_PHOTO_ID =
            "com.demo.flickrphotoapplication.PhotoActivity.KEY_PHOTO_ID";
    public static final String KEY_PHOTO_LIST_FILE =
            "com.demo.flickrphotoapplication.PhotoActivity.KEY_PHOTO_LIST_FILE";
    public static final String PHOTO_LIST_FILE =
            "PhotoActivity_photolist.json";

    /* intent actions */
    public static final String ACTION_VIEW_PHOTO_BY_ID =
            "com.demo.flickrphotoapplication.ACTION_VIEW_PHOTO_BY_ID";
    public static final String ACTION_VIEW_PHOTOLIST =
            "com.demo.flickrphotoapplication.ACTION_VIEW_PHOTOLIST";

    private static final String KEY_INTENT_CONSUMED =
            "com.demo.flickrphotoapplication.PhotoActivity.KEY_INTENT_CONSUMED";

    private List<Photo> mPhotos = new ArrayList<Photo>();

    private PhotoViewerPagerAdapter mAdapter;
    private ViewPager mPager;
    private int mCurrentAdapterIndex = 0;

    public Toolbar mToolBar;

    private PhotoOverviewFragment mPhotoInfoFragment;

    private Timer mTimer;

    protected ActionBar mActionBar;

    @Override
    public void onPhotosReady(List<Photo> photos, Exception e) {

    }

    /**
     * Start PhotoViewerActivity to view a list of photos, starting at a specific index.
     */
    public static void startPhotoViewer(Context context, List<Photo> photos, int index) {
        if (new GsonHelper(context).marshallObject(photos, PHOTO_LIST_FILE)) {
            Intent photoViewer = new Intent(context, PhotoActivity.class);
            photoViewer.setAction(ACTION_VIEW_PHOTOLIST);
            photoViewer.putExtra(KEY_START_INDEX, index);
            photoViewer.putExtra(KEY_PHOTO_LIST_FILE, PHOTO_LIST_FILE);
            context.startActivity(photoViewer);
        } else {
            Toast.makeText(context, "Error marshelling Photos, Cannot view the viewer", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        /* Must be called before adding content */
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photo);

        initToolBar();
        handleIntent(getIntent());

    }

    public void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.photoToolbar);
        mToolBar.setTitleTextAppearance(this, R.style.ToolBarTextStyle);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("PHOTO");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_VIEW_PHOTOLIST)) {

            intent.putExtra(KEY_INTENT_CONSUMED, true);
             final int startIndex = intent.getIntExtra(KEY_START_INDEX, 0);
            String photoListFile = intent.getStringExtra(KEY_PHOTO_LIST_FILE);
            GsonHelper gsonHelper = new GsonHelper(this);
            String json = gsonHelper.loadJson(photoListFile);

            if (json.length() > 0) {
                Type collectionType =new TypeToken<Collection<Photo>>(){}.getType();
                mPhotos = new Gson().fromJson(json, collectionType);
                initViewPager(startIndex, true);
            }
        }
    }

    private void initViewPager(int startIndex, boolean fetchExtraInfo) {
        mAdapter = new PhotoViewerPagerAdapter(getSupportFragmentManager(), fetchExtraInfo);
        mAdapter.onPageSelected(startIndex);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(startIndex);
        mPager.setOffscreenPageLimit(2);
    }

    class PhotoViewerPagerAdapter extends FragmentStatePagerAdapter
            implements ViewPager.OnPageChangeListener {
        private final boolean mFetchExtraInfo;

        public PhotoViewerPagerAdapter(FragmentManager fm, boolean fetchExtraInfo) {
            super(fm);
            mFetchExtraInfo = fetchExtraInfo;
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoViewerFragment.newInstance(mPhotos.get(position), mFetchExtraInfo,
                    position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            mCurrentAdapterIndex = position;

        }



        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }
    }
}
