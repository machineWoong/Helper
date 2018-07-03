package com.example.jeon.helper.helpList;

/**
 * Created by JEON on 2018-05-16.
 */

public class showDetailContentContent {

    String title;
    String sDate;
    String eDate;

    String gender;
    String helper;
    String pay;

    String location;
    String address;

    String imagePath;

    String mission;
    String meetting;

    String content;

    String makeUserNick;
    String makeUserGender;
    String makeUserLocation;
    String makeAskPoint;
    String makeGivePoint;
    String makeUserProfile;

    public showDetailContentContent(String title, String sDate, String eDate, String gender, String helper,
                                    String pay, String location, String address, String imagePath, String mission,
                                    String meetting, String content, String makeUserNick, String makeUserGender,
                                    String makeUserLocation, String makeAskPoint,String makeGivePoint, String makeUserProfile) {
        this.title = title;
        this.sDate = sDate;
        this.eDate = eDate;
        this.gender = gender;
        this.helper = helper;
        this.pay = pay;
        this.location = location;
        this.address = address;
        this.imagePath = imagePath; //8 번 인덱스
        this.mission = mission; // 9번
        this.meetting = meetting; // 10번
        this.content = content;
        this.makeUserNick = makeUserNick;
        this.makeUserGender = makeUserGender;
        this.makeUserLocation = makeUserLocation;
        this.makeAskPoint = makeAskPoint;
        this.makeGivePoint = makeGivePoint;
        this.makeUserProfile = makeUserProfile;
    }
}
