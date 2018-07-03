package com.example.jeon.helper.chatting;

/**
 * Created by JEON on 2018-05-30.
 */

public class friend_Ask_List_Content {

    String userId;
    String userNick;
    String userProfile;
    int accepMode;


    public friend_Ask_List_Content(String userId, String userNick, String userProfile, int accepMode) {
        this.userId = userId;
        this.userNick = userNick;
        this.userProfile = userProfile;
        this.accepMode = accepMode;
    }
}
