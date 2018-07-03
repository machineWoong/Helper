package com.example.jeon.helper.askHelp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.LinearLayout;
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

public class editAskHelp extends AppCompatActivity {

    Integer cameraCode = 1;
    Integer galleryCode = 2;
    Integer mapCode = 3;

    String userId;
    String loginMode;
    String userCash;

    // 초기 데이터
    ArrayList<showMyAskHelpShowMyContent> preData = new ArrayList<>();

    // 초기 사진 정보
    ArrayList<String> prePhoto = new ArrayList<>();
    String [] ImageFilter;
    String prePhotoPathForRemove = "";

    // 초기 지도 위치
    // 맵배열
    ArrayList<LatLng> missionLatLng = new ArrayList<LatLng>();
    // 수행지 직렬화 후 2개의 구분자를 가진 하나의 스트링
    String missonLatLngString ="";
    // 도착지 정보 ( 위치정보를 문자열로 저장 )
    String meettingLatLngString;

    String [] firstMissionMapFilter;
    String [] seccondMissionMapFilter;


    // 이미지 경로
    String singleImageAbsoluteURL; //네이티브 겔러리에서 가져온 이미지 절대경로
    String multiImagerAbsoluteURL; //사진에서 가져온 다중 이미지 절대경로
    String cameraAbsoluteURL; // 카메라로 찍은 이미지 절대 경로.

    String gender = null;



    ip ip = new ip();
    String ipad = ip.getIp();


