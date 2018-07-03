package com.example.jeon.helper.helpList;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.giveHelp.giveHelpContent;
import com.example.jeon.helper.giveHelp.showGiveHelp;
import com.example.jeon.helper.giveHelp.showGiveHelpAdapter;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;
import com.example.jeon.helper.selectMenu;
import com.example.jeon.helper.userAccount;
import com.example.jeon.helper.userPage.userPageMainActivity;
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

public class showDetailContent extends AppCompatActivity implements OnMapReadyCallback {

    ip ip = new ip();
    String ipad = ip.getIp();
    String key;
    String loginId;
    String makerId;


    // 게시물 값
    showDetailContentContent sDCC;
    String[] Filter;

    // 뷰페이저 관련
    ViewPager viewPager;
    showDetailPagerAdapter showDetailPagerAdapter;
    ArrayList<String> images = new ArrayList<>();  // 받아온 이미지 데이터를 넣을 곳.


    // 서버에서 온 데이터
    String allData;


    // 구글맵
    ArrayList<LatLng> missionLatLng = new ArrayList<LatLng>(); //미션지 배열
    LatLng meettingLatLng; // 도착지

    String[] meetingMapFilter;
    String[] firstMissionMapFilter;
    String[] seccondMissionMapFilter;

    int missionCount = 0;

