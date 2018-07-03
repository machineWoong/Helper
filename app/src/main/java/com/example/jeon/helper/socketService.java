package com.example.jeon.helper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class socketService extends Service {

    String loginUserId;
    com.github.nkzawa.socketio.client.Socket socket;


    com.example.jeon.helper.ip ip = new ip();
    String nodeIp = ip.getNodeIp();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try{
            socket = IO.socket(nodeIp);
            socket.connect();

            //소켓 연결
            GlobalApplication gg = (GlobalApplication)getApplication();
            gg.setSocket(socket);

        }catch (Exception e){

        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 로그인 정보 받아오기  ( 노드 서버로 전송 )

        getLoginUserData(intent);
        socket.emit("loginUserId",loginUserId);
        socket.emit("fireToken", FirebaseInstanceId.getInstance().getToken());

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("서비스 종료 됨","서비스 종료 됨");
    }

    public void getLoginUserData(Intent intent){
        try {
            loginUserId = intent.getStringExtra("loginUserId");
        }catch (Exception e){
        }

    }
}
