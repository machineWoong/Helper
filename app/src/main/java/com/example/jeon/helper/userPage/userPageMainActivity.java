package com.example.jeon.helper.userPage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.jeon.helper.R;
import com.example.jeon.helper.helpList.getApplyuserData;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;
import com.example.jeon.helper.selectMenu;
import com.github.nkzawa.socketio.client.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class userPageMainActivity extends AppCompatActivity {

    String userId;  // 보여주기 대상이되는 아이디
    String loginUserId; // 보러 들어온 사람의 아이디.
    String loginUserNick;
    Boolean equalUser = false;

    // ip
    ip ip = new ip();
    String ipad = ip.getIp();

    // 유저 정보
    userPageUserData uData;
    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자

    // 댓글
    ArrayList<commentData> commentArr = new ArrayList<>();
    commentData cd;
    commentAdapter cA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // 접속한 아이디와, 볼 아이디 둘다가져옴
        getUserId();

        // 유저정보 가져오기
        getUserPageData();

        // 버튼이벤트
        buttonEvent();
    }

    // -------------------------------------데이터 세팅 -------------------------------------------
    //아이디 세팅
    public void getUserId(){
        // 대상
        userId = getIntent().getStringExtra("userId");
        // 접속한 사람
        loginUserId= getIntent().getStringExtra("loginUserId");

        // 동일인 여부 판별
        if ( userId.equals(loginUserId)){
            equalUser = true;
        }else {
            equalUser = false;
        }

        getLoginUserNick();

    }

    // 회원 정보 페이지 정보 가지고 오기
    public void getUserPageData(){
        class getApplyUserDataHttp extends AsyncTask<Void,Void,String> {

            String result;
            ProgressDialog dialog = new ProgressDialog(userPageMainActivity.this);
            String userId;

            public getApplyUserDataHttp(String userId){
                this.userId = userId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("회원정보를 불러오는 중");
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
                    URL url = new URL(ipad+"/userPageGetData.php");
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
                    buffer.append("id").append("=").append(userId);                 // php 변수에 값 대입

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
                    result = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d("헬프리스트 메인 ",""+result);

                return result;
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
                Log.d("유저 정보 페이지의 정보들 ",""+s);
                divideGetMyAskData(s);

            }
        }

        getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(userId);
        getDataAsk.execute();
    }

    // 회원 정보 쪼개기
    public void divideGetMyAskData(String result){
        firstFilter = result.split("!"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("%");

            // 로그인 모드, URL , 유저 닉네임 순서,
            uData = new userPageUserData(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                    seccondFilter[3],seccondFilter[4],seccondFilter[5],seccondFilter[6],seccondFilter[7],seccondFilter[8],seccondFilter[9],seccondFilter[10],seccondFilter[11]); // 객체 생성
        }

        setWidgets();
    }

    // 데이터 뿌려주기
    public void setWidgets(){

        // 프로필 이미지
        if ( uData.profilePath.contains("http://k.kakaocdn.net")){
            // 카카오톡 이미지 인경우
            ImageView userPageProfile = (ImageView)findViewById(R.id.userPageProfile);
            Glide.with(this).load(uData.profilePath).into(userPageProfile);
        }else{
            ImageView userPageProfile = (ImageView)findViewById(R.id.userPageProfile);
            final String a = ipad+"/"+uData.profilePath;
            Glide.with(this).load(a).into(userPageProfile);
        }

        // 아이디
        if( uData.loginMode.equals("1")){
            // 일반 로그인
            TextView userPageUserId = (TextView)findViewById(R.id.userPageUserId);
            userPageUserId.setText(uData.id);
        }else{
            // 카카오 로그인
            TextView userPageUserId = (TextView)findViewById(R.id.userPageUserId);
            userPageUserId.setText("카카오 로그인");
            userPageUserId.setTextSize(15);
        }


        // 닉네임
        TextView userPageNickName = (TextView)findViewById(R.id.userPageNickName);
        userPageNickName.setText(uData.nick);

        // 이메일  ( 나중에 친구만 보여줄 예정 )
        TextView userPageUserEmail = (TextView)findViewById(R.id.userPageUserEmail);
        userPageUserEmail.setText(uData.eMail);

        // 도움요청 횟수
        TextView userPageAskCount = (TextView)findViewById(R.id.userPageAskCount);
        userPageAskCount.setText(uData.askCount+"회");

        // 도움 지원 횟수
        TextView userPageGiveCount = (TextView)findViewById(R.id.userPageGiveCount);
        userPageGiveCount.setText(uData.giveCount+"회");

        // 패널티 횟수
        TextView userPagePenaltyCount = (TextView)findViewById(R.id.userPagePenaltyCount);
        userPagePenaltyCount.setText(uData.penalty+"회");

        // 평점
        TextView userPageUserGPA = (TextView)findViewById(R.id.userPageUserGPA);

        // 총점 / 평가자 수
        double result = Double.parseDouble(uData.userGPA)/(Integer.parseInt(uData.giveCount)+(Integer.parseInt(uData.askCount)));

        if ( uData.userGPA.equals("0")){
            userPageUserGPA.setText("0.0 점");
        }else{
            userPageUserGPA.setText(result+"점");
        }


        // 자기소개
        TextView userPageIntroduceEditT = (TextView)findViewById(R.id.userPageIntroduceEditT);
        if ( uData.introduce.equals("없음")){
            userPageIntroduceEditT.setText("자기 소개가 없습니다.");
        }else{
            userPageIntroduceEditT.setText(uData.introduce);
        }

        // 위치
        TextView uasePageLocation = (TextView)findViewById(R.id.uasePageLocation);
        uasePageLocation.setText(uData.location);

        // 성별
        if(uData.gender.equals("남자")){
            ImageView userPageGender = (ImageView)findViewById(R.id.userPageGender);
            Glide.with(this).load(R.drawable.man).into(userPageGender);
        }else  if(uData.gender.equals("여자")){
            ImageView userPageGender = (ImageView)findViewById(R.id.userPageGender);
            Glide.with(this).load(R.drawable.girl).into(userPageGender);
        }

        // 레이팅 바
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setRating((float) result);



        // 댓글 데이터 가지고오기
        getRepleData();


        // 댓글 더보기 ( 아이디를 넘겨준다 )  ( 댓글 답글 을 보여줄게 아니라 본인에게 달린 모든 댓글을 보여준다 )
        TextView userPageModeViewRiple = (TextView)findViewById(R.id.userPageModeViewRiple);
        userPageModeViewRiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoMoreList = new Intent(userPageMainActivity.this,myAllRecomment.class);
                gotoMoreList.putExtra("loginUserId",loginUserId);
                gotoMoreList.putExtra("targetId",userId);
                startActivity(gotoMoreList);
            }
        });

    }

    // 버튼 이벤트
    public void buttonEvent(){
        // 확인
        Button userPageOkBtn = (Button)findViewById(R.id.userPageOkBtn);
        // 수정
        Button userPageEditBtn = (Button)findViewById(R.id.userPageEditBtn);
        // 친구신청
        Button userPageFriendBtn = (Button)findViewById(R.id.userPageFriendBtn);

        if (equalUser == true){
            // 동일인  ( 확인 수정만 보여주기 )
            userPageOkBtn.setVisibility(View.VISIBLE);
            userPageEditBtn.setVisibility(View.VISIBLE);
            userPageFriendBtn.setVisibility(View.GONE);
        }else{
            // 방문 ( 확인 친구 신청만 보여주기 )
            userPageOkBtn.setVisibility(View.VISIBLE);
            userPageEditBtn.setVisibility(View.GONE);
            userPageFriendBtn.setVisibility(View.VISIBLE);
        }

        //확인버튼
        userPageOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 수정버튼
        userPageEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoEditMyPage =new Intent(userPageMainActivity.this,myPageEdit.class);
                gotoEditMyPage.putExtra("myData",uData);
                startActivityForResult(gotoEditMyPage,1111);
            }
        });

        // 친구신청
        userPageFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAskFriend();
            }
        });

    }

    //-----------------------------( 댓글 ) 리사이클 러뷰 -------------------------------------------

    // 댓글 받아오기
    public void getRepleData(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(userPageMainActivity.this);
            String userId;

            public getRepleDataHttp(String userId){
                this.userId = userId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("회원정보를 불러오는 중");
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
                    URL url = new URL(ipad+"/getCommentData.php");
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
                    buffer.append("id").append("=").append(userId);                 // php 변수에 값 대입

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
                    result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d("헬프리스트 메인 ",""+result);

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
                Log.d("댓글 정보들 ",""+s);
                if(s.equals("없음")){
                    // 댓글이 없는경우는 없다고 텍스트 표시
                    TextView userPageNoRipleText = (TextView)findViewById(R.id.userPageNoRipleText);
                    userPageNoRipleText.setVisibility(View.VISIBLE);
                }else{

                    divideGetCommentData(s);
                }


            }
        }

        getRepleDataHttp getData = new getRepleDataHttp(userId);
        getData.execute();
    }

    // 댓글 데이터 쪼개기
    public void divideGetCommentData(String result){
        firstFilter = null;
        seccondFilter = null;
        commentArr.clear();

        firstFilter = result.split("@"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("\\+");

            // 로그인 모드, URL , 유저 닉네임 순서,
            cd = new commentData(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                    seccondFilter[3],seccondFilter[4],seccondFilter[5],Integer.parseInt(seccondFilter[6]),seccondFilter[7]); // 객체 생성

            commentArr.add(cd);
        }


        setAdapter();

    }

    public void setAdapter(){
        RecyclerView view = (RecyclerView)findViewById(R.id.userPageRiple);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        cA = new commentAdapter(commentArr,this,loginUserId,userId);
        view.setAdapter(cA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1111){
            if(resultCode==RESULT_OK){
                //수정
                uData = null;
                getUserPageData();
            }else{
                Toast.makeText(this, "수정 취소", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 리스타트일때 갱신을 위해서 다시 데이터를 뿌려준다 .
        commentArr.clear();
        cd = null;
        getRepleData();
        setAdapter();
    }

    // ------------------------------------------친 구신청 ----------------------------------------
    //( 서버 연결 ) 친구신청
    public void setAskFriend(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(userPageMainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("요청중");
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
                    URL url = new URL(ipad+"/friend_ask_list.php");
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
                    buffer.append("loginUserId").append("=").append(loginUserId).append("&");
                    buffer.append("targetId").append("=").append(userId).append("&");
                    buffer.append("mode").append("=").append(0);// php 변수에 값 대입

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

                Log.d("유저페이지의  친구신청",s);

                if(s.equals("이미신청")){
                    Toast.makeText(userPageMainActivity.this, "이미 친구신청한 사용자 입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(userPageMainActivity.this, "친구신청을 하였습니다.", Toast.LENGTH_SHORT).show();



                    // node 여기에서 만들어 줘야겟네..

                    GlobalApplication gg =(GlobalApplication)getApplication();
                    Socket so = gg.getSocket();

                    String dataString = loginUserId+"@"+userId+"@"+loginUserNick+"@";

                    so.emit("requastFriend",dataString);



                }
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }

    // 유저의 닉네임 가져오기
    public void getLoginUserNick(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(userPageMainActivity.this);

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
                    URL url = new URL(ipad+"/getLoginUserNick.php");
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
                    buffer.append("loginUserId").append("=").append(loginUserId);
                    // php 변수에 값 대입

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

                loginUserId = s;
                Log.d("유저의 닉네임",""+s);
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }
}
