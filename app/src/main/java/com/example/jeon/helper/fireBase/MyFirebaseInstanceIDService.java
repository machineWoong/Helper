package com.example.jeon.helper.fireBase;

import android.util.Log;

import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by JEON on 2018-06-14.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    // 토큰이다


    @Override
    public void onTokenRefresh() {
        // 토큰이 갱신 되었을때 처리하는 곳.
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("파이어 베이스 토큰 ",refreshedToken);

        // 서버에 전송하기 ( okhttp나, node에 )  당신의 웹 서버에 전송하시오라고 써있음.
        sendRegistrationToServer(refreshedToken);

        // 토큰 :
        // note4
        // cMngWNlqCUs:APA91bEDtg3T6Sqp2juGIMo_NdZT0iaSI58-myU3az7W1tZUkq3rFSLkk_zWJnO5JeaJHIt9TbhMdZrOY3MKx_fkTZJSoCDU4cVP70Y3BeFDuBAvx05wr36mHzeJ9l637zcaPSrKT5BH

        // lg폰
        //cUWMgVcArec:APA91bHhiB7J4dzDQB_3NvW7_aF1rYSTvpSW-nFqTFIFhFGZoBAgln98qMdawe-OU4TMZSUyFHZr6h9a8A3oDxscbdrkhqllB8mXg7DX5lJlk1hqcqmBrpYPKrMNoeujqiQ2k1y_06OR

    }

    private void sendRegistrationToServer(String refreshedToken) {
        // 서버에 토큰을 전송할때 사용 하자.
        // 새로 바뀌게 되는 경우니까 서버에서  맵에  키와  같이 저장을 하면 되려나 ??

    }
}
