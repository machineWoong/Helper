package com.example.jeon.helper.chatting;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-29.
 */

public class chattingMainAdapter extends FragmentPagerAdapter {
    public ArrayList<Fragment> mData;

    public chattingMainAdapter(FragmentManager fm){
        super(fm);
        mData = new ArrayList<>();

        // 여기다가 프래그 먼트 추가
       mData.add(new friendListFamgment());
       mData.add(new chattingList_Framgment());
       mData.add(new friend_Ask_List_Famgment());



    }

    @Override
    public Fragment getItem(int position) {
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
            name= "친구목록";
        }else if ( position == 1){
            name = "채팅목록";
        }else if ( position == 2){
            name = "친구요청 목록";
        }
        return name;
    }
}