    boolean existMap = false;
    boolean existImage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helplist_show_detail_content);


        // 정보가져오기
        getDataKey();
        getAskHelpData();


        // 버튼
        Button showDetailOkBtn = (Button)findViewById(R.id.showDetailOkBtn);
        showDetailOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getDataKey(){
        key = getIntent().getStringExtra("dataContentNumber");
        loginId = getIntent().getStringExtra("loginId");
        makerId = getIntent().getStringExtra("makerId");
        Log.d("가져온 키값",key);
    }

    // ( 서버 연결 )

    // (서버 연결) 닉네임에 따른 작성자 정보 가져오기
    public void getAskHelpData() {

        class getAddHelpDataToHttp extends AsyncTask<Void, Void, String> {
            ProgressDialog dialog = new ProgressDialog(showDetailContent.this);

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

                    URL url = new URL(ipad + "/showDetailContentGetData.php");
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
                Log.d("showDetail 값",""+result);
                divideData(result);

            }
        }
        getAddHelpDataToHttp getAddHelp = new getAddHelpDataToHttp();
        getAddHelp.execute();
    }

    // 쪼개기
    public void divideData(String s){

        // 기본 데이터 쪼개기
        Filter = s.split("@");
        sDCC = new showDetailContentContent(Filter[0],Filter[1],Filter[2],Filter[3],Filter[4],Filter[5],Filter[6],Filter[7]
                ,Filter[8],Filter[9],Filter[10],Filter[11],Filter[12],Filter[13],Filter[14],Filter[15],Filter[16],Filter[17]);

        // 지도 쪼개기
        if(sDCC.mission == null || sDCC.mission.equals("없음") || TextUtils.isEmpty(sDCC.mission)){
            // 미션지가 없음
        }else{
            firstMissionMapFilter = sDCC.mission.split("/");
            for (int i = 0; i < firstMissionMapFilter.length; i++) {
                seccondMissionMapFilter = firstMissionMapFilter[i].split(",");
                LatLng missionLL = new LatLng(Double.parseDouble(seccondMissionMapFilter[0]), Double.parseDouble(seccondMissionMapFilter[1]));
                missionLatLng.add(missionLL);
            }
            existMap = true;
        }

        // Meetting 랏랭 설정
        if (sDCC.meetting.equals("null") || sDCC.meetting.equals("없음") || TextUtils.isEmpty(sDCC.meetting) ) {
            // 도착지가 없음
        }else{
            Log.d("지도너 어떻게 들어오는데??///",sDCC.meetting+"//");
            meetingMapFilter = sDCC.meetting.split(",");
            meettingLatLng = new LatLng(Double.parseDouble(meetingMapFilter[0]), Double.parseDouble(meetingMapFilter[1]));
            existMap = true;
        }


        // 이미지 쪼개기
        if(sDCC.imagePath==null ||sDCC.imagePath.equals("없음")  || TextUtils.isEmpty(sDCC.imagePath) ){
            //이미지가 없음
        }else{
            // 이미지가 있어서 쪼개서 넣어야 함.
            String[] arr = sDCC.imagePath.split("!");
            for (int i = 0; i < arr.length; i++) {
                images.add(arr[i]);
            }
            existImage = true;
        }

        setWidgets();
    }

    // 위젯 세팅
    public void setWidgets(){
        //제목
        TextView showDetailGiveHelpTitle =(TextView)findViewById(R.id.showDetailGiveHelpTitle);
        showDetailGiveHelpTitle.setText(sDCC.title);
        //시작일, 종료일
        TextView showDetailGiveHelpSdate =(TextView)findViewById(R.id.showDetailGiveHelpSdate);
        showDetailGiveHelpSdate.setText(sDCC.sDate);
        TextView showDetailGiveHelpEdate =(TextView)findViewById(R.id.showDetailGiveHelpEdate);
        showDetailGiveHelpEdate.setText(sDCC.eDate);

        // 모집인원
        TextView showDetailGiveHelpHelper =(TextView)findViewById(R.id.showDetailGiveHelpHelper);
        showDetailGiveHelpHelper.setText(sDCC.helper);

        // 수고비 **
        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(sDCC.pay)).toString();

        TextView showDetailGiveHelpPay =(TextView)findViewById(R.id.showDetailGiveHelpPay);
        showDetailGiveHelpPay.setText(userHaveCashFormal);

        //지역
        TextView showDetailGiveHelpLocation =(TextView)findViewById(R.id.showDetailGiveHelpLocation);
        showDetailGiveHelpLocation.setText(sDCC.location);

        //상세주소
        TextView showDetailGiveHelpAddress =(TextView)findViewById(R.id.showDetailGiveHelpAddress);
        showDetailGiveHelpAddress.setText(sDCC.address);

        //내용
        TextView showDetailGiveHelpContent =(TextView)findViewById(R.id.showDetailGiveHelpContent);
        showDetailGiveHelpContent.setText(sDCC.content);

        //작성자 아이디
        TextView showDetailGiveHelpUserIdNickName =(TextView)findViewById(R.id.showDetailGiveHelpUserIdNickName);
        showDetailGiveHelpUserIdNickName.setText(sDCC.makeUserNick);

        //작성자 지역
        TextView showDetailGiveHelpUserLocation =(TextView)findViewById(R.id.showDetailGiveHelpUserLocation);
        showDetailGiveHelpUserLocation.setText(sDCC.makeUserLocation);

        //작성자 점수
        TextView showDetailGiveHelpUserAskPoint =(TextView)findViewById(R.id.showDetailGiveHelpUserAskPoint);
        int reasult = Integer.parseInt(sDCC.makeAskPoint)+Integer.parseInt(sDCC.makeGivePoint);
        if (reasult <= 10) {
            showDetailGiveHelpUserAskPoint.setText("보통");
        } else if (reasult > 11 && reasult >= 20) {
            showDetailGiveHelpUserAskPoint.setText("우수");
            showDetailGiveHelpUserAskPoint.setTextColor(Color.BLUE);
        }

        // 모집 성별
        if (sDCC.gender.equals("남자")) {
            ImageView showDetailGiveHelpGender = (ImageView)findViewById(R.id.showDetailGiveHelpGender);
            Glide.with(this).load(R.drawable.man).into(showDetailGiveHelpGender);
        } else if (sDCC.gender.equals("여자")){
            ImageView showDetailGiveHelpGender = (ImageView)findViewById(R.id.showDetailGiveHelpGender);
            Glide.with(this).load(R.drawable.girl).into(showDetailGiveHelpGender);
        }


        // 이미지 슬라이더
        if ( existImage == false){
            // 없는거니까 레이아웃을 보내버린다.
            LinearLayout showDetailGiveHelpViewPagerLayout = (LinearLayout)findViewById(R.id.showDetailGiveHelpViewPagerLayout);
            showDetailGiveHelpViewPagerLayout.setVisibility(View.GONE);
        }else{
            // 뷰페이저 어댑터 설정
            ImageViewPager();
        }

        // 지도
        if( existMap == false){
            // 없는거니까 레이아웃을 보내버린다.
            LinearLayout showDetailGiveHelpMapBtnLayout = (LinearLayout)findViewById(R.id.showDetailGiveHelpMapBtnLayout);
            LinearLayout showDetailGiveHelpMapLayout = (LinearLayout)findViewById(R.id.showDetailGiveHelpMapLayout);
            showDetailGiveHelpMapBtnLayout.setVisibility(View.GONE);
            showDetailGiveHelpMapLayout.setVisibility(View.GONE);
        }else{
            // 지도 위치 찍어주기 프래그먼트 설정
            mapSetting();
        }


        // 작성자 프로필
        if(sDCC.makeUserProfile.equals("없음") ){
        }else{
            if(sDCC.makeUserProfile.contains("http://k.kakaocdn.net")){
                ImageView showDetailGiveHelpUserProfileImage = (ImageView)findViewById(R.id.showDetailGiveHelpUserProfileImage);
                Glide.with(this).load(sDCC.makeUserProfile).into(showDetailGiveHelpUserProfileImage);
            }else{
                ImageView showDetailGiveHelpUserProfileImage = (ImageView)findViewById(R.id.showDetailGiveHelpUserProfileImage);
                Glide.with(this).load(ipad+"/"+sDCC.makeUserProfile).into(showDetailGiveHelpUserProfileImage);
            }
        }


        // 작성자 성별
        if (sDCC.makeUserGender.equals("남자")) {
            ImageView showDetailGiveHelpUserGender = (ImageView)findViewById(R.id.showDetailGiveHelpUserGender);
            Glide.with(this).load(R.drawable.man).into(showDetailGiveHelpUserGender);
        } else if (sDCC.makeUserGender.equals("여자")){
            ImageView showDetailGiveHelpUserGender = (ImageView)findViewById(R.id.showDetailGiveHelpUserGender);
            Glide.with(this).load(R.drawable.girl).into(showDetailGiveHelpUserGender);
        }


        // 유저 정보 자세히 보기
        LinearLayout showDetailMakeUserLayout = (LinearLayout)findViewById(R.id.showDetailMakeUserLayout);
        showDetailMakeUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoShowUserPage = new Intent(showDetailContent.this,selectMenu.class);
                gotoShowUserPage.putExtra("targetId",makerId);// 대상
                gotoShowUserPage.putExtra("loginUserId",loginId); // 접속한 아이디
                startActivity(gotoShowUserPage);
            }
        });
    }

    // --------------------------- 뷰페이저 관련 ----------------------------------------
    // 뷰페이저 실행
    public void ImageViewPager() {
        // 뷰페이저 세팅
        viewPager = (ViewPager) findViewById(R.id.show_Detail_Add_Ask_Help_ViewPager);
        showDetailPagerAdapter = new showDetailPagerAdapter(this, images);
        viewPager.setAdapter(showDetailPagerAdapter);
    }

    // -------------------------------지도 관련 --------------------------------------------

    public void mapSetting() {
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.showDetailGiveHelpMap);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
// 지도 마커세팅 및  화면 잡아주기
        setMarker(googleMap);


        // 수행지
        LinearLayout ShowDetailGiveHelpMissionLayout = (LinearLayout) findViewById(R.id.ShowDetailGiveHelpMissionLayout);
        ShowDetailGiveHelpMissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMissionMarker(googleMap);
            }
        });


        // 도착지
        LinearLayout ShowDetailGiveHelpMeetingLayout = (LinearLayout) findViewById(R.id.ShowDetailGiveHelpMeetingLayout);
        ShowDetailGiveHelpMeetingLayout.setOnClickListener(new View.OnClickListener() {
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
