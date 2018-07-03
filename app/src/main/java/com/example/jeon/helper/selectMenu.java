package com.example.jeon.helper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.chatting.chattingRoom;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.userPage.userPageMainActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class selectMenu extends AppCompatActivity {

    String loginUserId;
    String targetId;

    String loginUserNick;


    ip ip = new ip();
    String ipad = ip.getIp();
    String ipad2 = ip.getIp2();


    String []filter;
    String gender;
    String profile;
    String nick;

    String []filter2;


    com.github.nkzawa.socketio.client.Socket so;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_select_menu);

        // 취소버튼
        ImageView select_page_cencel = (ImageView)findViewById(R.id.select_page_cencel);
        select_page_cencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 정보보기
        Button select_page_showData = (Button)findViewById(R.id.select_page_showData);
        select_page_showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUserPage();
            }
        });

        // 1:1 채팅
        Button select_page_chatting = (Button)findViewById(R.id.select_page_chatting);
        select_page_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gotoChatting();
                searchChattingRoom();
            }
        });

        // 친구 신청
        Button askFriend = (Button)findViewById(R.id.askFriend);
        askFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askFriend();
            }
        });


        // 데이터 가지고 오기
        getData();
    }

    // 데이터 가지고오기
    public void getData(){
        loginUserId = getIntent().getStringExtra("loginUserId").toString();
        targetId = getIntent().getStringExtra("targetId").toString();

        Button askFriend = (Button)findViewById(R.id.askFriend);
        if(loginUserId.equals(targetId)){
            askFriend.setVisibility(View.GONE);
        }else{
            askFriend.setVisibility(View.VISIBLE);
        }

        setDataHttp();
        getLoginUserNick();
    }

    // (서버 연결) 데이터 불러오기  ( 프로필이미지, 성별, 닉네임 )
    public void setDataHttp(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(selectMenu.this);

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
                    URL url = new URL(ipad+"/selectMenuUserData.php");
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
                    buffer.append("targetId").append("=").append(targetId);                 // php 변수에 값 대입

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
                Log.d("selectMenu 데이터",""+s);
                divideData(s);
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }

    // 데이터 쪼기개
    public void divideData(String data){

        filter = data.split("!");

        for ( int i = 0 ; i < filter.length ;i++){
            nick = filter[0];
            profile = filter[1];
            gender = filter[2];
        }

        setDataWedget();

    }

    //데이터 뿌려주기
    public void setDataWedget(){
        ImageView select_page_Profile = (ImageView)findViewById(R.id.select_page_Profile);
        ImageView select_page_gender = (ImageView)findViewById(R.id.select_page_gender);
        TextView select_page_Nick = (TextView)findViewById(R.id.select_page_Nick);

        //프로필
        if(profile.equals("이미지 없음")){

        }else{
            if(profile.contains("http://k.kakaocdn.net")){
                Glide.with(this).load(profile).into(select_page_Profile);
            }else{
                Glide.with(this).load(ipad+"/"+profile).into(select_page_Profile);
            }
        }


        // 성별
        if(gender.equals("남자")){
            Glide.with(this).load(R.drawable.man).into(select_page_gender);

        }else if(gender.equals("여자")) {
            Glide.with(this).load(R.drawable.girl).into(select_page_gender);
        }

        // 닉네임
        select_page_Nick.setText(nick);

    }

    // 정보보기 버튼 클릭 이벤트
    public void gotoUserPage(){

        Intent gotoShowUserData = new Intent(selectMenu.this,userPageMainActivity.class);
        gotoShowUserData.putExtra("userId",targetId);  // 타겟 아이디
        gotoShowUserData.putExtra("loginUserId",loginUserId);  // 로그인한 아이디.
        startActivity(gotoShowUserData);

    }

    // ( 다이얼 로그 )친구 신청 이벤트
    public void askFriend(){
        // loginUserId 와 targetId의 관계
        setAskFriend();
    }

    //( 서버 연결 ) 친구신청
    public void setAskFriend(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(selectMenu.this);

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
                    buffer.append("targetId").append("=").append(targetId).append("&");
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

                Log.d("selectMenu에 친구신청",s);

                if(s.equals("이미신청")){
                    Toast.makeText(selectMenu.this, "이미 친구신청한 사용자 입니다.", Toast.LENGTH_SHORT).show();
                }else if(s.equals("비밀")){
                    Toast.makeText(selectMenu.this, "친구로 등록을 하였습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(selectMenu.this, "친구신청을 하였습니다.", Toast.LENGTH_SHORT).show();

                    // node 여기에서 만들어 줘야겟네..
                    GlobalApplication gg =(GlobalApplication)getApplication();
                    so = gg.getSocket();
                    String dataString = loginUserId+"@"+targetId+"@"+loginUserNick+"@";
                    so.emit("requastFriend",dataString);
                }
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }

    // 1:1 채팅 하기 클릭 이벤트
    public void gotoChatting(String no){
        int roomNo = Integer.parseInt(no);
        Intent gotoChattingRoom = new Intent(this,chattingRoom.class);
        gotoChattingRoom.putExtra("chattingRoomNo",roomNo);
        gotoChattingRoom.putExtra("loginUserId",loginUserId);
        gotoChattingRoom.putExtra("loginUserNick",loginUserNick);
        startActivity(gotoChattingRoom);
    }

    // 1:1 채팅방 있는지 조사하기
    public void searchChattingRoom(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(selectMenu.this);

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
                    URL url = new URL(ipad+"/searchChattingRoom.php");
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
                    buffer.append("targetId").append("=").append(targetId);
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
                Log.d("select메뉴 에서 채팅",s);

                if ( s.equals("본인")){
                    Toast.makeText(selectMenu.this, "본인과는 채팅할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    filter2 = s.split("@");

                    if ( filter2[1].equals("1")){ // 이미 방이 있는 경우
                        gotoChatting(filter2[0]);
                    }else if (filter2[1].equals("2")) // 방이 없는 경우 ( 소캣을 서버에 만들어서 보낸다 )
                        gotoChatting(filter2[0]);
                      //  makeSocket(filter2[0]+"@"+loginUserId);
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
            ProgressDialog dialog = new ProgressDialog(selectMenu.this);

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
                loginUserNick = s;
                Log.d("유저의 닉네임",""+s);
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }

}
