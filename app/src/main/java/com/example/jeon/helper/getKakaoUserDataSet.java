package com.example.jeon.helper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.askHalpMainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class getKakaoUserDataSet extends AppCompatActivity {

    String uesrId;

    String location;
    String gender;
    String eMail;

    boolean isEmailPatternCheck = false;

    String checkResult;

    ip ip = new ip();
    String ipad = ip.getIp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_kakao_user_data_set);


        // 아이디 가져오기
        getKakaouserId();

        // 스피너 설정
        setSpinner();

        // 체크박스 이벤트
        checkBoxEvent();

        //이메일 유효성 체크
        compareEmailCheck();


        // 버튼이벤트
        Button kakaoJoinBtn = (Button)findViewById(R.id.kakaoJoinBtn);
        kakaoJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWidget();
            }
        });
        Button KakaoJoinCancel =(Button) findViewById(R.id.KakaoJoinCancel);
        KakaoJoinCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    public void getKakaouserId(){
        uesrId = getIntent().getStringExtra("id");
    }

    // 위젯 값 가져오기
    public void checkWidget(){

        EditText kakaoEmail = (EditText)findViewById(R.id.kakaoEmail);
        Spinner kakaoLocation =(Spinner)findViewById(R.id.kakaoLocation);
        String loc = kakaoLocation.getSelectedItem().toString();

        if( isEmailPatternCheck == false || gender == null || loc.equals("---- 선택 ----") ){
            Toast.makeText(this, "입력되지 않은 데이터가 있습니다.", Toast.LENGTH_SHORT).show();
        }else{
            eMail = kakaoEmail.getText().toString();
            location = loc;
            // gender;
            // 서버를 불러와서  편집해준다.
            getKaKaoDataCheck();
        }
    }

    // 서버 연결

    public void getKaKaoDataCheck(){
        class getNickNameToHttp extends AsyncTask<Void,Void,String> {

            String id;

            ProgressDialog dialog = new ProgressDialog(getKakaoUserDataSet.this);
            public getNickNameToHttp (String id){
                this.id = id;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 로딩중");
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
                    URL url = new URL(ipad+"/addKakaouserData.php");
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
                    buffer.append("id").append("=").append(id).append("&");                 // php 변수에 값 대입
                    buffer.append("gender").append("=").append(gender).append("&");
                    buffer.append("location").append("=").append(location).append("&");
                    buffer.append("eMail").append("=").append(eMail);


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
                    checkResult = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("9999999999999999","   "+checkResult);
                return checkResult;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }

                if ( checkResult.equals("2")){
                    // 입력된 데이타가 없는 경우에는 이동
                    Toast.makeText(getKakaoUserDataSet.this, "등록 성공", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }

        }
        getNickNameToHttp getNickName = new getNickNameToHttp(uesrId);
        getNickName.execute();
    }


    // ( 유효성 검사 ) 이메일
    public void compareEmailCheck(){
        EditText kakaoEmail = (EditText) findViewById(R.id.kakaoEmail);

        kakaoEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmailPatternCheck = false;
                EditText kakaoEmail = (EditText) findViewById(R.id.kakaoEmail);
                String getEmail = kakaoEmail.getText().toString();
                TextView emailCheck = (TextView)findViewById(R.id.emailCheck);

                // 이메일 유효성 정규식
                String eMailPattern = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
                Boolean matchEmailPattern = Pattern.matches(eMailPattern,getEmail);

                if( matchEmailPattern == true){ // 형식에 일치 한다면.
                    emailCheck.setText("이메일 형식이 올바릅니다.");
                    String color = "#FF119F53"; // 초록색
                    emailCheck.setTextColor(Color.parseColor(color));
                    isEmailPatternCheck = true;
                }else{
                    emailCheck.setText("이메일 형식이 올바르지 않습니다.");
                    String color = "#FF0000"; // 빨간색
                    emailCheck.setTextColor(Color.parseColor(color));
                    isEmailPatternCheck = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // 스피너 어댑터 설정하기.
    public void setSpinner() {
        //스피너 어댑터 설정
        Spinner joinLocation = (Spinner) findViewById(R.id.kakaoLocation);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        joinLocation.setAdapter(yearAdapter);

    }

    // 체크박스 이벤트
    public void checkBoxEvent(){
        CheckBox kakaoMen = (CheckBox) findViewById(R.id.kakaoMen);
        CheckBox kakaoGirl = (CheckBox) findViewById(R.id.kakaoGirl);

        kakaoMen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked == true){
                    CheckBox joinCheckBoxGirl = (CheckBox) findViewById(R.id.kakaoGirl);
                    joinCheckBoxGirl.setChecked(false);
                    gender="남자";
                }
            }
        });

        kakaoGirl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked == true){
                    CheckBox joinCheckBoxMen = (CheckBox) findViewById(R.id.kakaoMen);
                    joinCheckBoxMen.setChecked(false);
                    gender="여자";
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}
