package com.example.jeon.helper.loginJoin;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.cash.GmailSender;
import com.example.jeon.helper.cash.exchange_Real_money;
import com.example.jeon.helper.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class searchPassWord extends AppCompatActivity {

    String userId;
    String confirmKey;

    String userEmail;



    ip ipa = new ip();
    String ipad = ipa.getIp();

    // ---------------------------- 다이얼 로그 ------------------------------------------
    AlertDialog dialog2;
    View mView;
    String getKey;


    AlertDialog dialog3;
    String pwPattern  = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,10}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_search_pass_word);

        confirmKey = null;
        int max = 999999;
        int min = 100000;
        long ketValue = (long) (Math.random() * (max - min)) + min;
        confirmKey = String.valueOf(ketValue);

        // 버튼
        Button search_page_code_send_Btn = (Button)findViewById(R.id.search_page_code_send_Btn);
        search_page_code_send_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail();
            }
        });

    }

    // 이메일 입력  처리 이벤트
    public void inputEmail(){
        EditText search_page_eMail = (EditText)findViewById(R.id.search_page_eMail);

        // 공백확인
        userId = search_page_eMail.getText().toString();
        if(TextUtils.isEmpty(userId) || userId == null || userId.equals("")){
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            makeConfirmKeyHttp(1);
        }

    }

    // 서버에 인증 메일 전송 및 이메일 전송
    public void makeConfirmKeyHttp(int mode){
        class setConfirmKeyHttp extends AsyncTask<Void,Void,String> {

            int mode;
            ProgressDialog dialog = new ProgressDialog(searchPassWord.this);

            public setConfirmKeyHttp(int mode) {
                this.mode = mode;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("인증메일 전송중 ");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/login_search_password.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------
                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    // 서버로 값 전달.
                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("id").append("=").append(userId).append("&");                 // php 변수에 값 대입
                    buffer.append("confirmKey").append("=").append(confirmKey).append("&");
                    buffer.append("userKey").append("=").append(getKey).append("&");
                    buffer.append("mode").append("=").append(mode);

                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();

                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    userEmail = builder.toString();                       // 전송결과를 전역 변수에 저장
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



              //   --------------------------------- 메일 전송 -------------------------------------
                if ( mode == 1){
                    if( userEmail.equals("없음")){
                    }else{
                        Log.d("사용자의 이메일 주소",userEmail);
                        sendKeyToUserMail();
                    }
                }

                return userEmail;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }

                if ( mode == 1){
                    if( userEmail.equals("없음")){
                        Toast.makeText(searchPassWord.this, "존재하지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(searchPassWord.this, "인증 메일 발송", Toast.LENGTH_SHORT).show();
                        Log.d("사용자의 이메일 주소",userEmail);
                        checkDialog();
                    }
                }else if (mode == 2){
                    if(s.equals("불일치")){
                        Toast.makeText(searchPassWord.this, "인증 번호가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }else if(s.equals("일치")){
                        Toast.makeText(searchPassWord.this, "인증 번호 일치", Toast.LENGTH_SHORT).show();
                        // 비밀번호 변경 페이지로 이동.

                        // 이전 다이얼로그 없앰
                        dialog2.dismiss();

                        // 새로운 다이얼로그 띄우기
                        changePassWord();



                    }else{
                        Log.d("계정 찾기 결과",""+s);
                    }
                }else if (mode == 3){
                    Log.d("계정 찾기 인증키 삭제",""+s);
                }

            }
        }

        setConfirmKeyHttp getData = new setConfirmKeyHttp(mode);
        getData.execute();
    }

    // (메일 보내기)
    public void sendKeyToUserMail() {
        try {
            GmailSender gMailSender = new GmailSender();
            //GMailSender.sendMail(제목, 본문내용, 받는사람);
            gMailSender.sendMail("helper 계정 찾기", "인증 키 입니다.\n 정확하게 입력하세요.\n" + confirmKey, userEmail);
            Log.d("이메일을 성공적으로 보냈습니다.", "이메일을 성공적으로 보냈습니다.");
        } catch (SendFailedException e) {
            Log.d("이메일 형식이 잘못되었습니다", "이메일 형식이 잘못되었습니다");
        } catch (MessagingException e) {
            Log.d("인터넷 연결을 확인해주십시오", "인터넷 연결을 확인해주십시오");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  인증키 입력 다이얼로그
    public void checkDialog(){
    //login_search_comfirm_key_check_dialog
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.login_search_comfirm_key_check_dialog, null);

        // 확인버튼
        Button gotoChangeBtn = (Button)mView.findViewById(R.id.gotoChangeBtn);
        gotoChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText search_pass_key = (EditText)mView.findViewById(R.id.search_pass_key);
                getKey = search_pass_key.getText().toString();
                if (TextUtils.isEmpty(getKey) || getKey == null || getKey == ""){
                    Toast.makeText(searchPassWord.this, "인증번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    makeConfirmKeyHttp(2);
                }
            }
        });

        //취소 버튼
        Button search_passCancel = (Button)mView.findViewById(R.id.search_passCancel);
        search_passCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeConfirmKeyHttp(3);
            }
        });



        aBuilder.setView(mView);
        dialog2 = aBuilder.create();
        dialog2.show();

    }

    // 비밀번호 변경 다이얼로그
    public void changePassWord(){
        AlertDialog.Builder aBuilder2 = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.login_change_pass_page, null);

        Button resetPassBtn = (Button)mView.findViewById(R.id.resetPassBtn);
        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetPass = (EditText)mView.findViewById(R.id.resetPass);
                EditText resetPassCheck = (EditText)mView.findViewById(R.id.resetPassCheck);

                String p1 = resetPass.getText().toString();
                String p2 = resetPassCheck.getText().toString();

                if(TextUtils.isEmpty(p1) || p1 == null || p1 == "" || TextUtils.isEmpty(p2) || p2 == null || p2==""){
                    Toast.makeText(searchPassWord.this, "새 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Boolean matchPwPattern1 = Pattern.matches(pwPattern,p1);
                    Boolean matchPwPattern2 = Pattern.matches(pwPattern,p2);

                    if( matchPwPattern1 == false || matchPwPattern2 == false){
                        Toast.makeText(searchPassWord.this, "비밀번호 서식이 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        if( p1.equals(p2)){

                            // 서버 전송을 통한 비밀 번호 변경
                            changePassHttp(p1);

                        }else{
                            Toast.makeText(searchPassWord.this, "비밀번호가 동일하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }

                }

            }
        });


        aBuilder2.setView(mView);
        dialog3 = aBuilder2.create();
        dialog3.show();
    }

    // 비밀 번호 변경
    public void changePassHttp(final String a){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(searchPassWord.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("비밀번호 설정중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/change_Password.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------

                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST

                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // 서버로 값 전달.
                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("userId").append("=").append(userId).append("&");                 // php 변수에 값 대입
                    buffer.append("newPass").append("=").append(a);

                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result2;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){
                }

                Log.d("비밀번호 변경 ",""+s);
                if(s.equals("성공")){
                    Toast.makeText(searchPassWord.this, "비밀번호 변경 완료", Toast.LENGTH_SHORT).show();
                    dialog3.dismiss();
                    finish();
                }

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }


}
