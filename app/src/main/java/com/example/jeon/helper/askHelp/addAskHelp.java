package com.example.jeon.helper.askHelp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.showBigImage;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.graphics.Color.RED;

public class addAskHelp extends Activity {

    String loginMode;  // 1일반 로그인 2카카오 로그인
    String userId;
    String userNickName;
    String userHaveCash;

    Integer cameraCode = 1;
    Integer galleryCode = 2;
    Integer mapCode = 3;

    Integer getStardData = 1111;
    Integer getEndData= 2222;


    // 날짜 선택
    AlertDialog dialog;
    CalendarView calendarView;
    String sDate;
    String eDate;
    TextView startDate;
    TextView endDate;

    //이미지 배열
    ArrayList<String> imageArray = new ArrayList<String>();

    // 맵배열
    ArrayList<LatLng> missionLatLng = new ArrayList<LatLng>();

    // 수행지 직렬화 후 2개의 구분자를 가진 하나의 스트링
    String missonLatLngString ="";

    // 도착지 정보 ( 위치정보를 문자열로 저장 )
    String meettingLatLngString;


    // 이미지 경로
    String singleImageAbsoluteURL; //네이티브 겔러리에서 가져온 이미지 절대경로
    String multiImagerAbsoluteURL; //사진에서 가져온 다중 이미지 절대경로
    String cameraAbsoluteURL; // 카메라로 찍은 이미지 절대 경로.

    // 저장할 데이터 목록
    String sex = null;

    ip ip = new ip();
    String ipad = ip.getIp();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시계창 없애기
        setContentView(R.layout.activity_add_ask_help);


        //----------------------- 위젯 아이디 ---------------------------------

        ImageView locationMap = (ImageView) findViewById(R.id.locationMap); // 맵버튼


        startDate = (TextView)findViewById(R.id.startDate);
        endDate = (TextView)findViewById(R.id.endDate);

        ImageView askHelpCamera = (ImageView) findViewById(R.id.askHelpCamera); // 카메라 버튼
        ImageView askHelpGallery = (ImageView) findViewById(R.id.askHelpGallery); // 갤러리 버튼

        Button askHelpSaveBtn = (Button) findViewById(R.id.askHelpSaveBtn); // 저장버튼
        Button askHelpCancelBtn = (Button) findViewById(R.id.askHelpCancelBtn); // 취소버튼




        // ( 권한 체크 ) 카메라 권한 확인
        requirePermission();
        // 스피너 설정
        setSpinner();
        // (인텐트 )유저정보 받아오기 + // 사용자 보유중 캐쉬 가져오기
        getUserInfo();


        // 체크박스 중복 클릭 방지
        checkBoxEvent();


