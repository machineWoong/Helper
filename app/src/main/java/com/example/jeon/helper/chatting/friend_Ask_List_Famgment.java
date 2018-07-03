package com.example.jeon.helper.chatting;

import android.app.Activity;
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

/**
 * Created by JEON on 2018-05-30.
 */

public class friend_Ask_List_Famgment extends android.support.v4.app.Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    ArrayList<friend_Ask_List_Content> faL = new ArrayList<>();
    friend_Ask_List_Content falc;

    ArrayList<friend_Ask_List_Content> filterFal = new ArrayList<>();

    String[] firstFilter; // 첫번째 구분자
    String[] seccondFilter;  // 두번째 구분자

    String loginUserId;
    String loginUserNick;

    View view;
    RecyclerView rv;
    Context context;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    public static friend_Ask_List_Famgment newInstance(int columnCount) {
        friend_Ask_List_Famgment fragment = new friend_Ask_List_Famgment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setUserData();

        view = inflater.inflate(R.layout.chatting_friend_ask_list, container, false);

        rv = (RecyclerView) view.findViewById(R.id.friend_Ask_List);
        context = view.getContext();

        rv.setHasFixedSize(true);


        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);


        // Inflate the layout for this fragment


        // 버튼 클릭 이벤트
        TextView friend_Ask_mode1TextView= view.findViewById(R.id.friend_Ask_mode1);
        friend_Ask_mode1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFal.clear();
                for (int i =0 ; i < faL.size(); i++){
                    if( faL.get(i).accepMode == 0){
                        filterFal.add(faL.get(i));
                    }
                }
                // 어댑터 설정
                GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
                friend_Ask_list_Adapter fra = new friend_Ask_list_Adapter(filterFal, context, loginUserId,gg.getSocket(),loginUserNick);
                rv.setAdapter(fra);
                fra.notifyDataSetChanged();
            }
        });


        TextView friend_Ask_mode2= view.findViewById(R.id.friend_Ask_mode2);
        friend_Ask_mode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFal.clear();
                for (int i =0 ; i < faL.size(); i++){
                    if( faL.get(i).accepMode == 2){
                        filterFal.add(faL.get(i));
                    }
                }
                // 어댑터 설정
                GlobalApplication gg = (GlobalApplication)getActivity().getApplication();
                friend_Ask_list_Adapter fra = new friend_Ask_list_Adapter(filterFal, context, loginUserId,gg.getSocket(),loginUserNick);
                rv.setAdapter(fra);
                fra.notifyDataSetChanged();
            }
        });

        return view;
    }

    public void setUserData() {
        loginUserId = ((chattingMainActivity) getActivity()).loginUserid;
        loginUserNick = ((chattingMainActivity) getActivity()).loginUserNick;

        // 서버로 부터 데이터 받아오기
        getDataFriendAskList();
    }

    public void getDataFriendAskList() {

        class getRepleDataHttp extends AsyncTask<Void, Void, String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(getActivity());
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("친구 목록 로딩중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(true); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad + "/friend_ask_list.php");
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
                    buffer.append("targetId").append("=").append(loginUserId).append("&");
                    buffer.append("mode").append("=").append(1);// php 변수에 값 대입

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

                Log.d("친구 요청 목록 리스트 받아오기 ", s);
                if (s.equals("없음")) {
                    TextView friend_ask_no = (TextView) view.findViewById(R.id.friend_ask_no);
                    friend_ask_no.setVisibility(View.VISIBLE);

                    rv = (RecyclerView) view.findViewById(R.id.friend_Ask_List);
                    rv.setVisibility(View.GONE);
                } else {
                    divideData(s);
                }

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();

    }

    public void divideData(String a) {

        try {
            faL.clear();
            firstFilter = null;
            firstFilter = a.split("!"); // 게시글 별로 나누기 위함
            for (int i = 0; i < firstFilter.length; i++) {
                seccondFilter = firstFilter[i].split("@");

                // 로그인 모드, URL , 유저 닉네임 순서,
                falc = new friend_Ask_List_Content(seccondFilter[0], seccondFilter[1], seccondFilter[2], Integer.parseInt(seccondFilter[3])); // 객체 생성
                faL.add(falc); // 어레이 리스트에 객체 추가.
            }

            GlobalApplication gg = (GlobalApplication)getActivity().getApplication();

            // 어댑터 설정
            friend_Ask_list_Adapter fra = new friend_Ask_list_Adapter(faL, context, loginUserId,gg.getSocket(),loginUserNick);
            rv.setAdapter(fra);

        } catch (Exception e) {

        }
    }

}
