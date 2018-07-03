package com.example.jeon.helper.noti_table;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class make_noti_page extends AppCompatActivity {

    int mode;   // 모드가 1이면 작성,  2면 수정
    String loginUserId;
    String loginUserNick;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    String existImage;

    // 수정할 데이터
    noti_Content edtiContent;

    // 데이터들
    String title;
    String content;
    String imagePath;
    String notiMode;
    String makeTime;

    // 갤러리
    Integer GALLERY_CODE = 1111;


    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년! M월 d일!");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_noti_page);

        getUserData();

        Button make_noti_saveBtn = (Button)findViewById(R.id.make_noti_saveBtn);
        Button make_noti_cancel = (Button)findViewById(R.id.make_noti_cancel);

        // 저장버튼
        make_noti_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 만들지 않은 데이터 체크
                // 이미지가 없는경우 없음으로 저장.
                if ( mode == 1){
                    //작성인 경우
                    getDataCheck();
                }else if ( mode == 2){
                    // 수정인 경우
                    getDataCheck();

                }

            }
        });

        // 취소 버튼
        make_noti_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 체크박스 이벤트
        setCheckBoxEvent();

        // 이미지 클릭 이벤트
        imageClickEvent();

    }

    // 기본데이터 가지고오기
    public void getUserData(){
        mode  = getIntent().getIntExtra("mode",0);
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");

        if (mode == 2){
            // 수정이기때문에 기존 데이터들 가지고 오기
            edtiContent= (noti_Content) getIntent().getSerializableExtra("editData");
            Toast.makeText(this, "수정 하기", Toast.LENGTH_SHORT).show();

            // 데이터 재 세팅

            // 모드
            if( edtiContent.notiMode.equals("공지사항")){
                CheckBox make_noti_notiCheck = (CheckBox)findViewById(R.id.make_noti_notiCheck);
                make_noti_notiCheck.setChecked(true);
                notiMode ="공지사항";
            }else{
                CheckBox make_noti_eventCheck = (CheckBox)findViewById(R.id.make_noti_eventCheck);
                make_noti_eventCheck.setChecked(true);
                notiMode = "이벤트";
            }

            // 제목
            EditText make_noti_title = (EditText)findViewById(R.id.make_noti_title);
            make_noti_title.setText(edtiContent.title);

            // 내용
            EditText make_noti_content = (EditText)findViewById(R.id.make_noti_content);
            make_noti_content.setText(edtiContent.content);

            //이미지
            if(edtiContent.image.equals("없음")){
                ImageView make_noti_Image = (ImageView)findViewById(R.id.make_noti_Image);
                make_noti_Image.setImageResource(R.drawable.default_image);
                existImage = "없음";
                imagePath = "없음";
            }else{
                ImageView make_noti_Image = (ImageView)findViewById(R.id.make_noti_Image);
                Glide.with(this).load(ipad+"/"+edtiContent.image).into(make_noti_Image);
                imagePath = edtiContent.image;
                existImage ="있음";
            }

            // 버튼
            Button make_noti_saveBtn = (Button)findViewById(R.id.make_noti_saveBtn);
            make_noti_saveBtn.setText("수정/저장");
        }
    }

    // 작성한 데이터
    public void getDataCheck(){

        EditText make_noti_title = (EditText)findViewById(R.id.make_noti_title);
        EditText make_noti_content = (EditText)findViewById(R.id.make_noti_content);

        title = make_noti_title.getText().toString();
        content = make_noti_content.getText().toString();


        if(TextUtils.isEmpty(title) || title == null || title.equals("")
                || TextUtils.isEmpty(content) || content == null || content.equals("")){
            Toast.makeText(this, "제목 또는 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            if( TextUtils.isEmpty(imagePath) || imagePath == null || imagePath.equals("") || imagePath.equals("없음")){
                imagePath = "없음";
                existImage = "없음";
            }else{
                existImage = "있음";
            }

            if( TextUtils.isEmpty(notiMode) || notiMode == null || notiMode.equals("") ){
                Toast.makeText(this, "공지사항 종류를 선택해 주세요", Toast.LENGTH_SHORT).show();
            }else{

                // 모든 값을 서버에 전송하여 저장 하기 ..
                Log.d("공지사항 제목 ",""+title);
                Log.d("공지사항 내용 ",""+content);
                Log.d("공지사항 체크박스 ",""+notiMode);
                Log.d("공지사항 이미지 ",""+imagePath);



                // 현재 시간 받아오기
                long mNow;
                Date mDate;
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                makeTime = mFormat.format(mDate);

                //서버 전송
                if ( mode == 1){
                    setDB();
                }else if (mode == 2){
                    setDB2();
                }

            }
        }
    }

    // (작성) 서버 전송
    public void setDB(){

        // 서버 연동 클래스
        class notiDataHttp extends AsyncTask<Void, Void, Void> {
            ProgressDialog dialog = new ProgressDialog(make_noti_page.this);
            // 쓰레드
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

                    URL url = new URL(ipad + "/noti.php");

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
                    // 인코딩 -> PHP -> DB - > 안드로이드
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"makeNick\"\r\n\r\n" + URLEncoder.encode(loginUserNick, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"title\"\r\n\r\n" + URLEncoder.encode(title, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"content\"\r\n\r\n" + URLEncoder.encode(content, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"notiMode\"\r\n\r\n" + URLEncoder.encode(notiMode, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"mode\"\r\n\r\n" + URLEncoder.encode(String.valueOf(mode), "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"existImage\"\r\n\r\n" + URLEncoder.encode(String.valueOf(existImage), "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"makeTime\"\r\n\r\n" + URLEncoder.encode(String.valueOf(makeTime), "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");



                    // 이전에 이미지경로와 비교해서  디렉터리명으로 끈나지 않는경우만  파일로 보낸다

                    if( !imagePath.equals("없음") && imagePath != null){
                        File sourceFile = new File(imagePath);
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);

                        //php단에서 $_FILES['uploaded_file'] 에  아래의  filename=""+ imageArray.get(i) 이들어간다
                        // 여러개를 보낼때 주의 사항은  $_FILES['uploaded_file']의  'uploaded_file' 는 키값처럼들어가는데
                        // 중복되는 경우 마지막 데이터만 전송됨으로  아래에서는 반복문의 i 값을 string으로 변환하여 구분을 주었다.
                        // php 단에서도 구분지어서 받아야 한다.
                        dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file"+"\";filename=\"" + imagePath + "\"" + lineEnd);

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
                        wr.flush();
                    }

//                    //--------------------------
//                    //   서버에서 전송받기
//                    //--------------------------

                    DataInputStream is = null;
                    BufferedReader in = null;

                    is = new DataInputStream(conn.getInputStream());
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                    String line = null;
                    StringBuffer buff = new StringBuffer();
                    while ((line = in.readLine()) != null) {
                        buff.append(line + "\n");
                    }
                    String test = buff.toString().trim();
                    // test = URLDecoder.decode(test,"utf-8");

                    Log.d("공지사항 등록 결과 ", ""+test);


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
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }
                setResult(RESULT_OK);
                finish();
            }
        }
        notiDataHttp setNotiDB = new notiDataHttp();
        setNotiDB.execute();
    }

    public void setDB2(){

        // 서버 연동 클래스
        class notiDataHttp extends AsyncTask<Void, Void, Void> {
            ProgressDialog dialog = new ProgressDialog(make_noti_page.this);

            String a;
            // 쓰레드
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

                    URL url = new URL(ipad + "/noti_edit.php");

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
                    // 인코딩 -> PHP -> DB - > 안드로이드
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"makeNick\"\r\n\r\n" + URLEncoder.encode(loginUserNick, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"title\"\r\n\r\n" + URLEncoder.encode(title, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"content\"\r\n\r\n" + URLEncoder.encode(content, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"notiMode\"\r\n\r\n" + URLEncoder.encode(notiMode, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"no\"\r\n\r\n" + URLEncoder.encode(String.valueOf(edtiContent.no), "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"existImage\"\r\n\r\n" + URLEncoder.encode(String.valueOf(existImage), "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"makeTime\"\r\n\r\n" + URLEncoder.encode(String.valueOf(makeTime), "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"image\"\r\n\r\n" + URLEncoder.encode(imagePath, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");


                    if(edtiContent.image.equals(imagePath)) {
                        a = "1";
                    }else{
                        a = "2";
                    }

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"change\"\r\n\r\n" + URLEncoder.encode(a, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    if(imagePath.equals(edtiContent.image)){
                        Log.d(" 이미지가  안바뀌었다!!!! ","이미지가  안바뀌었다 ");
                    }


                    // 이전에 이미지경로와 비교해서  디렉터리명으로 끈나지 않는경우만  파일로 보낸다
                    if( !imagePath.equals("없음") && imagePath != null){
                        if(edtiContent.image.equals(imagePath)) {

                        }else{
                            File sourceFile = new File(imagePath);
                            FileInputStream fileInputStream = new FileInputStream(sourceFile);

                            //php단에서 $_FILES['uploaded_file'] 에  아래의  filename=""+ imageArray.get(i) 이들어간다
                            // 여러개를 보낼때 주의 사항은  $_FILES['uploaded_file']의  'uploaded_file' 는 키값처럼들어가는데
                            // 중복되는 경우 마지막 데이터만 전송됨으로  아래에서는 반복문의 i 값을 string으로 변환하여 구분을 주었다.
                            // php 단에서도 구분지어서 받아야 한다.
                            dos = new DataOutputStream(conn.getOutputStream());
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file"+"\";filename=\"" + imagePath + "\"" + lineEnd);

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
                            wr.flush();
                        }
                    }

//                    //--------------------------
//                    //   서버에서 전송받기
//                    //--------------------------

                    DataInputStream is = null;
                    BufferedReader in = null;

                    is = new DataInputStream(conn.getInputStream());
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                    String line = null;
                    StringBuffer buff = new StringBuffer();
                    while ((line = in.readLine()) != null) {
                        buff.append(line + "\n");
                    }
                    String test = buff.toString().trim();
                    // test = URLDecoder.decode(test,"utf-8");

                    Log.d("공지사항 등록 결과 ", ""+test);


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
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }

                setResult(RESULT_OK);
                finish();

            }
        }
        notiDataHttp setNotiDB = new notiDataHttp();
        setNotiDB.execute();
    }

    // 체크박스 이벤트
    public void setCheckBoxEvent(){

        final CheckBox make_noti_notiCheck = (CheckBox)findViewById(R.id.make_noti_notiCheck);
        final CheckBox make_noti_eventCheck = (CheckBox)findViewById(R.id.make_noti_eventCheck);

        make_noti_notiCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ( make_noti_notiCheck.isChecked() == true){
                    notiMode = null;
                    notiMode = "공지사항";
                    make_noti_eventCheck.setChecked(false);
                }

            }
        });


        make_noti_eventCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ( make_noti_eventCheck.isChecked() == true){
                    notiMode = null;
                    notiMode = "이벤트";
                    make_noti_notiCheck.setChecked(false);
                }

            }
        });

    }

    // 이미지 클릭 이벤트
    public void imageClickEvent(){

        // 갤러리 불러 오기
        ImageView make_noti_Image = (ImageView)findViewById(R.id.make_noti_Image);
        make_noti_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
            }
        });


        // 롱클릭 삭제
        make_noti_Image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(make_noti_page.this, "이미지 등록을 취소합니다.", Toast.LENGTH_SHORT).show();
                imagePath = null;

                ImageView make_noti_Image = (ImageView)findViewById(R.id.make_noti_Image);
                Glide.with(make_noti_page.this).load(R.drawable.default_image).into(make_noti_Image);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == 1111){  // 겔러리에서 이미지를 가지고 오는 경우
            if( resultCode == RESULT_OK){
                // 절대 경로 로 변환
                imagePath = getRealPathFromURI(data.getData()); // path 경로
                Log.d("공지사항 이미지 절대 경로 획득 ",""+imagePath);

                ImageView make_noti_Image = (ImageView)findViewById(R.id.make_noti_Image);
                Glide.with(this).load(imagePath).into(make_noti_Image);

            }else{
                // 취소
            }
        }
    }

    //갤러리 절대경로로 변환하기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }


}
