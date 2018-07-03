package com.example.jeon.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.askHelp.askHalpMainActivity;
import com.example.jeon.helper.cash.cash_main;
import com.example.jeon.helper.chatting.chattingMainActivity;
import com.example.jeon.helper.giveHelp.giveHelpMainActivity;
import com.example.jeon.helper.helpList.helpListMain;
import com.example.jeon.helper.exchange.exchangeMainActivity;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.noti_table.noti_Content;
import com.example.jeon.helper.noti_table.noti_List_Main;
import com.example.jeon.helper.setting.settingMainActivity;
import com.example.jeon.helper.surrounding_Map.surround_loaction_data;
import com.example.jeon.helper.userPage.userPageMainActivity;
import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HelperMain extends Activity {

    public static Activity activity;

    String loginUserId; // 유저 아이디
    String loginUserNickName; // 유저 닉네임
    String gender;


    String loginMode; // 1 일반계정 로그인 2 카카오 계정 로그인
    String userCash; // 유저 보유 캐쉬

    String checkResult;
    String dataResult;


    ArrayList<bestHelper> bestHelpersArrary = new ArrayList<>(); // 베스트 헬퍼들
    bestHelper bH; //사용자의 이미지 경로와, 닉네임을 저장한객체 ( bestHelper 에 사용하기 위함 )
    int BestHelpersArraryCount; // 어레이 리스트의 총 크길

    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자
    String getNickNameUrl; // PHP 에서 가지고온 닉네임과 URL이 연결된 문장

    int defaultImage = R.drawable.kakao_default_profile_image; // 이미지가 없는 경우에 디폴트 이미지.

    ip ip = new ip();
    String ipad = ip.getIp();

    // --------------------------------------소켓 통신 관련 ----------------------------------------
    Handler msgHandler;
    Integer getMessageAlram= 2222;

    // --------------------------------------공지사항 관련 -----------------------------------------
    noti_Content nC;
    ArrayList<noti_Content> ncArray = new ArrayList<>();
    main_noti_Adapter ncAdapter;

    String [] filter;
    String [] filter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시계창 없애기
        setContentView(R.layout.activity_helper_main);

        activity =  HelperMain.this;


        // 상단 메뉴
        TextView mainLoginUserNickName = (TextView)findViewById(R.id.mainLoginUserNickName); //닉네임
        TextView mainUserGetCash = (TextView) findViewById(R.id.mainUserGetCash); //사용자 보유 머니
        ImageView userPage = (ImageView) findViewById(R.id.userPage); //개인정보 관리 페이지
        ImageView settingPage = (ImageView)findViewById(R.id.settingPage); //세팅 페이지


        // 버튼 메뉴
        ImageView gotoGiveHelp = (ImageView) findViewById(R.id.gotoGiveHelp);
        ImageView gotoAskHelp = (ImageView) findViewById(R.id.gotoAskHelp);
        ImageView gotoHelpList = (ImageView) findViewById(R.id.gotoHelpList);
        ImageView gotoOnGoingHelp = (ImageView) findViewById(R.id.gotoOnGoingHelp);
        ImageView gotochatting = (ImageView) findViewById(R.id.gotochatting);



        //----------------------------------------------상당메뉴이동 ----------------------------------------------
        // 유저페이지로 이동
        userPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HelperMain.this,userPageMainActivity.class);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("userId",loginUserId);
                startActivity(intent);
            }
        });

        // 환경설정 페이지로 이동
        settingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HelperMain.this,settingMainActivity.class);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginMode",loginMode);
                startActivity(intent);
            }
        });

        // ----------------------------------------------버튼메뉴이동 ----------------------------------------------

        // 도움주기 페이지로 이동
        gotoGiveHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperMain.this,giveHelpMainActivity.class);
                intent.putExtra("loginMode",loginMode);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginUserNickName",loginUserNickName);
                intent.putExtra("gender",gender);
                startActivity(intent);
            }
        });

        //도움요청 페이지로 이동
        gotoAskHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperMain.this,askHalpMainActivity.class);
                intent.putExtra("loginMode",loginMode);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginUserNickName",loginUserNickName);
                startActivity(intent);
            }
        });

        //도움목록 페이지로 이동
        gotoHelpList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperMain.this,helpListMain.class);
               // intent.putExtra("loginMode",loginMode);
                intent.putExtra("loginUserId",loginUserId);
               // intent.putExtra("loginUserNickName",loginUserNickName);
                startActivity(intent);
            }
        });

        //수행중인 도움 페이지 이동
        gotoOnGoingHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperMain.this,exchangeMainActivity.class);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginUserNickName",loginUserNickName);
                intent.putExtra("userCash",userCash);
                startActivity(intent);
            }
        });

        // 채팅 페이지로 이동
        gotochatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView getMessageAlarm = (ImageView)findViewById(R.id.getMessageAlarm);
                getMessageAlarm.setVisibility(View.GONE);

                Intent intent = new Intent(HelperMain.this,chattingMainActivity.class);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginUserNickName",loginUserNickName);
                startActivity(intent);
            }
        });

        // 캐쉬 사용 목록으로이동
        mainUserGetCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperMain.this,cash_main.class);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginUserNick",loginUserNickName);
                intent.putExtra("haveMoney",userCash);
                startActivity(intent);
            }
        });


        // 공지사항 더보기
        TextView moreNoti = (TextView)findViewById(R.id.moreNoti);
        moreNoti.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperMain.this,noti_List_Main.class);
                intent.putExtra("loginUserId",loginUserId);
                intent.putExtra("loginUserNick",loginUserNickName);
                startActivity(intent);
            }

        });


        // 주변정보 보기
        ImageView surround_Btn = (ImageView)findViewById(R.id.surround_Btn);
        surround_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSurround_Btn = new Intent(HelperMain.this, surround_loaction_data.class);
                startActivity(gotoSurround_Btn);
            }
        });




        // 일반로그인시와 카카오톡으로 로그인시 구분하여 데이터 처리 필요
        // 로그인 정보가져오기  1이면 일반 로그인 2는 카카오 로그인
        getLoginMode();

        // 이달의 우수도우미를 가지고 오기 위함.
        try{
            getBestHelper();
        }catch (Exception e){
            Toast.makeText(HelperMain.this, "서버와의 연결이 불안정 합니다.", Toast.LENGTH_SHORT).show();
        }


        // 공지사항 가지고 오기
        try{
            getNotiData();
        }catch (Exception e){
            Toast.makeText(HelperMain.this, "서버와의 연결이 불안정 합니다.", Toast.LENGTH_SHORT).show();
        }


       // Log.d("토큰 확인 메인 ",""+ FirebaseInstanceId.getInstance().getToken());


    }

    //---------------------------------- 계정 체크 ( 카카오인경우 추가 데이터를 받는다 )---------------
    // 카카오 계정 추가 정보 유무 확인
    public void getKaKaoDataCheck(){
        class getNickNameToHttp extends AsyncTask<Void,Void,String> {

            String id;
            public getNickNameToHttp (String id){
                this.id = id;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/checkNsetKakaoData.php");
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
                    buffer.append("id").append("=").append(id);               // php 변수에 값 대입


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
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
                    checkResult = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("77777777777","   "+checkResult);
                return checkResult;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try{
                    if ( result.equals("0")){
                        // 입력된 데이타가 없는 경우에는 이동
                        Intent setKakaoUserData = new Intent(HelperMain.this,getKakaoUserDataSet.class);
                        setKakaoUserData.putExtra("id",loginUserId);
                        startActivityForResult(setKakaoUserData,9999);
                    }else{
                        Log.d("결과값 : ",""+result);
                    }
                }catch (Exception e){
                    Toast.makeText(HelperMain.this, "서버와의 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

            }

        }
        getNickNameToHttp getNickName = new getNickNameToHttp(loginUserId);
        getNickName.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9999){
            if( resultCode == RESULT_OK){

            }else{
                finish();
            }
        }
    }

    // -----------------------------------------로그인 관련 -------------------------------------------
    // ( 인텐트 : 로그인 모드 ) 1을 가지고 오면 일반로그인이고  2를 가지고 오면, 카카오 로그인이다.
    public void getLoginMode(){

        Intent getMode = getIntent();
        loginMode =getMode.getExtras().getString("loginMode");
        if ( loginMode.equals("1")){
            // 일반 로그인 인경우
            loginUserId = getMode.getExtras().getString("loginId");
            setLoginNickName(loginUserId);
            getUserAccount(loginUserId);
            makeShardUserData();
        }
        else{
            loginUserId = getMode.getExtras().getString("loginId");
            loginUserNickName = getMode.getExtras().getString("loginNick");

            // 닉네임 설정
            TextView mainLoginUserNickName = (TextView)findViewById(R.id.mainLoginUserNickName);
            mainLoginUserNickName.setText(loginUserNickName);

            // 카카오 계정 정보 추가 여부 조사
            getKaKaoDataCheck();
            getUserAccount(loginUserId);

            makeShardUserData();

        }

        // 서비스 시작
       startServ();
    }

    //(서버 연결 : 로그인 닉네임 찾아오기 ) (텍스트뷰에 적용 : onPostExecute  ) 로그인한 아이디를 가지고 닉네임을 찾아서 가져오기 DB
    public void setLoginNickName(String userId){

        class getNickNameToHttp extends AsyncTask<Void,Void,String> {

            String id;


            public getNickNameToHttp (String id){
                this.id = id;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------


                    URL url = new URL(ipad+"/findLoginNickName.php");
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
                    buffer.append("loginMode").append("=").append(loginMode);


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
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
                    dataResult = builder.toString();                       // 전송결과를 전역 변수에 저장
                    // Log.d("bbbbbbbbbbbbbbbb", "aaaaaaaaaaaa/////" + loginUserNickName);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return dataResult;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                String []a = result.split("!");
                loginUserNickName = a[0];
                gender = a[1];
                TextView mainLoginUserNickName = (TextView)findViewById(R.id.mainLoginUserNickName);
                mainLoginUserNickName.setText(loginUserNickName);


            }

        }

        if (loginUserId.equals("가져오기 실패") ){

        }else{
            getNickNameToHttp getNickName = new getNickNameToHttp(userId);
            getNickName.execute();
        }


    }

    //( 서버 연결 : 사용자의 아이디를 가지고 현재 잔액을 가지고 온다 )
    public void getUserAccount(String userId){
        class getNickNameToHttp extends AsyncTask<Void,Void,String> {

            String id;
            public getNickNameToHttp (String id){
                this.id = id;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/userAccount.php");
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
                    buffer.append("loginMode").append("=").append(loginMode).append("&");
                    buffer.append("accountMode").append("=").append("1");

                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
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
                    userCash = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return userCash;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                userCash = result;

                TextView mainUserGetCash = (TextView) findViewById(R.id.mainUserGetCash); //사용자 보유 머니
                DecimalFormat dc = new DecimalFormat("###,###,###,###");
                String changeFormatuserCash = dc.format(Double.parseDouble(result)).toString();
                mainUserGetCash.setText(changeFormatuserCash+ "원");
            }

        }
        getNickNameToHttp getNickName = new getNickNameToHttp(userId);
        getNickName.execute();
    }

    // ------------------------------------ 공지 사항 관련 ---------------------------------------

    public void getNotiData(){
        class getNotiDataHttp extends AsyncTask<Void, Void, String> {
            String result;
            ProgressDialog dialog = new ProgressDialog(HelperMain.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 불러오는 중");
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
                    URL url = new URL(ipad + "/getNoti.php");
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
                    result = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                }

                Log.d("공지사항 정보들", "" + s);
                if (s.equals("없음")) {
                } else {
                    notiDataDivide(s);
                }
            }
        }

        getNotiDataHttp getData = new getNotiDataHttp();
        getData.execute();
    }

    public void notiDataDivide(String datas){
        filter = datas.split("###");
        for (int i = 0; i < filter.length; i++) {
            filter1 = filter[i].split("@@@");
            nC = new noti_Content(filter1[0], filter1[1], filter1[2], filter1[3], filter1[4], filter1[5], filter1[6]);
            ncArray.add(nC);
        }
        setNotiRecyclerView();

    }

    public void setNotiRecyclerView(){
        // 리사이클러 뷰
        RecyclerView view = (RecyclerView)findViewById(R.id.mainNoticeRecyclerView);


        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        //Log.d("메인 엑티비티 어레이 리스트 갯수 ",":///// :"+BestHelpersArraryCount);


        // 어댑터를 연결 시켜주는 부분
        ncAdapter = new main_noti_Adapter(this,ncArray,loginUserId,loginUserNickName);
        view.setAdapter(ncAdapter);
    }

    // -------------------------------------우수 회원 ---------------------------------------------
    public void getBestHelper(){
        class getNickNameToHttp extends AsyncTask<Void,Void,String> {

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/bestHelper.php");
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


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
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
                    getNickNameUrl = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return getNickNameUrl;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                // 결과로 구분자를 가진 상위 10 명의 데이터를 가지고 온다
                // 구분자로 쪼개서 배열에 넣으려고 한다.

                //여기서 배열로 쪼개서 넣는다 .

                Log.d("메인 유저 정보",""+result);
                divideStringForBestHelper(result);

                // 리사이클러뷰 호출
                setRecyclerView();
            }

        }
        getNickNameToHttp getNickName = new getNickNameToHttp();
        getNickName.execute();



    }
    // ( 리사이클러 뷰 ) getBestHelper 안에서 호출되면서 리사이클러뷰세팅
    public void setRecyclerView(){
        // 리사이클러 뷰
        RecyclerView view = (RecyclerView)findViewById(R.id.mainBestHelperRecyclerView);

        //그리드뷰로 만들것으로 정하는 부분 ( spanCount는 5개마다 개행 하겟다라는 뜻 )
        // RecyclerView.LayoutManager lm = new GridLayoutManager(this,5);
        // 그리드뷰로 셋팅
        // view.setLayoutManager(lm);


        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view.setLayoutManager(layoutManager);

        //Log.d("메인 엑티비티 어레이 리스트 갯수 ",":///// :"+BestHelpersArraryCount);


        // 어댑터를 연결 시켜주는 부분
        main_BestHelper_RecyclerViewAdater mBRVA = new main_BestHelper_RecyclerViewAdater(bestHelpersArrary,BestHelpersArraryCount,this,loginUserId);
        view.setAdapter(mBRVA);
    }
    // ( 배열로 쪼개기 ) BestHelper : 사용자의 닉네임과 프로필 사진 경로를  얻기위해 배열에서 쪼개기 .
    public void divideStringForBestHelper(String getNickNameUrl){

        firstFilter = getNickNameUrl.split("!"); // 유저별로 나누기 위함

//        Log.d("첫번째 문자 split","몇개 인가 ?????"+firstFilter.length);
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("%");

            if(seccondFilter[0] == "1"){  // 이미지가 없다면 디폴트 이미지로설정 해줌
                seccondFilter[0] = String.valueOf(defaultImage);
            }
            // 로그인 모드, URL , 유저 닉네임 순서
            bH = new bestHelper(seccondFilter[0],seccondFilter[1],seccondFilter[2],seccondFilter[3]); // 객체 생성
            bestHelpersArrary.add(bH); // 어레이 리스트에 객체 추가.
        }

        BestHelpersArraryCount = bestHelpersArrary.size();

