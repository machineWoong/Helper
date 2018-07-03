package com.example.jeon.helper.noti_table;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class noti_List_Main extends AppCompatActivity {

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    String loginUserId;
    String loginUserNick;


    noti_Content nC;
    ArrayList<noti_Content> ncArray = new ArrayList<>();
    noti_List_Adapter ncAdapter;


    ArrayList<noti_Content> filterArray = new ArrayList<>();

    String[] filter;
    String[] filter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti__list__main);
        getLoginData();
    }

    // 체크박스 이벤트
    public void checkBoxEvent() {

        final CheckBox allNotiCheck = (CheckBox) findViewById(R.id.allNotiCheck);

        final CheckBox NotiCheck = (CheckBox) findViewById(R.id.NotiCheck);

        final CheckBox eventCheck = (CheckBox) findViewById(R.id.eventCheck);

        allNotiCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == true){
                    NotiCheck.setChecked(false);
                    eventCheck.setChecked(false);
                    allNotiCheck.setChecked(true);
                    setRecyclerView(ncArray);
                    ncAdapter.notifyDataSetChanged();
                }else{
                    allNotiCheck.setChecked(false);
                }

            }
        });


        NotiCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == true){
                    allNotiCheck.setChecked(false);
                    eventCheck.setChecked(false);
                    NotiCheck.setChecked(true);
                    filterArray.clear();

                    for ( int i = 0 ; i< ncArray.size(); i++){
                        if(ncArray.get(i).notiMode.equals("공지사항")){
                            filterArray.add(ncArray.get(i));
                        }
                    }

                    setRecyclerView(filterArray);
                    ncAdapter.notifyDataSetChanged();
                }else{
                    NotiCheck.setChecked(false);
                }


            }
        });


        eventCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ( isChecked == true){
                    allNotiCheck.setChecked(false);
                    NotiCheck.setChecked(false);
                    eventCheck.setChecked(true);
                    filterArray.clear();


                    for ( int i = 0 ; i< ncArray.size(); i++){
                        if(ncArray.get(i).notiMode.equals("이벤트")){
                            filterArray.add(ncArray.get(i));
                        }
                    }

                    setRecyclerView(filterArray);
                    ncAdapter.notifyDataSetChanged();
                }else{
                    eventCheck.setChecked(false);
                }

            }
        });


    }

    // 로그인 유저 정보 가지고 오기
    public void getLoginData() {
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");

        if (loginUserId.equals("admin") && loginUserNick.equals("관리자")) {
            Button makeNotiBtn = (Button) findViewById(R.id.makeNotiBtn);
            makeNotiBtn.setVisibility(View.VISIBLE);

            makeNotiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoMakeNoti = new Intent(noti_List_Main.this, make_noti_page.class);
                    gotoMakeNoti.putExtra("loginUserId", loginUserId);
                    gotoMakeNoti.putExtra("loginUserNick", loginUserNick);
                    gotoMakeNoti.putExtra("mode", 1);
                    startActivityForResult(gotoMakeNoti, 1111);
                }
            });

        } else {
            Button makeNotiBtn = (Button) findViewById(R.id.makeNotiBtn);
            makeNotiBtn.setVisibility(View.GONE);
        }

    }

    // ( 서버 연결 )  게시글 가지고 오기
    public void getNotiData() {
        class getNotiDataHttp extends AsyncTask<Void, Void, String> {
            String result;
            ProgressDialog dialog = new ProgressDialog(noti_List_Main.this);

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
                    Toast.makeText(noti_List_Main.this, "등록된 공지사항이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    devideData(s);
                }
            }
        }

        getNotiDataHttp getData = new getNotiDataHttp();
        getData.execute();
    }

    public void devideData(String a) {
        filter = a.split("###");
        for (int i = 0; i < filter.length; i++) {
            filter1 = filter[i].split("@@@");
            nC = new noti_Content(filter1[0], filter1[1], filter1[2], filter1[3], filter1[4], filter1[5], filter1[6]);
            ncArray.add(nC);
        }
        setRecyclerView(ncArray);

        // 필터링
        checkBoxEvent();
    }

    // 리사이클러뷰 세팅
    public void setRecyclerView(ArrayList<noti_Content> ncArray22) {
        // 리사이클러 뷰
        RecyclerView view = (RecyclerView) findViewById(R.id.noti_list_in_notification);

        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        // 어댑터를 연결 시켜주는 부분
        ncAdapter = new noti_List_Adapter(this, ncArray22, loginUserId, loginUserNick);
        view.setAdapter(ncAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                //작성완료
                Toast.makeText(this, "작성완료", Toast.LENGTH_SHORT).show();
            } else {
                // 취소
                Toast.makeText(this, "작성취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ncArray.clear();
        getNotiData();
    }
}
