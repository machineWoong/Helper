package com.example.jeon.helper.chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.selectMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-30.
 */

public class friendList_recyclerView_adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<friend_list_content> friend_List = new ArrayList<>();
    Context context;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    String loginUserId;
    public friendList_recyclerView_adapter(ArrayList<friend_list_content> friend_List, Context context,String loginUserId) {
        this.friend_List = friend_List;
        this.context = context;
        this.loginUserId= loginUserId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_friend_list_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // 프로필 이미지 적용
        if(!friend_List.get(position).friendProfile.equals("없음")){
            if(friend_List.get(position).friendProfile.contains("http://k.kakaocdn.net")){
                Glide.with(context).load(friend_List.get(position).friendProfile).into(((RowCell)holder).friend_list_profile);
            }else{
                Glide.with(context).load(ipad+"/"+friend_List.get(position).friendProfile).into(((RowCell)holder).friend_list_profile);
            }
        }

        // 닉네임 적용
        ((RowCell)holder).friend_list_nick.setText(friend_List.get(position).friendNick);

        // 레이아웃 클릭 이벤트
        setClickLayout(position,holder);


    }

    @Override
    public int getItemCount() {
        return friend_List.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder {

        public TextView friend_list_nick;
        public ImageView friend_list_profile;
        public LinearLayout friend_list_layout;

        public RowCell(View view) {
            super(view);
            friend_list_nick = (TextView) view.findViewById(R.id.friend_list_nick);
            friend_list_profile = (ImageView) view.findViewById(R.id.friend_list_profile);
            friend_list_layout = (LinearLayout) view.findViewById(R.id.friend_list_layout);
        }
    }

    // 레이아웃클릭 이벤트
    public void setClickLayout(final int position, RecyclerView.ViewHolder holder ){
        ((RowCell)holder).friend_list_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSelect = new Intent(context,selectMenu.class);
                gotoSelect.putExtra("loginUserId",loginUserId);
                gotoSelect.putExtra("targetId",friend_List.get(position).friendId);
                ((chattingMainActivity)context).startActivity(gotoSelect);
            }
        });


        ((RowCell)holder).friend_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("친구삭제 이벤트","발생");
                deletFriend(friend_List.get(position).friendNick,friend_List.get(position).friendId,position);
                return true;
            }
        });

    }


    // 친구삭제 관련 다이얼로그
    public void deletFriend(String friendNick, final String friendId, final int position){
        AlertDialog.Builder cameraSelect = new AlertDialog.Builder(context);
        cameraSelect.setTitle("친구삭제");
        cameraSelect.setMessage(friendNick+"을 친구 목록에서 삭제하시겠습니까?");

        cameraSelect.setPositiveButton("삭제",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("삭제할 친구 아이디",""+friendId);
                deletFriendList(friendId);
                friend_List.remove(position);
                notifyDataSetChanged();
            }
        });

        cameraSelect.setNegativeButton("취소",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        cameraSelect.show();   // 실행
    }

    // ( 서버 전송 ) 친구삭제
    public void deletFriendList(String targetId){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(context);
            String targetId;

            public getRepleDataHttp(String targetId) {
                this.targetId = targetId;
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
                    URL url = new URL(ipad+"/deletFriend.php");
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
                    buffer.append("loginUserId").append("=").append(loginUserId).append("&");                 // php 변수에 값 대입
                    buffer.append("targetId").append("=").append(targetId);

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

                Log.d("친구삭제 결과",""+s);

            }
        }

        getRepleDataHttp getData = new getRepleDataHttp(targetId);
        getData.execute();
    }
}
