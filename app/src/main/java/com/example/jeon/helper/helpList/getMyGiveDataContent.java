package com.example.jeon.helper.helpList;

/**
 * Created by JEON on 2018-05-12.
 */

public class getMyGiveDataContent {

    String key;

    String id;
    String nickName;

    String title;
    String location;
    String address;

    String sDate;
    String eDate;

    String pay;

    String accept;
    String diny;

    String state;
    String onGoing;

    public getMyGiveDataContent(String key, String id,String nickName, String title, String sDate, String eDate,
                                String pay, String address, String location,String accept,String diny,String state,String onGoing) {
        this.key = key;
        this.id = id;
        this.nickName = nickName;
        this.title = title;
        this.sDate = sDate;
        this.eDate = eDate;

        this.pay = pay;

        this.location = location;
        this.address = address;


        this.accept = accept;
        this.diny = diny;

        this.state = state;
        this.onGoing = onGoing;
    }
}
