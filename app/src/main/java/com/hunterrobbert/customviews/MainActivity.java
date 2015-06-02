package com.hunterrobbert.customviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private int[] mTabTitles = new int[] {
            R.string.title_1,
            R.string.title_2,
            R.string.title_3
    };

    public CollapsibleHeaderLayout mCollapsibleHeaderLayout;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCollapsibleHeaderLayout = (CollapsibleHeaderLayout) findViewById(R.id.collapsible_header_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mCollapsibleHeaderLayout.setSlidingTabLayoutContentDescriptions(mTabTitles);

        FragmentViewPagerAdapter fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(fragmentViewPagerAdapter);
        mCollapsibleHeaderLayout.setViewPager(mViewPager);

    }

    //make the header layout accessible to the viewpager's fragments
    public CollapsibleHeaderLayout getCollapsibleHeaderLayout() {
        return mCollapsibleHeaderLayout;
    }


    private class FragmentViewPagerAdapter extends FragmentPagerAdapter {

        public FragmentViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.app.Fragment getItem(int i) {
            SampleFragment sampleFragment = new SampleFragment();
            Bundle args = new Bundle();
            args.putString(SampleFragment.KEY_FRAGMENT_TITLE, getString(mTabTitles[i]));
            sampleFragment.setArguments(args);
            return sampleFragment;

        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTabTitles[position]);
        }

    }
}
