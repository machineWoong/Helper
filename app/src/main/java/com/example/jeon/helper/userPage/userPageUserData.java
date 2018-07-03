package com.example.jeon.helper.userPage;

import java.io.Serializable;

/**
 * Created by JEON on 2018-05-16.
 */

public class userPageUserData implements Serializable {

    public String loginMode;
    public String id;
    public String nick;

    public String eMail;
    public String gender;
    public String location;
    public String profilePath;


    public String giveCount;
    public String askCount;
    public String penalty;

    public String userGPA;
    public String introduce;

    public userPageUserData(String loginMode, String id, String nick, String eMail, String gender, String location, String profilePath, String giveCount, String askCount, String penalty, String userGPA, String introduce) {
        this.loginMode = loginMode;
        this.id = id;
        this.nick = nick;

        this.eMail = eMail;
        this.gender = gender;
        this.location = location;

        this.profilePath = profilePath;
        this.giveCount = giveCount;
        this.askCount = askCount;

        this.penalty = penalty;
        this.userGPA = userGPA;
        this.introduce = introduce;
    }
}