    // 날짜 선택
    AlertDialog dialog;
    CalendarView calendarView;
    TextView EstartDate;
    String sDate;
    TextView EendDate;
    String eDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ask_help);


        // 위젯 아이디 세팅
        ImageView gotoCamera = (ImageView)findViewById(R.id.EditaskHelpCamera);
        ImageView gotoGall = (ImageView)findViewById(R.id.EditaskHelpGallery);
        ImageView gotoMap = (ImageView)findViewById(R.id.EditLocationMap);

        gotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCamera();
            }
        });
        gotoGall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGallery();
            }
        });
        gotoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMap();
            }
        });


        //데이터 가져오기
        getPreData();
        setSpinner();
        getUserAccount();  // 유저의  소지금


        //이미지 클릭
        setImageLongClickEvent();
        setImageShortClickEvent();

        // 체크 박스
        checkBoxEvent();

        // 저장 취소버튼
        editSaveBtn();
        editCancel();

        EstartDate = (TextView)findViewById(R.id.EstartDate);
        // 날짜 변경 다이얼로그
        EstartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSdate();
            }
        });
        EendDate = (TextView)findViewById(R.id.EendDate);
        EendDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdate();
            }
        });


    }

    // ---------------------------------------- 날짜 클릭 이벤트 ---------------------------

    public void setSdate(){
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(editAskHelp.this);
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
                EstartDate.setText(sDate);
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
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(editAskHelp.this);
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
                EendDate.setText(eDate);
                dialog.dismiss();     //닫기
            }
        });

        Button cancel_date = (Button)mView.findViewById(R.id.cancel_date);
        cancel_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eDate = null;
                dialog.dismiss();    //닫기
            }
        });
        aBuilder.setView(mView);
        AlertDialog dialog = aBuilder.create();
        dialog.show();

    }

    // ----------------------------------------- 지도 관련 ---------------------------------

    public void gotoMap(){
        if(TextUtils.isEmpty(missonLatLngString) && TextUtils.isEmpty(meettingLatLngString)){
            // 새로 맵설정을 하는경우 이동
            Toast.makeText(this, "이거타고가냐 ?", Toast.LENGTH_SHORT).show();
            Intent gotoMapSetting = new Intent(editAskHelp.this,editAskHelpMap.class);
            startActivityForResult(gotoMapSetting,mapCode);

        }else{
            // 수행지나 도착지중 데이터가 있는경우 데이터를 실어서 보낸다.
            Intent gotoMapSetting = new Intent(editAskHelp.this,editAskHelpMap.class);
            gotoMapSetting.putParcelableArrayListExtra("missonLatLng",missionLatLng);
            gotoMapSetting.putExtra("meettingLatLngString",meettingLatLngString);
            startActivityForResult(gotoMapSetting,mapCode);
        }
    }
    // 맵관련 정보를 가지고 있는경우를 판단해서, 배열에 넣어줄것이다.
    public void preMapData(){
        // 지도는 값이 없으면 0으로 온다.

        // 리스트에 넣어준다.
        if ( !preData.get(0).mission.toString().equals("0")){

            missonLatLngString = preData.get(0).mission.toString();


            firstMissionMapFilter = preData.get(0).mission.split("/");

            for ( int i = 0 ; i< firstMissionMapFilter.length ;i++){
                seccondMissionMapFilter = firstMissionMapFilter[i].split(",");
                LatLng missionLL = new LatLng (Double.parseDouble(seccondMissionMapFilter[0]),Double.parseDouble(seccondMissionMapFilter[1]));
                missionLatLng.add(missionLL);
            }

            Log.d("미션지 데이터 쪼개기 ",""+missionLatLng.toString());



        }

        // 스트링에 넣어주면 된다
        if ( !preData.get(0).meetting.toString().equals("0")){
            meettingLatLngString = preData.get(0).meetting.toString();
        }


    }

    //------------------------------------------- 카메라 버튼 갤러리 버튼 ------------------
    // 카메라로 이동
    public void gotoCamera(){
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
        if (prePhoto.size() < 5) { //배열에 추가
            prePhoto.add(cameraAbsoluteURL);
        } else {
            Toast.makeText(this, "이미지는 최대 5장 까지입니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // 갤러리로 이동
    //(갤러리로 이동)
    public void gotoGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);  // 암시적 인텐트 사용.

        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setType("image/*");
        gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(gallery, "단일 선택 : 갤러리   |   다중 선택 : 사진"), galleryCode);


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

    // ( 절대경로 : 리스트 : 갤러리 ) 갤러리에서 가져온 이미지 절대경로로 변환후 리스트에 저장
    public void exchangeImageAndAddList(Intent data) {

        // 갤러리로 단일 가지고 왔을때.
        Uri SingleImageUri = data.getData();
        // 사진으로 여러장 사진을 가지고 왔을때.
        ClipData clipData = data.getClipData();

        if (SingleImageUri != null) { // 싱글이미지 ( 비어있는 이미지를 확인해서 뿌려준다 /  비어있는 경우 배열에 담는다. / 배열에 담아서 뿌려줌)
            singleImageAbsoluteURL = getRealpath(SingleImageUri);

            if (prePhoto.size() >= 5) {  // 리스트가 꽉찼다.
                Toast.makeText(this, "이미지는 최대 5장까지 입니다.", Toast.LENGTH_SHORT).show();
            } else {
                prePhoto.add(singleImageAbsoluteURL);
            }

//            Log.d("단일 이미지 절대경로가 왔는가 ?","절대 경로 : "+singleImageAbsoluteURL);
//            Log.d("단일 이미지 절대경로가 왔는가 ?","절대 경로 : "+imageArray.get(0).toString());

        } else if (clipData != null) {// 멀티이미지 ( 비어있는 이미지를 확인해서 뿌려준다 / 비어있는 경우 배열에 담는다.)

            int getCount = clipData.getItemCount();

//                    if ( getCount > 5 || imageArray.size()+getCount > 5 ){
//                        Toast.makeText(this, "이미지는 최대 5장 입니다.", Toast.LENGTH_SHORT).show();
//                    }

            for (int i = 0; i < getCount; i++) {
                if (prePhoto.size() < 5) {
                    //절대경로 로 변환
                    multiImagerAbsoluteURL = getRealpath(clipData.getItemAt(i).getUri());

                    // 어레이 리스트에 저장.
                    prePhoto.add(multiImagerAbsoluteURL);
                }
            }
        }
    }


    //------------------------------------------- 초기 데이터 세팅 ---------------------------------
    // ( 데이터 가져오기 ) 수정할 데이터 원본 가져오기
    public void getPreData(){
        preData = (ArrayList)getIntent().getSerializableExtra("preData");
        userId = getIntent().getStringExtra("userId");
        loginMode = getIntent().getStringExtra("loginMode");

        // 이미지가 없는 경우에는 '없음' 으로 들어옴.
        Log.d("이미지는 어떻게 들어오니 ?",preData.get(0).photo);

        if (!preData.get(0).photo.equals("없음")){
            prePhotoPathForRemove = preData.get(0).photo;
            splitImage();
        }

        preMapData();

    }

    // 이미지 경로 쪼개기
    public void splitImage(){
        if (preData.get(0).photo.equals("없음")) {
        } else {
            // 이미지가 있음 나눠서 이미지 리스트에 넣어준다,

            ImageFilter = preData.get(0).photo.split("!");
            for (int i = 0; i < ImageFilter.length; i++) {
                prePhoto.add(ipad+"/"+ImageFilter[i]);
            }
            // 성공했습니다
            //Log.d("이미지 쪼개기 성공했니?",""+ prePhoto.get(0).toString());
            //이미지 뿌려주기
            showImageList();
        }
    }

    // (이미지 리스트 ) 사용자가 올린 이미지를 미리보기로 뿌려줌
    public void showImageList() {

        int listCount = prePhoto.size();

        ImageView[] arr = new ImageView[5];
        arr[0] = (ImageView) findViewById(R.id.EdituserImage1);
        arr[1] = (ImageView) findViewById(R.id.EdituserImage2);
        arr[2] = (ImageView) findViewById(R.id.EdituserImage3);
        arr[3] = (ImageView) findViewById(R.id.EdituserImage4);
        arr[4] = (ImageView) findViewById(R.id.EdituserImage5);

        for (int i = 0; i < 5; i++) {
            if ( i < listCount ){
                Glide.with(this).load(prePhoto.get(i)).into(arr[i]);
                setImageViewScale(arr[i]);
                arr[i].setScaleType(ImageView.ScaleType.FIT_XY);

            }
            else{
                arr[i].setImageResource(R.drawable.empty);
            }
        }

    }

    //이미지 크기 자르기
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

    //( 초기 위젯 데이터 세팅 )
    public void setPreWidgets(){
        // 제목
        EditText editAskHelpTilte = (EditText)findViewById(R.id.editAskHelpTilte);
        editAskHelpTilte.setText(preData.get(0).title);
        // 기간선택
        TextView EstartDate = (TextView)findViewById(R.id.EstartDate);
        TextView EendDate = (TextView)findViewById(R.id.EendDate);
        EstartDate.setText(preData.get(0).sdate);
        EendDate.setText(preData.get(0).edate);

        //성별
        CheckBox editMan = (CheckBox)findViewById(R.id.EaddHelpMan);
        CheckBox editGirl = (CheckBox)findViewById(R.id.EaddHelpGirl);
        CheckBox editGender = (CheckBox)findViewById(R.id.EaddHelpEvery);

        if(preData.get(0).gender.equals("남자")){
            editMan.setChecked(true);
            editGirl.setChecked(false);
            editGender.setChecked(false);
        }else if(preData.get(0).gender.equals("여자")){
            editMan.setChecked(false);
            editGirl.setChecked(true);
            editGender.setChecked(false);
        }else{
            editMan.setChecked(false);
            editGirl.setChecked(false);
            editGender.setChecked(true);
        }

        // 인원
        EditText Ehelpcount = (EditText)findViewById(R.id.Ehelpcount);
        Ehelpcount.setText(preData.get(0).helper);

        // 지역 스피너
        Spinner EaskHelpLocationSpinner = (Spinner) findViewById(R.id.EaskHelpLocationSpinner);
        if (preData.get(0).location.equals("서울특별시")){
            EaskHelpLocationSpinner.setSelection(1); // 0번째 부터 시작
        }else if (preData.get(0).location.equals("경기도")){
            EaskHelpLocationSpinner.setSelection(2);
        }else if (preData.get(0).location.equals("인천광역시")){
            EaskHelpLocationSpinner.setSelection(3);
        }else if (preData.get(0).location.equals("부산광역시")){
            EaskHelpLocationSpinner.setSelection(4);
        }else if (preData.get(0).location.equals("광주광역시")){
            EaskHelpLocationSpinner.setSelection(5);
        }else if (preData.get(0).location.equals("대전광역시")){
            EaskHelpLocationSpinner.setSelection(6);
        }else if (preData.get(0).location.equals("대구광역시")){
            EaskHelpLocationSpinner.setSelection(7);
        }else if (preData.get(0).location.equals("울산광역시")){
            EaskHelpLocationSpinner.setSelection(8);
        }else{
            EaskHelpLocationSpinner.setSelection(9); // 0번째 부터 시작
        }

        //상세주소
        EditText EditLocationAddress = (EditText)findViewById(R.id.EditLocationAddress);
        EditLocationAddress.setText(preData.get(0).address);

        // 보유중인 캐쉬
        // 서버로 연결해서 가지고와야하는 건가..
        TextView EdituserHaveCash = (TextView)findViewById(R.id.EdituserHaveCash);

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(userCash)).toString();

        EdituserHaveCash.setText(userHaveCashFormal);

        // 수고비
        EditText editAskHelpPay = (EditText)findViewById(R.id.editAskHelpPay);
        editAskHelpPay.setText(preData.get(0).pay);

        // 사진
        // 서버로 부터 전송 받아서 적용 할 예정.




        // 내용
        EditText EditaskHelpContent = (EditText)findViewById(R.id.EditaskHelpContent);
        EditaskHelpContent.setText(preData.get(0).content);




    }

    // 스피너 어댑터 설정하기.
    public void setSpinner() {
        //스피너 어댑터 설정
        Spinner joinLocation = (Spinner) findViewById(R.id.EaskHelpLocationSpinner);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        joinLocation.setAdapter(yearAdapter);

    }

    //( 서버 연결 ) 사용자의 돈 가져오기
    public void getUserAccount(){
        class getNickNameToHttp extends AsyncTask<Void,Void,String> {

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
                    buffer.append("accountMode").append("=").append("1");



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
                    userCash = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return userCash;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                userCash = result;

                // 데이터 설정.
                setPreWidgets();
            }

        }
        getNickNameToHttp getNickName = new getNickNameToHttp();
        getNickName.execute();
    }

    //------------------------------------------- 이미지 클릭이벤트---------------------------------
    //( 롱클릭 ) :  삭제
    public void setImageLongClickEvent() {


        ImageView userImage1 = (ImageView)findViewById(R.id.EdituserImage1); // 사용자가 올린 이미지
        ImageView userImage2 = (ImageView)findViewById(R.id.EdituserImage2); // 사용자가 올린 이미지
        ImageView userImage3 = (ImageView)findViewById(R.id.EdituserImage3); // 사용자가 올린 이미지
        ImageView userImage4 = (ImageView)findViewById(R.id.EdituserImage4); // 사용자가 올린 이미지
        ImageView userImage5 = (ImageView)findViewById(R.id.EdituserImage5); // 사용자가 올린 이미지


        userImage1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    prePhoto.remove(0);
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
                    prePhoto.remove(1);
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
                    prePhoto.remove(2);
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
                    prePhoto.remove(3);
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
                    prePhoto.remove(4);
                    showImageList();
                }catch (Exception e){
                }
                return true;
            }
        });
    }

    //( 숏클릭 ) : 이미지 확대 ( 엑티비티 전환 )
    public void setImageShortClickEvent() {
        ImageView userImage1 = (ImageView) findViewById(R.id.EdituserImage1); // 사용자가 올린 이미지
        ImageView userImage2 = (ImageView)findViewById(R.id.EdituserImage2); // 사용자가 올린 이미지
        ImageView userImage3 = (ImageView)findViewById(R.id.EdituserImage3); // 사용자가 올린 이미지
        ImageView userImage4 = (ImageView)findViewById(R.id.EdituserImage4); // 사용자가 올린 이미지
        ImageView userImage5 = (ImageView)findViewById(R.id.EdituserImage5); // 사용자가 올린 이미지

        userImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( prePhoto.get(0)!=null){
                        Intent gotoBigShowImage = new Intent(editAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",prePhoto);
                        gotoBigShowImage.putExtra("imageNumber",0);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(editAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        userImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( prePhoto.get(1)!=null){
                        Intent gotoBigShowImage = new Intent(editAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",prePhoto);
                        gotoBigShowImage.putExtra("imageNumber",1);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(editAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        userImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( prePhoto.get(2)!=null){
                        Intent gotoBigShowImage = new Intent(editAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",prePhoto);
                        gotoBigShowImage.putExtra("imageNumber",2);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(editAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        userImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ( prePhoto.get(3)!=null){
                        Intent gotoBigShowImage = new Intent(editAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",prePhoto);
                        gotoBigShowImage.putExtra("imageNumber",3);
                        startActivity(gotoBigShowImage);
                    }
                }catch (Exception e){
                    Toast.makeText(editAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        userImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if ( prePhoto.get(4)!=null){
                        Intent gotoBigShowImage = new Intent(editAskHelp.this,showBigImage.class);
                        gotoBigShowImage.putStringArrayListExtra("imageArray",prePhoto);
                        gotoBigShowImage.putExtra("imageNumber",4);
                        startActivity(gotoBigShowImage);
                    }

                }catch (Exception e){
                    Toast.makeText(editAskHelp.this, "이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    //------------------------- 저장 & 취소 --------------------------------------------------------
    // 저장 버튼
    public void editSaveBtn(){
        Button editSave = (Button)findViewById(R.id.EditAskHelpSaveBtn);
        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContent();

            }
        });
    }

    //------------------------------ 체크박스 ( 성별 ) 중복 처리 이벤트 ------------------

    public void checkBoxEvent() {
        CheckBox addHelpMan = (CheckBox) findViewById(R.id.EaddHelpMan); // 남자
        CheckBox addHelpGirl = (CheckBox) findViewById(R.id.EaddHelpGirl); // 여자
        CheckBox addHelpEvery = (CheckBox) findViewById(R.id.EaddHelpEvery); // 성별무관


        addHelpMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    CheckBox addHelpGirl = (CheckBox) findViewById(R.id.EaddHelpGirl); // 여자
                    addHelpGirl.setChecked(false);
                    CheckBox addHelpEvery = (CheckBox) findViewById(R.id.EaddHelpEvery); // 성별무관
                    addHelpEvery.setChecked(false);
                    gender = "남자";
                }
            }
        });

        addHelpGirl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    CheckBox addHelpMan = (CheckBox) findViewById(R.id.EaddHelpMan); // 남자
                    addHelpMan.setChecked(false);
                    CheckBox addHelpEvery = (CheckBox) findViewById(R.id.EaddHelpEvery); // 성별무관
                    addHelpEvery.setChecked(false);
                    gender = "여자";
                }
            }
        });

        addHelpEvery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    CheckBox addHelpMan = (CheckBox) findViewById(R.id.EaddHelpMan); // 남자
                    addHelpMan.setChecked(false);
                    CheckBox addHelpGirl = (CheckBox) findViewById(R.id.EaddHelpGirl); // 여자
                    addHelpGirl.setChecked(false);
                    gender = "무관";
                }

            }
        });

    }

    // 유효성 체크
    public void checkContent(){
        EditText editAskHelpTilte = (EditText)findViewById(R.id.editAskHelpTilte);
        TextView EstartDate = (TextView)findViewById(R.id.EstartDate);
        TextView EendDate = (TextView)findViewById(R.id.EendDate);
        EditText Ehelpcount = (EditText)findViewById(R.id.Ehelpcount);

        Spinner EaskHelpLocationSpinner =(Spinner)findViewById(R.id.EaskHelpLocationSpinner);
        EditText EditLocationAddress = (EditText)findViewById(R.id.EditLocationAddress);

        EditText editAskHelpPay = (EditText)findViewById(R.id.editAskHelpPay);
        EditText EditaskHelpContent = (EditText)findViewById(R.id.EditaskHelpContent);

        String title = editAskHelpTilte.getText().toString();
        String sdate = EstartDate.getText().toString();
        String edate = EendDate.getText().toString();
        //gender

        String helper =  Ehelpcount.getText().toString();
        String location =  EaskHelpLocationSpinner.getSelectedItem().toString();
        String address =  EditLocationAddress.getText().toString();
        String pay =  editAskHelpPay.getText().toString();
        String content =  EditaskHelpContent.getText().toString();

        String mission =  missonLatLngString;
        String meetting = meettingLatLngString;
        //ArrayList<String> prePhoto;

        int a = Integer.parseInt(userCash);
        int b = Integer.parseInt(pay);

        try{
            int c = Integer.parseInt(helper);
            b = b*c;
        }catch (Exception e){

        }


        if (a >= b){  // 소지금이 보수 보다 크거나 같다면 ( 거래 가능 )
            if( TextUtils.isEmpty(gender) || TextUtils.isEmpty(title)||TextUtils.isEmpty(sdate)|| TextUtils.isEmpty(helper)||TextUtils.isEmpty(edate)
                    || location.equals("---- 선택 ----")|| TextUtils.isEmpty(address) || TextUtils.isEmpty(pay)|| TextUtils.isEmpty(content)){
                Toast.makeText(this, "입력되지 않은 데이터가 있습니다.", Toast.LENGTH_SHORT).show();
            }else{
                // 서버 연결
                String key = preData.get(0).key;
                editAskHelpHttp(loginMode,userId,title,sdate,edate,gender,helper,location,address,pay,content,mission,meetting,prePhoto,key);
            }
        }else if (b <= 0){
            Toast.makeText(this, "수고비는 0원 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "보유중인 캐쉬가 부족합니다.", Toast.LENGTH_SHORT).show();
        }

    }

    // (서버 연동 )
    public void editAskHelpHttp(String loginMode, String id, String title,String sdate,String edate,String sex ,String helper,String location, String address, String pay,
                                String content,String mission,String meetting,ArrayList<String> imageArray,String key){
        // 이미지 경로 직렬화
        String path = ImagePathString();


        // 서버 연동 클래스
        class addAskHelpDB extends AsyncTask<Void, Void, Void> {
            String loginMode,id,title,sdate,edate,sex,location,address,pay,content,mission,meetting,helper,path,prePhotoPathForRemove,key;
            ArrayList<String> imageArray = new ArrayList<String>();
            int imageCount = 0;

            ProgressDialog dialog = new ProgressDialog(editAskHelp.this);


            public addAskHelpDB(String loginMode, String id,String title,String sdate,String edate,String sex ,String helper,String location, String address, String pay,
                                String content,String mission,String meetting,ArrayList<String> imageArray,String path,String prePhotoPathForRemove,String key ){

                this.loginMode = loginMode;  // 1 일반 로그인 2 카카오 로그인
                this.id = id;
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
                this.path = path;
                this.prePhotoPathForRemove = prePhotoPathForRemove;
                this.key = key;

            }
            // 쓰레드
            @Override
            protected void onPreExecute() {
                super.onPreExecute();


                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 저장중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();

                // 여기에서 데이터의 갯수를 다시 정해줘야한다.
                // 이 갯수는 php 단에서 반복의 횟수를 정해주는  역할을 한다.
                // 전체의 문자열에서, askHelpUploadImage/ 가 안들어간 갯수를 카운터 한다.

                for ( int i = 0 ; i < imageArray.size() ; i++){
                    if ( imageArray.get(i).contains("askHelpUploadImage/")){
                        // 포함하고 있으면  갯수를 증가시키지 않는다.
                    }else{
                        imageCount = imageCount+1;
                    }
                }

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

                    URL url = new URL(ipad+"/editAskHelp.php");

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

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"path\"\r\n\r\n" + URLEncoder.encode(path,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"forRemove\"\r\n\r\n" + URLEncoder.encode(prePhotoPathForRemove,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"key\"\r\n\r\n" + URLEncoder.encode(key,"utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // PHP 에서 반복문을 사용하기 위하여 이미지 갯수를 센다.
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"imageCount\"\r\n\r\n" + String.valueOf(imageCount));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");


                    // 파일의 존재 유무 확인 후 ( 파일이 없는 경우  그냥 지나간다 )
                    // 반복문으로 파일을 보낸다.
                    if( imageArray.size() > 0){

                        // 이전에 이미지경로와 비교해서  디렉터리명으로 끈나지 않는경우만  파일로 보낸다

                        for ( int i = 0 ; i < imageArray.size(); i++){

                            if ( imageArray.get(i).toString().contains("askHelpUploadImage/")){
                                // 포함하고 있다면. 아무것도 하지 않는다.
                            }else{
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

                                Log.d("이미지",""+i+"번째"+imageArray.get(i));
                                Log.d("이미지",""+a+"번째"+imageArray.get(i));
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
                    }


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
                    while ( ( line = in.readLine() ) != null )
                    {
                        buff.append(line + "\n");
                    }
                    String test = buff.toString().trim();
                    // test = URLDecoder.decode(test,"utf-8");

                    Log.d("수정 결과",test);



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

                setResult(RESULT_OK);
                finish();

            }
        }
        addAskHelpDB gotoDBUerId = new addAskHelpDB(loginMode,userId,title,sdate,edate,sex,helper,
                location,address,pay,content,missonLatLngString,meettingLatLngString,imageArray,path,prePhotoPathForRemove,key);
        gotoDBUerId.execute();

    }

    // 취소 버튼
    public void editCancel(){
        Button editCancel = (Button)findViewById(R.id.EditAskHelpCancelBtn);
        editCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 기존이미지 분류
    public String ImagePathString(){

        String path = "";

        if ( prePhoto.size() > 0 ){
            for ( int i = 0 ; i < prePhoto.size(); i++){
                if (prePhoto.get(i).toString().contains(ipad+"/") ){
                    path = path+prePhoto.get(i).toString()+"!";
                    path =path.replace(ipad+"/","");
                }
            }
        }
        return path;
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

        if ( requestCode == mapCode){
            if( resultCode == RESULT_OK){
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

        showImageList();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
