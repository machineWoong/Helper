package com.example.jeon.helper.exchange;

/**
 * Created by JEON on 2018-05-17.
 */

public class getApplyUserDataInExchange {

    String userId;
    String userNick;
    String gender;
    String location;
    String profilePath;
    String exChangeState;


    public getApplyUserDataInExchange(String userId, String userNick, String gender, String location, String profilePath,String exChangeState) {
        this.userId = userId;
        this.userNick = userNick;
        this.gender = gender;
        this.location = location;
        this.profilePath = profilePath;
        this.exChangeState = exChangeState;
    }


}
