package com.example.jeon.helper.surrounding_Map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.google.android.gms.common.api.Status;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;


import static android.location.LocationManager.GPS_PROVIDER;

public class surround_loaction_data extends AppCompatActivity implements OnMapReadyCallback,PlacesListener {

    List<Marker> previous_marker = null;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surround_loaction_data);

        previous_marker = new ArrayList<Marker>();


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


        // 맵 설정
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.surround_mapSetting);
        mapFragment.getMapAsync(this);



        // 버튼 설정
        setBtn_event();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;

        // 현재위치 받아오기
        findCurrentLocation();


        // 지도 검색 사용  ( 검색한 장소의 위치를 보여줌  마커 X)
        search();

        // 유저의 위치를 가져오지 못한다면 자동으로 디폴트 위치를 보여줌
        if ( isGetUserLocation == false){
            gm.moveCamera(CameraUpdateFactory.newLatLngZoom(deaultLocation, 14));
        }

    }

    // 버튼이벤트
    public void setBtn_event(){

        ImageView restaurant = (ImageView)findViewById(R.id.restaurant);
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getBack(userCurrentLocat,PlaceType.RESTAURANT);
                }catch (Exception e){
                    Toast.makeText(surround_loaction_data.this, "현재 위치를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView hospital = (ImageView)findViewById(R.id.hospital);
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getBack(userCurrentLocat,PlaceType.HOSPITAL);
                }catch (Exception e){
                    Toast.makeText(surround_loaction_data.this, "현재 위치를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView store = (ImageView)findViewById(R.id.store);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getBack(userCurrentLocat,PlaceType.STORE);
                }catch (Exception e){
                    Toast.makeText(surround_loaction_data.this, "현재 위치를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView book_store = (ImageView)findViewById(R.id.book_store);
        book_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getBack(userCurrentLocat,PlaceType.BOOK_STORE);
                }catch (Exception e){
                    Toast.makeText(surround_loaction_data.this, "현재 위치를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView cafe = (ImageView)findViewById(R.id.cafe);
        cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    getBack(userCurrentLocat,PlaceType.CAFE);
                }catch (Exception e){
                    Toast.makeText(surround_loaction_data.this, "현재 위치를 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImageView surrond_Back = (ImageView)findViewById(R.id.surrond_Back);
        surrond_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //---------------------------------------- 연결 및 권환 확인 -----------------------------------

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

    // ------------------------------------- 현재 위치 받아오기 ----------------------------------

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
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocat, 16));
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

    //----------------------------------------------플레이스  리스너 -------------------------------
    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());


                    String markerSnippet = getCurrentAddress(latLng);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);
                    Marker item = gm.addMarker(markerOptions);

                    previous_marker.add(item);
                }


                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    // 주변 위치 찾아오기
    public void showPlaceInformation(LatLng deaultLocation,String data){
        //PlaceType.RESTAURANT  음식점

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        // 구글 플레이스 키
        String a = "AIzaSyCqpE-0lkTKgWKlLOmgPuL61LV-O2t8_IY";
        new NRPlaces.Builder()
                .listener(surround_loaction_data.this)
                .key(a)
                .latlng(deaultLocation.latitude,deaultLocation.longitude)//현재 위치
                .radius(300) //300 미터 내에서 검색
                .type(data) //음식점
                .build()
                .execute();
    }

    // 백그라운드로 로딩
    public void getBack(LatLng pointLocation,String searchData){
        class getPlaceData extends AsyncTask<Void,Void,Void>{

            ProgressDialog dialog = new ProgressDialog(surround_loaction_data.this);
            LatLng deaultLocation;  // 기준 위치
            String searchData;

            public getPlaceData(LatLng deaultLocation, String searchData) {
                this.deaultLocation = deaultLocation;
                this.searchData = searchData;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("주변 정보 검색중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
                gm.clear();//지도 클리어

                try{

                }catch (Exception e){
                    Toast.makeText(surround_loaction_data.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected Void doInBackground(Void... voids) {

                showPlaceInformation(deaultLocation,searchData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if ( dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(surround_loaction_data.this, "탐색 완료", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                }
            }


        }
        getPlaceData getData = new getPlaceData(pointLocation,searchData);
        getData.execute();
    }

    // 지오 코더
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    // ---------------------------------------- 검색 ---------------------------------------------
    // (지도 검색)  ( 검색한 장소의 위치를 보여줌  마커 X) 구글 맵 검색 API
    public void search() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.surround_place_autocomplete_fragment);

        gm.clear();//지도 클리어
        previous_marker.clear(); // 마커 모음

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                // 검색어 마커에 띄워주기
                String name = (String) place.getName();

                // 검색 위치 가지고오기
                LatLng searchLoction = place.getLatLng();

                // 검색한 위치로 카메라 이동.
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLoction, 16));

                // 마커찍기
                gm.addMarker(new MarkerOptions().position(searchLoction).title(name).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }

            @Override
            public void onError(Status status) {
            }
        });
    }


}
