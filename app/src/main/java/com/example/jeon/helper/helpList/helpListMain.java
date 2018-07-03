package com.example.jeon.helper.helpList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.addAskHelp;
import com.example.jeon.helper.giveHelp.giveHelpContent;
import com.example.jeon.helper.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class helpListMain extends AppCompatActivity  {

    // ip

    ip ip = new ip();
    String ipad = ip.getIp();

    // 로그인 유저 정보
    String loginUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_list_main);

        // 뷰페이저 어댑터 연동
        ViewPager viewPager =(ViewPager)findViewById(R.id.pager);
        helpListViewPagerAdapter adapter = new helpListViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // 탭 레이아웃과 뷰페이저의 연동  ( 제목 지정은 helpListViewPagerAdapter 에서 한다 )
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        // 로그인 유저 정보 가지고 오기
        getLoginUserData();

    }

    // ---------------------------------- 데이터 설정 ----------------------------------------------
    //로그인 유저 정보 가져오기
    public void getLoginUserData(){
        loginUserID = getIntent().getStringExtra("loginUserId");
    }


    //myAskList 프레그먼트에 값전달
    public String getUserId(){
        String a= loginUserID;
        return a;
    }

}

