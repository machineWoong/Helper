package com.example.jeon.helper.askHelp;

import java.io.Serializable;

/**
 * Created by JEON on 2018-05-02.
 */

public class showMyAskHelpShowMyContent implements Serializable {

    String title;
    String location;
    String address;
    String pay;
    String gender;
    String mission;
    String meetting;
    String photo;
    String sdate;
    String edate;
    String helper;
    String content;
    String key;

    String state; // 이상태는 apply 상태 ( askHelpContent DB에서  acceptHelperCount 의 값이 0이 아닌경우 수정 삭제 안되게 함



    public showMyAskHelpShowMyContent(String title,String location,String address,String pay,String gender,
                                      String mission,String meetting,String photo,String sdate,String edate,String helper,
                                      String content,String key,String state){
        this.title = title;
        this.location = location;
        this.address = address;
        this.pay = pay;
        this.gender = gender;
        this.mission =mission;
        this.meetting = meetting;
        this.photo = photo;
        this.sdate = sdate;
        this.edate = edate;
        this.helper = helper;
        this.content = content;
        this.key = key;
        this.state = state;
    }
}
