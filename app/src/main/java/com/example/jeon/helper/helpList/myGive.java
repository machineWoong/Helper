package com.example.jeon.helper.helpList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class myGive extends android.support.v4.app.Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";


    // ip
    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    // 로그인 유저 정보
    String userId;

    // 데이터
    getMyGiveDataContent getGiveDataContent;
    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자
    ArrayList<getMyGiveDataContent> MyGiveDataArray = new ArrayList<>();
    String result2;

    // 필터링
    ArrayList<getMyGiveDataContent> MyGiveDataArrayFilter = new ArrayList<>();

    // 어댑터 세팅.
    myGiveAdapter mAdapter;
    View view;
    RecyclerView rv;
    Context context;


    @SuppressWarnings("unused")
    public static myGive newInstance(int columnCount) {
        myGive fragment = new myGive();
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
        view = inflater.inflate(R.layout.my_give_item_recyclerview, container, false);

        rv = (RecyclerView) view.findViewById(R.id.myGiveListRecyclerView);
        context = view.getContext();

        rv.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        mAdapter = new myGiveAdapter(MyGiveDataArray, context,userId);
        rv.setAdapter(mAdapter);

        //( 수행중 필터링 )
        TextView showMyGiveGetOnGoing =(TextView)view.findViewById(R.id.showMyGiveGetOnGoing);
        showMyGiveGetOnGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyGiveDataArrayFilter.clear();
                // 값이 1 인것들

                for ( int i = 0 ; i < MyGiveDataArray.size(); i++ ){
                    if( MyGiveDataArray.get(i).state.equals("1")){
                        MyGiveDataArrayFilter.add( MyGiveDataArray.get(i));
                    }
                }
                try {
                    mAdapter = new myGiveAdapter(MyGiveDataArrayFilter,context,userId);
                    rv.setAdapter(mAdapter);
                }catch (Exception e){

                }
            }
        });

        // 마감 종료
        TextView showMyAskGiveDeadLine =(TextView)view.findViewById(R.id.showMyAskGiveDeadLine);
        showMyAskGiveDeadLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyGiveDataArrayFilter.clear();
                // 값이 2 인것들

                for ( int i = 0 ; i < MyGiveDataArray.size(); i++ ){
                    if( MyGiveDataArray.get(i).onGoing.equals("2")){
                        MyGiveDataArrayFilter.add( MyGiveDataArray.get(i));
                    }
                }
                try {
                    mAdapter = new myGiveAdapter(MyGiveDataArrayFilter,context,userId);
                    rv.setAdapter(mAdapter);
                }catch (Exception e){

                }
            }
        });




        return view;
    }

    //----------------------------------------데이터 세팅 -----------------------------------------
    // 유저정보 및 데이터 세팅
    public void getUserId(){
        userId = ((helpListMain)getActivity()).getUserId();
        getDataApplyHelpHttp2();
    }

    // ( 서버연결, 도움 지원 정보 )
    public void getDataApplyHelpHttp2(){
        // 내가 지원한 정보를 가지고 와야함.
        class getGiveDataHttp extends AsyncTask<Void,Void,String> {

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


                    URL url = new URL(ipad+"/getMyGiveData.php");
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

                if ( s.equals("없음")){
                    Log.d("프레그먼트 2 데이터","없다는데?");
                }else{
                    Log.d("프레그먼트 2 데이터 ",""+s);
                    divideGetMyAskData(s);
                    try {
                        mAdapter.notifyDataSetChanged();
                    }catch (Exception e){

                    }

                }
            }
        }

        getGiveDataHttp getDataGive = new getGiveDataHttp();
        getDataGive.execute();
    }

    public void divideGetMyAskData(String allData){
        try{
            firstFilter = allData.split("####"); // 게시글 별로 나누기 위함
            for ( int i = 0 ; i < firstFilter.length ; i++ ){
                seccondFilter = firstFilter[i].split("@");

                // 로그인 모드, URL , 유저 닉네임 순서,
                getGiveDataContent = new getMyGiveDataContent(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                        seccondFilter[3],seccondFilter[4],seccondFilter[5],seccondFilter[6],seccondFilter[7],seccondFilter[8],
                        seccondFilter[9],seccondFilter[10],seccondFilter[11],seccondFilter[12]); // 객체 생성

                MyGiveDataArray.add(getGiveDataContent); // 어레이 리스트에 객체 추가.
            }
        }catch (Exception e){

        }

    }




}
