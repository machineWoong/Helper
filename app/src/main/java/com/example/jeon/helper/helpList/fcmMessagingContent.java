package com.example.jeon.helper.helpList;

/**
 * Created by JEON on 2018-06-19.
 */

public class fcmMessagingContent {

    String loginUserId;
    String targetId;
    String title;
    String result;

    public fcmMessagingContent(String loginUserId, String targetId, String title, String result) {
        this.loginUserId = loginUserId;
        this.targetId = targetId;
        this.title = title;
        this.result = result;
    }
}
