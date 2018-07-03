package com.example.jeon.helper.fireBase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.jeon.helper.HelperMain;
import com.example.jeon.helper.R;
import com.example.jeon.helper.chatting.chattingMainActivity;
import com.example.jeon.helper.chatting.chattingRoom;
import com.example.jeon.helper.helpList.helpListMain;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.userPage.myAllRecomment;
import com.example.jeon.helper.userPage.userPageMainActivity;
import com.github.nkzawa.emitter.Emitter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by JEON on 2018-06-14.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{


    String loginUserId;
    String loginUserNick;
    int room;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 서버를 통해서 메세지가 왔을 경우 ( 알림창을 만드는 곳 )
        super.onMessageReceived(remoteMessage);

        // 서버에서 보낸 데이터들을 맵의 형식으로 저장한다.
        Map<String, String> data = remoteMessage.getData();

        // 꺼내 쓸때는 서버에서 담아 보낸 변수명을 키값으로 가지고 사용한다 .
        String mode = data.get("FCMmode");
        Log.d(" FCM 이 모드 ",""+mode);

        String senderId = data.get("FCMsender");
        Log.d(" FCM 이 보낸아이디 ",""+senderId);

        String msg = data.get("FCMmessage");
        Log.d(" FCM 이 메세지내용 ",""+msg);

        String senderNick = data.get("FCMsenderNick");
        Log.d(" FCM 이 보낸사람 닉 ",""+senderNick);

        String roomNo = data.get("FCMroomNo");
        Log.d(" FCM 이 방번호 ",""+roomNo);

        int notiMode = Integer.parseInt(mode);
        int no = Integer.parseInt(roomNo);

        try {
            getUserData();
        }catch (Exception e){

        }

        try {
            getRoomNo();
        }catch (Exception e){

        }


        wakeApp();


        if(notiMode == 1){ // 메세지 알람
            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("message", true);

            if( result == true){
                msgNoti(msg,senderNick,no);
            }else{
            }

        }else if(notiMode == 2){ // 지원한 알람

            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("help", true);

            if( result == true){
                applyNoti(msg,senderNick);
            }else{
            }

        }else if(notiMode == 3){ // 지원 수락 거절 알람  ( 보류 )

            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("help", true);

            if( result == true){
                applyResultNoti(msg,senderNick);
            }else{
            }

        }else if(notiMode == 4){// 정산


            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("exchange", true);

            if( result == true){
                exchangeNoti(senderNick);
            }else{
            }
        }else if(notiMode == 5){ // 댓글 작성시


            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("comment", true);

            if( result == true){
                commentNoti(senderNick);
            }else{
            }

        }else if(notiMode == 6){ // 친구 신청시

            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("friend", true);

            if( result == true){
                requastFriendNoti(senderNick);
            }else{
            }

        }else if(notiMode == 7){ // 친구 신청 결과 알림

            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            Boolean result = pref.getBoolean("friend", true);

            if( result == true){
                requastFriendResultNoti(senderNick,msg);
            }else{
            }

        }

    }


    // ----------------------------------------채팅 관련 알림 -------------------------------------
    // 메세지 알람. ( 채팅방으로의 이동   방번호와,  로그인 유저 아이디, 로그인 유저 닉네임 )
    public void msgNoti(String msg, String senderNick,int roomNo){


        if (roomNo == room){
            Log.d("현재 보고 있는 방입니다. ","그렇습니다.");
        }else {
            //노티 클릭시 이동하는 곳.
            Intent intent = new Intent(this, chattingRoom.class);
            intent.putExtra("chattingRoomNo",roomNo);
            intent.putExtra("loginUserId",loginUserId);
            intent.putExtra("loginUserNick",loginUserNick);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Log.d("로그인 아이디 @@@@@",""+loginUserNick);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.help))
                    .setSmallIcon(R.drawable.help)
                    .setContentTitle("Helper")
                    .setContentText(senderNick+"님이 메세지를 보냈습니다.")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
    public void getUserData(){
        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        loginUserId = pref.getString("loginUserId",null);
        loginUserNick = pref.getString("loginUserNick",null);
    }
    // 수신자가 보고있는 방번호를 리턴한다.
    public void getRoomNo(){
        SharedPreferences enteredRoom = getSharedPreferences("enteredRoom", MODE_PRIVATE);
        room  = enteredRoom.getInt("roomNo",64000);
        Log.d("수신 한 방 번호@@@@",""+room);
    }

    // -------------------------------------- 지원자 알림 -----------------------------------------
    public void applyNoti(String title,String senderNick){
        Intent intent = new Intent(this, helpListMain.class);
        intent.putExtra("loginUserId",loginUserId);
        //intent.putExtra("loginUserNick",loginUserNick);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d("로그인 아이디 @@@@@",""+loginUserNick);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.help))
                .setSmallIcon(R.drawable.help)
                .setContentTitle("Helper")
                .setContentText(senderNick+"님이 "+title+" 에 지원하셨습니다.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

    //  ------------------------------------- 수락 또는 거절에 대한 알림. -------------------------
    public void applyResultNoti(String title,String senderNick){
        Intent intent = new Intent(this, helpListMain.class);
        intent.putExtra("loginUserId",loginUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d("로그인 아이디 @@@@@",""+loginUserNick);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.help))
                .setSmallIcon(R.drawable.help)
                .setContentTitle("Helper")
                .setContentText(title+" 도움지원이 "+senderNick+"되었습니다.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

    // -----------------------------------  정산 완료에 대한 알림 ---------------------------------
    public void exchangeNoti(String senderNick){
        Intent intent = new Intent(this, userPageMainActivity.class);
        intent.putExtra("loginUserId",loginUserId);
        intent.putExtra("userId",loginUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d("로그인 아이디 @@@@@",""+loginUserNick);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.help))
                .setSmallIcon(R.drawable.help)
                .setContentTitle("Helper")
                .setContentText(senderNick+"님이 정산을 하셨습니다.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // ----------------------------------   댓글에 대한 알림  -------------------------------------
    public void commentNoti(String senderNick){
        Intent intent = new Intent(this, myAllRecomment.class);
        intent.putExtra("loginUserId",loginUserId);
        intent.putExtra("targetId",loginUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d("로그인 아이디 @@@@@",""+loginUserNick);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.help))
                .setSmallIcon(R.drawable.help)
                .setContentTitle("Helper")
                .setContentText(senderNick+"님이 댓글을 남기셨습니다.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // -----------------------------------   친구 신청 알림 ---------------------------------------
    public void requastFriendNoti(String senderNick){
        Intent intent = new Intent(this, chattingMainActivity.class);
        intent.putExtra("loginUserId",loginUserId);
        intent.putExtra("loginUserNickName",loginUserNick);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d("로그인 아이디 @@@@@",""+loginUserNick);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.help))
                .setSmallIcon(R.drawable.help)
                .setContentTitle("Helper")
                .setContentText(senderNick+"님이 친구신청을 하셨습니다.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // ------------------------------------ 친구 신청 결과 ---------------------------------------
    public void requastFriendResultNoti(String senderNick,String title){
        Intent intent = new Intent(this, chattingMainActivity.class);
        intent.putExtra("loginUserId",loginUserId);
        intent.putExtra("loginUserNickName",loginUserNick);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.d("로그인 아이디 @@@@@",""+loginUserNick);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.help))
                .setSmallIcon(R.drawable.help)
                .setContentTitle("Helper")
                .setContentText(senderNick+"님이 친구신청을"+ title+" 하셨습니다.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    //화면 깨우기
    public void wakeApp(){

        PowerManager pm =(PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK,"tag" );
        wakeLock.acquire(1000);

    }
}
