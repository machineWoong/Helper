package com.example.jeon.helper.cash;

/**
 * Created by JEON on 2018-05-21.
 */

public class cash_Content {

    String senderId;
    String targetNickName;

    String date;
    String money;

    String title;
    String mode;

    public cash_Content(String senderId, String targetNickName, String date, String money, String title, String mode) {
        this.senderId = senderId;
        this.targetNickName = targetNickName;
        this.date = date;
        this.money = money;
        this.title = title;
        this.mode = mode;
    }
}
