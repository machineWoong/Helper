package com.example.jeon.helper.helpList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.askHelpAdapter;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class myAsk extends android.support.v4.app.Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // ip
    ip ip = new ip();
    String ipad = ip.getIp();

    // 로그인 유저 정보
    String userId;
    String loginUserNick;

    // 데이터
    getMyAskDataContent getAskDataContent;
    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자

    // 모든 데이터
    ArrayList<getMyAskDataContent> MyAskDataArray = new ArrayList<>();

    // 필터링 된데이터
    ArrayList<getMyAskDataContent> MyAskDataArrayFilter = new ArrayList<>();


    // 어댑터 세팅.
    myAskAdapter mAdapter;
    View view;
    RecyclerView rv;
    Context context;

    // 메인 어댑터 helpListViewPagerAdaper에서 프래그먼트를 생성하여 부를때 사용했다.
    public static myAsk newInstance(int columnCount) {
        myAsk fragment = new myAsk();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 로그인한 유저 아이디
        getUserId();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                 Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_ask_item_recyclerview, container, false);


        rv = (RecyclerView) view.findViewById(R.id.myAskLiskRecyclerView);
        context = view.getContext();


        rv.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);


        GlobalApplication gg = (GlobalApplication)getActivity().getApplication();

        // 이부분을 각자에 맞게 뿌려주면 될거같은데.
        mAdapter = new myAskAdapter(MyAskDataArray,context,userId,gg.getSocket(),loginUserNick);
        rv.setAdapter(mAdapter);

        //( 모집중 필터링 )
        TextView showMyAskGetApply =(TextView)view.findViewById(R.id.showMyAskGetApply);
        showMyAskGetApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAskDataArrayFilter.clear();
                // 값이 0 인것들

                for ( int i = 0 ; i < MyAskDataArray.size(); i++ ){
                    if( MyAskDataArray.get(i).onGoingState.equals("0")){
                        MyAskDataArrayFilter.add( MyAskDataArray.get(i));
                    }
                }

                try {
                    GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
                    mAdapter = new myAskAdapter(MyAskDataArrayFilter,context,userId,gg.getSocket(),loginUserNick);
                    rv.setAdapter(mAdapter);
                }catch (Exception e){

                }
            }
        });

        // (진행중 필터링)
        TextView showMyAskGetOnGoing =(TextView)view.findViewById(R.id.showMyAskGetOnGoing);
        showMyAskGetOnGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAskDataArrayFilter.clear();
                // 값이 1인것들
                for ( int i = 0 ; i < MyAskDataArray.size(); i++ ){
                    if( MyAskDataArray.get(i).onGoingState.equals("1")){
                        MyAskDataArrayFilter.add( MyAskDataArray.get(i));
                    }
                }
                Log.d("진행중 MyAskDater값",""+MyAskDataArrayFilter.size());
                try {

                    GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
                    mAdapter = new myAskAdapter(MyAskDataArrayFilter,context,userId,gg.getSocket(),loginUserNick);
                    rv.setAdapter(mAdapter);
                }catch (Exception e){

                }
            }
        });

        // (종료 마감 필터링)
        TextView showMyAskGetDeadLine =(TextView)view.findViewById(R.id.showMyAskGetDeadLine);
        showMyAskGetDeadLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAskDataArrayFilter.clear();
                // 값이 2인것들.
                for ( int i = 0 ; i < MyAskDataArray.size(); i++ ){
                    if( MyAskDataArray.get(i).onGoingState.equals("2")){
                        MyAskDataArrayFilter.add( MyAskDataArray.get(i));
                    }
                }
                try {
                    GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
                    mAdapter = new myAskAdapter(MyAskDataArrayFilter,context,userId,gg.getSocket(),loginUserNick);
                    rv.setAdapter(mAdapter);
                }catch (Exception e){

                }

            }
        });

        return view;
    }

    // 유저정보 및 데이터 세팅
    public void getUserId(){
        userId = ((helpListMain)getActivity()).getUserId();
        getDataAskHelpHttp();
    }

    // ( 서버연결, 도움 요청 정보 )
    public void getDataAskHelpHttp(){
        // 내가 작성한 글의 정보를 가지고 와야함.
        class getAskDataHttp extends AsyncTask<Void,Void,String>{

            String result;
            ProgressDialog dialog = new ProgressDialog(getActivity());
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 로드중");
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
                    URL url = new URL(ipad+"/getMyAskData.php");
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

                if ( s.equals("없음")){
                    mAdapter.notifyDataSetChanged();
                }else{
                    Log.d("프래그먼트 정보들 ",""+s);
                    // 가지고온 데이터들을 나눈다.
                    divideGetMyAskData(s);

                    // 데이터 불러온 이후에 어댑터 노티피 케이션
                    try {
                        mAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }
                }


            }
        }

        getAskDataHttp getDataAsk = new getAskDataHttp();
        getDataAsk.execute();
    }

    public void divideGetMyAskData(String allData){
        Log.d("마이에스크",""+allData);
        try {
            firstFilter = null;
            firstFilter = allData.split("####"); // 게시글 별로 나누기 위함
            for ( int i = 0 ; i < firstFilter.length ; i++ ){
                seccondFilter = firstFilter[i].split("@");

                // 로그인 모드, URL , 유저 닉네임 순서,
                getAskDataContent = new getMyAskDataContent(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                        seccondFilter[3],seccondFilter[4],seccondFilter[5],seccondFilter[6],seccondFilter[7],
                        seccondFilter[8],seccondFilter[9],seccondFilter[10],seccondFilter[11],seccondFilter[12],seccondFilter[13]); // 객체 생성

                MyAskDataArray.add(getAskDataContent); // 어레이 리스트에 객체 추가.
            }

        }catch (Exception e){

        }
        Log.d("마이에스크",""+MyAskDataArray.size());
    }


}
