package com.example.jeon.helper.chatting;

/**
 * Created by JEON on 2018-06-01.
 */

public class chattingMessageContent {

    int mode;
    String senderId;
    String senderNick;
    String message;
    String time;


    public chattingMessageContent(int mode, String senderId, String senderNick, String message, String time) {
        this.mode = mode;
        this.senderId = senderId;
        this.senderNick = senderNick;
        this.message = message;
        this.time = time;
    }
}
