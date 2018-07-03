package com.example.jeon.helper.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.HelperMain;
import com.example.jeon.helper.R;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.loginJoin.login;
import com.example.jeon.helper.socketService;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class settingMainActivity extends AppCompatActivity {

    String loginUserId;
    String loginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시계창 없애기
        setContentView(R.layout.activity_setting_main);

        getUserData();

        // 바깥 화면 클릭시 종료
        LinearLayout outSettingPage = (LinearLayout)findViewById(R.id.outSettingPage);
        outSettingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 로그아웃 버튼 클릭 이벤트
        TextView settingLogOut = (TextView)findViewById(R.id.settingLogOut);
        settingLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(settingMainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                if(loginMode.equals("1")){
                    // 일반로그인 인경우
                    logOutEvent();
                }else{
                    // 카카오 로그인인 경우
                    kakaoLogOutEvent();
                }
            }
        });




        // 환경설정 페이지로 이동
        TextView gotoSetMySetting  = (TextView)findViewById(R.id.gotoSetMySetting);
        gotoSetMySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoMySettingPage = new Intent(settingMainActivity.this,my_Setting_Page.class);
                startActivity(gotoMySettingPage);
            }
        });


        // 고객 센터 페이지로 이동
        TextView gotoServiceCenter = (TextView)findViewById(R.id.gotoServiceCenter);
        gotoServiceCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoServiceCenter = new Intent(settingMainActivity.this,service_Center.class);
                gotoServiceCenter.putExtra("loginUserId",loginUserId);
                startActivity(gotoServiceCenter);
            }
        });

    }

    // 기본 사용자 정보 가지고오기
    public void getUserData(){
        loginMode = getIntent().getStringExtra("loginMode");
        loginUserId= getIntent().getStringExtra("loginUserId");
    }

    // ( 일반 로그인 ) 로그아웃
    public void logOutEvent(){

        // 자동로그인 해제
        SharedPreferences autoLoginSharedDB = getSharedPreferences("autoSet",MODE_PRIVATE);
        SharedPreferences.Editor editor = autoLoginSharedDB.edit();
        editor.putInt("mode",0);
        editor.commit();


        // 계정 정보 비워주기
        SharedPreferences autoLoginUserData = getSharedPreferences("autoLoginUserData",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = autoLoginUserData.edit();
        editor2.clear();

        //서비스 종료
        try{
            stopService(new Intent(this, socketService.class));//종료방식2
        }catch(Exception e){

        }

        HelperMain a = (HelperMain) HelperMain.activity;
        a.finish();

        // 로그인 화면으로 이동
        Intent gotoLoginPage = new Intent(this,login.class);
        startActivity(gotoLoginPage);
        finish();
    }

    // ( 카카오 로그인 ) 로그아웃
    public void kakaoLogOutEvent(){
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

                // 자동로그인 해제
                SharedPreferences autoLoginSharedDB = getSharedPreferences("autoSet",MODE_PRIVATE);
                SharedPreferences.Editor editor = autoLoginSharedDB.edit();
                editor.putInt("mode",0);
                editor.commit();


                // 계정 정보 비워주기
                SharedPreferences autoLoginUserData = getSharedPreferences("autoLoginUserData",MODE_PRIVATE);
                SharedPreferences.Editor editor2 = autoLoginUserData.edit();
                editor2.clear();


                HelperMain a = (HelperMain) HelperMain.activity;
                a.finish();

                // 로그인 화면으로 이동
                Intent gotoLoginPage = new Intent(settingMainActivity.this,login.class);
                startActivity(gotoLoginPage);
                finish();
            }
        });

    }



}