//        Log.d("어레이 리스트 총 길이 ","길이  - "+BestHelpersArraryCount);

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getUserAccount(loginUserId);
        bestHelpersArrary.clear();
        getBestHelper();

        ncArray.clear();
        getNotiData();



    }
    // ---------------------------------------- ( 서비스 )소켓 연결 -------------------------------------------

    //  서비스 연결 및 ,   채팅 알림.
    public void startServ(){
        // 서비스 시작
        Intent intent = new Intent(HelperMain.this,socketService.class);
        intent.putExtra("loginUserId",loginUserId);
        startService(intent);

        try{
            GlobalApplication gg = (GlobalApplication)getApplication();
            gg.getSocket().on("messageAlram",getMessageAlream);
        }catch (Exception e){

        }

        handl(); // 메세지 수신시 알림할 핸들러 .
    }

    // 메세지 수신
    private Emitter.Listener getMessageAlream = new Emitter.Listener() { // 넌 쓰레드구나... ㅅㅂ놈아
        @Override
        public void call(Object... args) {
            //방번호를 받아온다 .
            String a = args[0].toString();
            gotoHandler(a);
        }
    };

    // 메세지 수신 알람 핸들러 처리
    public void gotoHandler(String a){
        Log.d("메세지가 왔슴니다",a+" 번 방입니다. ");

            Message hdmg = msgHandler.obtainMessage();
            // 핸들러에게 전달할 메세지의 식별자
            hdmg.what = getMessageAlram;
            // 메세지의 본문
            hdmg.obj = a;
            // 핸들러에게 메세지 전달 ( 화면 처리 )
            msgHandler.sendMessage(hdmg);

    }

    // 메세지 추가 처리 하는 핸들러
    public void handl(){
        // 서버로부터 수신한 메세지를 처리하는 곳
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == getMessageAlram) {
                    ImageView getMessageAlarm = (ImageView)findViewById(R.id.getMessageAlarm);
                    getMessageAlarm.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        // 서버단에서 배열을 삭제하기 위함.
        GlobalApplication gg = (GlobalApplication)getApplication();
        gg.getSocket().emit("logOut", loginUserId);


        // 서비스 종료
        Intent intent = new Intent(HelperMain.this,socketService.class);
        stopService(intent);
    }

    // 로그인시  쉐어드 프리퍼 런스를 만들어 저장하는데  여기에는 아이디와 닉네임을 담는다.
    public void makeShardUserData(){
        SharedPreferences loginData = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = loginData.edit();
        editor.putString("loginUserId", loginUserId);
        editor.putString("loginUserNick", loginUserNickName);
        editor.commit();
    }


    // 종료시 다이얼로그
    public void endDialog(){
        AlertDialog.Builder cameraSelect = new AlertDialog.Builder(HelperMain.this);
        cameraSelect.setTitle("알림");
        cameraSelect.setMessage("어플리케이션을 종료하시겠습니까?");

        cameraSelect.setPositiveButton("종료",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        cameraSelect.setNegativeButton("취소",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        cameraSelect.show();   // 실행

    }

    @Override
    public void onBackPressed() {
        endDialog();
    }
}

