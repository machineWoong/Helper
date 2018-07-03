package com.example.jeon.helper.askHelp;

/**
 * Created by JEON on 2018-05-01.
 */

public class askHelpContent {

    String date;
    String title;
    String address;
    String pay;
    String gender;
    String map;
    String photo;
    String sdate;
    String edate;
    String contentKey;

    String state; // 이상태는 apply 상태 ( askHelpContent DB에서  acceptHelperCount 의 값이 0이 아닌경우 수정 삭제 안되게 함

    public askHelpContent(String date,String title,String address,String pay,String gender,String map,String photo,
                          String sdate,String edate,String contentKey,String state){
        this.date =date;
        this.title = title;
        this.address = address;
        this.pay = pay;
        this.gender = gender;
        this.map = map;
        this.photo = photo;
        this.sdate = sdate;
        this.edate = edate;
        this.contentKey = contentKey;
        this.state = state;
    }


}
