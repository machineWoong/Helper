package com.example.jeon.helper.noti_table;

import java.io.Serializable;

/**
 * Created by JEON on 2018-06-12.
 */

public class noti_Content implements Serializable {

    public String no;
    public String notiMode;
    public String title;
    public String content;
    public String makeNick;
    public String makeDate;
    public String image;

    public noti_Content(String no, String notiMode, String title, String content, String makeNick, String makeDate, String image) {
        this.no = no;
        this.notiMode = notiMode;
        this.title = title;
        this.content = content;
        this.makeNick = makeNick;
        this.makeDate = makeDate;
        this.image = image;
    }
}
