package com.example.jeon.helper.chatting;

/**
 * Created by JEON on 2018-06-01.
 */

public class chattingRoomContent {

    String loginUserId;
    String loginUserNick;
    String targetId;
    String targetNickName;
    String targetProfile;


    public chattingRoomContent(String loginUserId, String loginUserNick, String targetId, String targetNickName, String targetProfile) {
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
        this.targetId = targetId;
        this.targetNickName = targetNickName;
        this.targetProfile = targetProfile;
    }
}
