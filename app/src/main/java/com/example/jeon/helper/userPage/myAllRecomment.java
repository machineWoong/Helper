package com.example.jeon.helper.userPage;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import java.util.ArrayList;

public class myAllRecomment extends AppCompatActivity {

    String loginUserId;
    String targetId;

    // ip
    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    // 댓글
    ArrayList<commentData> commentArr = new ArrayList<>();
    commentData cd;
    myAllRecoAdapter cA;

    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_all_recomment);
        getUserData();
    }


    public void getUserData(){
        loginUserId = getIntent().getStringExtra("loginUserId");
        targetId = getIntent().getStringExtra("targetId");
        getRepleData();
    }

    // 댓글 받아오기
    public void getRepleData(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {
            String result2;
            ProgressDialog dialog = new ProgressDialog(myAllRecomment.this);
            String userId;
            public getRepleDataHttp(String userId){
                this.userId = targetId;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("회원정보를 불러오는 중");
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
                    URL url = new URL(ipad+"/getCommentDataAll.php");
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
                    buffer.append("id").append("=").append(userId);                 // php 변수에 값 대입

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
                    result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d("헬프리스트 메인 ",""+result);

                return result2;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){

                }
                Log.d("댓글 정보들 ",""+s);
                if(s.equals("없음")){
                    // 댓글이 없는경우는 없다고 텍스트 표시
                    TextView userPageNoRipleText = (TextView)findViewById(R.id.userPageNoRipleText);
                    userPageNoRipleText.setVisibility(View.VISIBLE);
                }else{

                    divideGetCommentData(s);
                }


            }
        }

        getRepleDataHttp getData = new getRepleDataHttp(targetId);
        getData.execute();
    }

    // 댓글 데이터 쪼개기
    public void divideGetCommentData(String result){
        firstFilter = null;
        seccondFilter = null;
        commentArr.clear();

        firstFilter = result.split("@"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("\\+");

            // 로그인 모드, URL , 유저 닉네임 순서,
            cd = new commentData(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                    seccondFilter[3],seccondFilter[4],seccondFilter[5],Integer.parseInt(seccondFilter[6]),seccondFilter[7]); // 객체 생성

            commentArr.add(cd);
        }


        setAdapter();

    }

    public void setAdapter(){
        RecyclerView view = (RecyclerView)findViewById(R.id.allReco);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        cA = new myAllRecoAdapter(commentArr,this,loginUserId);
        view.setAdapter(cA);
    }

}
