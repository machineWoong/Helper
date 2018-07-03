package com.example.jeon.helper.userPage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class showMoreRiple extends AppCompatActivity {

    String loginUserid;
    String targetId;
    String commentNo;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();
    commentData perComment;



    // 대댓글 정보
    ArrayList<recomment_Data> recoData = new ArrayList<>();
    recomment_Data rD;
    recommentListAdapter rLAdapter;

    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자


    //댓글 수정
    String changeDataCommentContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpage_show_more_riple);

        // 데이터 받아오기
        getUserData();
    }

    // ------------------------------------------ 데이터 설정 ------------------------------------
    // 데이터 받아오기
    public void getUserData(){
        loginUserid = getIntent().getStringExtra("loginUserId");
        targetId= getIntent().getStringExtra("targetId");  // 지금 보고잇는 화면의 부모 댓글 작성자
        commentNo= getIntent().getStringExtra("commentNo");
        perComment = (commentData) getIntent().getSerializableExtra("perComment");

        setPerComment();
        getRecommentHttp();
    }

    // 부모 댓글 뿌려주기
    public void setPerComment(){

        ImageView show_more_reple_user_profile = (ImageView)findViewById(R.id.show_more_reple_user_profile);
        TextView show_more_reple_user_nickName = (TextView)findViewById(R.id.show_more_reple_user_nickName);
        TextView show_more_reple_comment_date = (TextView)findViewById(R.id.show_more_reple_comment_date);
        TextView show_more_reple_comment_Count = (TextView)findViewById(R.id.show_more_reple_comment_Count);
        TextView show_more_reple_comment_content = (TextView)findViewById(R.id.show_more_reple_comment_content);

        TextView show_more_reple_comment_edit = (TextView)findViewById(R.id.show_more_reple_comment_edit);
        TextView show_more_reple_comment_delete = (TextView)findViewById(R.id.show_more_reple_comment_delete);


        //이미지 처리
        if (perComment.makeProfile.equals("이미지 없음")) {
        } else {
            if (perComment.makeProfile.contains("http://k.kakaocdn.net")) {
                Glide.with(this).load(perComment.makeProfile).into(show_more_reple_user_profile);
            } else {
                Glide.with(this).load(ipad + "/" + perComment.makeProfile).into(show_more_reple_user_profile);
            }
        }

        if(perComment.makeContent.equals("삭제된 글입니다.")){
            show_more_reple_user_nickName.setText("");
        }else{

            // 닉네임
            show_more_reple_user_nickName.setText(perComment.makeNick);
        }


        // 날짜
        show_more_reple_comment_date.setText(perComment.makeDate);

        // 댓글 갯수
        show_more_reple_comment_Count.setText("("+perComment.count+")");

        // 내용
        show_more_reple_comment_content.setText(perComment.makeContent);


        // 버튼
        if(loginUserid.equals(perComment.makeId)){
            show_more_reple_comment_edit.setVisibility(View.VISIBLE);
            show_more_reple_comment_delete.setVisibility(View.VISIBLE);
        }else{
            show_more_reple_comment_edit.setVisibility(View.GONE);
            show_more_reple_comment_delete.setVisibility(View.GONE);
        }


        // 댓글 달기
        EditText show_more_make_reple = (EditText)findViewById(R.id.show_more_make_reple);
        show_more_make_reple.setHint(perComment.makeContent+"     -> 댓글달기");

        setRe_Recomment();
    }

    // 대댓글 받아오기
    public void getRecommentHttp(){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(showMoreRiple.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 불러오는 중");
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
                    URL url = new URL(ipad+"/getRecommentData.php");
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
                    buffer.append("commentNo").append("=").append(perComment.no);                 // php 변수에 값 대입

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
                    result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                Log.d("대댓글 페이지 정보 ",s);
                divideData(s);
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();
    }

    // 데이터 쪼개기
    public void divideData(String dataString){
        firstFilter = null;
        seccondFilter = null;
        recoData.clear();

        firstFilter = dataString.split("!"); // 게시글 별로 나누기 위함
        for ( int i = 0 ; i < firstFilter.length ; i++ ){
            seccondFilter = firstFilter[i].split("\\+");
            rD = new recomment_Data(seccondFilter[0],seccondFilter[1],seccondFilter[2],
                    seccondFilter[3],seccondFilter[4],Integer.parseInt(seccondFilter[5]),Integer.parseInt(seccondFilter[6])); // 객체 생성

            recoData.add(rD);
        }

        // 답글 수정
        editCommentData();

        // 답글 삭제
        setAdapter();
    }

    // 어댑터 설정하기
    public void setAdapter(){
        RecyclerView view = (RecyclerView)findViewById(R.id.show_more_reple_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(layoutManager);

        rLAdapter = new recommentListAdapter(recoData,this,loginUserid,targetId);
        view.setAdapter(rLAdapter);
    }

    // ------------------------------------------- 대댓 글 달기 ------------------------------------

    // 댓글 추가
    public void setRe_Recomment(){

        // 작성인 ID;
        // 수신인 ID;
        // 내용
        // 모드  1쓰기 2수정 3삭제
        // 코멘트 번호  -> 키값으로 들어갈것.

        ImageView make_reRecomment = (ImageView)findViewById(R.id.make_reRecomment);


        make_reRecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText show_more_make_reple = (EditText)findViewById(R.id.show_more_make_reple);
                String data = show_more_make_reple.getText().toString();

                if(data == null || data.equals("") || TextUtils.isEmpty(data)){
                    Toast.makeText(showMoreRiple.this, "추가할 댓글의 내용을 작성해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    // 서버전송 모드 1추가 2수정 3삭제
                    make_Re_recomment(data,1);
                    show_more_make_reple.setText(null);
                }
            }
        });




    }

    // (서버 전송 ) 대댓글 편집  ( 1답글추가 2대댓글 수정 3대댓글 삭제 )
    public void make_Re_recomment(String content,int mode){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(showMoreRiple.this);

            String loginUserId;
            String targetId;
            String content;
            int commentNo;
            int mode;


            public getRepleDataHttp(String loginUserId, String targetId, String content, int commentNo, int mode) {
                this.loginUserId = loginUserId;
                this.targetId = targetId;
                this.content = content;
                this.commentNo = commentNo;
                this.mode = mode;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 불러오는 중");
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
                    URL url = new URL(ipad+"/editModeForRe_Recomment.php");
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

                    // php 변수에 값 대입

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("senderId").append("=").append(loginUserId).append("&");
                    buffer.append("targetId").append("=").append(targetId).append("&");
                    buffer.append("content").append("=").append(content).append("&");
                    buffer.append("commentNo").append("=").append(commentNo).append("&");
                    buffer.append("mode").append("=").append(mode).append("&");


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
                    result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

                Log.d("대댓글 추가 작성",s);
                if(s.equals("답글등록")){
                    EditText show_more_make_reple = (EditText)findViewById(R.id.show_more_make_reple);
                    Toast.makeText(showMoreRiple.this, "댓글 등록 완료", Toast.LENGTH_SHORT).show();
                    // 키보드 숨기기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(show_more_make_reple.getWindowToken(), 0);


                    perComment.count = perComment.count+1;
                    // 데이터 재세팅
                    TextView show_more_reple_comment_Count = (TextView)findViewById(R.id.show_more_reple_comment_Count);
                    show_more_reple_comment_Count.setText("("+perComment.count+")");

                    changeData();
                }else{

                }
            }
        }
        getRepleDataHttp getData = new getRepleDataHttp(loginUserid,targetId,content,Integer.parseInt(perComment.no),mode);
        getData.execute();
    }

    // 데이터 재세팅 ( 이건 리사이클러뷰 아이템을 재세팅하는 것 )
    public void changeData(){
        recoData.clear();
        getRecommentHttp();
        setAdapter();
    }


    // -------------------------------------------  답글 수정 삭제 ---------------------------------
    // 답글 수정 삭제 버튼
    public void editCommentData(){
        // 수정 하기
        TextView show_more_reple_comment_edit = (TextView)findViewById(R.id.show_more_reple_comment_edit);

        show_more_reple_comment_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 레이아웃 보려주기
                LinearLayout re_recomment_edit_Comment = (LinearLayout)findViewById(R.id.re_recomment_edit_Comment);
                re_recomment_edit_Comment.setVisibility(View.VISIBLE);

                EditText re_recomment_editText = (EditText)findViewById(R.id.re_recomment_editText);
                re_recomment_editText.setText(perComment.makeContent);
            }
        });

        // 변경 버튼 버튼
        TextView re_comment_edit_commentEditBtn= (TextView)findViewById(R.id.re_comment_edit_commentEditBtn);
        re_comment_edit_commentEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText re_recomment_editText = (EditText)findViewById(R.id.re_recomment_editText);
                changeDataCommentContent = re_recomment_editText.getText().toString();

                if ( changeDataCommentContent.equals("") || changeDataCommentContent == null || TextUtils.isEmpty(changeDataCommentContent)){
                    Toast.makeText(showMoreRiple.this, "수정할 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    // 변경 이벤트
                    // 서버전송
                    setCommentHttp(2,perComment.no,loginUserid,targetId,changeDataCommentContent);
                    // EditText 비워주기
                    re_recomment_editText.setText(null);


                    // 레이아웃 숨기기
                    LinearLayout re_recomment_edit_Comment = (LinearLayout)findViewById(R.id.re_recomment_edit_Comment);
                    re_recomment_edit_Comment.setVisibility(View.GONE);
                }

            }
        });

        // 취소버튼
        TextView re_comment_edit__cancel = (TextView)findViewById(R.id.re_comment_edit__cancel);
        re_comment_edit__cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout re_recomment_edit_Comment = (LinearLayout)findViewById(R.id.re_recomment_edit_Comment);
                re_recomment_edit_Comment.setVisibility(View.GONE);
            }
        });


        // 삭제 버튼
        TextView show_more_reple_comment_delete = (TextView)findViewById(R.id.show_more_reple_comment_delete);
        show_more_reple_comment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletCommentData();
            }
        });


    }

    // 답글 삭제 버튼 다이얼로그
    public void deletCommentData(){

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("댓글 삭제");
        ab.setMessage("답글을 삭제하여도 댓글은 남아있습니다.\n등록하신 댓글을 삭제하시겠습니까?");

        ab.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ab.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setCommentHttp(3,perComment.no,"",targetId,"삭제된 글입니다.");


            }
        });

        ab.show();

    }

    // (서버 전송 댓글 수정 삭제 용 )
    // ( 서버 전송 )  모드 1 등록 2 수정 3 삭제
    public void setCommentHttp(int editMode,String commentNumber, String makeUserId, String targetId, String content){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;

            ProgressDialog dialog = new ProgressDialog(showMoreRiple.this);

            int editMode;  // 1  추가 . 2 수정 3 삭제
            String makeUserId;
            String targetId;
            String content;
            String commentNumber;

            public getRepleDataHttp(int editMode, String makeUserId, String targetId, String content,String commentNumber) {
                this.editMode = editMode;
                this.makeUserId = makeUserId;
                this.targetId = targetId;
                this.content = content;
                this.commentNumber = commentNumber;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                if( editMode == 1 || editMode == 2){
                    dialog.setMessage("답글을 저장하는 중");
                }else{
                    dialog.setMessage("저장하는 중");
                }
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
                    URL url = new URL(ipad+"/editModeForReComment.php");
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
                    buffer.append("editMode").append("=").append(editMode).append("&");                 // php 변수에 값 대입
                    buffer.append("makeUserId").append("=").append(makeUserId).append("&");
                    buffer.append("targetId").append("=").append(targetId).append("&");
                    buffer.append("content").append("=").append(content).append("&");
                    buffer.append("commentNumber").append("=").append(commentNumber);


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
                Log.d("답글 결과",""+s);

                if ( s.equals("답글등록")){
                    Toast.makeText(showMoreRiple.this, "답글 등록", Toast.LENGTH_SHORT).show();
                }else if (s.equals("댓글 수정")){
                    Toast.makeText(showMoreRiple.this, "댓글 수정", Toast.LENGTH_SHORT).show();
                    TextView show_more_reple_comment_content = (TextView)findViewById(R.id.show_more_reple_comment_content);

                    // 데이터 변경해서 보여주기
                    show_more_reple_comment_content.setText(changeDataCommentContent);
                    changeDataCommentContent = null;
                }else if (s.equals("댓글 삭제")){
                    Toast.makeText(showMoreRiple.this, "댓글 삭제", Toast.LENGTH_SHORT).show();

                    // 프로필 사진 없애고
                    ImageView show_more_reple_user_profile = (ImageView)findViewById(R.id.show_more_reple_user_profile);
                    show_more_reple_user_profile.setImageResource(R.drawable.kakao_default_profile_image);

                    // 작성자 없애고
                    TextView show_more_reple_user_nickName = (TextView)findViewById(R.id.show_more_reple_user_nickName);
                    show_more_reple_user_nickName.setText("");

                    // 내용 변경
                    TextView show_more_reple_comment_content = (TextView)findViewById(R.id.show_more_reple_comment_content);
                    show_more_reple_comment_content.setText("삭제된 글입니다.");


                }

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp(editMode,makeUserId,targetId,content,commentNumber);
        getData.execute();

    }

    //----------------------------------------- 어댑터 내부에서 사용할 메소드 ---------------------
    // 댓글수 변경
    public void minCount(){
        perComment.count = perComment.count-1;
        // 데이터 재세팅
        TextView show_more_reple_comment_Count = (TextView)findViewById(R.id.show_more_reple_comment_Count);
        show_more_reple_comment_Count.setText("("+perComment.count+")");
    }


}
