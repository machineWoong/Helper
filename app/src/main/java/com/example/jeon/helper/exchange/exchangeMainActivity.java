package com.example.jeon.helper.exchange;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.giveHelp.giveHelpAdapter;
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

public class exchangeMainActivity extends AppCompatActivity {

    ip ip = new ip();
    String ipad = ip.getIp();


    // 정산하기 페이지
    String loginUserId;
    String loginUserNick;
    String haveCash;


    // 받아오는 데이터들.
    ArrayList<exchangeContent> exchangeData = new ArrayList<>();
    exchangeContent eC;
    String allData;
    String [] a1;
    String [] b1;

    RecyclerView view;
    exchangerRecyclerViewAdapter adapter;

    // 필터링용
    ArrayList<exchangeContent> filterArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_main);

        // 로그인 유저 정보
        getMydata();

        // 데이터 받기
        getExchangeDataHttp();

    }

    // 데이터 받기
    public void getMydata(){
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNickName");
        haveCash = getIntent().getStringExtra("userCash");
    }

    //( 서버로부터 받기 )
    public void getExchangeDataHttp(){
        class getExchangeDataHttp extends AsyncTask<Void,Void,String> {

            ProgressDialog dialog = new ProgressDialog(exchangeMainActivity.this);
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


                    URL url = new URL(ipad+"/getExchangeData.php");
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
                    buffer.append("id").append("=").append(loginUserId);                 // php 변수에 값 대입

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
                    allData = builder.toString();                       // 전송결과를 전역 변수에 저장
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return allData;
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
                Log.d("exchang 데이터들 ",s);

                if(s.equals("없음")){
                    Toast.makeText(exchangeMainActivity.this, "정산할 내역이 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    divideDataString(s);
                }

            }
        }

        getExchangeDataHttp getDataGive = new getExchangeDataHttp();
        getDataGive.execute();
    }
    public void divideDataString(String a){

        a1 = a.split("&");
        for ( int i =0 ; i< a1.length ; i++){
            b1 = a1[i].split("%");
            eC = new exchangeContent(b1[0],b1[1],b1[2],b1[3],b1[4],b1[5]);
            exchangeData.add(eC);
        }

        for ( int i = 0 ; i < exchangeData.size(); i++){
            Log.d("exChang 메인에서  상태 ",""+exchangeData.get(i).state);
        }

        setExchangeDataAdapter(exchangeData);

        // 데이터 필터링
        TextView showExchangeOnGoing = (TextView)findViewById(R.id.showExchangeOnGoing);
        showExchangeOnGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilter(1);
            }
        });
        TextView showExchangeSuccess = (TextView)findViewById(R.id.showExchangeSuccess);
        showExchangeSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilter(2);
            }
        });



    }

    //데이터 필터링
    public void setFilter(int mode){
        if ( mode == 1){
            filterArray.clear();
            for (int i= 0 ; i < exchangeData.size(); i++){
                if (exchangeData.get(i).state.equals("0")){
                    // 정산중인 데이터
                    filterArray.add(exchangeData.get(i));
                }
            }

            setExchangeDataAdapter(filterArray);
            adapter.notifyDataSetChanged();

        }else if (mode == 2){
            filterArray.clear();
            for (int i= 0 ; i < exchangeData.size(); i++){
                if (exchangeData.get(i).state.equals("1")){
                    // 정산완료 데이터
                    filterArray.add(exchangeData.get(i));
                }
            }
            setExchangeDataAdapter(filterArray);
            adapter.notifyDataSetChanged();
        }
    }

    // 어댑터 설정
    public void setExchangeDataAdapter(ArrayList<exchangeContent> data){
        // 리사이클러 뷰
        view = (RecyclerView)findViewById(R.id.exchangeRecyclerView);
        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);


        GlobalApplication gg = (GlobalApplication)getApplication();

        // 어댑터를 연결 시켜주는 부분  ( 배열 + 갯수 )
        adapter = new exchangerRecyclerViewAdapter(data,this,loginUserId,gg.getSocket(),loginUserNick);
        view.setAdapter(adapter);

    }
}
