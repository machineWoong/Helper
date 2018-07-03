package com.example.jeon.helper.giveHelp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.askHalpMainActivity;
import com.example.jeon.helper.askHelp.askHelpAdapter;
import com.example.jeon.helper.askHelp.askHelpContent;
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

public class giveHelpMainActivity extends AppCompatActivity {

    // 유저 정보
    String userId;
    String loginMode;
    String userNickName;
    String gender;

    // 데이터 가져오기
    ArrayList<giveHelpContent> giveData = new ArrayList<>();
    giveHelpContent gC;
    String allData;

    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자

    RecyclerView view;
    giveHelpAdapter gHA;

    // 검색 관련
    ArrayList<giveHelpContent> setLocationFilter = new ArrayList<>();
    ArrayList<giveHelpContent> setSearchKeyWordFilter = new ArrayList<>();
    String locationValue=null;

    ip ip = new ip();
    String ipad = ip.getIp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_help_main);

        // 유저 데이터 가져오기  ( 아이디 닉네임 로그인모드 )
        getUserData();
        //스피너 설정
        setSpinner();

        // 검색 필터링
        filterSpinner();
        getSearchKeyWord();
        try {
            getGiveHelpData();
        }catch (Exception e){
        }
    }

    //--------------------------------------데이터 관련 --------------------------------------------
    // 유저 데이터 가져오기  ( 아이디 닉네임 로그인모드 )
    public void getUserData(){
        loginMode = getIntent().getExtras().getString("loginMode");
        userId = getIntent().getExtras().getString("loginUserId");
        userNickName = getIntent().getExtras().getString("loginUserNickName");
        gender = getIntent().getExtras().getString("gender");


    }

    // 스피너 어댑터 설정하기.
    public void setSpinner() {
        //스피너 어댑터 설정
        Spinner giveHelpLocation = (Spinner) findViewById(R.id.giveHelpSpinner);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.giveHelpLocation, android.R.layout.simple_spinner_item);
        giveHelpLocation.setAdapter(yearAdapter);

    }

    // 서버 연결 ( 요청 데이터들 가지고 오기 )
    public void getGiveHelpData(){

        class getGiveHelpDataToHttp extends AsyncTask<Void,Void,String> {
            ProgressDialog dialog = new ProgressDialog(giveHelpMainActivity.this);

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
                    URL url = new URL(ipad+"/getGiveHelpData.php");
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
                    buffer.append("loginMode").append("=").append(loginMode).append("&");
                    buffer.append("userId").append("=").append(userId);

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
                try{
                    if( !allData.equals("0")){
                        //( 객체생성 및 어레이 추가 ) 쪼개기

                        Log.d("giveHelpMain 데이터 //",""+allData);
                        divideGiveData(allData);
                    }else{
                    }
                }catch (Exception e){
                }
            }
        }
        getGiveHelpDataToHttp getGiveHelp = new getGiveHelpDataToHttp();
        getGiveHelp.execute();

    }

    // 가져온 데이터 쪼개기
    public void divideGiveData(String allData){
        firstFilter = allData.split("%"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("@");

            // 로그인 모드, URL , 유저 닉네임 순서,
            gC = new giveHelpContent(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                    seccondFilter[3],seccondFilter[4],seccondFilter[5],seccondFilter[6],seccondFilter[7],seccondFilter[8],seccondFilter[9],seccondFilter[10],
                    seccondFilter[11],seccondFilter[12],seccondFilter[13],seccondFilter[14],seccondFilter[15],seccondFilter[16]); // 객체 생성

            giveData.add(gC); // 어레이 리스트에 객체 추가.
        }

       // Log.d("giveHelMain////",""+giveData.get(0).title);

        // 리사이클러 뷰 보여주기
       setRecyclerView(giveData);

    }

    // ( 리사이클러 뷰 )
    public void setRecyclerView(ArrayList<giveHelpContent> data){
        // 리사이클러 뷰
        view = (RecyclerView)findViewById(R.id.giveHelpRecyclerView);
        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        // 어댑터를 연결 시켜주는 부분  ( 배열 + 갯수 )
        gHA = new giveHelpAdapter(data,this,userId,gender,userNickName);
        view.setAdapter(gHA);
    }

    //------------------------------------검색, 스피너 이벤트 --------------------------------------
    // 스피너 필터링
    public void filterSpinner(){
        final Spinner giveHelpLocation = (Spinner) findViewById(R.id.giveHelpSpinner);

        giveHelpLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);


                locationValue = (String) giveHelpLocation.getSelectedItem();
                if (locationValue.equals("- 모든지역 -")){
                    // 아무것도 하지 않음
                    setLocationFilter.clear();
                    setRecyclerView(giveData);

                }else{
                    // 없음이 아닌 경우 다시 뿌려준다.
                    locationValue = giveHelpLocation.getSelectedItem().toString();
                    setLocationFilter.clear();

                    for ( int i = 0 ; i < giveData.size() ; i++){
                        if ( giveData.get(i).location.equals(locationValue) ){
                            setLocationFilter.add((giveData.get(i)));
                        }
                    }

                    // 리사이클러 뷰 다시 뿌려주기
                    try {
                        setRecyclerView(setLocationFilter);
                    }catch (Exception e){

                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    // 스피너 필터링 + 검색어 필터링
    public void getSearchKeyWord(){
        SearchView giveHelpSearch = (SearchView)findViewById(R.id.giveHelpSearch);
        giveHelpSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 입력한 검색어를 query로 받는다.
                // 스크롤 뷰 필터링이 없는 경우는 그냥 검색값만 가지고 찾고
                // 필터링이 있는 경우는 필터링 + 입력된값을 가지고 찾는다.

                if ( query.equals("")){
                    // 검색어가 없는 경우
                    Toast.makeText(giveHelpMainActivity.this, "값없음", Toast.LENGTH_SHORT).show();
                    setRecyclerView(giveData);
                }else if ( locationValue.equals("- 모든지역 -") || locationValue ==null ){
                    // 입력된 값으로만 찾기
                    setSearchKeyWordFilter.clear();
                    for ( int i = 0 ; i<giveData.size(); i++){
                        if (  giveData.get(i).title.contains(query) || giveData.get(i).content.contains(query)){
                            setSearchKeyWordFilter.add(giveData.get(i));
                        }
                    }
                    setRecyclerView(setSearchKeyWordFilter);
                }else{
                    // 스피너 값 + 입력된 값으로 찾기.
                    setSearchKeyWordFilter.clear();
                    for ( int i = 0 ; i < giveData.size(); i++){
                        if ( giveData.get(i).location.equals(locationValue)){
                            if ( giveData.get(i).title.equals(query) || giveData.get(i).content.contains(query)){
                                setSearchKeyWordFilter.add(giveData.get(i));
                            }
                        }
                    }
                    setRecyclerView(setSearchKeyWordFilter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // onQueryTextSubmit의  query에는 빈값이나 null을 받아들이지 않는다.
                // 그래서 텍스트 입력 변경시 공백인 경우 다시 onQueryTextSubmit 를 호출하면서 인자로 빈공백을 넣어준다.
                if(newText.equals("")){
                    this.onQueryTextSubmit("");
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3333){
            // 컨텐츠 보기
            if (resultCode == 1111){
                // 지원한 경우
                Toast.makeText(this, "지원 접수 완료", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == 2222){
                // 취소한 경우

            }

        }
    }
}
