package com.example.jeon.helper.setting;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jeon.helper.R;

public class service_Center extends AppCompatActivity {


    String loginUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service__center);

        // 로그인 사용자 아이디 가지고 오기
        getUserdata();



        // 뒤로가기 버튼
        ImageView service_center_backBtn = (ImageView)findViewById(R.id.service_center_backBtn);
        service_center_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 전화하기 버튼
        LinearLayout call_service_Center_Btn = (LinearLayout)findViewById(R.id.call_service_Center_Btn);
        call_service_Center_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel ="tel:0100000000";
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
            }
        });

        // 이메일 문의 버튼
        LinearLayout mail_service = (LinearLayout)findViewById(R.id.mail_service);
        mail_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSendEmail = new Intent(service_Center.this,mail_send_service_Center.class);
                startActivity(gotoSendEmail);
            }
        });

    }

    // 로그인 사용자 데이터 받아오기
    public void getUserdata(){
        loginUserId = getIntent().getStringExtra("loginUserId");
        Log.d("서비스 센터 로그인 사용자 아이디",""+loginUserId);

    }
}
