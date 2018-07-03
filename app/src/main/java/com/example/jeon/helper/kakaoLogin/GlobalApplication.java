package com.example.jeon.helper.kakaoLogin;

import android.app.Activity;
import android.app.Application;
import android.icu.text.LocaleDisplayNames;
import android.util.Log;
import android.widget.Toast;

import com.example.jeon.helper.ip;
import com.github.nkzawa.socketio.client.IO;
import com.kakao.auth.KakaoSDK;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by JEON on 2018-04-21.
 */


/**
 * 이미지를 캐시를 앱 수준에서 관리하기 위한 애플리케이션 객체이다.
 * 로그인 기반 샘플앱에서 사용한다.
 *
 * @author MJ
 */

public class GlobalApplication extends Application {

    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    com.github.nkzawa.socketio.client.Socket socket;



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        KakaoSDK.init(new KakaoSDKAdapter());


    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }



    // 소켓 통신
    public void setSocket(com.github.nkzawa.socketio.client.Socket socket){
        this.socket = socket;
    }

    public com.github.nkzawa.socketio.client.Socket getSocket(){
        return  socket;
    }

}
