package com.example.jeon.helper;

/**
 * Created by JEON on 2018-05-10.
 */

public class userAccount {

    // 아이디 이메일 성별 위치 프로필 이미지 경로,  가진돈, 수행중 미션 도와준 포인트,  도움받은 포인트 경고, 도움 점수

    public String userid;
    public String loginMode;
    public String eMail;
    public String gender;
    public String location;
    public String profilePath;
    public String money;
    public String onGoing;
    public String giveHelp;
    public String askHelp;
    public String penalty;
    public String helpCount;

    ip ip = new ip();
    String ipad = ip.getIp();

    public userAccount(String userid,String loginMode,String eMail,String gender,String location,String profilePath,String money,String onGoing,String giveHelp,String askHelp,
                       String penalty, String helpCount){
        this.userid = userid;
        this.loginMode = loginMode;
        this.eMail = eMail;
        this.gender =gender;
        this.location = location;
        this.profilePath = profilePath;
        this.money = money;
        this.onGoing = onGoing;
        this.giveHelp = giveHelp;
        this.askHelp = askHelp;
        this.penalty = penalty;
        this.helpCount = helpCount;
    }

    public String setHelpCount(){
        int result;
        result = Integer.parseInt(giveHelp)+Integer.parseInt(askHelp);
        helpCount = String.valueOf(result);
        return helpCount;
    }

    public String imagePath(){
        String result=null;

        if ( loginMode.equals("1")){
            //일반 로그인
            if (profilePath.equals("1")){
                // 이미지가 없는 경우
            }else{
                // 이미지가 있는 경우
                result=ipad+"/"+profilePath;
            }
        }
        else{
            // 카카오 로그인
            if(profilePath.contains("http://k.kakaocdn.net")){
                result = profilePath;
            }else{
                result = ipad+"/"+profilePath;
            }


        }


        return result;
    }

}
