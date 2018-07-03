package com.example.jeon.helper.helpList;

/**
 * Created by JEON on 2018-05-14.
 */

public class getApplyuserData {

    String loginMode;
    String userId;
    String userNick;
    String gender;
    String location;
    String profilePath;
    String givePoint;
    String askPoint;
    String penalty;
    String accept;
    String diny;
    Boolean choice;

    public getApplyuserData(String loginMode, String userId, String userNick, String gender, String location,
                            String profilePath, String givePoint, String askPoint, String penalty,String accept,String diny,Boolean choice) {
        this.loginMode = loginMode;
        this.userId = userId;
        this.userNick = userNick;
        this.gender = gender;
        this.location = location;
        this.profilePath = profilePath;
        this.givePoint = givePoint;
        this.askPoint = askPoint;
        this.penalty = penalty;
        this.accept =accept;
        this.diny = diny;
        this.choice = choice;
    }
}
