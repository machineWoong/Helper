package com.example.jeon.helper.userPage;

/**
 * Created by JEON on 2018-05-25.
 */

public class recomment_Data {

    String recomentProfile;
    String recomentNick;
    String recomentId;
    String recomentDate;
    String recomentContent;

    int recomentCount;
    int recomentNo;

    public recomment_Data(String recomentProfile, String recomentNick, String recomentId, String recomentDate, String recomentContent, int recomentCount,  int recomentNo) {
        this.recomentProfile = recomentProfile;
        this.recomentNick = recomentNick;
        this.recomentId = recomentId;
        this.recomentDate = recomentDate;
        this.recomentContent = recomentContent;
        this.recomentCount = recomentCount;
        this.recomentNo = recomentNo;

        this.recomentDate = recomentDate.replace("2018/","");
    }
}
