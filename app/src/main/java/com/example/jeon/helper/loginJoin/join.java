package com.example.jeon.helper.loginJoin;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class join extends Activity {

    public Boolean passCompareResult; // 비밀번호가 일치하는가 여부
    public Boolean passPatternResult; // 비밀번호 패턴 일치 여부
    public Boolean isUseingID = false;
    public Boolean isUseingNickName = false;
    public Boolean isCheckedIdBtn = false;
    public Boolean isCheckedNickBtn = false;
    public Boolean isEmailPatternCheck = false;

    ip ipa = new ip();
    String ipad = ipa.getIp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 시계창 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_join);

        // 위젯 아이디
        EditText joinUserID = (EditText) findViewById(R.id.joinUserID);   // 입력방식을 영어 숫자로만 사용하도록 하기위해서 사용했음

        // 유효성 검사 텍스트박스 아이디 및 중복확인 버튼
        Button idCheck = (Button) findViewById(R.id.idCheck);
        Button nickNameCheck = (Button) findViewById(R.id.nickNameCheck);


        // 버튼및 체크박스, 스피너 위젯 아이디
        Button joinBtn = (Button) findViewById(R.id.joinBtn);
        Button joinCancel = (Button) findViewById(R.id.joinCancel);


        //스피너
        setSpinner();

        // ------------------------------------------ 버튼 이벤트 ----------------------------------

        // 아이디 중복확인 버튼 ( DB에 있는 데이터와 비교합니다. )
        idCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheck();
            }
        });

        // 닉네임 중복확인 버튼  ( DB에 있는 데이터와비교합니다. )
        nickNameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickNameCheck();
            }
        });


        // 가입버튼 ( DB에  저장합니다. )
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinCheck();
            }
        });

        // 취소버튼 ( 이전 로그인 페이지로 이동합니다. )
        joinCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //------------------------------------------ 아이디 입력 타입 필터링 -----------------------

        // 포커스가 주어졌을 시  키보드 타입을 영어로
        joinUserID.setPrivateImeOptions("defaultInputmode=english;");

        // 아이디 : 영어나 숫자가 아닌경우 입력을 받지 않을거임 (영어나 숫자가 아닌경우 입력되지 않음 )
        joinUserID.setFilters(new InputFilter[]{filterAlphaNum});


        // ---------------------------------------- 중복확인, 유효성 검사 -------------------------
        // 아이디 닉네임 중복확인후 변경하는 경우 다시 체크하기 위해서
        checkChangeId();
        checkChangeNick();

        // 비밀번호 유효성 검사.
        comparePassCheck();

        // 이메일 유효성 검사
        compareEmailCheck();

        //---------------------------------------체크박스 중복 클릭 이벤트 처리 -------------------------
        checkBoxEvent();

    }

    // 스피너 어댑터 설정하기.
    public void setSpinner() {
        //스피너 어댑터 설정
        Spinner joinLocation = (Spinner) findViewById(R.id.joinLocation);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        joinLocation.setAdapter(yearAdapter);

    }

    //------------------------------ 체크박스 ( 성별 ) 중복 처리 이벤트 ------------------

    public void checkBoxEvent(){
        CheckBox joinCheckBoxMen = (CheckBox) findViewById(R.id.joinCheckBoxMen);
        CheckBox joinCheckBoxGirl = (CheckBox) findViewById(R.id.joinCheckBoxGirl);

        joinCheckBoxMen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked == true){
                    CheckBox joinCheckBoxGirl = (CheckBox) findViewById(R.id.joinCheckBoxGirl);
                    joinCheckBoxGirl.setChecked(false);
                }
            }
        });

        joinCheckBoxGirl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked == true){
                    CheckBox joinCheckBoxMen = (CheckBox) findViewById(R.id.joinCheckBoxMen);
                    joinCheckBoxMen.setChecked(false);
                }
            }
        });
    }

    //------------------------------------  유효성 검사 ---------------------------------

    // ( 유효성 검사 ) 비밀번호 유효성 검사하기. ( 영문 숫자 특수문자 조합 )
    public void comparePassCheck(){

        EditText joinUserPass = (EditText) findViewById(R.id.joinUserPass);
        EditText joinUserPassCom = (EditText) findViewById(R.id.joinUserPassCom);

        //입력 이벤트 첫번째 비밀번호 박스
        joinUserPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력되는 텍스트에 변화가 있을때.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력이 끈났을때
                EditText joinUserPass = (EditText) findViewById(R.id.joinUserPass);
                String comPass1 = joinUserPass.getText().toString();

                // 자바 정규식  ( 영문 대소문자 구분,  숫자, 특스문자  8자리에서 10자리 사이 )
                String pwPattern  = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,10}$";

                // 정규식 일치 여부
                Boolean matchPwPattern1 = Pattern.matches(pwPattern,comPass1);
                TextView joinPassCheck1 = (TextView)findViewById(R.id.joinPassCheck1);
                TextView joinPassCheck2 = (TextView)findViewById(R.id.joinPassCheck2);

                if ( matchPwPattern1 == true){
                    joinPassCheck1.setText("비밀번호 서식에 일치합니다");
                    String color = "#FF119F53"; // 초록색
                    joinPassCheck1.setTextColor(Color.parseColor(color));
                    passPatternResult = true;
                }
                else{
                    joinPassCheck1.setText("비밀번호 서식에 일치하지 않습니다.");
                    String color = "#FF0000"; // 빨간색
                    joinPassCheck1.setTextColor(Color.parseColor(color));
                    passPatternResult = false;
                }

                EditText joinUserPassCom = (EditText) findViewById(R.id.joinUserPassCom);
                String comPass2 = joinUserPassCom.getText().toString();

                if( comPass1.equals(comPass2)){
                    joinPassCheck2.setText("비밀번호 일치");
                    String color = "#FF119F53"; // 초록색
                    joinPassCheck2.setTextColor(Color.parseColor(color));
                    passCompareResult = true;
                }else{
                    joinPassCheck2.setText("비밀번호 불일치");
                    String color = "#FF0000"; // 빨간색
                    joinPassCheck2.setTextColor(Color.parseColor(color));
                    passCompareResult= false;
                }



            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력하기 전에.
            }
        });

        // 두번째 비밀번호박스 체크
        joinUserPassCom.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력되는 텍스트에 변화가 있을때.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력이 끈났을때
                // 비교대상이되는 값 가져오기
                EditText joinUserPass = (EditText) findViewById(R.id.joinUserPass);
                String comPass1 = joinUserPass.getText().toString();

                EditText joinUserPassCom = (EditText) findViewById(R.id.joinUserPassCom);
                String comPass2 = joinUserPassCom.getText().toString();
                TextView joinPassCheck2 = (TextView)findViewById(R.id. joinPassCheck2);

                if(comPass2.equals("")||TextUtils.isEmpty(comPass2)){
                    joinPassCheck2.setText("비밀번호 불일치");
                    String color = "#FF0000"; // 빨간색
                    joinPassCheck2.setTextColor(Color.parseColor(color));
                    passCompareResult = false;
                }else{


                    if( comPass1.equals(comPass2)){
                        joinPassCheck2.setText("비밀번호 일치");
                        String color = "#FF119F53"; // 초록색
                        joinPassCheck2.setTextColor(Color.parseColor(color));
                        passCompareResult = true;
                    }else{
                        joinPassCheck2.setText("비밀번호 불일치");
                        String color = "#FF0000"; // 빨간색
                        joinPassCheck2.setTextColor(Color.parseColor(color));
                        passCompareResult = false;
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력하기 전에.


            }
        });


    }

    // ( 유효성 검사 ) 이메일
    public void compareEmailCheck(){
        EditText joinUserEmail = (EditText) findViewById(R.id.joinUserEmail);

        joinUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText joinUserEmail = (EditText) findViewById(R.id.joinUserEmail);
                String getEmail = joinUserEmail.getText().toString();
                TextView joinEmailCheck = (TextView)findViewById(R.id. joinEmailCheck);

                // 이메일 유효성 정규식
                String eMailPattern = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
                Boolean matchEmailPattern = Pattern.matches(eMailPattern,getEmail);

                if( matchEmailPattern == true){ // 형식에 일치 한다면.
                    joinEmailCheck.setText("이메일 형식이 올바릅니다.");
                    String color = "#FF119F53"; // 초록색
                    joinEmailCheck.setTextColor(Color.parseColor(color));
                    isEmailPatternCheck = true;
                }else{
                    joinEmailCheck.setText("이메일 형식이 올바르지 않습니다.");
                    String color = "#FF0000"; // 빨간색
                    joinEmailCheck.setTextColor(Color.parseColor(color));
                    isEmailPatternCheck = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





    }




    // ---------------------------------- 아이디 닉네임 -------------------------------

    // ( 중복 확인 )아이디 중복확인 버튼
    public void idCheck() {
        EditText joinUserID = (EditText) findViewById(R.id.joinUserID);
        String comID = joinUserID.getText().toString();  //아이디 창에 입력 된 아이디 일시 저장

        dbCheckForId(comID);
        isCheckedIdBtn = true;

    }

    // ( 중복 확인 )닉네임 중복확인 버튼
    public void nickNameCheck(){
        EditText joinUserNickName = (EditText) findViewById(R.id.joinUserNickName);
        String comNickName = joinUserNickName.getText().toString();  //닉네임 창에 입력 된 닉네임 일시 저장

        dbCheckForNickName(comNickName);
        isCheckedNickBtn = true;
    }


    // (아이디 중복확인 버튼 ) 디비에 중복된 아이디가 있는지 php를 보내서 확인한다.
    public void dbCheckForId(String id) {

        class idCheckHTTPt extends AsyncTask<Void, Void, String> {
            String id;
            String myResult;


            public idCheckHTTPt(String id) {
                this.id = id;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Log.d("aaaaaaaaaaaaaaaaaa", "Val/////" + id);

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------


                    URL url = new URL(ipad+"/joinIdCheck.php");
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
                    buffer.append("id").append("=").append(id).append("&");                 // php 변수에 값 대입
                    buffer.append("loginMode").append("=").append("1");

                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    myResult = builder.toString();                       // 전송결과를 전역 변수에 저장
                    Log.d("bbbbbbbbbbbbbbbb", "aaaaaaaaaaaa/////" + myResult);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return myResult;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "사용가능", Toast.LENGTH_SHORT).show();
                    isUseingID = true;
                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(), "이미 사용중인 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    isUseingID = false;
                }


            }
        }

        idCheckHTTPt gotoDBUerId = new idCheckHTTPt(id);
        gotoDBUerId.execute();


    }

    // ( 닉네임 중복확인 버튼 )
    public void dbCheckForNickName(String nickName){

        class nickNameCheckHTTPt extends AsyncTask<Void, Void, String> {
            String nickName;
            String myResult;


            public nickNameCheckHTTPt(String nickName) {
                this.nickName = nickName;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Log.d("aaaaaaaaaaaaaaaaaa", "Val/////" + nickName);

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------


                    URL url = new URL(ipad+"/joinNickNameCheck.php");
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
                    buffer.append("nickName").append("=").append(nickName);                 // php 변수에 값 대입


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    myResult = builder.toString();                       // 전송결과를 전역 변수에 저장
                    Log.d("bbbbbbbbbbbbbbbb", "aaaaaaaaaaaa/////" + myResult);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return myResult;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "사용가능", Toast.LENGTH_SHORT).show();
                    isUseingNickName = true;
                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(), "이미 사용중인 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                    isUseingNickName = false;
                }


            }
        }

        nickNameCheckHTTPt gotoDBUerId = new nickNameCheckHTTPt(nickName);
        gotoDBUerId.execute();
    }


    // 아이디 닉네임 중복확인후 변경하는 경우 다시 체크하기 위해서
    public void checkChangeId(){
        EditText joinUserID = (EditText) findViewById(R.id.joinUserID);   // 입력방식을 영어 숫자로만 사용하도록 하기위해서 사용했음
        joinUserID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCheckedIdBtn = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void checkChangeNick(){
        EditText joinUserNickName = (EditText) findViewById(R.id.joinUserNickName);
        joinUserNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isCheckedNickBtn = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    //------------------------------- 회원 가입 ---------------------------------------

    // ( 회원 가입 ) 비어있는 데이터가 있는경우 토스트 메세지로 알려줄 것이다. 비어있지 않은경우 DB에 저장.
    public void joinCheck() {
        // 위젯 아이디
        EditText joinUserID = (EditText) findViewById(R.id.joinUserID);
        EditText joinUserNickName = (EditText) findViewById(R.id.joinUserNickName);
        EditText joinUserPass = (EditText) findViewById(R.id.joinUserPass);
        EditText joinUserPassCom = (EditText) findViewById(R.id.joinUserPassCom);
        EditText joinUserEmail = (EditText) findViewById(R.id.joinUserEmail);

        // 버튼및 체크박스, 스피너 위젯 아이디
        CheckBox joinCheckBoxMen = (CheckBox) findViewById(R.id.joinCheckBoxMen);
        CheckBox joinCheckBoxGirl = (CheckBox) findViewById(R.id.joinCheckBoxGirl);
        Spinner joinLocation = (Spinner) findViewById(R.id.joinLocation);


        // 값 가져오기
        String id = joinUserID.getText().toString();
        String nickName = joinUserNickName.getText().toString();
        String pass = joinUserPass.getText().toString();
        String passCom = joinUserPassCom.getText().toString();
        String eMail = joinUserEmail.getText().toString();

        String sex;  // 체크박스에 따른 값을 저장
        String location = joinLocation.getSelectedItem().toString(); // 지역을 저장

        // 성별 체크박스에 따른 문자 변환
        if (joinCheckBoxMen.isChecked()) {
            sex = "남자";
        } else if (joinCheckBoxGirl.isChecked()) {
            sex = "여자";
        } else {
            sex = null;
        }

        // 비밀번호가 같은 경우 하나로 통일 시켜서 사용
        String setUserPass = "";
        if (pass.equals(passCom)) {
            setUserPass = pass;
        }


        // 비어 있는 데이터가 있는 경우
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(nickName) || TextUtils.isEmpty(pass) ||
                TextUtils.isEmpty(passCom) || TextUtils.isEmpty(eMail) || TextUtils.isEmpty(sex) || location.equals("---- 선택 ----")) {
            Toast.makeText(this, "데이터를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }else if (passCompareResult == true && passPatternResult == true && isUseingID == true && isUseingNickName == true &&
                isCheckedIdBtn == true && isCheckedNickBtn == true && isEmailPatternCheck == true){
            // 디비로 전송하기 ( 중복확인 비밀번호 일치 여부 모두확인 )
            postDB(id, nickName, setUserPass, eMail, sex, location);
            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
            onBackPressed();

        }else {
            Toast.makeText(this, "입력 데이터를 확인해주세요 (ex 중복확인)", Toast.LENGTH_SHORT).show();
        }


    }

    // 디비에 회원가입 정보를 저장한다. ( 회원가입 Join 버튼 클릭시 발생 이벤트 )
    // 아이디    닉네임       비밀번호            이메일         성별          지역
    public void postDB(String id, String nickName, String setUserPass, String eMail, String sex, String location) {

        class joinHTTPt extends AsyncTask<Void, Void, Void> {
            String id;
            String nickName;
            String setUserPass;
            String eMail;
            String sex;
            String location;

            public joinHTTPt(String id, String nickName, String setUserPass, String eMail, String sex, String location) {
                this.id = id;
                this.nickName = nickName;
                this.setUserPass = setUserPass;
                this.eMail = eMail;
                this.sex = sex;
                this.location = location;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기

                    URL url = new URL(ipad+"/join.php");
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
                    buffer.append("id").append("=").append(id).append("&");                 // php 변수에 값 대입
                    buffer.append("nickName").append("=").append(nickName).append("&");
                    buffer.append("pass").append("=").append(setUserPass).append("&");   // php 변수 앞에 '$' 붙이지 않는다
                    buffer.append("eMail").append("=").append(eMail).append("&");           // 변수 구분은 '&' 사용
                    buffer.append("sex").append("=").append(sex).append("&");
                    buffer.append("loginMode").append("=").append("1").append("&");
                    buffer.append("location").append("=").append(location);


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                    }
//                    String myResult = builder.toString();                       // 전송결과를 전역 변수에 저장
//
//                    Log.d("bbbbbbbbbbbbbbbb","aaaaaaaaaaaa/////"+myResult);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

            joinHTTPt gotoDBUerId = new joinHTTPt(id, nickName, setUserPass, eMail, sex, location);
            gotoDBUerId.execute();

    }



    // 아이디를 입력받는 에디트 텍스트에 문자 사용 제한을 함.
    protected InputFilter filterAlphaNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                // Toast.makeText(join.this, "아이디는 영문, 숫자로만 입력이 가능합니다.", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };



}





