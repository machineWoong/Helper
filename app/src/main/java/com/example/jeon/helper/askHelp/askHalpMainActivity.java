package com.example.jeon.helper.askHelp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.bestHelper;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class askHalpMainActivity extends Activity {

    String loginMode;
    String userId;
    String userNickName;

    // 새로만들기로 한 이후에 결과 코드
    Integer addAskHelpREQUEST = 1111;
    Integer editAskHelpREQUEST = 2222;

    askHelpContent dataContent; //데이터가 있는경우 객체를 만들어서 array에 저장
    ArrayList<askHelpContent> dataArray = new ArrayList<>();
    String allDat=null;
    String allDat2=null;


    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자


    RecyclerView view;
    askHelpAdapter aHA;

    Boolean test = false;


    ip ip = new ip();
    String ipad = ip.getIp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시계창 없애기
        setContentView(R.layout.activity_ask_halp_main);





        // 유저 정보 가져오기
        getUserInfo();

        TextView gotoSetAskHelpTextView = (TextView)findViewById(R.id.gotoSetAskHelpTextView);
        ImageView gotoSetAskHelpAddBtn = (ImageView)findViewById(R.id.gotoSetAskHelpAddBtn);

        gotoSetAskHelpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(askHalpMainActivity.this,addAskHelp.class);
                intent.putExtra("loginMode",loginMode);
                intent.putExtra("loginUserId",userId);
                intent.putExtra("loginUserNickName",userNickName);
                startActivityForResult(intent,addAskHelpREQUEST);
            }
        });

        gotoSetAskHelpAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(askHalpMainActivity.this,addAskHelp.class);
                intent.putExtra("loginMode",loginMode);
                intent.putExtra("loginUserId",userId);
                intent.putExtra("loginUserNickName",userNickName);
                startActivityForResult(intent,addAskHelpREQUEST);
            }
        });


//        Log.d("askHelp 메인","유저 정보 ( loginMode ): "+loginMode);
//        Log.d("askHelp 메인","유저 정보 ( userId ): "+userId);
//        Log.d("askHelp 메인","유저 정보 ( userNickName ): "+userNickName);

        try {
            // 리스트뷰 뿌려주기
            getAskHelpData();
        }catch (Exception e){

        }

        // 권한체크
        requirePermission();
    }


    //---------------------------------------- 기본 데이터 생성-------------------------------------
    // 유저 정보가지고 오기
    public void getUserInfo(){
        Intent getMode = getIntent();
        loginMode =getMode.getExtras().getString("loginMode");
        userId=getMode.getExtras().getString("loginUserId");
        userNickName=getMode.getExtras().getString("loginUserNickName");
    }
    //---------------------------------------- 리스트 뿌려주기 --------------------------------------
    // ( 서버 연결 : 데이터 받기  )
    public void getAskHelpData(){

        dataArray.clear();

        class getAddHelpDataToHttp extends AsyncTask<Void,Void,String> {
            ProgressDialog dialog = new ProgressDialog(askHalpMainActivity.this);

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


                    URL url = new URL(ipad+"/getAskHelpData.php");
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
                    buffer.append("userId").append("=").append(userId);


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
                    allDat = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("999999999999999999",""+allDat);
                return allDat;
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

                allDat = result;
                // 결과값이 0 이면 데이터 없음
                try{
                    if( !allDat.equals("0")){
                        //( 객체생성 및 어레이 추가 ) 쪼개기
                        divideStringForAskHelp(allDat);
                    }else{
                    }
                }catch (Exception e){

                }

            }
        }
        getAddHelpDataToHttp getAddHelp = new getAddHelpDataToHttp();
        getAddHelp.execute();

    }
    // (객체 생성 및 Array 추가 ) 가지고 온 모든 데이터 쪼개기
    public void divideStringForAskHelp(String data){
        firstFilter = data.split("%"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("@");

            // 로그인 모드, URL , 유저 닉네임 순서,
            dataContent = new askHelpContent(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                            seccondFilter[3],seccondFilter[4],seccondFilter[5],seccondFilter[6],
                    seccondFilter[7],seccondFilter[8],seccondFilter[9],seccondFilter[10]); // 객체 생성

            dataArray.add(dataContent); // 어레이 리스트에 객체 추가.
        }
         setRecyclerView();
    }
    // ( 리사이클러 뷰 )
    public void setRecyclerView(){
        // 리사이클러 뷰
        view = (RecyclerView)findViewById(R.id.askHelpRecyclerView);

        // LinearLayoutManager는  수평/수직의 스크롤 리스트
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        // 어댑터를 연결 시켜주는 부분  ( 배열 + 갯수 )
        aHA = new askHelpAdapter(dataArray,this,userId,loginMode);
        view.setAdapter(aHA);

    }

    // -------------------------------- 새로운 데이터 등록 후 결과 --------------------------------
    // 데이터 추가하고 즉시 갱신하기 위해서.  ( 서버 연결 : 데이터 다시 받기 )
    public void getAskHelpData2(){

        class getAddHelpDataToHttp extends AsyncTask<Void,Void,String> {

            ProgressDialog dialog = new ProgressDialog(askHalpMainActivity.this);
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


                    URL url = new URL(ipad+"/getAskHelpData.php");
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
                    buffer.append("userId").append("=").append(userId);


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
                    allDat2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return allDat2;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dialog.dismiss();
                allDat2 = result;
                // 결과값이 0 이면 데이터 없음
                try{
                    if( !allDat2.equals("0")){
                        //( 객체생성 및 어레이 추가 ) 쪼개기
                        divideStringForAskHelp2(allDat2);
                    }else{
                    }
                }catch (Exception e){

                }

            }
        }
        getAddHelpDataToHttp getAddHelp = new getAddHelpDataToHttp();
        getAddHelp.execute();

    }
    public void divideStringForAskHelp2(String data){
        firstFilter = data.split("%"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("@");

            // 로그인 모드, URL , 유저 닉네임 순서,
            dataContent = new askHelpContent(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                    seccondFilter[3],seccondFilter[4],seccondFilter[5],seccondFilter[6],seccondFilter[7],seccondFilter[8],seccondFilter[9]
                    ,seccondFilter[10]); // 객체 생성

            dataArray.add(dataContent); // 어레이 리스트에 객체 추가.
        }
        setRecyclerView();
    }

    // 결과값 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //새로운 도움을 요청하고 돌아온 경우 (리스트를 리프래쉬 해준다 )
        if (requestCode == addAskHelpREQUEST){
            if (resultCode == 1111){ //저장한 경우 ( DB에 저장을 해야함 )
                Toast.makeText(this, "작성 완료", Toast.LENGTH_SHORT).show();
                dataArray.clear();
                getAskHelpData2();
                test = true;
            }
            else{
                Toast.makeText(this, "작성 취소", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == editAskHelpREQUEST){
            if ( resultCode == RESULT_OK){
                Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
                dataArray.clear();
                getAskHelpData2();
                test = true;
            }
            else{
            }
        }




    }

    // <<  권한 설정 : 현재위치 >>
    public void requirePermission() {
        String[] per = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        ArrayList<String> lper = new ArrayList<>();

        for (String pers : per) {
            if (ContextCompat.checkSelfPermission(this, pers) == PackageManager.PERMISSION_DENIED) {
                //권한이 허가가 안됬을경우 요청할 권한을 모집하는 부분
                lper.add(pers);
            }
        }

        if (!lper.isEmpty()) {
            // 권한 요청 하는 부분
            ActivityCompat.requestPermissions(this, lper.toArray(new String[lper.size()]), 1);
        }

    }

}
