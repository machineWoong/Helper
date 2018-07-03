package com.example.jeon.helper.helpList;

import java.io.Serializable;

/**
 * Created by JEON on 2018-05-12.
 */

public class getMyAskDataContent implements Serializable{

    String key;
    String makeDate;

    String title;
    String sDate;
    String eDate;
    String content;

    String pay;
    String gender;
    String helper;  // 모집인원
    String acceptHelpCount;  // 수락한 인원

    String address;
    String location;

    String applyHelperCount;  // 지원한 인원.

    String onGoingState;

    public getMyAskDataContent(String key,String makeDate, String title, String sDate, String eDate, String content, String pay,
                               String gender, String helper, String acceptHelpCount,String address,String location, String applyHelperCount,String onGoingState) {
        this.key = key;
        this.makeDate = makeDate;
        this.title = title;
        this.sDate = sDate;
        this.eDate = eDate;
        this.content = content;
        this.pay = pay;
        this.gender = gender;
        this.helper = helper;
        this.acceptHelpCount=acceptHelpCount;
        this.address = address;
        this.location = location;
        this.applyHelperCount = applyHelperCount;
        this.onGoingState = onGoingState;
    }
}
