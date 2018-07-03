package com.example.jeon.helper.chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.userPage.userPageMainActivity;
import com.github.nkzawa.socketio.client.Socket;

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

public class friend_Ask_list_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<friend_Ask_List_Content> faL = new ArrayList<>();
    Context context;
    String loginUserId;
    String loginUserNick;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    Socket so;

    public friend_Ask_list_Adapter(ArrayList<friend_Ask_List_Content> faL, Context context,String loginUserId,Socket so,String loginUserNick) {
        this.faL = faL;
        this.context = context;
        this.loginUserId = loginUserId;
        this.so = so;
        this.loginUserNick = loginUserNick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_friend_ask_list_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // 닉네임 설정
        ((RowCell)holder).friend_ask_nick.setText(faL.get(position).userNick);

        // 프로필 설정
        if(faL.get(position).userProfile.equals("없음")){
        }else{
            if(faL.get(position).userProfile.contains("http://k.kakaocdn.net")){
                Glide.with(context).load(faL.get(position).userProfile).into(((RowCell) holder).friend_ask_profile);
            }else {
                Glide.with(context).load(ipad+"/"+faL.get(position).userProfile).into(((RowCell) holder).friend_ask_profile);
            }
        }

        // 상태 설정
        if ( faL.get(position).accepMode == 0){  // 수락대기중
            ((RowCell)holder).friend_ask_accept.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_diny.setVisibility(View.VISIBLE);

            ((RowCell)holder).friend_ask_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickEvent(1,(RowCell)holder,faL.get(position).userId);
                }
            });

            ((RowCell)holder).friend_ask_diny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 거절
                    clickEvent(2,(RowCell)holder,faL.get(position).userId);
                }
            });

        }else if ( faL.get(position).accepMode == 2){
            ((RowCell)holder).friend_ask_accept.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_accept.setVisibility(View.GONE);

            ((RowCell)holder).friend_ask_diny.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_diny.setVisibility(View.GONE);

            ((RowCell)holder).friend_ask_state.setText("거절함");
            ((RowCell)holder).friend_ask_state.setBackgroundColor(Color.RED);
            ((RowCell)holder).friend_ask_state.setVisibility(View.VISIBLE);
        }

        ((RowCell)holder).friend_ask_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayout(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return faL.size();
    }


    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder {

        public TextView friend_ask_nick,friend_ask_accept,friend_ask_diny,friend_ask_state;
        public ImageView friend_ask_profile;
        public LinearLayout friend_ask_item_layout;

        public RowCell(View view) {
            super(view);
            friend_ask_nick = (TextView) view.findViewById(R.id.friend_ask_nick);
            friend_ask_profile = (ImageView) view.findViewById(R.id.friend_ask_profile);
            friend_ask_item_layout = (LinearLayout) view.findViewById(R.id.friend_ask_item_layout);

            // 버튼
            friend_ask_accept = (TextView) view.findViewById(R.id.friend_ask_accept);
            friend_ask_diny = (TextView) view.findViewById(R.id.friend_ask_diny);
            // 상태
            friend_ask_state = (TextView) view.findViewById(R.id.friend_ask_state);
        }
    }

    // 수락 또는 거절 클릭 이벤트
    public void clickEvent(int mode,RecyclerView.ViewHolder holder,String id){
        if(mode == 1){
            // 수락
            setFriendAccept(1,id);

            ((RowCell)holder).friend_ask_accept.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_accept.setVisibility(View.GONE);

            ((RowCell)holder).friend_ask_diny.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_diny.setVisibility(View.GONE);

            ((RowCell)holder).friend_ask_state.setText("수락함");
            ((RowCell)holder).friend_ask_state.setBackgroundColor(Color.GREEN);
            ((RowCell)holder).friend_ask_state.setVisibility(View.VISIBLE);


        }else if (mode == 2){
            // 거절
            setFriendAccept(2,id);

            ((RowCell)holder).friend_ask_accept.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_accept.setVisibility(View.GONE);

            ((RowCell)holder).friend_ask_diny.setVisibility(View.VISIBLE);
            ((RowCell)holder).friend_ask_diny.setVisibility(View.GONE);

            ((RowCell)holder).friend_ask_state.setText("거절함");
            ((RowCell)holder).friend_ask_state.setBackgroundColor(Color.RED);
            ((RowCell)holder).friend_ask_state.setVisibility(View.VISIBLE);
        }
    }

    // (서버 연결) 수락 1 또는 거절 2
    public void setFriendAccept(int mode,String id){
        class getRepleDataHttp extends AsyncTask<Void,Void,String> {

            String result2;
            ProgressDialog dialog = new ProgressDialog(context);

            int mode;
            String id;

            public getRepleDataHttp(int mode, String id) {
                this.mode = mode;
                this.id = id;
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
                    URL url = new URL(ipad+"/friendAcceptOrDiny.php");
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
                    buffer.append("mode").append("=").append(mode).append("&");
                    buffer.append("AskId").append("=").append(id);


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

                String dataString;

                if(mode == 1){
                    // 수락
                    dataString = loginUserId+"@"+id+"@"+loginUserNick+"@"+"수락"+"@";
                    so.emit("requastFriendResult",dataString);
                }else if(mode == 2){
                    // 거절
                    dataString = loginUserId+"@"+id+"@"+loginUserNick+"@"+"거절"+"@";
                    so.emit("requastFriendResult",dataString);
                }

                Log.d("친구 수락 리스트 결과",s);
            }
        }

        getRepleDataHttp getData = new getRepleDataHttp(mode,id);
        getData.execute();
    }

    // 레이아웃 클릭 이벤트  (상대방 프로필 보여주기 )
    public void setLayout(int position){
        Intent showUserPage = new Intent(context,userPageMainActivity.class);
        showUserPage.putExtra("loginUserId",loginUserId);
        showUserPage.putExtra("userId",faL.get(position).userId);
        ((chattingMainActivity)context).startActivity(showUserPage);

    }


}
