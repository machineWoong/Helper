package com.example.jeon.helper.giveHelp;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.helpList.showDetailContent;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.selectMenu;
import com.example.jeon.helper.userAccount;
import com.example.jeon.helper.userPage.userPageMainActivity;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class showGiveHelp extends AppCompatActivity implements OnMapReadyCallback {

    String loginUser;
    String userGender;
    String loginUserNick;
    Boolean preVolunteer = false;

    // 게시물 값
    ArrayList<giveHelpContent> giveHelpData = new ArrayList<>();
    int position;
    String helpContentKey;


    // 뷰페이저 관련
    ViewPager viewPager;
    showGiveHelpAdapter imageSliderAdapter;
    ArrayList<String> images = new ArrayList<>();  // 받아온 데이터를 넣을 곳.


    // 서버에서 온 데이터
    String allData;

    // 작성자 데이터
    public ArrayList<userAccount> makeUserArr = new ArrayList<>();


    // 구글맵
    ArrayList<LatLng> missionLatLng = new ArrayList<LatLng>(); //미션지 배열
    LatLng meettingLatLng; // 도착지

    String[] meetingMapFilter;
    String[] firstMissionMapFilter;
    String[] seccondMissionMapFilter;

    int missionCount = 0;


    ip ip = new ip();
    String ipad = ip.getIp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_give_help);

        // 데이터 가져오기
        getData();

        // 서버 연결 : 작성자 정보 가지고오기
        try {
            getAskHelpData();
        } catch (Exception e) {

        }

        // 버튼 이벤트
        //저장
        Button showGiveHelpApplyBtn = (Button) findViewById(R.id.showGiveHelpApplyBtn);
        showGiveHelpApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyBtn();
            }
        });

        //취소
        Button showGiveHelpCancel = (Button) findViewById(R.id.showGiveHelpCancel);
        showGiveHelpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        //신고
        ImageView showGiveHelpReport = (ImageView) findViewById(R.id.showGiveHelpReport);
        showGiveHelpReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( loginUser.equals(makeUserArr.get(0).userid)){
                    Toast.makeText(showGiveHelp.this, "본인 게시물은 신고할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    report();
                }
            }
        });


    }



    // ------------------------------------ 버튼 이벤트 ------------------------------------------

    //저장
    public void applyBtn() {
        // 도움요청 DB에 저장을 한다.
        // 토스트 메세지를 띄워준다.
        String key = helpContentKey;
        String applyUserID = loginUser;
        String gender = userGender;

        // 추가 예외처리 필요 .
        if (loginUser.equals(makeUserArr.get(0).userid)) {
            Toast.makeText(this, "본인 게시물에는 지원 할 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else if (preVolunteer == true) {
            Toast.makeText(this, "이미 지원했습니다.", Toast.LENGTH_SHORT).show();
        } else {
            if (giveHelpData.get(position).gender.equals(gender) || giveHelpData.get(position).gender.equals("무관")) {
                // 지원 가능
                // 담아 보낼 내용 게시글 번호 모집인원, 수고비, 지원한 아이디
                setApplyHelpDB(key, applyUserID, giveHelpData.get(position).helper, giveHelpData.get(position).pay);


                String dataString = loginUser+"@"+loginUserNick+"@"+makeUserArr.get(0).userid+"@"+giveHelpData.get(0).title+"@";

                //node 로 알림 전송
                //node 로 알림메세지를 위해 데이터 전송.
                GlobalApplication gg = (GlobalApplication)getApplication();
                gg.getSocket().emit("applyMessage",dataString);


            } else {
                Toast.makeText(this, "요청하는 성별과 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //( 서버 전송 ) 저장
    public void setApplyHelpDB(String key, String applyUserId, String helperCount, String pay) {
        class setApplyHelp extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog = new ProgressDialog(showGiveHelp.this);

            String key;
            String applyUserId;
            String helperCount;
            String pay;


            public setApplyHelp(String key, String applyUserId, String helperCount, String pay) {

                this.key = key;
                this.applyUserId = applyUserId;
                this.helperCount = helperCount;
                this.pay = pay;
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 저장중");
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
                    URL url = new URL(ipad + "/applyHelp.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------
                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST

                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("key").append("=").append(key).append("&");
                    buffer.append("helperCount").append("=").append(helperCount).append("&");
                    buffer.append("pay").append("=").append(pay).append("&");
                    buffer.append("applyUserId").append("=").append(applyUserId);

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
                    allData = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return allData;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }
                Log.d("지원하기 서버전송후 결과 ", "" + result);
                setResult(1111);
                finish();
            }
        }
        setApplyHelp setApplyHelp = new setApplyHelp(key, applyUserId, helperCount, pay);
        setApplyHelp.execute();
    }

    // ( 서버 전송 받기 ) 기존에 지원한 사람이라면 ? preVolunteer = true;
    public void searchApplyHelpDB(String loginUser) {
        class setApplyHelp extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog = new ProgressDialog(showGiveHelp.this);

            String loginUser;
            String key;

            public setApplyHelp(String loginUser, String key) {
                this.loginUser = loginUser;
                this.key = key;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 탐색중");
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
                    URL url = new URL(ipad + "/getApplyHelpData.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------
                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST

                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("loginUser").append("=").append(loginUser).append("&");
                    buffer.append("key").append("=").append(key);


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
                    allData = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return allData;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }


                if (result.equals("있음")) {
                    preVolunteer = true;
                } else {
                    preVolunteer = false;
                }

                Log.d("이전에 지원했던 사람인가요 ?", "" + preVolunteer);

            }
        }
        setApplyHelp setApplyHelp = new setApplyHelp(loginUser, helpContentKey);
        setApplyHelp.execute();
    }

    // 취소
    public void cancel() {
        finish();
    }

    //신고
    public void report() {
        // 신고시  다이얼로그를 띄워서  드롭다운 스피너를 넣는다.
        // 직접 입력란을 줄수도 있다.
        // DB에 전송한다. ( 신고관련된 DB와 유저정보에 관련된 DB에 넣는다 )
        // 토스트를 띄운다.


        // 빌더 설정
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(showGiveHelp.this);
        View reportView = getLayoutInflater().inflate(R.layout.report_content, null);

        // 제목설정
        aBuilder.setTitle("신고하기");

        final Spinner sp = (Spinner) reportView.findViewById(R.id.report_content_spinner);
        final EditText freeReport = (EditText) reportView.findViewById(R.id.report_EditText);

        // 스피너 어댑터 설정
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.report_item, android.R.layout.simple_spinner_item);
        sp.setAdapter(yearAdapter);


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 0 부터 시작임
                if (position == 6) {
                    freeReport.setVisibility(View.VISIBLE);
                } else {
                    freeReport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


// 확인 버튼 설정
        aBuilder.setNegativeButton("신고", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Text 값 받아서 로그 남기기
                String reportContent = sp.getSelectedItem().toString();
                if (reportContent.equals("----신고내용----") || reportContent.equals("")) {
                    Toast.makeText(showGiveHelp.this, "신고내용을 정해주세요", Toast.LENGTH_SHORT).show();
                } else if (reportContent.equals("직접입력")) {
                    reportContent = freeReport.getText().toString();
                    if (reportContent.equals("")) {
                        Toast.makeText(showGiveHelp.this, "신고내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        // 조건 만족 ( 서버 전송 )
                        sendReport(reportContent);
                        Toast.makeText(showGiveHelp.this, "신고접수", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();     //닫기
                    }
                } else {
                    // 조건 만족 ( 서버 전송 )
                    sendReport(reportContent);
                    Toast.makeText(showGiveHelp.this, "신고접수", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();     //닫기
                }
            }
        });

// 취소 버튼 설정
        aBuilder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });

// 창 띄우기
        aBuilder.setView(reportView);
        AlertDialog dialog = aBuilder.create();
        dialog.show();
    }


    // ( 서버 전송 ) 신고한 내용 DB에 저장   글번호  신고자/신고내용 구분자 %
    public void sendReport(String content) {

        class sendReportHttp extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog = new ProgressDialog(showGiveHelp.this);

            String content;
            public sendReportHttp(String content){
                this.content = content;
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

                    URL url = new URL(ipad + "/reportContent.php");
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
                    buffer.append("loginUser").append("=").append(loginUser).append("&");
                    buffer.append("contentNumber").append("=").append(giveHelpData.get(position).key).append("&");
                    buffer.append("content").append("=").append(content);

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
                    allData = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return allData;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }

                Log.d("신고 결과 ",""+result);

            }
        }
        sendReportHttp sendReport = new sendReportHttp(content);
        sendReport.execute();
    }


    //-------------------------------------데이터 관련 --------------------------------------------
    //데이터 불러오기
    public void getData() {
        giveHelpData = (ArrayList<giveHelpContent>) getIntent().getSerializableExtra("data");
        position = getIntent().getIntExtra("position", 0);
        loginUser = getIntent().getStringExtra("loginUser");
        userGender = getIntent().getStringExtra("gender");
        loginUserNick =  getIntent().getStringExtra("loginUserNick");
        // 미션지가 있는경우  , 와 / 를 구분자로 가진다
        // 미팅지가 있는 경우 , 로구분자를 가진다
        // 미션지가 없는 경우 ""  미팅지가 없는 경우 null  둘다 없는 경우 map 이 0
        // 이미지가 있는 경우 1 없는 경우 0

//        Log.d("ShowGiveHelpData//////", "" + giveHelpData.get(position).title);
//        Log.d("ShowGiveHelpData//////", "사진" + giveHelpData.get(position).imageUrl);
//        Log.d("ShowGiveHelpData//////", "사진경로 " + giveHelpData.get(position).imagePath);
//        Log.d("ShowGiveHelpData//////", "지도 " + giveHelpData.get(position).map);
//        Log.d("ShowGiveHelpData//////", "미션지 " + giveHelpData.get(position).mission);
//        Log.d("ShowGiveHelpData//////", "미팅지 " + giveHelpData.get(position).meetting);
//        Log.d("ShowGiveHelpData//////", "성별 " + giveHelpData.get(position).gender);
//        Log.d("ShowGiveHelpData//////", "닉네임 " + giveHelpData.get(position).nickName);

        helpContentKey = giveHelpData.get(position).key;
        // 데이터 뿌려주기
        setShowData();

        searchApplyHelpDB(loginUser);
    }

    // 데이터 뿌려주기
    public void setShowData() {

        TextView showGiveHelpTitle, showGiveHelpSdate, showGiveHelpEdate, showGiveHelpHelper, showGiveHelpPay, showGiveHelpLocation,
                showGiveHelpAddress, showGiveHelpContent;

        ImageView showGiveHelpGender;

        // 제목
        showGiveHelpTitle = (TextView) findViewById(R.id.showGiveHelpTitle);
        showGiveHelpTitle.setText(giveHelpData.get(position).title);

        //시작일 종료일
        showGiveHelpSdate = (TextView) findViewById(R.id.showGiveHelpSdate);
        showGiveHelpSdate.setText(giveHelpData.get(position).sDate);

        showGiveHelpEdate = (TextView) findViewById(R.id.showGiveHelpEdate);
        showGiveHelpEdate.setText(giveHelpData.get(position).eDate);

        // 인원수
        showGiveHelpHelper = (TextView) findViewById(R.id.showGiveHelpHelper);
        showGiveHelpHelper.setText(giveHelpData.get(position).helper);

        // 수고비

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(giveHelpData.get(position).pay)).toString();

        showGiveHelpPay = (TextView) findViewById(R.id.showGiveHelpPay);
        showGiveHelpPay.setText(userHaveCashFormal);

        // 지역
        showGiveHelpLocation = (TextView) findViewById(R.id.showGiveHelpLocation);
        showGiveHelpLocation.setText(giveHelpData.get(position).location);

        // 주소
        showGiveHelpAddress = (TextView) findViewById(R.id.showGiveHelpAddress);
        showGiveHelpAddress.setText(giveHelpData.get(position).address);

        // 내용
        showGiveHelpContent = (TextView) findViewById(R.id.showGiveHelpContent);
        showGiveHelpContent.setText(giveHelpData.get(position).content);


        // 성별 처리
        if (giveHelpData.get(position).gender.equals("남자")) {
            showGiveHelpGender = (ImageView) findViewById(R.id.showGiveHelpGender);
            Glide.with(this).load(R.drawable.man).into(showGiveHelpGender);

        } else if (giveHelpData.get(position).gender.equals("여자")) {
            showGiveHelpGender = (ImageView) findViewById(R.id.showGiveHelpGender);
            Glide.with(this).load(R.drawable.girl).into(showGiveHelpGender);
        }

        // 사진이 있으면 레이아웃을 보여주고 없으면 안보여준다 .
        LinearLayout showGiveHelpViewPagerLayout = (LinearLayout) findViewById(R.id.showGiveHelpViewPagerLayout);
        if (giveHelpData.get(position).imageUrl.equals("1")) {
            // 이미지 경로를 쪼개는 메소드
            divideStringImagePath();

            // 뷰페이저 세팅
            ImageViewPager();

        } else {
            showGiveHelpViewPagerLayout.setVisibility(View.GONE);
        }

        // 맵정보가 있으면 버튼과 맵 프래그먼트가 있는 레이아웃을 보여준다
        LinearLayout showGiveHelpMapBtnLayout = (LinearLayout) findViewById(R.id.showGiveHelpMapBtnLayout);
        LinearLayout showGiveHelpMapLayout = (LinearLayout) findViewById(R.id.showGiveHelpMapLayout);

        if (giveHelpData.get(position).map.equals("1")) {
            divideMissionString();
            mapSetting();
        } else {
            showGiveHelpMapBtnLayout.setVisibility(View.GONE);
            showGiveHelpMapLayout.setVisibility(View.GONE);
        }

    }

    // (서버 연결) 닉네임에 따른 작성자 정보 가져오기
    public void getAskHelpData() {

        class getAddHelpDataToHttp extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog = new ProgressDialog(showGiveHelp.this);

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

                    URL url = new URL(ipad + "/getAllUserAcount.php");
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
                    buffer.append("nickName").append("=").append(giveHelpData.get(position).nickName);

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
                    allData = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return allData;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }

                allData = result;
                try {
                    if (!allData.equals("0")) {
                        //( 객체생성 및 어레이 추가 ) 쪼개기
                        divideStringForGiveHelp(allData);
                    }
                } catch (Exception e) {

                }
            }
        }
        getAddHelpDataToHttp getAddHelp = new getAddHelpDataToHttp();
        getAddHelp.execute();
    }

    // 유저 정보
    public void divideStringForGiveHelp(String data) {

        Log.d("유저 정보 값 ", "" + data);

        String[] filterA = data.split("&");
        String[] filterB = null;
        for (int i = 0; i < filterA.length; i++) {
            filterB = filterA[i].split("!");
        }
        userAccount makeUser = new userAccount(filterB[0], filterB[1], filterB[2], filterB[3], filterB[4], filterB[5], filterB[6], filterB[7], filterB[8], filterB[9]
                , filterB[10], filterB[11]);

        makeUserArr.add(makeUser);
        // 작성자 정보 세팅
        Log.d("작성자의 성별입니다.", "" + makeUserArr.get(0).gender);

        setMakeUserContent();
    }

    // 작성자 정보 세팅
    public void setMakeUserContent() {


        TextView showGiveHelpUserIdNickName, showGiveHelpUserAskPoint, showGiveHelpUserLocation;
        ImageView showGiveHelpUserProfile, showGiveHelpUserGender;

        // 작성자 닉네임
        showGiveHelpUserIdNickName = (TextView) findViewById(R.id.showGiveHelpUserIdNickName);
        showGiveHelpUserIdNickName.setText(giveHelpData.get(position).nickName);

        // 작성자 지역
        showGiveHelpUserLocation = (TextView) findViewById(R.id.showGiveHelpUserLocation);
        showGiveHelpUserLocation.setText("지역 :" + makeUserArr.get(0).location);

        // 작성자 신용점수
        showGiveHelpUserAskPoint = (TextView) findViewById(R.id.showGiveHelpUserAskPoint);
        int setPoint = Integer.parseInt(makeUserArr.get(0).setHelpCount());
        if (setPoint <= 10) {
            showGiveHelpUserAskPoint.setText("보통");
        } else if (setPoint > 11 && setPoint >= 20) {
            showGiveHelpUserAskPoint.setText("우수");
            showGiveHelpUserAskPoint.setTextColor(Color.BLUE);
        }


        // 유저 성별

        if (makeUserArr.get(0).gender.equals("남자")) {
            showGiveHelpUserGender = (ImageView) findViewById(R.id.showGiveHelpUserGender);
            Glide.with(this).load(R.drawable.man).into(showGiveHelpUserGender);
        } else if (makeUserArr.get(0).gender.equals("여자")) {
            showGiveHelpUserGender = (ImageView) findViewById(R.id.showGiveHelpUserGender);
            Glide.with(this).load(R.drawable.girl).into(showGiveHelpUserGender);
        }

        // 작성자 이미지 프로필
        showGiveHelpUserProfile = (ImageView) findViewById(R.id.showGiveHelpUserProfileImage);
        // 경로가 1 이면 값이 없음으로 디폴트 이미지
        String imageResult = makeUserArr.get(0).imagePath();
        if (imageResult.equals("1")) {

        } else {
            Glide.with(this).load(imageResult).into(showGiveHelpUserProfile);
        }


        // 유저 정보 자세히 보기
        LinearLayout showGiveHelpUserLayout = (LinearLayout)findViewById(R.id.showGiveHelpUserLayout);
        showGiveHelpUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gotoShowUserPage = new Intent(showGiveHelp.this,selectMenu.class);
                gotoShowUserPage.putExtra("targetId",makeUserArr.get(0).userid);// 대상
                gotoShowUserPage.putExtra("loginUserId",loginUser); // 접속한 아이디
                startActivity(gotoShowUserPage);
            }
        });
    }

    //------------------------------------ 이미지 뷰페이저 -----------------------------------------
    // 뷰페이저 실행
    public void ImageViewPager() {
        // 뷰페이저 세팅
        viewPager = (ViewPager) findViewById(R.id.show_Add_Ask_Help_ViewPager);
        imageSliderAdapter = new showGiveHelpAdapter(this, images);
        viewPager.setAdapter(imageSliderAdapter);
    }

    //(ArrayList <String > ) 이미지 배열 쪼개서 리스트에 담기
    public void divideStringImagePath() {
        String imagePath = giveHelpData.get(position).imagePath.toString();
        Log.d("showGiveHelpImagePath", "/////" + imagePath);
        String[] arr = imagePath.split("!");

        for (int i = 0; i < arr.length; i++) {
            images.add(arr[i]);
        }
    }

    //------------------------------------ 맵관련 --------------------------------------------------
    public void divideMissionString() {
        // Mission 지도정보 쪼개주기
        if (!giveHelpData.get(position).mission.equals("")) {
            firstMissionMapFilter = giveHelpData.get(position).mission.split("/");
            for (int i = 0; i < firstMissionMapFilter.length; i++) {
                seccondMissionMapFilter = firstMissionMapFilter[i].split(",");
                LatLng missionLL = new LatLng(Double.parseDouble(seccondMissionMapFilter[0]), Double.parseDouble(seccondMissionMapFilter[1]));
                missionLatLng.add(missionLL);
            }
        }

        // Meetting 랏랭 설정
        try{
            if (giveHelpData.get(position).meetting != null) {
                meetingMapFilter = giveHelpData.get(position).meetting.split(",");
                meettingLatLng = new LatLng(Double.parseDouble(meetingMapFilter[0]), Double.parseDouble(meetingMapFilter[1]));
            }
        }catch (Exception e){

        }

    }

    // 맵 프래그먼트 생성
    public void mapSetting() {
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.showGiveHelpMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // 지도 마커세팅 및  화면 잡아주기
        setMarker(googleMap);


        // 수행지
        LinearLayout ShowMyAskmissionLayout = (LinearLayout) findViewById(R.id.ShowGiveHelpMissionLayout);
        ShowMyAskmissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMissionMarker(googleMap);
            }
        });


        // 도착지
        LinearLayout ShowMyAskmeetingLayout = (LinearLayout) findViewById(R.id.ShowGiveHelpMeetingLayout);
        ShowMyAskmeetingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMeetingMarker(googleMap);
            }
        });


    }

    // 마커 클릭이벤트
    public void showMeetingMarker(GoogleMap google) {
        try {
            if (meettingLatLng != null) {
                google.moveCamera(CameraUpdateFactory.newLatLngZoom(meettingLatLng, 16));
            } else {
                Toast.makeText(this, "도착지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    public void showMissionMarker(GoogleMap google) {
        try {
            if (missionLatLng.get(missionCount) != null) {
                google.moveCamera(CameraUpdateFactory.newLatLngZoom(missionLatLng.get(missionCount), 16));
                missionCount++;

                if (missionCount == missionLatLng.size()) { // 어레이리스트의 크기만큼 반복하기 위함.
                    missionCount = 0;
                }
            } else {
                Toast.makeText(this, "수행지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                missionCount = 0;
            }
        } catch (Exception e) {
        }
    }

    // 지도 보여주기 + 마커찍기
    public void setMarker(GoogleMap google) {
        // 도착지 보여주기
        if (meettingLatLng != null) {
            google.moveCamera(CameraUpdateFactory.newLatLngZoom(meettingLatLng, 14));
        } else {
            // 미션지 보여주기
            google.moveCamera(CameraUpdateFactory.newLatLngZoom(missionLatLng.get(0), 14));
        }

        // 마커 찍기
        if (meettingLatLng != null) {
            google.addMarker(new MarkerOptions().position(meettingLatLng).title("도착지")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.meeting_marker)));
        }
        if (missionLatLng != null || missionLatLng.size() > 0) {
            for (int i = 0; i < missionLatLng.size(); i++) {
                // 마커 찍어주기
                google.addMarker(new MarkerOptions().position(missionLatLng.get(i)).title("수행지")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mission_marker)));
            }
        }
    }



}
