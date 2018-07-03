package com.example.jeon.helper.setting;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;

import java.util.regex.Pattern;

public class mail_send_service_Center extends AppCompatActivity {

    String title =null;
    String content;
    String eMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_send_service__center);

        setSpinner();

        // 보내기
        Button send = (Button)findViewById(R.id.send_service_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMailToServiceCenter();
            }
        });

        // 취소
        Button send_service_cancel_btn = (Button)findViewById(R.id.send_service_cancel_btn);
        send_service_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // 스피너 설정
    public void setSpinner(){
        //스피너 어댑터 설정
        final Spinner setTitle = (Spinner) findViewById(R.id.title_Spinner);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.service_title, android.R.layout.simple_spinner_item);
        setTitle.setAdapter(yearAdapter);


        setTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText self_make_title = (EditText)findViewById(R.id.self_make_title);

                if(position == 0){
                    title = null;
                    self_make_title.setVisibility(View.GONE);
                    self_make_title.setText(title);
                }else if(position == 1){
                    title = (String) setTitle.getSelectedItem();
                    self_make_title.setVisibility(View.GONE);
                    self_make_title.setText(title);

                }else if(position == 2){
                    title = (String) setTitle.getSelectedItem();
                    self_make_title.setVisibility(View.GONE);
                    self_make_title.setText(title);
                }else if(position == 3){
                    title = (String) setTitle.getSelectedItem();
                    self_make_title.setVisibility(View.GONE);
                    self_make_title.setText(title);
                }else if(position == 4){
                    title = (String) setTitle.getSelectedItem();
                    self_make_title.setVisibility(View.GONE);
                    self_make_title.setText(title);
                }else if(position == 5){
                    self_make_title.setVisibility(View.VISIBLE);
                    self_make_title.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // 이메일 관련 패턴
    // ( 유효성 검사 ) 이메일
    public void compareEmailCheck(){
        EditText UserEmail_forService = (EditText) findViewById(R.id.UserEmail_forService);

        UserEmail_forService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText UserEmail_forService = (EditText) findViewById(R.id.UserEmail_forService);
                String getEmail = UserEmail_forService.getText().toString();

                // 이메일 유효성 정규식
                String eMailPattern = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
                Boolean matchEmailPattern = Pattern.matches(eMailPattern,getEmail);

                if( matchEmailPattern == true){ // 형식에 일치 한다면.
                    eMail = getEmail;
                }else{
                    eMail = null;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //저장 버튼 이벤트
    public void sendMailToServiceCenter(){
        //이메일 서식 처리 이벤트
        compareEmailCheck();

        // 제목 가지고 오기
        EditText self_make_title = (EditText)findViewById(R.id.self_make_title);
        String result = self_make_title.getText().toString();


        // 내용 가지고 오기
        EditText eMaileContentToservice = (EditText)findViewById(R.id.eMaileContentToservice);
        content = eMaileContentToservice.getText().toString();


        if(result.equals("") || content == null || content.equals("") || eMail==null || eMail.equals("") ){
            Toast.makeText(this, "서식에 알맞게 입력해주세요", Toast.LENGTH_SHORT).show();
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }else{
            Toast.makeText(this, "접수 완료", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

}
