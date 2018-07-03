package com.example.jeon.helper.cash;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.askHelpAdapter;
import com.example.jeon.helper.ip;

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

public class cash_main extends AppCompatActivity {


    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    String loginUserid;
    String loginUserNick;
    String userCash;


    // 전체 거래 장부
    ArrayList<cash_Content> accountList = new ArrayList<>();
    cash_Content cC;
    String dataResult;
    String []a;
    String []b;

    RecyclerView view;
    cash_adapter ca;

    // 필터링
    ArrayList<cash_Content> filterList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_main_input);


        //스피너 어댑터 설정
        setAccountModeSpinner();

        //사용자 아이디 받아오기
        getUserId();

        // 스피너 변경 이벤트
        try {
            setSpinnerChangeEvent();
        }catch (Exception e){

        }

        //환전하기
        TextView changeRealMoney = (TextView)findViewById(R.id.changeRealMoney);
        changeRealMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoChangRealMoney = new Intent(cash_main.this,exchange_Real_money.class);
                gotoChangRealMoney.putExtra("loginUserId",loginUserid);
                gotoChangRealMoney.putExtra("haveMoney",userCash);
                startActivityForResult(gotoChangRealMoney,1111);

            }
        });

        //충전하기
        TextView refillCash = (TextView)findViewById(R.id.refillCash);
        refillCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoRefillMoney = new Intent(cash_main.this,refillCash_Main.class);
                gotoRefillMoney.putExtra("loginUserId",loginUserid);
                gotoRefillMoney.putExtra("haveMoney",userCash);
                startActivityForResult(gotoRefillMoney,2222);
            }
        });

    }

    // 사용자 아이디 받아오기
    public void getUserId(){
        loginUserid = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");
        userCash= getIntent().getStringExtra("haveMoney");


        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String changeFormatuserCash = dc.format(Double.parseDouble(userCash)).toString();

        TextView totalRecentCash = (TextView)findViewById(R.id.totalRecentCash);
        totalRecentCash.setText(changeFormatuserCash+"원");

        // 거래 장부 받아오기
        getAccountList();
    }

    // -------------------------------------------스피너 -------------------------------------------
    // 스피너 설정
    public void setAccountModeSpinner(){
        Spinner accountMode = (Spinner)findViewById(R.id.accountMode);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.accountMode, android.R.layout.simple_spinner_item);
        accountMode.setAdapter(yearAdapter);
    }

    // 스피너 값 변경이벤트
    public void setSpinnerChangeEvent(){
        Spinner accountMode = (Spinner)findViewById(R.id.accountMode);
        accountMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){ // 전체 보기라면
                    setAdepter(accountList,0);
                }else if (position == 1){
                    setfilterList(1);
                    setAdepter(filterList,1);
                }else if (position == 2){
                    setfilterList(2);
                    setAdepter(filterList,2);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //-----------------------------------------리사이클러 뷰 관련 ---------------------------------
    // ( 서버 연결 ) 거래장부 받아오기
    public void getAccountList(){

        class getAccountListToHttp extends AsyncTask<Void,Void,String> {

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------


                    URL url = new URL(ipad+"/cash_account_list.php");
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
                    buffer.append("id").append("=").append(loginUserid);                 // php 변수에 값 대입


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
                    dataResult = builder.toString();                       // 전송결과를 전역 변수에 저장

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

                Log.d("거래 장부 ",""+result);
                if(result.equals("없음")){

                }else{
                    devideDateString(result);
                }

            }

        }

        getAccountListToHttp getList = new getAccountListToHttp();
        getList.execute();

    }

    // 데이터 쪼개주기
    public void devideDateString(String data){
        a =data.split("!");
        accountList.clear();
        for ( int i = 0 ; i < a.length ; i++){
            b = a[i].split("@");
            cC = new cash_Content(b[0],b[1],b[2],b[3],b[4],b[5]);
            accountList.add(cC);
        }

        setAdepter(accountList,0);
    }

    // 리사이클러뷰 어댑터 설정
    public void setAdepter(ArrayList<cash_Content> accountList,int mode){
        // 리사이클러 뷰
        view = (RecyclerView)findViewById(R.id.cashRecyclerView);

        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        // 어댑터를 연결 시켜주는 부분  ( 배열 + 갯수 )
        ca = new cash_adapter(accountList,this,loginUserNick,mode);
        view.setAdapter(ca);
    }

    // 필터링
    public void setfilterList(int mode){
        filterList.clear();
        if ( mode == 1){
            // 입금받은  내역만 보기
            for ( int i= 0; i < accountList.size() ; i++){
                if ( !loginUserNick.equals(accountList.get(i).senderId)){
                    filterList.add(accountList.get(i));
                }
            }

        }else if (mode == 2){
            // 출금한 내역만 보기
            for ( int i= 0; i < accountList.size() ; i++){
                if ( loginUserNick.equals(accountList.get(i).senderId)){
                    filterList.add(accountList.get(i));
                }
            }
        }
    }
    //----------------------------------------환전 충전 결과 ---------------------------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1111){
            // 환전하기
            if(resultCode == RESULT_OK){
                // 환전하고 변환된 돈의 양을 보여준다. 그리고  거래 내역을 다시 불러온다.
                Long changeMoney = data.getLongExtra("changeMoney",0);


                // 돈 재설정.
                Long returnMoney = Long.parseLong(userCash);
                returnMoney = returnMoney-changeMoney;
                userCash = String.valueOf(returnMoney);

                DecimalFormat dc = new DecimalFormat("###,###,###,###");
                String changeFormatuserCash = dc.format(Double.parseDouble(userCash)).toString();

                TextView totalRecentCash = (TextView)findViewById(R.id.totalRecentCash);
                totalRecentCash.setText(changeFormatuserCash+"원");

                // 디비 다시뿌려주기
                accountList.clear();
                getAccountList();

            }
        }

        if(requestCode == 2222){
            // 충전하기
            if(resultCode == RESULT_OK){
                // 충전한 후 돈 . 그리고  거래 내역을 다시 불러온다.
                Long changeMoney = Long.valueOf(data.getIntExtra("changeMoney",0));

                Log.d("충전후 금액",""+changeMoney);

                // 돈 재설정.
                Long returnMoney = Long.parseLong(userCash);
                returnMoney = returnMoney+changeMoney;
                userCash = String.valueOf(returnMoney);

                DecimalFormat dc = new DecimalFormat("###,###,###,###");
                String changeFormatuserCash = dc.format(Double.parseDouble(userCash)).toString();

                TextView totalRecentCash = (TextView)findViewById(R.id.totalRecentCash);
                totalRecentCash.setText(changeFormatuserCash+"원");

                // 디비 다시뿌려주기
                accountList.clear();
                getAccountList();
            }
        }

    }
}
