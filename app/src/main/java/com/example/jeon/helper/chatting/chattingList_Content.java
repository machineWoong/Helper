package com.example.jeon.helper.chatting;

import java.io.Serializable;

/**
 * Created by JEON on 2018-05-31.
 */

public class chattingList_Content implements Serializable{

    int roomNo;
    String roomIcon;
    String roomName;
    String [] people;
    int count;
    int newMsg;


    public chattingList_Content(int roomNo, String roomIcon, String roomName, String[] people, int count, int newMsg) {
        this.roomNo = roomNo;
        this.roomIcon = roomIcon;
        this.roomName = roomName;
        this.people = people;
        this.count = count;
        this.newMsg = newMsg;   // 1일 때 새로운 메세지
    }
}
