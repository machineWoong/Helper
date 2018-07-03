package com.example.jeon.helper.askHelp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class showMyAskHelp extends Activity implements OnMapReadyCallback {


    Integer editAskHelpREQUEST = 2222;

    // 뷰페이저 관련
    ViewPager viewPager;
    showMyAskHelpSliderAdater imageSliderAdapter;
    ArrayList<String> images = new ArrayList<>();  // 받아온 데이터를 넣을 곳.

    // 인텐트로 받은 데이터
    String userId;
    String loginMode;
    String title;
    String date;
    String key;

    // 서버로 받을 데이터
    ArrayList<showMyAskHelpShowMyContent> dataArray = new ArrayList<>();
    showMyAskHelpShowMyContent dataContent;

    // 서버에서 온 데이터
    String allData;
    String[] firstFilter; // 첫번째 구분자
    String[] seccondFilter;  // 두번째 구분자

    String[] ImageFilter;  // 이미지 구분자


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
        setContentView(R.layout.activity_show_my_ask_help);


        // 인텐트데이터 받아오기
        getIntentData();


        // 확인 버튼
        Button show_Add_Ask_Help_okBtn = (Button)findViewById(R.id.show_Add_Ask_Help_okBtn);
        show_Add_Ask_Help_okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 수정 버튼
        Button show_Add_Ask_Help_editBtn = (Button)findViewById(R.id.show_Add_Ask_Help_editBtn);
        show_Add_Ask_Help_editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEditAskHelp();
            }
        });



    }

    // (수정버튼)
    public void gotoEditAskHelp(){

        if( dataArray.get(0).state.equals("0")){

            // 돌아올때는 메인으로 돌아온다.
            Intent gotoEditAskHelp = new Intent(this,editAskHelp.class);
            // 배열을 담아서 보내겟어
            gotoEditAskHelp.putExtra("preData",dataArray);
            gotoEditAskHelp.putExtra("userId",userId);
            gotoEditAskHelp.putExtra("loginMode",loginMode);
            startActivityForResult(gotoEditAskHelp,editAskHelpREQUEST);
        }else{
            Toast.makeText(this, "이미 지원자가 있는 상태에서는 \n수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2222){
            if(resultCode ==RESULT_OK){
                // 수정한거 받아서  -> 메인으로 전달
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    //---------------------------------------데이터 처리 ------------------------------------------

    // 인텐트로 데이터 가지고 오기
    public void getIntentData() {
        userId = getIntent().getStringExtra("userId");
        loginMode = getIntent().getStringExtra("loginMode");
        title = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        key = getIntent().getStringExtra("key");

        Log.d("키값은 ",key);

        // 서버 연결
        getAskHelpData();
    }

    // 서버 연결
    // ( 서버 연결 : 데이터 받기  )
    public void getAskHelpData() {

        class getAddHelpDataToHttp extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog = new ProgressDialog(showMyAskHelp.this);
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

                    URL url = new URL(ipad+"/getAskHelpDataForMyShow.php");
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
                    buffer.append("loginMode").append("=").append(loginMode).append("&");
                    buffer.append("userId").append("=").append(userId).append("&");
                    buffer.append("title").append("=").append(title).append("&");
                    buffer.append("key").append("=").append(key).append("&");
                    buffer.append("date").append("=").append(date);

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
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }


                allData = result;
                // 결과값이 0 이면 데이터 없음
                Log.d("데이터 가지고 오는데 성공했느냐 ?", allData);
                try {
                    if (!allData.equals("0")) {
                        //( 객체생성 및 어레이 추가 ) 쪼개기
                        divideStringForAskHelp(allData);
                    }

                } catch (Exception e) {

                }
            }
        }
        getAddHelpDataToHttp getAddHelp = new getAddHelpDataToHttp();
        getAddHelp.execute();

    }

    // (객체 생성 및 Array 추가 및 데이터 세팅 ) 가지고 온 모든 데이터 쪼개기
    public void divideStringForAskHelp(String data) {
        firstFilter = data.split("%"); // 게시글 별로 나누기 위함
        for (int i = 0; i < firstFilter.length; i++) {
            seccondFilter = firstFilter[i].split("@");
            dataContent = new showMyAskHelpShowMyContent(seccondFilter[0], seccondFilter[1], seccondFilter[2],
                    seccondFilter[3], seccondFilter[4], seccondFilter[5], seccondFilter[6], seccondFilter[7], seccondFilter[8],
                    seccondFilter[9], seccondFilter[10],seccondFilter[11],seccondFilter[12],seccondFilter[13]); // 객체 생성

            dataArray.add(dataContent); // 어레이 리스트에 객체 추가.
        }

        // 데이터 세팅
        setWigetData();

        // 이미지 경로 쪼개서 이미지 배열에 넣기  구분자는 ! 이미지가 없는경우는 0이 들어오게된다.
        //이미지 존재 여부 확인 및 뷰페이저 설정
        if (dataArray.get(0).photo.equals("없음")) {
            // 이미지 없음 뷰페이저가 있는 레이아웃 숨김
            LinearLayout show_Add_Ask_Help_ViewPagerLayout = (LinearLayout)findViewById(R.id.show_Add_Ask_Help_ViewPagerLayout);
            show_Add_Ask_Help_ViewPagerLayout.setVisibility(View.GONE);

        } else {
            // 이미지가 있음 나눠서 이미지 리스트에 넣어준다,
            ImageFilter = dataArray.get(0).photo.split("!");
            for (int i = 0; i < ImageFilter.length; i++) {
                images.add(ImageFilter[i]);
                Log.d("이미지 경로 변환", ImageFilter[i]);
                Log.d("이미지 배열 갯수 ", "" + images.size());
            }
            // 뷰페이저 세팅
            viewPager = (ViewPager) findViewById(R.id.show_Add_Ask_Help_ViewPager);
            imageSliderAdapter = new showMyAskHelpSliderAdater(this, images,images.size());
            viewPager.setAdapter(imageSliderAdapter);
        }


        //맵 이 있는지 확인
        if (dataArray.get(0).meetting.equals("0") && dataArray.get(0).mission.equals("0")){
            // 지도 정보가 없는 경우
            LinearLayout show_Add_Ask_Help_Map_Layout = (LinearLayout)findViewById(R.id.show_Add_Ask_Help_Map_Layout);
            show_Add_Ask_Help_Map_Layout.setVisibility(View.GONE);

            LinearLayout showMyAskHelpBtnLayout = (LinearLayout)findViewById(R.id.showMyAskHelpBtnLayout);
            showMyAskHelpBtnLayout.setVisibility(View.GONE);

        }else{
            // 지도 정보가 있는경우 마커를 찾아서 찍어주도록 한다.
            // 여기에서는 지도 정보 변경이 되지 않도록!
            mapSetting();

            // 맵 마커 세팅  ( 도착지 구분자 , 미션지 구분자 , 와 / )

            if(  !dataArray.get(0).meetting.equals("0")){
                // 도착지 세팅
                meetingMapFilter = dataArray.get(0).meetting.split(",");
                meettingLatLng = new LatLng(Double.parseDouble(meetingMapFilter[0]),Double.parseDouble(meetingMapFilter[1]));
            }

            if ( !dataArray.get(0).mission.equals("0")){
                // 미션지 세팅
                firstMissionMapFilter = dataArray.get(0).mission.split("/");
                for ( int i = 0 ; i< firstMissionMapFilter.length ;i++){
                    seccondMissionMapFilter = firstMissionMapFilter[i].split(",");
                    LatLng missionLL = new LatLng (Double.parseDouble(seccondMissionMapFilter[0]),Double.parseDouble(seccondMissionMapFilter[1]));
                    missionLatLng.add(missionLL);
                }
            }
        }
    }

    // 데이터 세팅
    public void setWigetData(){
        TextView showForMyTitle = (TextView)findViewById(R.id.show_Add_Ask_Help_Title);
        TextView show_Add_Ask_Help_Date = (TextView)findViewById(R.id.show_Add_Ask_Help_Date); // 시작 기간
        TextView show_Add_Ask_Help_Date2 = (TextView)findViewById(R.id.show_Add_Ask_Help_Date2); // 종료 기간
        ImageView show_Add_Ask_Help_Gender =(ImageView)findViewById(R.id.show_Add_Ask_Help_Gender);// 성별이미지
        TextView show_Add_Ask_Help_helper = (TextView)findViewById(R.id.show_Add_Ask_Help_helper); // 모집인원

        TextView show_Add_Ask_Help_Money = (TextView)findViewById(R.id.show_Add_Ask_Help_Money); // 수고비
        TextView show_Add_Ask_Help_Location = (TextView)findViewById(R.id.show_Add_Ask_Help_Location); // 지역
        TextView show_Add_Ask_Help_Address = (TextView)findViewById(R.id.show_Add_Ask_Help_Address); // 상세 주소

        TextView showMyAskHelpContent = (TextView)findViewById(R.id.showMyAskHelpContent);
        showMyAskHelpContent.setText(dataArray.get(0).content);

        showForMyTitle.setText(dataArray.get(0).title);
        show_Add_Ask_Help_Date.setText(dataArray.get(0).sdate);
        show_Add_Ask_Help_Date2.setText(dataArray.get(0).edate);

        //성별
        if (dataArray.get(0).gender.equals("남자")){
            show_Add_Ask_Help_Gender.setImageResource(R.drawable.man);
        }else if (dataArray.get(0).gender.equals("여자")){
            show_Add_Ask_Help_Gender.setImageResource(R.drawable.girl);
        }else{
            show_Add_Ask_Help_Gender.setImageResource(R.drawable.genderdefault);
        }

        show_Add_Ask_Help_helper.setText(dataArray.get(0).helper);

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(dataArray.get(0).pay)).toString();

        show_Add_Ask_Help_Money.setText(userHaveCashFormal);
        show_Add_Ask_Help_Location.setText(dataArray.get(0).location);
        show_Add_Ask_Help_Address.setText(dataArray.get(0).address);

    }



    //-------------------------------------- 지도 관련 ---------------------------------------------
    // 맵 프래그먼트 생성
    public void mapSetting(){
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.show_Add_Ask_Help_Map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // 지도 마커세팅 및  화면 잡아주기
        setMarker(googleMap);


        // 수행지
        LinearLayout ShowMyAskmissionLayout = (LinearLayout)findViewById(R.id.ShowMyAskmissionLayout);
        ShowMyAskmissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMissionMarker(googleMap);
            }
        });


        // 도착지
        LinearLayout ShowMyAskmeetingLayout = (LinearLayout)findViewById(R.id.ShowMyAskmeetingLayout);
        ShowMyAskmeetingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMeetingMarker(googleMap);
            }
        });




    }

    // 마커 클릭이벤트
    public void showMeetingMarker(GoogleMap google){
        try {
            if( meettingLatLng != null){
                google.moveCamera(CameraUpdateFactory.newLatLngZoom(meettingLatLng, 16));
            }else{
                Toast.makeText(this, "도착지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
        }
    }

    public void showMissionMarker(GoogleMap google){
        try{
            if ( missionLatLng.get(missionCount)!= null){
                google.moveCamera(CameraUpdateFactory.newLatLngZoom(missionLatLng.get(missionCount), 16));
                missionCount++;

                if (missionCount == missionLatLng.size()){ // 어레이리스트의 크기만큼 반복하기 위함.
                    missionCount = 0;
                }
            }else{
                Toast.makeText(this, "수행지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                missionCount = 0;
            }
        }catch (Exception e){
        }
    }

    // 지도 보여주기 + 마커찍기
    public void setMarker(GoogleMap google){
        // 도착지 보여주기
        if (meettingLatLng != null){
            google.moveCamera(CameraUpdateFactory.newLatLngZoom(meettingLatLng, 14));
        }else {
            // 미션지 보여주기
            google.moveCamera(CameraUpdateFactory.newLatLngZoom(missionLatLng.get(0), 14));
        }

        // 마커 찍기
        if( meettingLatLng != null ){
            google.addMarker(new MarkerOptions().position(meettingLatLng).title("도착지")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.meeting_marker)));
        }
        if(missionLatLng != null || missionLatLng.size() > 0){
            for ( int i = 0 ; i < missionLatLng.size() ; i++){
                // 마커 찍어주기
                google.addMarker(new MarkerOptions().position(missionLatLng.get(i)).title("수행지")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mission_marker)));
            }
        }
    }
}
