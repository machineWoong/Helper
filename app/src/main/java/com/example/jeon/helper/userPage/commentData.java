package com.example.jeon.helper.userPage;

import java.io.Serializable;

/**
 * Created by JEON on 2018-05-24.
 */

public class commentData implements Serializable {

    String makeId;
    String makeDate;
    String makeContent;
    String mode;
    String makeNick;
    String makeProfile;
    int count;
    String no;

    public commentData(String makeId, String makeDate, String makeContent, String mode, String makeNick, String makeProfile,int count,String no) {
        this.makeId = makeId;
        this.makeDate = makeDate;
        this.makeContent = makeContent;
        this.mode = mode;
        this.makeNick = makeNick;
        this.makeProfile = makeProfile;
        this.count = count;
        this.no = no;


        this.makeDate = makeDate.replace("2018/","");
    }
}
