package com.example.jeon.helper.askHelp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;

public class editAskHelpMap extends AppCompatActivity implements OnMapReadyCallback {

    String preMission;
    String preMeetting;


    Boolean gpsEnable;
    Boolean isOnline;
    Boolean isGetUserLocation = false;

    // 따로 선언해 준이유 : 메소드를 통해서 onMapReady에서 실행하기위해서 ( googleMap 가 final 로 선언이 되어있지 않기 때문에 )
    GoogleMap gm;
    LocationManager locationManager;


    // 유저의 현재위치 받아오기
    Double userCurrentLocationLat;
    Double userCurrentLocationLng;
    LatLng userCurrentLocat;

    // 디폴트 위치 서울역
    LatLng deaultLocation = new LatLng(37.554816,126.970180);

    // 미션 위치
    ArrayList<LatLng> missionLatLng = new ArrayList<LatLng>();
    int missionCount = 0;

    // 도착 위치
    LatLng meettingLatLng;
    Boolean existMeettingMark = false; // true라면 마커를 만들수 없음  마커 선택시 다시 false로 변환


    // 이전 데이터
    ArrayList<LatLng> preMissionLatLng = new ArrayList<LatLng>();
    String preMeeting;
    Boolean preExistMission = false;
    Boolean preExistMeetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ask_help_map);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapSetting);
        mapFragment.getMapAsync(this);

        // ---------- 위젯 아이디------------------------------------------------------------------
        LinearLayout gotoCurrentLocation = (LinearLayout)findViewById(R.id.EditCurrentLayout);
        LinearLayout missionLocation = (LinearLayout)findViewById(R.id.EditMissionLayout);
        LinearLayout meettingLocation = (LinearLayout)findViewById(R.id.EditMettingLayout);

        // 저장버튼
        Button saveMapLocaion = (Button)findViewById(R.id.EditSaveMapLocaion);

        // 취소버튼
        Button cancelSaveMapLocation = (Button)findViewById(R.id.EditCancelSaveMapLocation);


        //------------------------------------------------------------------------------------------


        // --------- 권한 및 연결 확인 -------------------------------------------------------------
        //권한 확인
        requirePermission();


        // 연결 확인
        isConnectedInternet(this); // 네트워크 연결 여부확인
        isConnectedGPS(this); // GPS 연결 여부 확인

        if (gpsEnable == false) {
            isOnGPS();
        }
        if (isOnline == false) {
            Toast.makeText(this, "데이터 / 와이파이가 연결되 있지 않습니다.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        // -----------------------------------------------------------------------------------------


        //-----------  버튼 이벤트 -----------------------------------------------------------------


        gotoCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserCurrentLocation();
            }
        });
        missionLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 순차적으로 수행지를 본다
                showToMissionLocation();
            }
        });
        meettingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 도착지를 본다.
                showToMeettingLocation();
            }
        });

        //저장버튼
        saveMapLocaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수행지 도착지 정보리턴
                setSaveMapLocaiont();
            }
        });

        // 취소버튼
        cancelSaveMapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCancelSaveMapLocation();
            }
        });

        //-----------------------------------------------------------------------------------------

        // ------------- 이전에 등록한 데이터 확인하기 -------------

        try{
            preSaveGetData();
        }catch (Exception e){

        }
    }
    // ---------------------------------- 기존 데이터 가지고 오기 ---------------------------------


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gm = googleMap;

        // 현재위치 받아오기
        findCurrentLocation();

        // 유저의 위치를 가져오지 못한다면 자동으로 디폴트 위치를 보여줌
        if ( isGetUserLocation == false){
            gm.moveCamera(CameraUpdateFactory.newLatLngZoom(deaultLocation, 14));
        }

        // 지도 검색 사용  ( 검색한 장소의 위치를 보여줌  마커 X)
        search();


        // 이전의 위치정보 가져와서 마커 찍기
        if ( preExistMission == true){
            for ( int i = 0 ; i < missionLatLng.size(); i++){
                gm.addMarker(new MarkerOptions().position(missionLatLng.get(i)).title("수행지")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mission_marker)));
            }
        }

        if ( preExistMeetting == true && existMeettingMark == true){
            gm.addMarker(new MarkerOptions().position(meettingLatLng).title("도착지")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.meeting_marker)));
        }


        // 마커생성
        gm.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // 롱클릭시 다이얼 로그를 띄워서 수행지 도착지를 선택할수  있도록 할 것임.
                makeMarkDialog(latLng); // 클릭한 위치의 위도경도를 전달
            }
        });

        // 마커 클릭 이벤트
        gm.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                LatLng a = marker.getPosition();
                removeMark(title,a,marker);
                return false;
            }
        });


    }

    //---------------------( 세이브전에 다시 보러 왔을 경우 )--------------------------------------
    //-------- 수행지, 도착지 정보 가지고 오기 ----------------------------------------------------
    public void preSaveGetData(){
        preMissionLatLng = getIntent().getParcelableArrayListExtra("missonLatLng");
        preMeeting = getIntent().getStringExtra("meettingLatLngString");


        // 수행지
        if (preMissionLatLng == null){ // 가지고온 값이 없다면. 냅둔다.
        }else{
            missionLatLng = preMissionLatLng;
            preExistMission =true;
        }

        // 도착지
        if(preMeeting == null){
        }else{
            // 가지고 왔으니 다시 구분해서 랏과 랭을  만들어 줍시다.
            String [] splitData ;
            splitData = preMeeting.split(","); // 쪼개고

            String a = splitData[0].replace("lat/lng: (", "");  // 특수문자제거
            String b = splitData[1].replace(")", ""); // 특수문자 제거

            // 재설정.
            meettingLatLng = new LatLng(Double.parseDouble(a),Double.parseDouble(b));
            existMeettingMark =true;
            preExistMeetting = true;
        }

        // 버튼 텍스트 변경
        if ( preExistMission == true || preExistMeetting == true ){

            // 저장버튼
            Button saveMapLocaion = (Button)findViewById(R.id.saveMapLocaion);
            saveMapLocaion.setText("수정");
            // 취소버튼
            Button cancelSaveMapLocation = (Button)findViewById(R.id.cancelSaveMapLocation);
            cancelSaveMapLocation.setText("수정 취소");
        }


    }

    //---------------------( 다이얼로그 ) 마커 선택 및 마커 생성// 삭제-----------------------------
    // 마커 생성
    public void makeMarkDialog(final LatLng latLng){

        AlertDialog.Builder makeMaker = new AlertDialog.Builder(editAskHelpMap.this);
        makeMaker.setTitle("마커 선택");
        makeMaker.setMessage("수행지는 5개 까지 선택이 가능합니다.");

        makeMaker.setNegativeButton("수행지",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( missionLatLng.size() < 5){
                    missionLatLng.add(latLng);

                    //마커 찍기 + 마커 이미지 설정
                    gm.addMarker(new MarkerOptions().position(latLng).title("수행지")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mission_marker)));

                    Toast.makeText(editAskHelpMap.this, "수행지 : "+missionLatLng.size()+" 개 선택.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(editAskHelpMap.this, "수행지는 최대 5개 선택 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        makeMaker.setPositiveButton("도착지",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( existMeettingMark == false){ //마커가 없음 (생성가능)
                    meettingLatLng = latLng;

                    //마커 찍기
                    gm.addMarker(new MarkerOptions().position(meettingLatLng).title("도착지")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.meeting_marker)));

                    existMeettingMark = true;
                    Toast.makeText(editAskHelpMap.this, "도착지 설정", Toast.LENGTH_SHORT).show();
                }else{ // 이미 마커가있음 ( 생성 불가능 )
                    Toast.makeText(editAskHelpMap.this, "이미 도착지가 설정되어 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        makeMaker.show();   // 실행
    }

    // (마커 클릭 이벤트 ) 마커 삭제
    public void removeMark(String title,LatLng a,Marker marker){
        if ( title.equals("수행지")){ // 선택한 마커가 수행지라면
            // 어레이리스트에서 비워준다.
            // 해당하는 마커의 인덱스를 찾아서 지워준다.
            int findListIndex = missionLatLng.indexOf(a);  // 인덱스 찾기
            missionLatLng.remove(findListIndex); // 인덱스에서 삭제
            marker.remove(); // 마커삭제


        }else if (title.equals("도착지")){  // 선택한 마커가 도착지라면.
            // 도착지에 마커에 대한 위치정보를 없애주고
            // 선택되었다는 boolean 변수도 true로 바꿔준다
            meettingLatLng = null;
            existMeettingMark = false;
            marker.remove();
        }

    }



    //--------------------------------------버튼 이벤트 --------------------------------------------
    // 유저의 현재 위치로 이동하기
    public void goToUserCurrentLocation(){
        try {
            // 사용자의 현재위치나 디폴트위치를 연결상태에 따라 보여준다.
            if ( userCurrentLocationLat != null){
                //사용자의 현재위치를 가지고 왔음
                userCurrentLocat = new LatLng(userCurrentLocationLat, userCurrentLocationLng);
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocat, 14));
                isGetUserLocation = true;
            }else{
                //사용자의 위치를 못가지고옴 디폴트위치
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(deaultLocation, 14));
            }
        }catch (Exception e){

        }

    }

    // 수행지 순차적으로보기
    public void showToMissionLocation(){
        try{
            if ( missionLatLng.get(missionCount)!= null){
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(missionLatLng.get(missionCount), 16));
                missionCount++;

                if (missionCount == missionLatLng.size()){ // 어레이리스트의 크기만큼 반복하기 위함.
                    missionCount = 0;
                }
            }else{
                Toast.makeText(this, "수행지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                missionCount = 0;
            }
        }catch (Exception e){
            Toast.makeText(this, "수행지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 도착지 보기
    public void showToMeettingLocation(){
        try {
            if( meettingLatLng != null){
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(meettingLatLng, 16));
            }else{
                Toast.makeText(this, "도착지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "도착지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 세이브 버튼
    public void setSaveMapLocaiont(){
        // setResult 사용
        Intent returnToList = new Intent();

        if ( missionLatLng != null){
            returnToList.putParcelableArrayListExtra("MissionLocation", missionLatLng);
        }
        if (meettingLatLng != null){
            String meettingString = meettingLatLng.toString(); //문자열로 변환
            returnToList.putExtra("MeettingLocation", meettingString);
        }

        setResult(RESULT_OK, returnToList);
        finish();
    }

    // 취소버튼
    public void setCancelSaveMapLocation(){
        // 다이얼로그
        // ( 예정 )진짜로 취소할것인지 다이얼로그 만들예정
        noSaveData();

    }


    //--------------------------------------지도 검색 ----------------------------------------------

    // (지도 검색)  ( 검색한 장소의 위치를 보여줌  마커 X) 구글 맵 검색 API
    public void search() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // 검색어 마커에 띄워주기
                // String name = (String) place.getName();


                // 검색 위치 가지고오기
                LatLng searchLoction = place.getLatLng();


                // 검색한 위치로 카메라 이동.
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLoction, 16));

                // 마커찍기
//                gm.addMarker(new MarkerOptions().position(searchLoction).title("").icon(BitmapDescriptorFactory
//                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            }

            @Override
            public void onError(Status status) {
            }
        });

    }

    // -------------------------------------현재 위치 ----------------------------------------------
    // 현재위치 받아오기  ( 현재위치 버튼을 누르거나, 맵에 왔을때 처음 보여줄 위치 )
    public void findCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);    // Stop the update if it is in progress.

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            gm.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        if(  isOnline == true ){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);  // 네트워크 로 받음
        }else  if ( gpsEnable == true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);  // GPS로 받음
        }else {

        }
    }

    // (로케이션 리스너) 현재 위치 결과를 requestLocationUpdates 에 전달해준다 .
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 위치 변화시 발생하는 곳

            Log.d("유저의 현재 위치 ","///"+location);

            // 유저의 현재 위치 저장
            userCurrentLocationLat = location.getLatitude();
            userCurrentLocationLng = location.getLongitude();

            // 사용자의 현재위치나 디폴트위치를 연결상태에 따라 보여준다.
            if ( userCurrentLocationLat != null){
                //사용자의 현재위치를 가지고 왔음
                userCurrentLocat = new LatLng(userCurrentLocationLat, userCurrentLocationLng);
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocat, 14));
                isGetUserLocation = true;
            }else{
                //사용자의 위치를 못가지고옴 디폴트위치
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(deaultLocation, 14));
            }

            locationManager.removeUpdates(this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };




    // ---------------------------------권한 및 상태 확인-------------------------------------------

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

    // GPS 작동 확인
    public void isOnGPS() {
        //GPS가 켜져있는지 체크
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("GPS 연결 확인");
        ab.setMessage("GPS가 연결되어 있지 않습니다. 연결 하시겠습니까?");
        ab.setPositiveButton("연결", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //GPS 설정화면으로 이동
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }
        });


        ab.setNegativeButton("거부", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 체크해서  false 그대로 인경우에는 다시 연결 확인을 함.
                gpsEnable = false;
            }
        });

        ab.show();
    }

    // 네트워크 연결 상태 확인  연결이됬을 시에  isOnline 는 true를 반환
    public void isConnectedInternet(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
            if (wifi == NetworkInfo.State.CONNECTED) {
                isOnline = true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
            if (mobile == NetworkInfo.State.CONNECTED) {
                isOnline = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GPS 연결 상태 확인   연결이 된다면   gpsEnable true
    public void isConnectedGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
            gpsEnable = true;
        } else {
            gpsEnable = false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // 취소또는 뒤로가시 시 다이얼로그
    public void noSaveData() {
        android.support.v7.app.AlertDialog.Builder setBack = new android.support.v7.app.AlertDialog.Builder(editAskHelpMap.this);

        setBack.setTitle("알림");
        if ( preExistMission == true || preExistMeetting == true ){
            setBack.setMessage("취소시 변경된 위치정보가 \n저장되지 않습니다.\n취소 하시겠습니까?");
        }else{
            setBack.setMessage("취소시 위치정보가 저장되지 않습니다.\n취소 하시겠습니까?");
        }


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

}
