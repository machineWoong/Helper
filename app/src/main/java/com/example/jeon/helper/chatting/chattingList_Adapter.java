package com.example.jeon.helper.chatting;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;
import com.github.nkzawa.emitter.Emitter;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-31.
 */

public class chattingList_Adapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<chattingList_Content> chatting_room_data = new ArrayList<>();
    Context context;
    String loginUserId;
    String loginUserNick;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    com.github.nkzawa.socketio.client.Socket so;

    Handler msgHandler;
    Integer getMessageAlram= 2222;

    public chattingList_Adapter(ArrayList<chattingList_Content> chatting_room_data, Context context, String loginUserId, String loginUserNick,com.github.nkzawa.socketio.client.Socket so) {
        this.chatting_room_data = chatting_room_data;
        this.context = context;
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
        this.so = so;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_room_item,parent,false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        // 방이미지 설정 (1:1 인경우 상대방의 프로필 이미지), 여러명인경우 방 아이콘 이미지
        if (chatting_room_data.get(position).count == 2 ){
            if( !chatting_room_data.get(position).roomIcon.equals("이미지 없음")){
                if( chatting_room_data.get(position).roomIcon.contains("http://k.kakaocdn.net")){
                    Glide.with(context).load(chatting_room_data.get(position).roomIcon).into(((RowCell) holder).chattingRoomIcon);
                }else{
                    Glide.with(context).load(ipad+"/"+chatting_room_data.get(position).roomIcon).into(((RowCell) holder).chattingRoomIcon);
                }
            }
        }

        // 방이름 설정
        ((RowCell)holder).chattingRoomPeople.setText(chatting_room_data.get(position).roomName);

        // 사람 수
        ((RowCell)holder).chattingRoomPeopleCount.setText("("+chatting_room_data.get(position).count+"명)");

        //레이아웃 클릭 이벤트
        ((RowCell)holder).chattingRoomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterChattingRoom(position);
                chatting_room_data.get(position).newMsg = 0;
                notifyItemChanged(position);

            }
        });

        if(chatting_room_data.get(position).newMsg == 1){
            ((RowCell)holder).newMessageCheck.setVisibility(View.VISIBLE);
            ((RowCell)holder).newMessageCheck.setVisibility(View.VISIBLE);
        }else{
            ((RowCell)holder).newMessageCheck.setVisibility(View.INVISIBLE);
            ((RowCell)holder).newMessageCheck.setVisibility(View.INVISIBLE);
        }

        so.on("newMessageAlram",newMessageAlream);
        handl();

    }

    private Emitter.Listener newMessageAlream = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //방번호를 받아온다 .
            String a = args[0].toString();
            Log.d("채팅방 어댑터 알람",""+a);
            for (int i = 0 ; i < chatting_room_data.size(); i++ ){
                if(chatting_room_data.get(i).roomNo == Integer.parseInt(a)){
                    chatting_room_data.get(i).newMsg = 1;
                    gotoHandler(i);
                    break;
                }
            }
        }
    };

    @Override
    public int getItemCount() {
        return chatting_room_data.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder{

        public TextView chattingRoomPeople,chattingRoomPeopleCount;
        public ImageView chattingRoomIcon, newMessageCheck;
        public LinearLayout chattingRoomLayout;

        public RowCell(View view) {
            super(view);
            chattingRoomPeople = view.findViewById(R.id.chattingRoomPeople);
            chattingRoomPeopleCount = view.findViewById(R.id.chattingRoomPeopleCount);

            chattingRoomIcon = view.findViewById(R.id.chattingRoomIcon);
            chattingRoomLayout = view.findViewById(R.id.chattingRoomLayout);


            newMessageCheck = view.findViewById(R.id.newMessageCheck);
        }
    }

    public void enterChattingRoom(int position){
        Intent gotoChattingRoom = new Intent(context,chattingRoom.class);
        gotoChattingRoom.putExtra("chattingRoomNo",chatting_room_data.get(position).roomNo);
        gotoChattingRoom.putExtra("loginUserId",loginUserId);
        gotoChattingRoom.putExtra("loginUserNick",loginUserNick);

        Log.d("채팅방 리스트에서 항목 클릭",""+loginUserNick);
        ((chattingMainActivity)context).startActivity(gotoChattingRoom);
    }


    // 핸들러
    // 메세지 알림 처리 하는 핸들러
    public void handl(){
        // 서버로부터 수신한 메세지를 처리하는 곳
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == getMessageAlram) {

                    String data = msg.obj.toString();
                    int a = Integer.parseInt(data);

                    notifyItemChanged(a);
                }
            }
        };
    }

    // 메세지 수신 알람 핸들러 처리
    public void gotoHandler(int a){

        Message hdmg = msgHandler.obtainMessage();
        // 핸들러에게 전달할 메세지의 식별자
        hdmg.what = getMessageAlram;
        // 메세지의 본문
        hdmg.obj = a;
        // 핸들러에게 메세지 전달 ( 화면 처리 )
        msgHandler.sendMessage(hdmg);

    }


}
