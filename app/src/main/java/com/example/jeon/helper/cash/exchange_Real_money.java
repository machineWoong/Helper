package com.example.jeon.helper.cash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class exchange_Real_money extends AppCompatActivity {

    ip ip = new ip();
    String ipad = ip.getIp();

    String loginUserid;
    String userCash;
    String loginUserEmailAddress;

    String key;


    View mView;

    String result;
    Long b;
    AlertDialog dialog2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_exchange__real_money);

        getUserData();

        Button exchangeBtn = (Button) findViewById(R.id.exchangeBtn);
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExchangeBtn();
            }
        });

        Button exchangeCancelBtn = (Button) findViewById(R.id.exchangeCancelBtn);
        exchangeCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 인터넷 사용을 위한 권한
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        try {
            getUserEmailAddress();
        } catch (Exception e) {

        }

    }

    // 정보 받기
    public void getUserData() {
        loginUserid = getIntent().getStringExtra("loginUserId");
        userCash = getIntent().getStringExtra("haveMoney");

        TextView exchangeUserHaveCash = (TextView) findViewById(R.id.exchangeUserHaveCash);


        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String changeFormatuserCash = dc.format(Double.parseDouble(userCash)).toString();
        exchangeUserHaveCash.setText(changeFormatuserCash);
    }

    // 환전 버튼
    public void setExchangeBtn() {
        //소지 캐쉬와  출금 돈 비교

        EditText exchangeCash = (EditText) findViewById(R.id.exchangeCash);
        String userWantCash = exchangeCash.getText().toString();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (String.valueOf(userWantCash).equals("") || String.valueOf(userWantCash) == null) {
            Toast.makeText(this, "환전 금액을 입력하세요", Toast.LENGTH_SHORT).show();
            vibrator.vibrate(1000);

        } else {


            Long a = Long.parseLong(userCash); // 소지금
            b = Long.parseLong(userWantCash); // 환전금

            // 소지금이 환전금보다 적은 경우
            if (a < b) {
                Toast.makeText(this, "환전 금액이 소지금을 초과하였습니다.", Toast.LENGTH_SHORT).show();
                vibrator.vibrate(1000);
            } else {
                // 다이얼로그 창을 띄움과동시에 메일을 보낸다.  서버에도 인증번호를 저장해서 보낸다.
                //랜덤키 생성
                int max = 999999;
                int min = 100000;
                long ketValue = (long) (Math.random() * (max - min)) + min;
                key = String.valueOf(ketValue);

                Toast.makeText(this, "인증 메일을 보냈습니다.", Toast.LENGTH_SHORT).show();

                // 여기에서 메일을 보내야겟구만
                Log.d("보낼주소 ", "" + loginUserEmailAddress);
                sendKeyToUserMail(key, loginUserEmailAddress);


                setMakeConfirmKey("1", b);
                makeConfirmDialog();
            }

        }


    }

    // 인증 다이얼 로그 생성
    public void makeConfirmDialog() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.cash_exchange_confirm, null);


        // 확인 버튼
        TextView exchangeCommit = (TextView) mView.findViewById(R.id.exchangeCommit);
        exchangeCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText getConfirmKey = mView.findViewById(R.id.getConfirmKey);
                key = getConfirmKey.getText().toString();

                if (key.equals("") || key == null || TextUtils.isEmpty(key)) {
                    Toast.makeText(exchange_Real_money.this, "인증키를 입력하세요.", Toast.LENGTH_SHORT).show();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                } else {
                    setMakeConfirmKey("2", b);
                }


            }
        });

        // 취소버튼
        TextView exchangeCommitCancel = (TextView) mView.findViewById(R.id.exchangeCommitCancel);
        exchangeCommitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMakeConfirmKey("3", b);
            }
        });


        aBuilder.setView(mView);
        dialog2 = aBuilder.create();
        dialog2.show();
    }

    // ( 서버 연결 ) 키생성
    public void setMakeConfirmKey(String mode, Long money) {
        class getApplyUserDataHttp extends AsyncTask<Void, Void, String> {

            ProgressDialog dialog = new ProgressDialog(exchange_Real_money.this);
            String mode;
            Long money;
            String loginUserEmailAddress1;

            public getApplyUserDataHttp(String mode, Long money, String loginUserEmailAddress) {
                this.mode = mode;
                this.money = money;
                this.loginUserEmailAddress1 = loginUserEmailAddress;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("확인중");
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
                    URL url = new URL(ipad + "/makeConfirmKey.php");
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
                    buffer.append("id").append("=").append(loginUserid).append("&");
                    buffer.append("key").append("=").append(key).append("&");
                    buffer.append("money").append("=").append(money).append("&");
                    buffer.append("mode").append("=").append(mode);


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

                Log.d("인증 결과", "" + s);

                if (s.equals("승인")) {
                    Toast.makeText(exchange_Real_money.this, "환전 처리 되었습니다.", Toast.LENGTH_SHORT).show();
                    dialog2.dismiss();
                    Intent backTo = new Intent(exchange_Real_money.this, cash_main.class);
                    backTo.putExtra("changeMoney", b);
                    setResult(RESULT_OK, backTo);
                    finish();

                } else if (s.equals("거절")) {
                    Toast.makeText(exchange_Real_money.this, "인증키가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                } else if (s.equals("삭제")) {
                    dialog2.dismiss();
                }

            }
        }

        getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(mode, money, loginUserEmailAddress);
        getDataAsk.execute();
    }

    // (메일 보내기)
    public void sendKeyToUserMail(String key, String loginUserEmailAddress) {
        try {
            GmailSender gMailSender = new GmailSender();
            //GMailSender.sendMail(제목, 본문내용, 받는사람);
            gMailSender.sendMail("helper 환전 이메일 인증", "인증 키 입니다.\n정확하게 입력하세요.\n" + key, loginUserEmailAddress);
            Log.d("이메일을 성공적으로 보냈습니다.", "이메일을 성공적으로 보냈습니다.");
        } catch (SendFailedException e) {
            Log.d("이메일 형식이 잘못되었습니다", "이메일 형식이 잘못되었습니다");
        } catch (MessagingException e) {
            Log.d("인터넷 연결을 확인해주십시오", "인터넷 연결을 확인해주십시오");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 사용자의 이메일 주소 받아오기
    public void getUserEmailAddress() {
        class getUserEmailDataHttp extends AsyncTask<Void, Void, String> {

            ProgressDialog dialog = new ProgressDialog(exchange_Real_money.this);
            String userId;
            String getEmail;

            public getUserEmailDataHttp(String userId) {
                this.userId = userId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("사용자 정보를 가지고 오는중");
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
                    URL url = new URL(ipad + "/getUserMail.php");
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
                    buffer.append("id").append("=").append(loginUserid);


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
                    getEmail = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return getEmail;
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

                Log.d("사용자의 이메일 가져오기", s);

                loginUserEmailAddress = s;

            }
        }
        getUserEmailDataHttp getEmail = new getUserEmailDataHttp(loginUserid);
        getEmail.execute();

    }
}
