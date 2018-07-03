package com.example.jeon.helper.userPage;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.editAskHelp;
import com.example.jeon.helper.giveHelp.giveHelpMainActivity;
import com.example.jeon.helper.ip;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class myPageEdit extends AppCompatActivity {

    // ip
    ip ip = new ip();
    String ipad = ip.getIp();

    // 유저 정보
    userPageUserData uData;

    // 카메라 이미지 경로
    String ImageURl;
    Boolean imageChange = false;

    String gender;

    boolean emailPatton = true;


    String reasult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_page_edit);

        // 내정보 가져오기
        getMyData();
        setSpinner();
        setWidgets();
        checkBoxEvent();

        // 이미지 클릭 이벤트
        setClickImage();

        // 이메일 유효성
        compareEmailCheck();

        // 버튼이벤트
        Button EditUserPageOkBtn = (Button) findViewById(R.id.EditUserPageOkBtn);
        EditUserPageOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn();
            }
        });

        Button EditUserPageCancel = (Button) findViewById(R.id.EditUserPageCancel);
        EditUserPageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //-----------------------------------------저장 버튼 -----------------------------
    public void saveBtn() {
        EditText EditUserPageUserEmail = (EditText) findViewById(R.id.EditUserPageUserEmail);
        EditText EditUserPageIntroduceEditT = (EditText) findViewById(R.id.EditUserPageIntroduceEditT);
        Spinner EditUserPageLocation = (Spinner) findViewById(R.id.EditUserPageLocation);


        String location = EditUserPageLocation.getSelectedItem().toString();
        String eMail = EditUserPageUserEmail.getText().toString();
        String intro = EditUserPageIntroduceEditT.getText().toString();

        if (TextUtils.isEmpty(intro)) {
            intro = "";
        }

        if (TextUtils.isEmpty(eMail) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "이메일 / 성별 / 지역은 \n반드시 입력해야합니다.", Toast.LENGTH_SHORT).show();
        } else if (emailPatton == false) {
            Toast.makeText(this, "이메일 형식이 틀립니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 서버에 전송.
            if (imageChange == true) {

                if ( ImageURl == null){
                    // 이미지를 없앤경우
                    editAskHelpHttpText(uData.id,eMail,location,gender,intro,ImageURl);
                } else{
                    // 이미지가 바뀐경우  서버에 이미지 다시 전송,
                    editAskHelpHttp(eMail,location,gender,intro,ImageURl,uData.id);
                }

            } else {
                // 이미지는 그대로 이메일 성별 지역 자기소개만 전송
                editAskHelpHttpText(uData.id,eMail,location,gender,intro,ImageURl);
            }
        }
    }

    // ( 유효성 검사 ) 이메일
    public void compareEmailCheck() {
        EditText EditUserPageUserEmail = (EditText) findViewById(R.id.EditUserPageUserEmail);

        EditUserPageUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText EditUserPageUserEmail = (EditText) findViewById(R.id.EditUserPageUserEmail);
                String getEmail = EditUserPageUserEmail.getText().toString();

                // 이메일 유효성 정규식
                String eMailPattern = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
                Boolean matchEmailPattern = Pattern.matches(eMailPattern, getEmail);

                if (matchEmailPattern == true) { // 형식에 일치 한다면.
                    emailPatton = true;
                } else {
                    emailPatton = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    // 파일까지 전송하는 코드
    // (서버 연동 )
    public void editAskHelpHttp(String eMail,String location,String gender,String intro,String proPath,String userId) {

        // 서버 연동 클래스
        class editMyPageDataHttp extends AsyncTask<Void, Void, Void> {

            String eMail;
            String location;
            String gender;
            String intro;
            String userId;

            public editMyPageDataHttp(String eMail, String location, String gender, String intro, String proPath,String usrId) {
                this.eMail = eMail;
                this.location = location;
                this.gender = gender;
                this.intro = intro;
                this.proPath = proPath;
                this.userId = usrId;
            }

            String proPath;

            ProgressDialog dialog = new ProgressDialog(myPageEdit.this);

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

                    URL url = new URL(ipad + "/userPageEditDataIncludeFile.php");

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
                    wr.writeBytes("Content-Disposition: form-data; name=\"userId\"\r\n\r\n" + URLEncoder.encode(userId, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"eMail\"\r\n\r\n" + URLEncoder.encode(eMail, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"location\"\r\n\r\n" + URLEncoder.encode(location, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"gender\"\r\n\r\n" + URLEncoder.encode(gender, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"intro\"\r\n\r\n" + URLEncoder.encode(intro, "utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");




                    // 파일의 존재 유무 확인 후 ( 파일이 없는 경우  그냥 지나간다 )
                    // 반복문으로 파일을 보낸다.

                    // 이전에 이미지경로와 비교해서  디렉터리명으로 끈나지 않는경우만  파일로 보낸다


                    File sourceFile = new File(ImageURl);
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);

                    //php단에서 $_FILES['uploaded_file'] 에  아래의  filename=""+ imageArray.get(i) 이들어간다
                    // 여러개를 보낼때 주의 사항은  $_FILES['uploaded_file']의  'uploaded_file' 는 키값처럼들어가는데
                    // 중복되는 경우 마지막 데이터만 전송됨으로  아래에서는 반복문의 i 값을 string으로 변환하여 구분을 주었다.
                    // php 단에서도 구분지어서 받아야 한다.
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file"+"\";filename=\"" + proPath + "\"" + lineEnd);

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

                    Log.d("수정 결과", test);


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
        editMyPageDataHttp gotoDBUerId = new editMyPageDataHttp(eMail,location,gender,intro,proPath,userId);
        gotoDBUerId.execute();

    }

    // 글만 전송하는 코드
    // 서버 연결 ( 요청 데이터들 가지고 오기 )
    public void editAskHelpHttpText(String userId,String eMail,String location,String gender,String intro,String ImageURl){

        class getGiveHelpDataToHttp extends AsyncTask<Void,Void,String> {
            ProgressDialog dialog = new ProgressDialog(myPageEdit.this);
            String userId;
            String eMail;
            String location;
            String gender;
            String intro;
            String ImageURl;


            public getGiveHelpDataToHttp(String userId, String eMail, String location, String gender, String intro,String ImageURl) {
                this.userId = userId;
                this.eMail = eMail;
                this.location = location;
                this.gender = gender;
                this.intro = intro;
                this.ImageURl = ImageURl;
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
                    URL url = new URL(ipad+"/userPageEditDataIncludeText.php");
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
                    buffer.append("userId").append("=").append(userId).append("&");
                    buffer.append("eMail").append("=").append(eMail).append("&");
                    buffer.append("location").append("=").append(location).append("&");
                    buffer.append("gender").append("=").append(gender).append("&");
                    buffer.append("ImageURl").append("=").append(ImageURl).append("&");
                    buffer.append("intro").append("=").append(intro);


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
                    reasult = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return reasult;
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

                Log.d("개인정보 수정 텍스트만",""+result);
                setResult(RESULT_OK);
                finish();

            }
        }
        getGiveHelpDataToHttp getGiveHelp = new getGiveHelpDataToHttp(userId,eMail,location,gender,intro,ImageURl);
        getGiveHelp.execute();

    }
    // 이미지만 없앤코드



    // ------------------------------------------ 정보 세팅 -------------------------------
    // 정보가져오기
    public void getMyData() {
        uData = (userPageUserData) getIntent().getSerializableExtra("myData");
        gender = uData.gender;
        ImageURl =uData.profilePath;
    }

    // 스피너 어댑터 설정하기.
    public void setSpinner() {
        //스피너 어댑터 설정
        Spinner joinLocation = (Spinner) findViewById(R.id.EditUserPageLocation);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        joinLocation.setAdapter(yearAdapter);

    }

    // 위젯 세팅
    public void setWidgets() {

        ImageView EdituserPageProfile = (ImageView) findViewById(R.id.EdituserPageProfile);

        if(uData.profilePath.contains("http://k.kakaocdn.net")){
            // 카카오 로그인
            Glide.with(this).load(uData.profilePath).into(EdituserPageProfile);
        }else{
            final String a = ipad + "/" + uData.profilePath;
            Glide.with(this).load(a).into(EdituserPageProfile);
        }


        // 체크박스
        if (uData.gender.equals("남자")) {
            CheckBox EditUserPageGenderMen = (CheckBox) findViewById(R.id.EditUserPageGenderMen);
            EditUserPageGenderMen.setChecked(true);
            CheckBox EditUserPageGenderGirl = (CheckBox) findViewById(R.id.EditUserPageGenderGirl);
            EditUserPageGenderGirl.setChecked(false);
        } else {
            CheckBox EditUserPageGenderGirl = (CheckBox) findViewById(R.id.EditUserPageGenderGirl);
            EditUserPageGenderGirl.setChecked(true);
            CheckBox EditUserPageGenderMen = (CheckBox) findViewById(R.id.EditUserPageGenderMen);
            EditUserPageGenderMen.setChecked(false);
        }


        // 지역 스피너
        Spinner EditUserPageLocation = (Spinner) findViewById(R.id.EditUserPageLocation);
        if (uData.location.equals("서울특별시")) {
            EditUserPageLocation.setSelection(1); // 0번째 부터 시작
        } else if (uData.location.equals("경기도")) {
            EditUserPageLocation.setSelection(2);
        } else if (uData.location.equals("인천광역시")) {
            EditUserPageLocation.setSelection(3);
        } else if (uData.location.equals("부산광역시")) {
            EditUserPageLocation.setSelection(4);
        } else if (uData.location.equals("광주광역시")) {
            EditUserPageLocation.setSelection(5);
        } else if (uData.location.equals("대전광역시")) {
            EditUserPageLocation.setSelection(6);
        } else if (uData.location.equals("대구광역시")) {
            EditUserPageLocation.setSelection(7);
        } else if (uData.location.equals("울산광역시")) {
            EditUserPageLocation.setSelection(8);
        } else {
            EditUserPageLocation.setSelection(9); // 0번째 부터 시작
        }

        // 이메일
        EditText EditUserPageUserEmail = (EditText) findViewById(R.id.EditUserPageUserEmail);
        EditUserPageUserEmail.setText(uData.eMail);


        // 자기소개
        EditText EditUserPageIntroduceEditT = (EditText) findViewById(R.id.EditUserPageIntroduceEditT);
        if( !uData.equals("없음")){
            EditUserPageIntroduceEditT.setText(uData.introduce);
        }

    }

    // 체크박스 중복체크 처리
    public void checkBoxEvent() {
        // 체크박스
        CheckBox EditUserPageGenderMen = (CheckBox) findViewById(R.id.EditUserPageGenderMen);
        CheckBox EditUserPageGenderGirl = (CheckBox) findViewById(R.id.EditUserPageGenderGirl);

        EditUserPageGenderMen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    CheckBox EditUserPageGenderGirl = (CheckBox) findViewById(R.id.EditUserPageGenderGirl);
                    EditUserPageGenderGirl.setChecked(false);
                    gender = "남자";
                }
            }
        });

        EditUserPageGenderGirl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    CheckBox EditUserPageGenderMen = (CheckBox) findViewById(R.id.EditUserPageGenderMen);
                    EditUserPageGenderMen.setChecked(false);
                    gender = "여자";
                }
            }
        });
    }

    // ------------------------------------------프로필 이미지 관련 -------------------------------
    // 이미지 클릭 이벤트
    public void setClickImage() {

        ImageView EdituserPageProfile = (ImageView) findViewById(R.id.EdituserPageProfile);
        EdituserPageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMothod();
            }
        });

        EdituserPageProfile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageURl = null;
                ImageView EdituserPageProfile = (ImageView) findViewById(R.id.EdituserPageProfile);
                Glide.with(myPageEdit.this).load(R.drawable.kakao_default_profile_image).into(EdituserPageProfile);
                imageChange = true;
                return true;
            }
        });

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
        ImageURl = image.getAbsolutePath();
        return image;
    }

    //  << 저장 >>사진 파일 저장 ( 여기에서  리스트에 추가 )
    private void galleryAddPic() {    // 찍은 사진 앨범에 저장
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(ImageURl);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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

    // ( 다이얼 로그 )
    public void selectMothod() {
        AlertDialog.Builder cameraSelect = new AlertDialog.Builder(myPageEdit.this);
        cameraSelect.setTitle("사진등록");

        cameraSelect.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(myPageEdit.this, "카메라 실행", Toast.LENGTH_SHORT).show();
                gotoCamera();
            }
        });

        cameraSelect.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(myPageEdit.this, "갤러리 실행", Toast.LENGTH_SHORT).show();
                gotoGallery();
            }
        });
        cameraSelect.show();   // 실행
    }

    //(카메라로 이동)
    public void gotoCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.jeon.helper.askHelp.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, 1111);
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
    }

    //(갤러리로 이동)
    public void gotoGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, 2222);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {
            // 카메라
            if (resultCode == RESULT_OK) {
                galleryAddPic();
                imageChange = true;
                ImageView EdituserPageProfile = (ImageView) findViewById(R.id.EdituserPageProfile);
                Glide.with(myPageEdit.this).load(ImageURl).into(EdituserPageProfile);
            }
        } else if (requestCode == 2222) {
            // 갤러리
            if (resultCode == RESULT_OK) {
                ImageURl = getRealpath(data.getData());
                imageChange = true;
                ImageView EdituserPageProfile = (ImageView) findViewById(R.id.EdituserPageProfile);
                Glide.with(myPageEdit.this).load(ImageURl).into(EdituserPageProfile);
            }
        }
    }
}
