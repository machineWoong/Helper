package com.example.jeon.helper.chatting;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jeon.helper.R;
import com.example.jeon.helper.helpList.helpListViewPagerAdapter;

public class chattingMainActivity extends AppCompatActivity {

    String loginUserid;
    String loginUserNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_main);

        // 로그인 유저 정보 받아오기
        getLoginUserData();

        // 뷰페이저 어댑터 연동
        ViewPager viewPager =(ViewPager)findViewById(R.id.chattingPager);
        chattingMainAdapter adapter = new chattingMainAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // 탭 레이아웃과 뷰페이저의 연동  ( 제목 지정은 helpListViewPagerAdapter 에서 한다 )
        TabLayout tabLayout = (TabLayout)findViewById(R.id.chattingTab);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void getLoginUserData(){
        loginUserid = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNickName");
    }


}
