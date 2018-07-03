package com.example.jeon.helper;

/**
 * Created by JEON on 2018-05-11.
 */

public class ip {
    // 사무실
    String ipAd = "http://192.168.10.171";
    String ipadd2 ="192.168.10.171";

    //집192.168.0.2
    //String ipAd = "http://192.168.0.2";
    //String ipadd2 ="192.168.0.2";
    int port = 9999;

    String nodeIp = ipAd+":9999";

    public String getIp(){
        return ipAd;
    }

    public String getIp2(){
        return ipadd2;
    }

    public int getPort(){
        return port;
    }

    public String getNodeIp (){
        return nodeIp;
    }
}
