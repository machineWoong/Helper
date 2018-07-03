package com.example.jeon.helper;

/**
 * Created by JEON on 2018-04-23.
 */

public class bestHelper {

    String profileURL;  // 유저 프로필 경로
    String bestHelperNickName; // 유저 닉네임
    String loginMode;
    String id;


    public bestHelper(String a, String b, String c, String id) { //생성자
        loginMode = a;
        profileURL = b;
        bestHelperNickName = c;
        this.id = id;
    }

}
