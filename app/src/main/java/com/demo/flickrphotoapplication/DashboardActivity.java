package com.demo.flickrphotoapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.flickrphotoapplication.adapter.DashboardPagerAdapter;
import com.demo.flickrphotoapplication.fragment.SearchFragment;
import com.demo.flickrphotoapplication.widgets.DepthPageTransformer;
import com.demo.flickrphotoapplication.widgets.SlidingTabLayout;

/**
 * Created by madhur
 */
public class DashboardActivity extends AppCompatActivity {

    private DashboardPagerAdapter mDashboardPagerAdapter;
    public ViewPager mViewPager;
    public Toolbar mToolBar;
    public SlidingTabLayout mSlidingTabLayout;
    public EditText mSearchBox;
    public ProgressBar mProgressBar;
    private boolean mKeyboardStatus;
    public ImageView mClearSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initDashboardViewPager();
        initSlidingTabs();
        initToolBar();
        initSearchBox();
        initProgressBar();
        getSupportActionBar().setTitle(R.string.recent);
    }

    /** Ready the search box for search tab view **/
    public void initSearchBox() {
        mClearSearchButton = (ImageView) findViewById(R.id.search_clear);
        mSearchBox = (EditText) findViewById(R.id.toolbar_search_box);
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() <= 0) {
                    mSearchBox.setHintTextColor(Color.parseColor("#b3ffffff"));
                }
            }
        });

        //Submit search
        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchFragment searchFragment = (SearchFragment)getSupportFragmentManager()
                            .findFragmentByTag(
                                    "android:switcher:"
                                            + mViewPager.getId()
                                            + ":"
                                            + mDashboardPagerAdapter.getItemId(1));

                    View view = searchFragment.getView();
                    searchFragment.onSearchReceived(mSearchBox.getText().toString());
                    dismissKeyboard();
                    return true;
                }
                return false;
            }
        });

        mClearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchBox.setText("");
            }
        });
    }


    public void initDashboardViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        mDashboardPagerAdapter = new DashboardPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setOffscreenPageLimit(mDashboardPagerAdapter.getCount() - 1);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(mDashboardPagerAdapter);
    }

    //Initialize the Search and Recent Tabview
    public void initSlidingTabs() {
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.homeSlidingTabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_layout, R.id.tab_text);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getApplicationContext(), R.color.tabScroll);
            }
        });
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }


            @Override
            public void onPageSelected(int position) {
                setToolbarTitle(position);
                if (position == DashboardPagerAdapter.RECENT_POSITION) {
                    mSearchBox.setVisibility(View.GONE);

                    mClearSearchButton.setVisibility(View.GONE);

                } else {
                    mSearchBox.setVisibility(View.VISIBLE);
                    mSearchBox.requestFocus();
                    if (!isKeyboardActive()) {
                        showKeyboard();
                    } else {
                        dismissKeyboard();
                    }
                    mClearSearchButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    /*
    Helpers
     */
    private void setToolbarTitle(int position) {
        switch(position){
            case DashboardPagerAdapter.RECENT_POSITION:
                getSupportActionBar().setTitle(R.string.recent);
                break;
            case DashboardPagerAdapter.SEARCH_POSITION:
                getSupportActionBar().setTitle("");
                break;
        }
    }

    public void dismissKeyboard(){
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
    }

    public void showKeyboard(){
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mKeyboardStatus = true;
    }


    public void initProgressBar(){
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar_main);
    }

    private boolean isKeyboardActive(){
        return mKeyboardStatus;
    }

    public ProgressBar getProgressBar(){
        return mProgressBar;
    }


    public void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.homeToolbar);
        mToolBar.setTitleTextAppearance(this, R.style.ToolBarTextStyle);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(mViewPager.getCurrentItem());
    }
}