        //카메라 이동
        askHelpCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCamera();
            }
        });

        //갤러리 이동
        askHelpGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGallery();
            }
        });

        //맵 이동
        locationMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMap();
            }
        });

        // ---------------------------------------------- 버튼 이벤트 -----------------------------
        //(다이얼 로그 등장 ) 취소버튼
        askHelpCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noSaveBackDialog();
            }
        });

        // 저장버튼  ( 데이터 유효성 체크 )
        askHelpSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAskHelpData();
                //Toast.makeText(addAskHelp.this, ""+userNickName, Toast.LENGTH_SHORT).show();
            }
        });

        // ( 이미지 버튼 롱클릭 : 삭제 )
        setImageLongClickEvent();
        // ( 이미지 버튼 숏크릭 : 확대 )
        setImageShortClickEvent();


        //날자 선택 이벤트
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSdate();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdate();
            }
        });
    }
    // --------------------------------------- 날짜 선택 이벤트 --------------------------------
    public void setSdate(){
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(addAskHelp.this);
        View mView = getLayoutInflater().inflate(R.layout.calendar_view,null);

        calendarView = (CalendarView)mView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                sDate = String.valueOf(month+1)+"월"+ String.valueOf(dayOfMonth)+"일";
            }
        });

        TextView select_date_Title = (TextView)mView.findViewById(R.id.select_date_Title);
        select_date_Title.setText("시작 날짜 선택");


        // 날자 설정 버튼
        Button setSdataBtn = (Button)mView.findViewById(R.id.select_date);
        setSdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate.setText(sDate);
                dialog.dismiss();     //닫기
            }
        });

        Button cancel_date = (Button)mView.findViewById(R.id.cancel_date);
        cancel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDate = null;
                dialog.dismiss();     //닫기
            }
        });



        aBuilder.setView(mView);
        dialog = aBuilder.create();
        dialog.show();

    }

    public void setEdate(){
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(addAskHelp.this);
        View mView = getLayoutInflater().inflate(R.layout.calendar_view,null);

        calendarView = (CalendarView)mView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                eDate = String.valueOf(month+1)+"월"+ String.valueOf(dayOfMonth)+"일";
            }
        });

        TextView select_date_Title = (TextView)mView.findViewById(R.id.select_date_Title);
        select_date_Title.setText("종료 날짜 선택");


        // 날자 설정 버튼
        Button setSdataBtn = (Button)mView.findViewById(R.id.select_date);
        setSdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDate.setText(eDate);
                dialog.dismiss();     //닫기
            }
        });

        Button cancel_date = (Button)mView.findViewById(R.id.cancel_date);
        cancel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eDate = null;
                dialog.dismiss();     //닫기
            }
        });

        aBuilder.setView(mView);
        dialog = aBuilder.create();
        dialog.show();

    }



    //-----------------------------------------화면 구성 관련 ----------------------------------
    // (이미지 리스트 ) 사용자가 올린 이미지를 미리보기로 뿌려줌
    public void showImageList() {

        int listCount = imageArray.size();

        ImageView[] arr = new ImageView[5];
        arr[0] = (ImageView) findViewById(R.id.userImage1);
        arr[1] = (ImageView) findViewById(R.id.userImage2);
        arr[2] = (ImageView) findViewById(R.id.userImage3);
        arr[3] = (ImageView) findViewById(R.id.userImage4);
        arr[4] = (ImageView) findViewById(R.id.userImage5);

        for (int i = 0; i < 5; i++) {
            if ( i < listCount ){
                Glide.with(this).load(imageArray.get(i)).into(arr[i]);
                setImageViewScale(arr[i]);
                arr[i].setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else{
                arr[i].setImageResource(R.drawable.empty);
            }
        }

    }
    // (인텐트)유저정보 가지고 오기  +  유저 소지금액 보여주기(getUserHaveCash)
    public void getUserInfo() {
        loginMode = getIntent().getExtras().getString("loginMode");
        userId = getIntent().getExtras().getString("loginUserId");
        userNickName = getIntent().getExtras().getString("loginUserNickName");

       // Toast.makeText(this, ""+loginMode, Toast.LENGTH_SHORT).show();

        getUserHaveCash();

    }
    //( 서버 연결 : 사용자 보유 캐쉬 가져오기 )
    public void getUserHaveCash() {

        class getHaveCashToHttp extends AsyncTask<Void, Void, String> {

            String id;
            String loginMode;


            public getHaveCashToHttp(String id, String loginMode) {
                this.id = id;
                this.loginMode = loginMode;
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------


                    URL url = new URL(ipad+"/userAccount.php");
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
                    buffer.append("id").append("=").append(userId).append("&");                 // php 변수에 값 대입
                    buffer.append("loginMode").append("=").append(loginMode).append("&");
                    buffer.append("userNickName").append("=").append(userNickName).append("&");
                    buffer.append("accountMode").append("=").append("1");


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
                    userHaveCash = builder.toString();                       // 전송결과를 전역 변수에 저장
                    // Log.d("bbbbbbbbbbbbbbbb", "aaaaaaaaaaaa/////" + loginUserNickName);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return userHaveCash;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                userHaveCash = result;

                Log.d("gggggggggggggggg", "gggggggggggggggg//" + userHaveCash);

                String userHaveCashFormal;

                DecimalFormat dc = new DecimalFormat("###,###,###,###");
                userHaveCashFormal = dc.format(Double.parseDouble(result)).toString();
                TextView userHaveCashTV = (TextView) findViewById(R.id.userHaveCash); // 유저 소지금액
                userHaveCashTV.setText(userHaveCashFormal);
                userHaveCashTV.setTextColor(RED);

            }

        }
        getHaveCashToHttp getNickName = new getHaveCashToHttp(userId, loginMode);
        getNickName.execute();
    }
    // 스피너 어댑터 설정하기.
    public void setSpinner() {
        //스피너 어댑터 설정
        Spinner joinLocation = (Spinner) findViewById(R.id.askHelpLocationSpinner);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        joinLocation.setAdapter(yearAdapter);

    }
    // 엑티비티 전체의 크기 구하기  ( 이미지 크기 비율 맞추기 위함 )
    public void setImageViewScale(ImageView img){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

       // ImageView img = (ImageView) findViewById(R.id.imgView);
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) img.getLayoutParams();
        params.width =  metrics.widthPixels/5;
        params.height = metrics.widthPixels/5;

        img.setLayoutParams(params);
    }

    // ---------------------------------------- 저장 관련----------------------------------------
    // 저장 버튼 클릭 이벤트
    public void saveAskHelpData(){
        EditText askHelpTilte = (EditText) findViewById(R.id.askHelpTilte); // 제목
        // 성별  변수명 sex
        Spinner askHelpLocation = (Spinner) findViewById(R.id.askHelpLocationSpinner); // 지역
        EditText locationAddress = (EditText) findViewById(R.id.locationAddress);  // 상세주소
        EditText askHelpPay = (EditText) findViewById(R.id.askHelpPay); // 수고비
        // 지도 경로들  수행지 missonLatLngString , 도착지  meettingLatLngString
        EditText askHelpContent = (EditText) findViewById(R.id.askHelpContent); // 내용

        TextView startDate = (TextView)findViewById(R.id.startDate);
        TextView endDate = (TextView) findViewById(R.id.endDate);
        EditText helpcount = (EditText)findViewById(R.id.helpcount);

        // 이미지들을 서버에 전송하면서 이름을 리네임한후 저장 한다.
        String title = askHelpTilte.getText().toString();
        // String sex
        String location = askHelpLocation.getSelectedItem().toString();
        String address = locationAddress.getText().toString();
        String pay = askHelpPay.getText().toString();
        String content = askHelpContent.getText().toString();
        String sDate = startDate.getText().toString();
        String eDate = endDate.getText().toString();
        String helper = helpcount.getText().toString();

        // 네트워크 연결 체크
        Boolean netConnect = netStateCheck();
        // 유효성이나 null 체크

        // 소지금 비교
        int a = Integer.parseInt(userHaveCash);
        int b = Integer.parseInt(pay);

        try{
            int c = Integer.parseInt(helper);
            b = b*c;
        }catch (Exception e){

        }

        if (a >= b){  // 소지금이 보수 보다 크거나 같다면 ( 거래 가능 )
            if( TextUtils.isEmpty(sex) || TextUtils.isEmpty(title)||TextUtils.isEmpty(sDate)|| TextUtils.isEmpty(helper)||TextUtils.isEmpty(eDate) || location.equals("---- 선택 ----")|| TextUtils.isEmpty(address) ||
                    TextUtils.isEmpty(pay) || TextUtils.isEmpty(content) || sDate.equals("시작일(월/일)") || eDate.equals("종료일(월/일)")){
                Toast.makeText(this, "입력되지 않은 데이터가 있습니다.", Toast.LENGTH_SHORT).show();
            }else{
                if (netConnect == true){
                    // ( 서버 전송 ) 이미지와 텍스트 데이터들 을 전달
                    postDBimageNdata(loginMode,userId,userNickName,title,sDate,eDate,sex,helper,location,address,pay,content,missonLatLngString,meettingLatLngString,imageArray);

                }else{
                    Toast.makeText(this, "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (b <= 0){
            Toast.makeText(this, "수고비는 0원 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "보유중인 캐쉬가 부족합니다.", Toast.LENGTH_SHORT).show();
        }
    }
    // 네트워크 상태 체크 연결 :  서버 호출 메소드 실행
    public Boolean netStateCheck(){
        NetworkInfo mNetState = getNetworkInfo();
        if ( mNetState != null){
            // 네트워크에 연결이 되어있다면?
            return true;
        }else{
            Toast.makeText(this, "네트워크와 연결이 되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    // 서버 호출 ( 데이터 전송  데이터 + 이미지 )
    public void postDBimageNdata(String loginMode, String id,String nick, String title,String sdate,String edate,String sex ,String helper,String location, String address, String pay,
                          String content,String mission,String meetting,ArrayList<String> imageArray)  {
        class addAskHelpDB extends AsyncTask<Void, Void, Void> {
            String loginMode,id,nick,title,sdate,edate,sex,location,address,pay,content,mission,meetting,helper;
            ArrayList<String> imageArray = new ArrayList<String>();


            ProgressDialog dialog = new ProgressDialog(addAskHelp.this);

            public addAskHelpDB(String loginMode, String id, String nick,String title,String sdate,String edate,String sex ,String helper,String location, String address, String pay,
                                String content,String mission,String meetting,ArrayList<String> imageArray ){

                this.loginMode = loginMode;  // 1 일반 로그인 2 카카오 로그인
                this.id = id;
                this.nick = nick;
                this.title = title;
                this.sdate = sdate;
                this.edate = edate;
                this.sex = sex;
                this.helper = helper;
                this.location =location;
                this.address = address;
                this.pay = pay;
                this.content= content;
                this.mission = mission;
                this.meetting = meetting;
                this.imageArray = imageArray;

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

            // 쓰레드
            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    HttpURLConnection conn = null;
                    DataOutputStream dos = null;
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1 * 1024 * 1024;


                    URL url = new URL(ipad+"/addAskHelp.php");



                    // Open a HTTP  connection to  the URL

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                    // 텍스트 데이터들
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"loginMode\"\r\n\r\n" + loginMode);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"userId\"\r\n\r\n" + userId);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"nick\"\r\n\r\n" + URLEncoder.encode(nick,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // 인코딩 -> PHP -> DB - > 안드로이드
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"title\"\r\n\r\n" +  URLEncoder.encode(title,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"sDate\"\r\n\r\n" +  URLEncoder.encode(sdate,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"eDate\"\r\n\r\n" +  URLEncoder.encode(edate,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"sex\"\r\n\r\n" + URLEncoder.encode(sex,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"helpcount\"\r\n\r\n" + URLEncoder.encode(helper,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"local\"\r\n\r\n" + URLEncoder.encode(location,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"localAddress\"\r\n\r\n" + URLEncoder.encode(address,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"pay\"\r\n\r\n" + pay);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"missionLocation\"\r\n\r\n" + mission);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"meettingLocation\"\r\n\r\n" + meetting);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"content\"\r\n\r\n" + URLEncoder.encode(content,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // PHP 에서 반복문을 사용하기 위하여 이미지 갯수를 센다.
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"imageCount\"\r\n\r\n" + imageArray.size());
                    wr.writeBytes("\r\n--" + boundary + "\r\n");


                    // 파일의 존재 유무 확인 후 ( 파일이 없는 경우  그냥 지나간다 )
                    // 반복문으로 파일을 보낸다.
                    if( imageArray.size() > 0){

                        for ( int i = 0 ; i < imageArray.size(); i++){
                            String a = String.valueOf(i);

                            File sourceFile = new File(imageArray.get(i));
                            FileInputStream fileInputStream = new FileInputStream(sourceFile);

                            //php단에서 $_FILES['uploaded_file'] 에  아래의  filename=""+ imageArray.get(i) 이들어간다
                            // 여러개를 보낼때 주의 사항은  $_FILES['uploaded_file']의  'uploaded_file' 는 키값처럼들어가는데
                            // 중복되는 경우 마지막 데이터만 전송됨으로  아래에서는 반복문의 i 값을 string으로 변환하여 구분을 주었다.
                            // php 단에서도 구분지어서 받아야 한다.
                            dos = new DataOutputStream(conn.getOutputStream());
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file"+a+"\";filename=\""+ imageArray.get(i) + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);

                            // create a buffer of  maximum size
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        }
                    }


                    wr.flush();


//
//                    //--------------------------
//                    //   서버에서 전송받기
//                    //--------------------------
//                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
//                    BufferedReader reader = new BufferedReader(tmp);
//                    StringBuilder builder = new StringBuilder();
//                    String str;
//                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
//                        builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
//                    }
//                    String myResult = builder.toString();                       // 전송결과를 전역 변수에 저장
//
//                    Log.d("bbbbbbbbbbbbbbbb","aaaaaaaaaaaa/////"+myResult);

                    DataInputStream is = null;
                    BufferedReader in = null;

                    is = new DataInputStream(conn.getInputStream());
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                    String line = null;
                    StringBuffer buff = new StringBuffer();
                    while ( ( line = in.readLine() ) != null )
                    {
                        buff.append(line + "\n");
                    }
                    String test = buff.toString().trim();
                   // test = URLDecoder.decode(test,"utf-8");

                    Log.d("00000000000",test);



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }
                setResult(1111);
                finish();
            }
        }
        addAskHelpDB gotoDBUerId = new addAskHelpDB(loginMode,userId,userNickName,title,sdate,edate,sex,helper,location,address,pay,content,missonLatLngString,meettingLatLngString,imageArray);
        gotoDBUerId.execute();

    }
    //-----------------------------------------이동 이벤트 --------------------------------------

    //(카메라로 이동)
    public void gotoCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.jeon.helper.askHelp.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, cameraCode);
        } catch (IOException ex) {
            // Error occurred while creating the File
        }

    }

    //(갤러리로 이동)
    public void gotoGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);  // 암시적 인텐트 사용.

        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setType("image/*");
        gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(gallery, "단일 선택 : 갤러리   |   다중 선택 : 사진"), galleryCode);


//        // 네이티브 갤러리에서 이미지 다중선택 가능
//        Intent intent = new Intent("android.intent.action.MULTIPLE_PICK");
//        //intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),galleryCode);


    }

    //------------------------------ 체크박스 ( 성별 ) 중복 처리 이벤트 ------------------

    public void checkBoxEvent() {
        CheckBox addHelpMan = (CheckBox) findViewById(R.id.addHelpMan); // 남자
        CheckBox addHelpGirl = (CheckBox) findViewById(R.id.addHelpGirl); // 여자
        CheckBox addHelpEvery = (CheckBox) findViewById(R.id.addHelpEvery); // 성별무관


        addHelpMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    CheckBox addHelpGirl = (CheckBox) findViewById(R.id.addHelpGirl); // 여자
                    addHelpGirl.setChecked(false);
                    CheckBox addHelpEvery = (CheckBox) findViewById(R.id.addHelpEvery); // 성별무관
                    addHelpEvery.setChecked(false);
                    sex = "남자";
                }
            }
        });

        addHelpGirl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    CheckBox addHelpMan = (CheckBox) findViewById(R.id.addHelpMan); // 남자
                    addHelpMan.setChecked(false);
                    CheckBox addHelpEvery = (CheckBox) findViewById(R.id.addHelpEvery); // 성별무관
                    addHelpEvery.setChecked(false);
                    sex = "여자";
                }
            }
        });

        addHelpEvery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    CheckBox addHelpMan = (CheckBox) findViewById(R.id.addHelpMan); // 남자
                    addHelpMan.setChecked(false);
                    CheckBox addHelpGirl = (CheckBox) findViewById(R.id.addHelpGirl); // 여자
                    addHelpGirl.setChecked(false);
                    sex = "무관";
                }

            }
        });

    }

    //----------------------------------------카메라 갤러리 처리 --------------------------------
    // ( 절대경로 : 리스트 : 갤러리 ) 갤러리에서 가져온 이미지 절대경로로 변환후 리스트에 저장
    public void exchangeImageAndAddList(Intent data) {

        // 갤러리로 단일 가지고 왔을때.
        Uri SingleImageUri = data.getData();
        // 사진으로 여러장 사진을 가지고 왔을때.
        ClipData clipData = data.getClipData();

        if (SingleImageUri != null) { // 싱글이미지 ( 비어있는 이미지를 확인해서 뿌려준다 /  비어있는 경우 배열에 담는다. / 배열에 담아서 뿌려줌)
            singleImageAbsoluteURL = getRealpath(SingleImageUri);

            if (imageArray.size() >= 5) {  // 리스트가 꽉찼다.
                Toast.makeText(this, "이미지는 최대 5장까지 입니다.", Toast.LENGTH_SHORT).show();
            } else {
                imageArray.add(singleImageAbsoluteURL);
            }

//            Log.d("단일 이미지 절대경로가 왔는가 ?","절대 경로 : "+singleImageAbsoluteURL);
//            Log.d("단일 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(0).toString());

        } else if (clipData != null) {// 멀티이미지 ( 비어있는 이미지를 확인해서 뿌려준다 / 비어있는 경우 배열에 담는다.)

            int getCount = clipData.getItemCount();

//                    if ( getCount > 5 || imageArray.size()+getCount > 5 ){
//                        Toast.makeText(this, "이미지는 최대 5장 입니다.", Toast.LENGTH_SHORT).show();
//                    }

            for (int i = 0; i < getCount; i++) {
                if (imageArray.size() < 5) {
                    //절대경로 로 변환
                    multiImagerAbsoluteURL = getRealpath(clipData.getItemAt(i).getUri());

                    // 어레이 리스트에 저장.
                    imageArray.add(multiImagerAbsoluteURL);
                }
            }
//                    Log.d("멀티 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(0).toString());
//                    Log.d("멀티 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(1).toString());
//                    Log.d("멀티 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(2).toString());
//                    Log.d("멀티 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(3).toString());
//                    Log.d("멀티 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(4).toString());
        }
    }

    // ( 절대경로 : 카메라 )
    // << 파일 >> 이미지 파일로 생성하는 부분
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        cameraAbsoluteURL = image.getAbsolutePath();
        return image;
    }

    //  << 저장 >>사진 파일 저장 ( 여기에서  리스트에 추가 )
    private void galleryAddPic() {    // 찍은 사진 앨범에 저장
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(cameraAbsoluteURL);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);


        Log.d("카메라를 찍고난 후 ", "경로인가요 ? : " + cameraAbsoluteURL);
        if (imageArray.size() < 5) { //배열에 추가
            imageArray.add(cameraAbsoluteURL);
        } else {
            Toast.makeText(this, "이미지는 최대 5장 까지입니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // << 절대 경로 >> 갤러리에서 가져온 이미지.
    public String getRealpath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }

    //-----------------------------------------------이미지 클릭 이벤트 -------------------------
    //( 숏클릭 ) : 이미지 확대 ( 엑티비티 전환 )
    public void setImageShortClickEvent() {
        ImageView userImage1 = (ImageView) findViewById(R.id.userImage1); // 사용자가 올린 이미지
        ImageView userImage2 = (ImageView)findViewById(R.id.userImage2); // 사용자가 올린 이미지
        ImageView userImage3 = (ImageView)findViewById(R.id.userImage3); // 사용자가 올린 이미지
        ImageView userImage4 = (ImageView)findViewById(R.id.userImage4); // 사용자가 올린 이미지
        ImageView userImage5 = (ImageView)findViewById(R.id.userImage5); // 사용자가 올린 이미지

        userImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( imageArray.get(0)!=null){
                        Intent gotoBigShowImage = new Intent(addAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",imageArray);
                        gotoBigShowImage.putExtra("imageNumber",0);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(addAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        userImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( imageArray.get(1)!=null){
                        Intent gotoBigShowImage = new Intent(addAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",imageArray);
                        gotoBigShowImage.putExtra("imageNumber",1);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(addAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        userImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( imageArray.get(2)!=null){
                        Intent gotoBigShowImage = new Intent(addAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",imageArray);
                        gotoBigShowImage.putExtra("imageNumber",2);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(addAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( imageArray.get(3)!=null){
                        Intent gotoBigShowImage = new Intent(addAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",imageArray);
                        gotoBigShowImage.putExtra("imageNumber",3);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(addAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        userImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if ( imageArray.get(4)!=null){
                        Intent gotoBigShowImage = new Intent(addAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",imageArray);
                        gotoBigShowImage.putExtra("imageNumber",4);
                        startActivity(gotoBigShowImage);
                    }

                }catch (Exception e){
                    Toast.makeText(addAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //( 롱클릭 ) :  삭제
    public void setImageLongClickEvent() {

        ImageView userImage1 = (ImageView) findViewById(R.id.userImage1); // 사용자가 올린 이미지
        ImageView userImage2 = (ImageView)findViewById(R.id.userImage2); // 사용자가 올린 이미지
        ImageView userImage3 = (ImageView)findViewById(R.id.userImage3); // 사용자가 올린 이미지
        ImageView userImage4 = (ImageView)findViewById(R.id.userImage4); // 사용자가 올린 이미지
        ImageView userImage5 = (ImageView)findViewById(R.id.userImage5); // 사용자가 올린 이미지

        userImage1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    imageArray.remove(0);
                    showImageList();
                }catch (Exception e){
                }

                return true;
            }
        });
        userImage2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    imageArray.remove(1);
                    showImageList();
                }catch (Exception e){
                }
                return true;
            }
        });

        userImage3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    imageArray.remove(2);
                    showImageList();
                }catch (Exception e){
                }
                return true;
            }
        });

        userImage4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                try {
                    imageArray.remove(3);
                    showImageList();
                }catch (Exception e){
                }
                return true;
            }
        });

        userImage5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                try {
                    imageArray.remove(4);
                    showImageList();
                }catch (Exception e){
                }
                return true;
            }
        });



    }

    // ----------------------------------------------맵 관련 이벤트 -------------------------------
    public void gotoMap(){
        if(TextUtils.isEmpty(missonLatLngString)&& TextUtils.isEmpty(meettingLatLngString)){
            // 새로 맵설정을 하는경우 이동
            Intent gotoMapSetting = new Intent(addAskHelp.this,addHelpMap.class);
            startActivityForResult(gotoMapSetting,mapCode);
        }else{
            // 수행지나 도착지중 데이터가 있는경우 데이터를 실어서 보낸다.
            Intent gotoMapSetting = new Intent(addAskHelp.this,addHelpMap.class);
            gotoMapSetting.putParcelableArrayListExtra("missonLatLng",missionLatLng);
            gotoMapSetting.putExtra("meettingLatLngString",meettingLatLngString);
            startActivityForResult(gotoMapSetting,mapCode);
        }

    }


    //---------------------------------권한 및 네트워크 연결 처리 ----------------------------------
    // <<  권한 설정  >>
    public void requirePermission() {
        String[] per = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ArrayList<String> lper = new ArrayList<>();

        for (String pers : per) {
            if (ContextCompat.checkSelfPermission(this, pers) == PackageManager.PERMISSION_DENIED) {
                //권한이 헉 가 안됬을경우 요청할 권한을 모집하는 부분
                lper.add(pers);
            }
        }

        if (!lper.isEmpty()) {
            // 권한 요청 하는 부분
            ActivityCompat.requestPermissions(this, lper.toArray(new String[lper.size()]), 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 카메라 결과
        if (requestCode == cameraCode) {
            if (resultCode == RESULT_OK) { //카메라를 찍은 경우
                // 방금찍은 카메라의 절대 경로를 가지고 온다.
                galleryAddPic();
            }
        }

        // 갤러리 결과
        if (requestCode == galleryCode) {
            // Log.d("갤러리를 가지고 온후  ","경로인가요 ?"+data.getData());
            if (resultCode == RESULT_OK) {
                // 갤러리에서 가져온 이미지 절대경로로 변환후 리스트에 저장
                exchangeImageAndAddList(data);
            }
        }

        // 맵 결과
        if ( requestCode == mapCode){
            if(resultCode == RESULT_OK){// 확인으로 해서 가지고 온것이라면  변수에 정보를 저장.
                missionLatLng = data.getParcelableArrayListExtra("MissionLocation");
                String latLngData = data.getStringExtra("MeettingLocation");

                if(missionLatLng.size() > 0){ // 수행지가 있다면
                    // 사이즈 크기만큼 반복을 하는데   마지막 ) 를 / 로 변환 할 것이다.
                    for ( int i = 0 ; i< missionLatLng.size() ; i++){
                        missonLatLngString = missonLatLngString+missionLatLng.get(i).toString().replace("lat/lng: (","");
                        missonLatLngString = missonLatLngString.replace(")","/");

                        Log.d("수행지 스트링 "," : "+missonLatLngString);
                    }

                }

                if(latLngData != null){  // 도착지가 있다면 ( 문자열에서 실수부분 즉 latlng만 추출 )

                    // 랏,랭 의 구조로만 남는다.
                    meettingLatLngString = latLngData.replace("lat/lng: (","");
                    meettingLatLngString = meettingLatLngString.replace(")","");
                }else if (latLngData == null){
                    meettingLatLngString = null;
                }

            }else{
                Toast.makeText(this, "위치저장 취소", Toast.LENGTH_SHORT).show();
            }
        }

        // 날짜 받아주기
        // 시작일
        if(requestCode == getStardData){
            if( resultCode == RESULT_OK){

            }
        }
        // 종료일
        if(requestCode == getEndData){
            if( resultCode == RESULT_OK){

            }
        }



        showImageList();

    }

    // 네트워크 연결 확인
    private NetworkInfo getNetworkInfo(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni;
    }


    // ( 다이얼 로그 )취소 버튼시
    // <<다이얼로그 >> 자료를 저장하지 않고  뒤로가기를 눌렀을때, 다이얼로그가 나오게 된다.
    public void noSaveBackDialog() {
        AlertDialog.Builder setBack = new AlertDialog.Builder(addAskHelp.this);

        setBack.setTitle("알림");
        setBack.setMessage("저장되지 않은 데이터가 있습니다. \n이 페이지를 벗어나시겠습니까?");

        setBack.setNegativeButton("Yes", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                //   overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_out_right);
            }
        });

        setBack.setPositiveButton("No", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setBack.show();
    }

    @Override
    public void onBackPressed() {
        noSaveBackDialog();
    }

}
