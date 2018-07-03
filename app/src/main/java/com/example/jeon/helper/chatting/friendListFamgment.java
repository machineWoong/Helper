package com.example.jeon.helper.chatting;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class friendListFamgment extends android.support.v4.app.Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";


    ArrayList<friend_list_content> friend_List = new ArrayList<>();
    friend_list_content  fListContent;
    String [] firstFilter; // 첫번째 구분자
    String [] seccondFilter;  // 두번째 구분자

    String loginUserId;
    String loginUserNick;

    View view;
    RecyclerView rv;
    Context context;
    friendList_recyclerView_adapter fra;


    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    ArrayList<friend_list_content> findFriend = new ArrayList<>();


    public static friendListFamgment newInstance(int columnCount) {
        friendListFamgment fragment = new friendListFamgment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUserData();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chatting_friendlist_activity, container, false);

        rv = (RecyclerView) view.findViewById(R.id.friendList);
        context = view.getContext();

        rv.setHasFixedSize(true);


        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);



        try {
            fra = new friendList_recyclerView_adapter(friend_List,context,loginUserId);
            rv.setAdapter(fra);
        }catch (Exception e){

        }

        // 검색 이벤트
        findFriend();

        // Inflate the layout for this fragment
        return view;
    }

    public void setUserData(){
        loginUserId = ((chattingMainActivity)getActivity()).loginUserid;
        loginUserNick  = ((chattingMainActivity)getActivity()).loginUserNick;

        // 서버로 부터 데이터 받아오기
        getDataFriendList();
    }

    public void getDataFriendList(){

        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("친구목록을 불러오는 중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(true); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad+"/getFriendList.php");
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
                    buffer.append("loginUserId").append("=").append(loginUserId);                 // php 변수에 값 대입

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

                Log.d("친구 목록 리스트 받아오기 ",s);
                if( s.equals("없음")){
                    TextView tv = view.findViewById(R.id.noFriendList);
                    tv.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                }else{
                    divideData(s);
                }

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp();
        getData.execute();

    }

    public void divideData(String a){

        try {
            firstFilter = null;
            firstFilter = a.split("!"); // 게시글 별로 나누기 위함
            for ( int i = 0 ; i < firstFilter.length ; i++ ){
                seccondFilter = firstFilter[i].split("@");

                // 로그인 모드, URL , 유저 닉네임 순서,
                fListContent = new friend_list_content(seccondFilter[0],seccondFilter[1],seccondFilter[2]); // 객체 생성
                friend_List.add(fListContent); // 어레이 리스트에 객체 추가.
            }

        }catch (Exception e){

        }
    }

    // 검색
    public void findFriend(){

        SearchView search_friend = (SearchView)view.findViewById(R.id.search_friend);
        search_friend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if( query.equals("")){
                    try {
                        fra = new friendList_recyclerView_adapter(friend_List,context,loginUserId);
                        rv.setAdapter(fra);
                    }catch (Exception e){

                    }
                }else{
                    findFriend.clear();

                    for ( int i = 0 ; i < friend_List.size() ;i++){
                        if (friend_List.get(i).friendNick.contains(query)){
                            findFriend.add(friend_List.get(i));
                        }
                    }

                    try {
                        fra = new friendList_recyclerView_adapter(findFriend,context,loginUserId);
                        rv.setAdapter(fra);
                    }catch (Exception e){

                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    this.onQueryTextSubmit("");
                }
                return false;
            }
        });

    }

}
