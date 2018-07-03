package com.example.jeon.helper.chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.github.nkzawa.emitter.Emitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-30.
 */

public class chattingList_Framgment extends android.support.v4.app.Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    ArrayList<chattingList_Content> chattingRoomData = new ArrayList<>();
    chattingList_Content chattingRoomContent;
    String [] filterA;
    String [] filterB;
    String [] filterPeople;

    String loginUserId;
    String loginUserNick;

    View view;
    RecyclerView rv;
    Context context;

    chattingList_Adapter fra;


    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    public static chattingList_Framgment newInstance(int columnCount) {
        chattingList_Framgment fragment = new chattingList_Framgment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserData();



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        try{
//            GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
//            gg.getSocket().on("newMessageAlram",newMessageAlream);
//        }catch (Exception e){
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chatting_chatting_room_list, container, false);
        rv = (RecyclerView) view.findViewById(R.id.chattingRoomRecyclerView);
        context = view.getContext();


        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);


        return view;
    }

    // 로그인 유저 정보 가지고 오기
    public void setUserData() {
        loginUserId = ((chattingMainActivity) getActivity()).loginUserid;
        loginUserNick = ((chattingMainActivity) getActivity()).loginUserNick;
        getLoginUserChattingRoomList();
    }

    // ( 서버 연결 )로그인 유저가 들어가있는 채팅방 정보가지고 오기
    public void getLoginUserChattingRoomList(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(getActivity());

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
                    URL url = new URL(ipad+"/getLoginUserChattingRoomList.php");
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
                    buffer.append("loginUserId").append("=").append(loginUserId);                 // php 변수에 값 대입

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

                Log.d("채팅 리스트 프래그먼트 결과",s);

                if ( s.equals("없음")){
                    rv.setVisibility(View.GONE);
                    TextView noChattingRoom = (TextView)view.findViewById(R.id.noChattingRoom);
                    noChattingRoom.setVisibility(View.VISIBLE);

                }else{
                    divideData(s);
                }

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }

    public void divideData(String data){
        // 끝 구분자 # , 중간구분자 @  사람 구분자 !
        filterA = data.split("#");
        for ( int i = 0 ; i < filterA.length ; i++){
            filterB = filterA[i].split("@");
            filterPeople =  filterB[3].split("!");
            chattingRoomContent = new chattingList_Content(Integer.parseInt(filterB[0]),filterB[1],filterB[2],filterPeople,Integer.parseInt(filterB[4]),Integer.parseInt(filterB[5]));
            Log.d("사람 수는 ",""+filterPeople.length);
            Log.d("새 메세지 여부  ",""+filterB[5]);
            chattingRoomData.add(chattingRoomContent);
        }

        GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
        // 어댑터 설정
        fra = new chattingList_Adapter(chattingRoomData, context, loginUserId,loginUserNick,gg.getSocket());
        rv.setAdapter(fra);
    }

}