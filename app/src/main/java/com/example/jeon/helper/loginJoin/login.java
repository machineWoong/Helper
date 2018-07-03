package com.example.jeon.helper.loginJoin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.HelperMain;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;



import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends Activity {

    private SessionCallback callback;      //콜백 선언

    // 콜백을 통해서 카카오톡 로그인시 아이디와 닉네임을 저장하여, DB에 전송하여 비교하여 가입하거나 바로 이동.
    String kakaoID;
    String kakaoNickName;
    String kakaoProfileImageURL;


    ip ipa = new ip();
    String ipad = ipa.getIp();

    Boolean setAutoLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        callback = new SessionCallback();                  // 이 두개의 함수 중요함
        Session.getCurrentSession().addCallback(callback);

        // --------------------------- 위젯 아이디 ------------------------------------------------
        // 아이디 비밀번호
        EditText loginUserID = (EditText)findViewById(R.id.LoginUserID);
        EditText LoginUserPassWord = (EditText)findViewById(R.id.LoginUserPassWord);
        // 버튼 ( 로그인 카카오 로그인 )
        Button LoginBtn = (Button)findViewById(R.id.LoginBtn);

        // 회원가입, 비밀번호 찾기.
        TextView JoinTextBtn = (TextView)findViewById(R.id.JoinTextBtn);
        TextView SearchIdPassTextBtn = (TextView)findViewById(R.id.SearchIdPassTextBtn);
        // ----------------------------------------------------------------------------------------

        // 포커스가 주어졌을 시  키보드 타입을 영어로
        loginUserID.setPrivateImeOptions("defaultInputmode=english;");

        // 아이디 : 영어나 숫자가 아닌경우 입력을 받지 않을거임 (영어나 숫자가 아닌경우 입력되지 않음 )
        loginUserID.setFilters(new InputFilter[]{filterAlphaNum});



        // ----------------------------클릭 이벤트 ------------------------------------------
        // 로그인 버튼 클릭이벤트
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClickEvent();
                // getHashKey();  해쉬키가 필요해서 호출해서 사용했엇음.
            }
        });

        // 회원가입 클릭이벤트
        JoinTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoJoinAct();
            }
        });

        // 아이디 비밀번호 찾기 클릭 이벤트
        SearchIdPassTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSearchPassWord = new Intent(login.this,searchPassWord.class);
                startActivity(gotoSearchPassWord);
            }
        });

        // 자동 로그인 체크 박스

        CheckBox autoLogin = (CheckBox)findViewById(R.id.autoLogin);
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    setAutoLogin = true;
                }else{
                    setAutoLogin = false;
                }
            }
        });


        try {
            checkShardDB();
        }catch (Exception e){

        }

    }

    // ---------------------------------- 자동 로그인 관련 -----------------------------------------



    // 쉐어드 프리퍼런스 값 세팅
    public void setShardDB(int mode){
        SharedPreferences autoLoginSharedDB = getSharedPreferences("autoSet",MODE_PRIVATE);
        SharedPreferences.Editor editor = autoLoginSharedDB.edit();
        editor.putInt("mode",mode);
        editor.commit();

        if (mode == 1){
            Log.d("자동 로그인 모드 설정","설정");
        }else{
            Log.d("자동 로그인 모드 설정","해제");
        }

    }


    // 일반 로그인 정보 세팅
    public void autoLoginUserData(String loginMode, String id,String nick){
        // 아이디와 비밀번호 로그인 모드를 저장할 쉐어드 프리퍼런스 생성
        SharedPreferences autoLoginUserData = getSharedPreferences("autoLoginUserData",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = autoLoginUserData.edit();
        editor2.clear();

        editor2.putString("loginMode",loginMode);
        editor2.putString("id",id);
        editor2.putString("nick",nick);
        editor2.commit();
    }

    public void checkShardDB(){
        // 쉐어드 프리퍼 런스 값 조사. ( 오토 로그인인지 아닌지 )
        SharedPreferences autoLoginSharedDB = getSharedPreferences("autoSet", MODE_PRIVATE);
        int nVal = autoLoginSharedDB.getInt("mode", 9999);

        Log.d("불러온 값",""+nVal);

        if ( nVal  == 0){
            // 일반 로그인으로 아무것도 안하고 가만히 있어라
        }else if ( nVal == 1){
            // 자동로그인을 위해서 값을 불러온다.
            SharedPreferences autoLoginUserData = getSharedPreferences("autoLoginUserData",MODE_PRIVATE);

            Log.d("자동 로그인 실행","ㄱㄱ");

            String loginMode = autoLoginUserData.getString("loginMode","없음");
            String id = autoLoginUserData.getString("id","없음");
            String nick = autoLoginUserData.getString("nick","없음");

            Log.d("모드값",""+loginMode);


            if(loginMode.equals("1")){
                // 일반 로그인
                // 로그인 모드와 아이디를 싣어서 인텐트로 보낸다
                Intent intent = new Intent(login.this,HelperMain.class);
                intent.putExtra("loginMode","1");
                intent.putExtra("loginId",id);
                startActivity(intent);
                finish();

            }else{

                // 카카오 로그인
                // 로그인 모드와 아이디 닉을 싣어서 인텐트로 보낸다 .
                Intent intent = new Intent(login.this,HelperMain.class);
                intent.putExtra("loginMode","2");
                intent.putExtra("loginId",id);
                intent.putExtra("loginNick",nick);
                startActivity(intent);
                finish();
            }
        }

    }



    // --------------------------------------- 로그인 ----------------------------------------------

    // 로그인 버튼 클릭 이벤트  onPostExecute 안에서 화면 이동하면서 인텐트로 값을 전달해준다.
    public void loginClickEvent(){

        EditText loginUserID = (EditText)findViewById(R.id.LoginUserID);
        EditText loginUserPassWord = (EditText)findViewById(R.id.LoginUserPassWord);

        String getId = loginUserID.getText().toString();
        String getPass =loginUserPassWord.getText().toString();

        class loginCheckHttp extends AsyncTask <Void,Void,String>{

            String id;
            String pass;
            String loginResult;
            ProgressDialog dialog = new ProgressDialog(login.this);


            public loginCheckHttp (String id, String pass){
                this.id = id;
                this.pass = pass;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("로그인 정보 확인중");
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


                    URL url = new URL(ipad+"/login.php");
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
                    buffer.append("pass").append("=").append(pass);                 // php 변수에 값 대입



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
                    loginResult = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return loginResult;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){
                }


                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();


                    //자동 로그인 이벤트
                    //체크 박스가 클릭이 되어있을 시에  쉐어드 프리퍼 런스에 저장하도록한다 .

                    if(setAutoLogin == true){
                        // 자동로그인 인경우
                        // 자동로그인 세팅 1
                        setShardDB(1);

                        // 로그인 데이터 등록
                        autoLoginUserData("1",id,"일반로그인");
                    }else if (setAutoLogin == false){
                        // 자동로그인 해제 0
                        setShardDB(0);

                        // 로그인 데이터 삭제
                        SharedPreferences autoLoginUserData = getSharedPreferences("autoLoginUserData",MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = autoLoginUserData.edit();
                        editor2.clear();
                        editor2.commit();

                    }

                    //화면 이동
                    Intent intent = new Intent(login.this,HelperMain.class);
                    intent.putExtra("loginMode","1");
                    intent.putExtra("loginId",id);
                    startActivity(intent);
                    finish();




                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(), "아이디 / 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                }
            }

        }

        if( TextUtils.isEmpty(getId) || TextUtils.isEmpty(getPass)  ){
            Toast.makeText(this, "아이디/비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            try {
                loginCheckHttp gotoDbLoginCheck = new loginCheckHttp(getId,getPass);
                gotoDbLoginCheck.execute();
            }catch (NullPointerException e){
            }
        }
    }

    //----------------------------------- 카카오 로그인 관련 ---------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
    // 카카오 로그인 콜백
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login); // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }
    // (세션 연결 성공 시 동작) 세션 연결과, 사용자의 데이터 값 받아오기
    protected void redirectSignupActivity() {
        // 세션 연결과, 사용자의 데이터 값 받아오기
        requestMe();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */

    //유저의 정보를 받아오는 함수  * 세션이 연결되어있는가?
    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {  // 세션 요청 실패
                Log.d("111111111111111111","1111111111111111111111");

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {  // 세션이 닫혀잇을때
                Log.d("222222222222222222222","2222222222222222222222");

                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                // 세션 오픈은 했으나 사용자 정보 요청 결과 사용자 가입이 안된 상태로 일반적으로 가입창으로 이동한다.
                // 자동가입 앱이 아닌 경우에만 호출된다.

                // 지금 들어와서 정보를 가지고 오려구하면 여기서 걸리는 구만

                Log.d("44444444444444444444","444444444444444444");


            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {

                //성공 시 userProfile 형태로 반환
                //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                //이곳에서 로그인이 완료될시 실행시킬 동작을 추가시켜주시면 됩니다 ~

                // 회원가입이 되어있다면 1  없다면 2 로해서 회원가입을 하도록 해보자

                // 카카오 유저의 로그인 아이디와, 닉네임, 프로필을 받아오자
                kakaoID = Long.toString(userProfile.getId());
                kakaoNickName = userProfile.getNickname();
                kakaoProfileImageURL = userProfile.getProfileImagePath();

                if ( kakaoProfileImageURL == null){
                    kakaoProfileImageURL = "1";  // 베스트 불러올때 이미지가 없는 경우, 설정.
                }

                isUserJoined(); // 신규 기존 유저 확인 DB

            }
        });
    }

    //카카오 로그인 성공시 메인 화면으로 이동 ( + 카카오 자동로그인 )
    private void redirectMainActivity() {

        Log.d("카카오톡 자동 로그인 설정","설정");

        // 자동 로그인 만들기
        setShardDB(1);
        autoLoginUserData("2",kakaoID,kakaoNickName);


        // 데이터  추가로 받기
        Intent intent = new Intent(this,HelperMain.class);
        intent.putExtra("loginMode","2");
        intent.putExtra("loginId",kakaoID);
        intent.putExtra("loginNick",kakaoNickName);
        startActivity(intent);
    }

    // 로그인 실패시 로그인 엑티비티로 이동.
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    // 회원가입 여부 확인하기   0 디비에 있음  1 신규라 디비에 넣어야함 ( 0 이면 바로 redirectMainActivity 이동 1 이면 postDB 하고  redirectMainActivity 이동 )
    public void isUserJoined(){

        class idCheckHTTPt extends AsyncTask<Void, Void, String> {
            String id;
            String myResult;


            public idCheckHTTPt(String id) {
                this.id = id;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

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
                    buffer.append("id").append("=").append(kakaoID).append("&");                 // php 변수에 값 대입
                    buffer.append("loginMode").append("=").append("2");

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

                if (result.equals("1")) { // 신규 회원
                    // 디비에 저장
                    postDB(kakaoID,kakaoNickName,kakaoProfileImageURL);
                    // 메인으로 이동
                    redirectMainActivity();

                } else if (result.equals("0")) { // 기존회원
                    redirectMainActivity();
                }


            }
        }

        idCheckHTTPt gotoDBUerId = new idCheckHTTPt(kakaoID);
        gotoDBUerId.execute();

    }

    // 디비에 회원가입 정보를 저장한다. ( 회원가입 Join 버튼 클릭시 발생 이벤트 )
    // 아이디    닉네임       비밀번호
    public void postDB(String id, String nickName,String kakaoProfileImageURL) {

        class joinHTTPt extends AsyncTask<Void, Void, Void> {
            String id;
            String nickName;
            String kakaoProfileImageURL;

            public joinHTTPt(String id, String nickName, String kakaoProfileImageURL) {
                this.id = id;
                this.nickName = nickName;
                this.kakaoProfileImageURL = kakaoProfileImageURL;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Log.d("aaaaaaaaaaaaaaaaaa", "Val/////" + id);

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------


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
                    buffer.append("kakaoImageURL").append("=").append(kakaoProfileImageURL).append("&");   // php 변수 앞에 '$' 붙이지 않는다
                    buffer.append("loginMode").append("=").append("2");           // 변수 구분은 '&' 사용


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

        joinHTTPt gotoDBUerId = new joinHTTPt(id,nickName,kakaoProfileImageURL);
        gotoDBUerId.execute();

    }

// ------------------------------------------------------------------------( 카카오 로그인 관련 끝 )

    // 아이디를 입력받는 에디트 텍스트에 문자 사용 제한을 함.
   protected InputFilter filterAlphaNum = new InputFilter() {
       @Override
       public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
           Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
           if(!ps.matcher(source).matches()){
              // Toast.makeText(login.this, "아이디는 영문, 숫자로만 입력이 가능합니다.", Toast.LENGTH_SHORT).show();
               return "";
           }
           return null;
       }
   };

    // 회원가입 페이지로 이동
    public void gotoJoinAct(){
        Intent intent=new Intent(login.this,join.class);
        startActivity(intent);
    }





    // 카카오톡 로그인 버튼 클릭 이벤트 (키 해쉬를 획득 )
    public void kakaoLoginClickEvent(){

//        // 해쉬키 가져오는 방법
//        getHashKey();

    }

    // 카카오 API 사용시 키해시를 구해야하는데. 자바 코드로 구하는 방법이다. 필요시 호출해서 써라.
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("해쉬키입니다", "해쉬키는"+Base64.encodeToString(md.digest(), Base64.DEFAULT)+"입니다");
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "예외가 발생했어요 " + signature, e);
            }
        }
    }


}


