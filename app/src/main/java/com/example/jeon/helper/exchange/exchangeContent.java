package com.example.jeon.helper.exchange;

/**
 * Created by JEON on 2018-05-17.
 */

public class exchangeContent {

    String title;  // 지원글 제목
    String key;
    String makeDate; // 지원글을 올렸던 날짜.
    String applyeHelper;  // 수락했던 인원 수
    String pay;
    String state;  // 정산이 모두 됬는지 확인 하기 위함 0 인경우  미정산됨 1이면 정산됨  ( aksContent DB에서 exchangeState);





    public exchangeContent(String title, String key, String makeDate, String applyeHelper, String pay,String state) {
        this.title = title;
        this.key = key;
        this.makeDate = makeDate;
        this.applyeHelper = applyeHelper;
        this.pay = pay;

        this.state = state;

    }



}
