package com.ewenfeng.ewenfeng.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ewenfeng.ewenfeng.ui.fragment.second.child.childpager.FirstPagerFragment;


public class ZhihuPagerFragmentAdapter extends FragmentPagerAdapter {
    private String[] mTab = new String[]{"推荐", "热门", "收藏"};

    public ZhihuPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return FirstPagerFragment.newInstance();
        } else {
            //return OtherPagerFragment.newInstance(position);
            return FirstPagerFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mTab.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTab[position];
    }
}
