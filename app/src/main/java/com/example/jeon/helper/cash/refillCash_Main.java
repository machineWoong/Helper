package com.example.jeon.helper.cash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.text.DecimalFormat;

public class refillCash_Main extends AppCompatActivity {

    String loginUserId;
    String userCash;

    int choice =0;
    int choiceMoney =0;

    View mView;
    AlertDialog dialog2;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_refill_cash_main);

        // 데이터 받기
        getUserData();

        // 체크박스 이벤트
        setCheckBoxEvent();

        // 버튼 클릭 이벤트
        Button refillBtn = (Button)findViewById(R.id.refillBtn);
        Button refillCancelBtn = (Button)findViewById(R.id.refillCancelBtn);

        refillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRefillBtn();
            }
        });

        refillCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // 데이터 받기
    public void getUserData(){
        loginUserId = getIntent().getStringExtra("loginUserId");
        userCash = getIntent().getStringExtra("haveMoney");
    }

    //체크박스 중복 체크 이벤트
    public void setCheckBoxEvent(){
        CheckBox item1 = (CheckBox)findViewById(R.id.first_Item);
        CheckBox item2 = (CheckBox)findViewById(R.id.second_Item);
        CheckBox item3 = (CheckBox)findViewById(R.id.third_Item);
        CheckBox item4 = (CheckBox)findViewById(R.id.fourth_Item);
        CheckBox item5 = (CheckBox)findViewById(R.id.fifth_Item);

        item1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == true){
                    choice = 1;
                    choiceMoney = 5000;

                    CheckBox item2 = (CheckBox)findViewById(R.id.second_Item);
                    CheckBox item3 = (CheckBox)findViewById(R.id.third_Item);
                    CheckBox item4 = (CheckBox)findViewById(R.id.fourth_Item);
                    CheckBox item5 = (CheckBox)findViewById(R.id.fifth_Item);

                    item2.setChecked(false);
                    item3.setChecked(false);
                    item4.setChecked(false);
                    item5.setChecked(false);

                }else{
                }

            }
        });

        item2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    choice = 2;
                    choiceMoney = 10000;

                    CheckBox item1 = (CheckBox)findViewById(R.id.first_Item);
                    CheckBox item3 = (CheckBox)findViewById(R.id.third_Item);
                    CheckBox item4 = (CheckBox)findViewById(R.id.fourth_Item);
                    CheckBox item5 = (CheckBox)findViewById(R.id.fifth_Item);

                    item1.setChecked(false);
                    item3.setChecked(false);
                    item4.setChecked(false);
                    item5.setChecked(false);

                }else{
                }
            }
        });

        item3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    choice = 3;
                    choiceMoney = 20000;

                    CheckBox item1 = (CheckBox)findViewById(R.id.first_Item);
                    CheckBox item2 = (CheckBox)findViewById(R.id.second_Item);
                    CheckBox item4 = (CheckBox)findViewById(R.id.fourth_Item);
                    CheckBox item5 = (CheckBox)findViewById(R.id.fifth_Item);

                    item1.setChecked(false);
                    item2.setChecked(false);
                    item4.setChecked(false);
                    item5.setChecked(false);

                }else{
                }
            }
        });

        item4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    choice = 4;
                    choiceMoney = 50000;

                    CheckBox item1 = (CheckBox)findViewById(R.id.first_Item);
                    CheckBox item2 = (CheckBox)findViewById(R.id.second_Item);
                    CheckBox item3 = (CheckBox)findViewById(R.id.third_Item);
                    CheckBox item5 = (CheckBox)findViewById(R.id.fifth_Item);

                    item1.setChecked(false);
                    item2.setChecked(false);
                    item3.setChecked(false);
                    item5.setChecked(false);

                }else{
                }
            }
        });

        item5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    choice = 5;
                    choiceMoney = 100000;

                    CheckBox item1 = (CheckBox)findViewById(R.id.first_Item);
                    CheckBox item2 = (CheckBox)findViewById(R.id.second_Item);
                    CheckBox item3 = (CheckBox)findViewById(R.id.third_Item);
                    CheckBox item4 = (CheckBox)findViewById(R.id.fourth_Item);

                    item1.setChecked(false);
                    item2.setChecked(false);
                    item3.setChecked(false);
                    item4.setChecked(false);

                }else{
                }
            }
        });



    }

    // 충전하기 버튼 이벤트
    public void setRefillBtn(){

        CheckBox item1 = (CheckBox)findViewById(R.id.first_Item);
        CheckBox item2 = (CheckBox)findViewById(R.id.second_Item);
        CheckBox item3 = (CheckBox)findViewById(R.id.third_Item);
        CheckBox item4 = (CheckBox)findViewById(R.id.fourth_Item);
        CheckBox item5 = (CheckBox)findViewById(R.id.fifth_Item);

        if(choice == 0 || choiceMoney == 0){
            Toast.makeText(this, "충전할 금액을 선택해 주세요", Toast.LENGTH_SHORT).show();
        }else if(item1.isChecked() == false && item2.isChecked() == false && item3.isChecked() == false && item4.isChecked() == false && item5.isChecked() == false){
            Toast.makeText(this, "충전할 금액을 선택해 주세요", Toast.LENGTH_SHORT).show();
        }else{
            makeRefill_Dialog(loginUserId,userCash,choice,choiceMoney);
        }

    }

    // 다이얼로그 버튼
    public void makeRefill_Dialog(final String loginUserId, final String userCash, int choice, final int choiceMoney){
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.cash_refill_dialog, null);


        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String changeFormatuserCash = dc.format((double)choiceMoney).toString();


        // 텍스트 설정
        TextView refill_money = (TextView)mView.findViewById(R.id.refill_money);
        refill_money.setText(changeFormatuserCash+"원");
        TextView refill_cash = (TextView)mView.findViewById(R.id.refill_cash);
        refill_cash.setText(changeFormatuserCash);

        // 버튼
        TextView refill_cashBtn = (TextView)mView.findViewById(R.id.refill_cashBtn);
        refill_cashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 결제 이벤트
                setRefillMoneyHttp(loginUserId,userCash,choiceMoney);
            }
        });

        TextView refill_cashCancelBtn = (TextView)mView.findViewById(R.id.refill_cashCancelBtn);
        refill_cashCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        aBuilder.setView(mView);
        dialog2 = aBuilder.create();
        dialog2.show();
    }

    // (서버전송) 서버에 전송 refillMoney.php
    public void setRefillMoneyHttp(String loginUserId,String userCash,int cash){
        class getApplyUserDataHttp extends AsyncTask<Void, Void, String> {

            ProgressDialog dialog = new ProgressDialog(refillCash_Main.this);
            String result;

            String loginUserId;
            String haveCash;
            int refill_Cash;
            int setCash;

            public getApplyUserDataHttp(String loginUserId, String haveCash, int refill_Cash) {
                this.loginUserId = loginUserId;
                this.haveCash = haveCash;
                this.refill_Cash = refill_Cash;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("확인중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();

                setCash = Integer.parseInt(haveCash)+refill_Cash;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad + "/refillMoney.php");
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
                    buffer.append("id").append("=").append(loginUserId).append("&");
                    buffer.append("setCash").append("=").append(setCash).append("&");
                    buffer.append("refill_Cash").append("=").append(refill_Cash);

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

                Log.d("충전 결과",s);
                dialog2.dismiss();
                Toast.makeText(refillCash_Main.this, "결제 완료", Toast.LENGTH_SHORT).show();

                Intent gotoChangeMoney = new Intent(refillCash_Main.this,cash_main.class);
                gotoChangeMoney.putExtra("changeMoney",choiceMoney);
                setResult(RESULT_OK,gotoChangeMoney);
                finish();

            }
        }

        getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(loginUserId,userCash,cash);
        getDataAsk.execute();
    }

}
