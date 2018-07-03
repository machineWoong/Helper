package com.example.jeon.helper.chatting;

import android.content.Context;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by JEON on 2018-06-01.
 */

public class chattingRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    chattingRoomContent chattingData;
    Context context;
    ArrayList<chattingMessageContent> messageData = new ArrayList<>();

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    String[] date1;

    String[] comdate;


    public chattingRoomAdapter(chattingRoomContent chattingData, Context context, ArrayList<chattingMessageContent> messageData) {
        this.chattingData = chattingData;
        this.context = context;
        this.messageData = messageData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_message, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        date1 = null;
        date1 = messageData.get(position).time.split("!");


        // 날짜
        setChangeDate(holder,position,date1[1]);


        // 수신  ( 이미지 닉네임 시간, 할말 )
        if (messageData.get(position).mode == 1) {

            // 이미지 설정
            if (messageData.get(position).senderId.equals(chattingData.targetId) && chattingData.targetProfile.equals("이미지 없음")) {

            } else {
                if (chattingData.targetProfile.contains("http://")) {
                    Glide.with(context).load(chattingData.targetProfile).into(((RowCell) holder).ChattingBubbleProfilImage);
                } else {
                    Glide.with(context).load(ipad + "/" + chattingData.targetProfile).into(((RowCell) holder).ChattingBubbleProfilImage);
                }
            }
            // 닉네임 설정
            //   ((RowCell)holder).ChattingBubbleNickName.setText(messageData.get(position).senderNick);

            Log.d("보낸 사람 닉네임", "" + messageData.get(position).senderNick);
            ((RowCell) holder).ChattingBubbleNickName.setText(messageData.get(position).senderNick);

            // 시간 설정
            // ((RowCell) holder).ChattingBubbleReceiveTime.setText(messageData.get(position).time);
            ((RowCell) holder).ChattingBubbleReceiveTime.setText(date1[2]);

            //할말
            ((RowCell) holder).ChattingBubbleComment.setText(messageData.get(position).message);
            ((RowCell) holder).ChattingBubbleComment.setBackgroundResource(R.drawable.sendbubble);

            // 레이아웃 설정
            ((RowCell) holder).sendBubbleLayout.setVisibility(View.VISIBLE);
            ((RowCell) holder).sendBubbleLayout.setVisibility(View.GONE);
            ((RowCell) holder).receiveBubbleLayout.setVisibility(View.VISIBLE);
        }

        // 발신
        else if (messageData.get(position).mode == 2) {
            ((RowCell) holder).receiveBubbleLayout.setVisibility(View.VISIBLE);
            ((RowCell) holder).receiveBubbleLayout.setVisibility(View.GONE);
            ((RowCell) holder).sendBubbleLayout.setVisibility(View.VISIBLE);
            //할말
            ((RowCell) holder).ChattingBubbleSendComment.setText(messageData.get(position).message);
            ((RowCell) holder).ChattingBubbleSendComment.setBackgroundResource(R.drawable.receivebubble);

            // 시간 설정
            ((RowCell) holder).ChattingBubbleSendTime.setText(date1[2]);
            // ((RowCell) holder).ChattingBubbleSendTime.setText(messageData.get(position).time);
        }


        // --------------------------------------   이전 다음 글에 따른 위젯 숨김 처리 -----------

        if (messageData.size() == 1) {

        } else {
            hideReciverWidget(holder, position);
        }

        if (messageData.size() >= 2) {
            hideSenderWidget(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return messageData.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder {

        // 수신
        public TextView ChattingBubbleNickName, ChattingBubbleComment, ChattingBubbleReceiveTime;
        public ImageView ChattingBubbleProfilImage;
        public LinearLayout receiveBubbleLayout;


        // 발신
        public TextView ChattingBubbleSendComment, ChattingBubbleSendTime;
        public LinearLayout sendBubbleLayout;

        // 날짜 경계
        public TextView dateChangeText;
        public LinearLayout dateChangeLayout;

        public RowCell(View view) {
            super(view);
            // 수신
            ChattingBubbleNickName = (TextView) view.findViewById(R.id.ChattingBubbleNickName);
            ChattingBubbleComment = (TextView) view.findViewById(R.id.ChattingBubbleComment);
            ChattingBubbleReceiveTime = (TextView) view.findViewById(R.id.ChattingBubbleReceiveTime);
            ChattingBubbleProfilImage = (ImageView) view.findViewById(R.id.ChattingBubbleProfilImage);
            receiveBubbleLayout = (LinearLayout) view.findViewById(R.id.receiveBubbleLayout);


            // 발신
            ChattingBubbleSendComment = (TextView) view.findViewById(R.id.ChattingBubbleSendComment);
            ChattingBubbleSendTime = (TextView) view.findViewById(R.id.ChattingBubbleSendTime);
            sendBubbleLayout = (LinearLayout) view.findViewById(R.id.sendBubbleLayout);

            // 날짜 경계
            dateChangeLayout = (LinearLayout) view.findViewById(R.id.dateChangeLayout);
            dateChangeText = (TextView) view.findViewById(R.id.dateChangeText);

        }


    }

    // 메세지 수신 위젯 이벤트 처리
    public void hideReciverWidget(RecyclerView.ViewHolder holder, int position) {
        // 머리

        if (position == 0 && messageData.get(position).mode == 1 && messageData.get(position + 1).mode == 1) {
            if (messageData.get(position).time.equals(messageData.get(position + 1).time)) {
                ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.GONE);
            } else {
                ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
            }
        }

        // 몸통
        if (position != 0 && position + 1 < messageData.size() && messageData.get(position).mode == 1) {

            if (messageData.get(position - 1).mode == 1 && messageData.get(position - 1).time.equals(messageData.get(position).time)) {

                if (messageData.get(position + 1).mode == 2 || !messageData.get(position + 1).time.equals(messageData.get(position).time)) {
                    ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.INVISIBLE);
                    ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.GONE);
                    ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
                } else {
                    if (messageData.get(position + 1).mode == 1) {
                        ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.INVISIBLE);
                        ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.GONE);
                        ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.GONE);
                    } else {
                        ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.INVISIBLE);
                        ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.GONE);
                      //  ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
                        ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.GONE);
                    }
                }
            } else if (messageData.get(position + 1).mode == 1 && messageData.get(position).time.equals(messageData.get(position + 1).time)) {
                ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.GONE);
            } else {
                ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
            }

        }

        // 꼬리
        if (position + 1 == messageData.size()) {
            if (messageData.get(position - 1).mode == 1) {
                if (messageData.get(position).time.equals(messageData.get(position - 1).time)) {
                    ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.INVISIBLE);
                    ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.GONE);
                    ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
                } else {
                    ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.VISIBLE);
                    ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.VISIBLE);
                    ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
                }
            } else {
                ((RowCell) holder).ChattingBubbleProfilImage.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleNickName.setVisibility(View.VISIBLE);
                ((RowCell) holder).ChattingBubbleReceiveTime.setVisibility(View.VISIBLE);
            }
        }
    }

    // 메세지 발신 위젯 이벤트 처리
    public void hideSenderWidget(RecyclerView.ViewHolder holder, int position) {


        // 몸통 else 머리

        if (position + 2 <= messageData.size()) {
            if (messageData.get(position + 1).mode == 2) {

                if (messageData.get(position).time.equals(messageData.get(position + 1).time)) {
                    ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.GONE);
                } else {
                    ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.VISIBLE);
                }

            } else {
                ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.VISIBLE);
            }
        } else {
            ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.GONE);
        }


        // 꼬리

        if (position + 1 == messageData.size()) {
            if (messageData.get(position - 1).mode == 2) {
                if (messageData.get(position).time.equals(messageData.get(position - 1).time)) {
                    ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.VISIBLE);
                } else {
                    ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.VISIBLE);
                }
            } else {
                ((RowCell) holder).ChattingBubbleSendTime.setVisibility(View.VISIBLE);
            }
        }


    }

    // 날짜 변경 이벤트 처리
    public void setChangeDate(RecyclerView.ViewHolder holder,int position, String a){

        if( position == 0){
            // 첫대화
            ((RowCell) holder).dateChangeLayout.setVisibility(View.VISIBLE);
            ((RowCell) holder).dateChangeText.setVisibility(View.VISIBLE);
            ((RowCell) holder).dateChangeText.setText(a);
        }else if (position != 0 && messageData.size() >= 2){
            // 그 이후의 대화.
            comdate = messageData.get(position-1).time.split("!");

            if( !a.equals(comdate[1])){
                ((RowCell) holder).dateChangeLayout.setVisibility(View.VISIBLE);
                ((RowCell) holder).dateChangeText.setVisibility(View.VISIBLE);
                ((RowCell) holder).dateChangeText.setText(a);
            }else{
                ((RowCell) holder).dateChangeLayout.setVisibility(View.GONE);
                ((RowCell) holder).dateChangeText.setVisibility(View.GONE);
            }
        }else{
            ((RowCell) holder).dateChangeLayout.setVisibility(View.GONE);
            ((RowCell) holder).dateChangeText.setVisibility(View.GONE);
        }

    }

}
