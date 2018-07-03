package com.example.jeon.helper.setting;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.jeon.helper.R;

public class my_Setting_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my__setting__page);

        // 설정 페이지는  sharedPreference 를 활용해서 알람에 관련된 설정을 저장하고,  메세지 수신시 발생할 노티 이벤트를 없애줄 것이다.

        ImageView my_setting_page_backBtn = (ImageView)findViewById(R.id.my_setting_page_backBtn);
        my_setting_page_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAlarmsSetting();

        messageAlarm();
        helpAlarm();
        friendAlarm();
        commentAlarm();
        exchangAlarm();
    }

    public void messageAlarm(){
        // 메세지 알람
        Switch chattingNoti_switch = (Switch)findViewById(R.id.chattingNoti_switch);
        chattingNoti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == false){
                    Toast.makeText(my_Setting_Page.this, "메세지 알람 해제", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("message", false);
                    editor.commit();

                }else{
                    Toast.makeText(my_Setting_Page.this, "메세지 알람 설정", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("message", true);
                    editor.commit();
                }

            }
        });
    }

    public void helpAlarm(){
        // 도움알림
        Switch helperNoti_switch = (Switch)findViewById(R.id.helperNoti_switch);
        helperNoti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == false){
                    Toast.makeText(my_Setting_Page.this, "도움 알람 해제", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("help", false);
                    editor.commit();
                }else{
                    Toast.makeText(my_Setting_Page.this, "도움 알람 설정", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("help", true);
                    editor.commit();
                }

            }
        });
    }

    public void friendAlarm(){
        // 친구신청 알림
        Switch applyFriendNoti_switch = (Switch)findViewById(R.id.applyFriendNoti_switch);
        applyFriendNoti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == false){
                    Toast.makeText(my_Setting_Page.this, "친구신청 알람 해제", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("friend", false);
                    editor.commit();

                }else{
                    Toast.makeText(my_Setting_Page.this, "친구신청 알람 설정", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("friend", true);
                    editor.commit();

                }


            }
        });
    }

    public void commentAlarm(){


        // 댓글 알림
        Switch commentNoti_switch = (Switch)findViewById(R.id.commentNoti_switch);
        commentNoti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == false){
                    Toast.makeText(my_Setting_Page.this, "댓글 알람 해제", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("comment", false);
                    editor.commit();
                }else{
                    Toast.makeText(my_Setting_Page.this, "댓글 알람 설정", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("comment", true);
                    editor.commit();
                }

            }
        });
    }

    public void exchangAlarm(){
        // 정산알림
        Switch exchangeNoti_switch = (Switch)findViewById(R.id.exchangeNoti_switch);
        exchangeNoti_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == false){
                    Toast.makeText(my_Setting_Page.this, "정산 알람 해제", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("exchange", false);
                    editor.commit();
                }else{
                    Toast.makeText(my_Setting_Page.this, "정산 알람 설정", Toast.LENGTH_SHORT).show();

                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("exchange", true);
                    editor.commit();
                }

            }
        });
    }


    // 기본 세팅 값 가지고 옥
    public void getAlarmsSetting(){
        SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
        Boolean result = pref.getBoolean("message", true);
        Switch chattingNoti_switch = (Switch)findViewById(R.id.chattingNoti_switch);
        if(result == true){
            chattingNoti_switch.setChecked(true);
        }else{
            chattingNoti_switch.setChecked(false);
        }

        Switch helperNoti_switch = (Switch)findViewById(R.id.helperNoti_switch);
        Boolean result1 = pref.getBoolean("help", true);
        if(result1 == true){
            helperNoti_switch.setChecked(true);
        }else{
            helperNoti_switch.setChecked(false);
        }

        Switch exchangeNoti_switch = (Switch)findViewById(R.id.exchangeNoti_switch);
        Boolean result2 = pref.getBoolean("exchange", true);
        if(result2 == true){
            exchangeNoti_switch.setChecked(true);
        }else{
            exchangeNoti_switch.setChecked(false);
        }


        Switch commentNoti_switch = (Switch)findViewById(R.id.commentNoti_switch);
        Boolean result3 = pref.getBoolean("comment", true);
        if(result3 == true){
            commentNoti_switch.setChecked(true);
        }else{
            commentNoti_switch.setChecked(false);
        }

        Switch applyFriendNoti_switch = (Switch)findViewById(R.id.applyFriendNoti_switch);
        Boolean result4 = pref.getBoolean("friend", true);
        if(result4 == true){
            applyFriendNoti_switch.setChecked(true);
        }else{
            applyFriendNoti_switch.setChecked(false);
        }
    }
}
