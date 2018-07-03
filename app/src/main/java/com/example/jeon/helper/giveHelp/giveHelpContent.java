package com.example.jeon.helper.giveHelp;

import java.io.Serializable;

/**
 * Created by JEON on 2018-05-08.
 */

public class giveHelpContent implements Serializable {

    String makeTime;
    String sDate;
    String eDate;


    String title;
    String location;
    String address;

    String nickName;
    String pay;
    String gender;
    String helper;
    String map;

    String mission;
    String meetting;

    String imageUrl;
    String imagePath;

    String content;
    String key;

    public giveHelpContent(String makeTime,String sDate,String eDate,String title,String location,String address,String nickName,String pay,
                           String gender, String helper,String map,String mission,String meetting,String imageUrl,String imagePath,String content,String key){

        this.makeTime = makeTime;
        this.sDate = sDate;
        this.eDate = eDate;
        this.title= title;
        this.location = location;
        this.address = address;
        this.nickName = nickName;
        this.pay =pay;
        this.gender = gender;
        this.helper = helper;
        this.map = map;
        this.mission = mission;
        this.meetting = meetting;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;

        this.content = content;
        this.key = key;

    }



}
