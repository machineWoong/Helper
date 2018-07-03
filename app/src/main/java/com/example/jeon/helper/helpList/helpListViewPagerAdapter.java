package com.example.jeon.helper.helpList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-12.
 */

public class helpListViewPagerAdapter extends FragmentPagerAdapter {

    // 이 어댑터는 프래그먼트들을 가지고 있다.
    public ArrayList<Fragment> mData;

    public helpListViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mData = new ArrayList<>();
        mData.add(new myAsk());
        mData.add(new myGive());
    }

    @Override
    public Fragment getItem(int position) {
        // 포지션에 맞는 프레그먼트를 리턴해주는 것.

        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String name="";
        if(position == 0 ){
            name= "도움요청내역";
        }else if ( position == 1){
            name = "도움지원내역";
        }
        return name;
    }
}
