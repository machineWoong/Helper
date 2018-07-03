package com.example.jeon.helper.chatting;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chattingRoom extends AppCompatActivity {


    // 방번호 로그인 유저 아이디 닉네임
    int roomNo;
    String loginUserId;
    String loginUserNick;


    // 메세지 전송시 날짜
     SimpleDateFormat mFormat = new SimpleDateFormat("yy년! M월 d일!aa hh:mm!");


    // 리사이클러 뷰 관련
    RecyclerView rv;

    // 메세지 리스트
    ArrayList<chattingMessageContent> messageData = new ArrayList<>();

    // 방정보
    chattingRoomContent chattingData;

    // 메세지 리스트를 뿌려줄 어댑터
    chattingRoomAdapter chattingRoomAdapter;

    // 메세지 컨텐트
    chattingMessageContent messageContent;

    String[] fillter;
    String targetId;
    String targetNickName;
    String targetProfile;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();
    int port = ip.getPort();

    //--------------------------------------  소켓 통신 관련 ---------------------------------------

    Handler msgHandler;
    Context context;

    String[] msgFilter;
    String[] roomMsgFilter;

    Socket socket;
    String   nodeIp = ip.getNodeIp();

    //----------------------------------------------메세지 디비 관련 ------------------------------

    String[] mFilter;
    String[] mFilter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_room);

        //방정보 가지고 오기
        context = this;
        getRoomData();

        // 핸들러
        handl();


        try {
            // 소켓 생성 및 노드 js 서버와 연결.
            GlobalApplication gg = (GlobalApplication)getApplication();
            gg.getSocket().on("receiveMsg",onConnect);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    // 채팅방 정보 가지고 오기
    public void getRoomData() {
        roomNo = getIntent().getIntExtra("chattingRoomNo", 0);
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");


        Log.d("채팅방에서 닉네임",""+loginUserNick);

        setShardEnterRoom();

        //  채팅 글 정보 가지고 오기
        getRoomDataTohttp();
        getMessageLog();
    }
    // ( 서버에서 방정보 가지고 오기 )
    public void getRoomDataTohttp() {
        class getRoomDataHTTP extends AsyncTask<Void, Void, String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(chattingRoom.this);

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
                    URL url = new URL(ipad + "/getChattingRoomData.php");
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
                    buffer.append("loginUserId").append("=").append(loginUserId).append("&");                 // php 변수에 값 대입
                    buffer.append("roomNo").append("=").append(roomNo);

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
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                }

                Log.d("채팅방 안에서 데이터 결과", s);
                setWebget(s);


            }
        }
        getRoomDataHTTP getData = new getRoomDataHTTP();
        getData.execute();
    }
    // 위젯 세팅
    public void setWebget(String a) {
        fillter = a.split("@");

        targetId = fillter[0];
        targetNickName = fillter[1];
        targetProfile = fillter[2];

        chattingData = new chattingRoomContent(loginUserId, loginUserNick, targetId, targetNickName, targetProfile);

        ImageView enterRoomImage = (ImageView) findViewById(R.id.enterRoomImage);
        TextView enterRoomTargetName = (TextView) findViewById(R.id.enterRoomTargetName);

        if (targetProfile.equals("이미지 없음")) {
            Glide.with(this).load(R.drawable.kakao_default_profile_image).into(enterRoomImage);
        } else {
            if (targetProfile.contains("http://k.kakaocdn.net")) {
                Glide.with(this).load(targetProfile).into(enterRoomImage);
            } else {
                Glide.with(this).load(ipad + "/" + targetProfile).into(enterRoomImage);
            }
        }

        enterRoomTargetName.setText(targetNickName);

        setAdapter1();
    }
    // 리사이클러뷰 어댑터 연결
    public void setAdapter1() {
        rv = (RecyclerView) findViewById(R.id.enterRoomChattingList);

        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        // 어댑터를 연결 시켜주는 부분  ( 배열 + 갯수 )
        chattingRoomAdapter = new chattingRoomAdapter(chattingData, this, messageData);
        rv.setAdapter(chattingRoomAdapter);

        rv.scrollToPosition(messageData.size() - 1);

        setSendBtn();
    }
    // 채팅 발신 이벤트
    public void setSendBtn() {
        ImageView enterRoomChattingSend = (ImageView) findViewById(R.id.enterRoomChattingSend);

        enterRoomChattingSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText enterRoomChattingEditText = (EditText) findViewById(R.id.enterRoomChattingEditText);
                String message = enterRoomChattingEditText.getText().toString();

                if (message == null || TextUtils.isEmpty(message) || message.equals("")) {
                    Toast.makeText(chattingRoom.this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();

                } else {
                    int mode = 2;
                    String senderId = loginUserId;
                    String senderNick = loginUserNick;

                    // 현재 시간 받아오기
                    long mNow;
                    Date mDate;
                    mNow = System.currentTimeMillis();
                    mDate = new Date(mNow);


                    // 시간 원본 전달 .
                    //String time = mDate.toString();

                     String time = mFormat.format(mDate);

                     Log.d("날짜 포멧 ",""+time);

                    messageContent = null;
                    messageContent = new chattingMessageContent(mode, senderId, senderNick, message, time);

                    messageData.add(messageContent);
                    chattingRoomAdapter.notifyDataSetChanged();

                    Log.d("말한사람",""+senderNick);

                    String dataString = roomNo+"@"+senderId+"@"+targetId+"@"+message+"@"+time+"@"+senderNick+"@";

                    //node 로 알림메세지를 위해 데이터 전송.
                    GlobalApplication gg = (GlobalApplication)getApplication();
                    try {
                        gg.getSocket().emit("chattingMsg",dataString);
                    }catch (Exception e){
                        try {
                            socket = IO.socket(nodeIp);
                            socket.connect();

                            //소켓 연결
                            GlobalApplication gg2 = (GlobalApplication)getApplication();
                            gg2.setSocket(socket);
                            gg2.getSocket().emit("chattingMsg",dataString);
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }

                    }


                  //  gg.getSocket().emit("chattingMsg",dataString);


                    // 에디트 텍스트 비워주기
                    enterRoomChattingEditText.setText(null);

                    rv.scrollToPosition(messageData.size() - 1);
                    // 키보드 내려주기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(enterRoomChattingEditText.getWindowToken(), 0);

                    Toast.makeText(chattingRoom.this, "전송", Toast.LENGTH_SHORT).show();

                    saveMessageLog();
                }
            }
        });
    }

    // ---------------------------------채팅방을 떠날시.---대화 내용 DB에  저장 ----------------------------------
    public void saveMessageLog() {

        String dataString = "";
        int roomno = roomNo;

        if (messageData.size() != 0 || messageData != null) {
            for (int i = 0; i < messageData.size(); i++) {
                dataString = dataString + messageData.get(i).mode + "##" + messageData.get(i).senderId + "##" + messageData.get(i).message + "##" + messageData.get(i).time + "@@@";
            }
        }

        Log.d("채팅방 데이터 직렬화 한 결과", "" + dataString);

        MessageLogDbHttp(1, dataString);

    }
    // 기존 메세지 가지고 오기
    public void getMessageLog() {
        MessageLogDbHttp(2, "getgetget");
    }
    // 데이터 쪼개기
    public void divideMessageData(String a) {

        Log.d("어떻게 들어오니 ", "" + a);

        int m;
        mFilter = a.split("@@@");
        for (int i = 0; i < mFilter.length; i++) {
            mFilter2 = mFilter[i].split("##");

            if (mFilter2[1].equals(loginUserId)) {
                m = 2; // 내가 보낸거
            } else {
                m = 1; // 내가 받은거
            }
            messageContent = new chattingMessageContent(m, mFilter2[1], targetNickName, mFilter2[2], mFilter2[3]);
            messageData.add(messageContent);
        }

        // 메세지의 끝으로 이동.
        rv.scrollToPosition(messageData.size() - 1);


    }
    // 서버에 전송
    public void MessageLogDbHttp(int mode, String dataString) {

        class getDataHttp extends AsyncTask<Void, Void, String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(chattingRoom.this);

            int mode;
            int roomNo2;
            String dataString;

            public getDataHttp(int mode, int roomNo2, String dataString) {
                this.mode = mode;
                this.roomNo2 = roomNo2;
                this.dataString = dataString;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("로드중");
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
                    URL url = new URL(ipad + "/chattingLogSave.php");
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
                    buffer.append("mode").append("=").append(mode).append("&");                 // php 변수에 값 대입
                    buffer.append("roomNo").append("=").append(roomNo2).append("&");
                    if ( mode == 1){
                        buffer.append("sendId").append("=").append(loginUserId).append("&");
                        buffer.append("dataString").append("=").append(dataString).append("&");
                        buffer.append("messageCount").append("=").append(messageData.size());
                    }else{
                        buffer.append("sendId").append("=").append("no").append("&");
                        buffer.append("dataString").append("=").append(dataString).append("&");
                        buffer.append("messageCount").append("=").append(0);
                    }



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
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                }


                if (s.equals("없음") || s.equals("저장완료")) {
                    Log.d("저장 했냐??", "" + s);

                } else {
                    divideMessageData(s);
                }

            }
        }

        getDataHttp getData = new getDataHttp(mode, roomNo, dataString);
        getData.execute();

    }

    // ------------------------------------------ 서 버  연 결  소 캣 채 팅 ------------------------
    // 메세지 수신
    private Emitter.Listener onConnect = new Emitter.Listener() { // 넌 쓰레드구나... ㅅㅂ놈아
        @Override
        public void call(Object... args) {
            String a = args[0].toString();
            gotoHandler(a);
        }
    };


    // 메세지 추가 처리 하는 핸들러
    @SuppressLint("HandlerLeak")
    public void handl(){

        // 서버로부터 수신한 메세지를 처리하는 곳  ( AsyncTesk를  써도됨 )
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1111) {
                    // 메세지가 왔다면.
                    Log.d("받은 메세지 ", msg.obj.toString());
                    msgFilter = msg.obj.toString().split("@"); // 방번호, 보낸사람 받는 사람 내용 시간
                    // 수신 1
                    // messageContent = new chattingMessageContent(1, msgFilter[0], targetNickName, msgFilter[1], msgFilter[2]);

                    // 수신 2     ( 모드  1 수신 , 아이디 , 닉네임, 내용 , 시간 )
                    messageContent = new chattingMessageContent(1, msgFilter[1], targetNickName, msgFilter[3], msgFilter[4]);
                    messageData.add(messageContent);

                    // *******   전체를 노티할 것이 아니라 일부분만 가지고 노티를 하자
                   // chattingRoomAdapter.notifyDataSetChanged();
                    chattingRoomAdapter.notifyItemRangeChanged (messageData.size()-4,messageData.size()-1);
                    rv.scrollToPosition(messageData.size() - 1);
                }
            }
        };
    }
    // 메세지 수신 핸들러 처리
    public void gotoHandler(String a){
        Log.d("메세지가 왔슴니다",a);
        roomMsgFilter = null;
        roomMsgFilter = a.split("@");
        if ( roomMsgFilter[0].equals(String.valueOf(roomNo))){
            Message hdmg = msgHandler.obtainMessage();
            // 핸들러에게 전달할 메세지의 식별자
            hdmg.what = 1111;
            // 메세지의 본문
            hdmg.obj = a;
            // 핸들러에게 메세지 전달 ( 화면 처리 )
            msgHandler.sendMessage(hdmg);
        }else{
            Log.d("다른방 메세지","다른방 메세지");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveMessageLog();
        removeShardExitRoom();

        GlobalApplication gg = (GlobalApplication)getApplication();
        gg.getSocket().off("receiveMsg",onConnect);
        finish();
    }


    // 메세지  FCM 을 위한 보고 있는 방 쉐어드  지정
    // 방에서 나갈때는 쉐어드 값 비워주기 .
    public void setShardEnterRoom(){
        SharedPreferences enteredRoom = getSharedPreferences("enteredRoom", MODE_PRIVATE);
        SharedPreferences.Editor editor = enteredRoom.edit();
        editor.putInt("roomNo", roomNo);
        editor.commit();
    }
    public void removeShardExitRoom(){
        SharedPreferences enteredRoom = getSharedPreferences("enteredRoom", MODE_PRIVATE);
        SharedPreferences.Editor editor = enteredRoom.edit();
        editor.remove("roomNo");
        editor.commit();
    }
}
