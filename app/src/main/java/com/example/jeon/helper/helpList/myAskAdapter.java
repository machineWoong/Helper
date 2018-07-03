package com.example.jeon.helper.helpList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.addAskHelp;
import com.example.jeon.helper.giveHelp.giveHelpAdapter;
import com.example.jeon.helper.ip;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.internal.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class myAskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<getMyAskDataContent> MyAskDataArray;
    Context context;

    String loginUserId;
    String loginUserNick;

    ip ip = new ip();
    String ipad = ip.getIp();

    getApplyuserData getApplyuserData;
    ArrayList<getApplyuserData> AuD = new ArrayList<>();

    String[] firstFilter; // 첫번째 구분자
    String[] seccondFilter;  // 두번째 구분자

    // 다이얼로그
    View mView;
    getApplyUserDataAdapter gUa;

    static String getAcceptContentNumAndUserId;
    static String getDinyContentNumAndUserId;

    AlertDialog dialog;


    // 노티를 보내기 위해 일시적으로 담기위한 리스트
    static ArrayList<fcmMessagingContent> fcmList = new ArrayList<>();

    Socket so;



    public myAskAdapter(ArrayList<getMyAskDataContent> MyAskDataArray, Context context, String loginUserId, Socket so,String loginUserNick) {
        this.MyAskDataArray = MyAskDataArray;
        this.context = context;
        this.loginUserId = loginUserId;
        this.so = so;
        this.loginUserNick =loginUserNick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ask_item, parent, false);
        return new myAskAdapter.RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Log.d("모집중 수",""+MyAskDataArray.size());

        //작성일
        ((RowCell) holder).myAskMakeDateItem.setText(MyAskDataArray.get(position).makeDate);
        //시작일
        ((RowCell) holder).myAskSdateItem.setText(MyAskDataArray.get(position).sDate);
        //종료일
        ((RowCell) holder).myAskEdateItem.setText(MyAskDataArray.get(position).eDate);
        //제목
        ((RowCell) holder).myAskTitleItem.setText(MyAskDataArray.get(position).title);
        //지역
        ((RowCell) holder).myAskLocationItem.setText(MyAskDataArray.get(position).location);
        //주소
        ((RowCell) holder).myAskAddressItem.setText(MyAskDataArray.get(position).address);
        Log.d("프레그먼트 리사이클러 뷰 안의 데이터", "" + MyAskDataArray.get(position).location);

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(MyAskDataArray.get(position).pay)).toString();
        //소지금
        ((RowCell) holder).myAskPayItem.setText(userHaveCashFormal);

        //모집자수
        ((RowCell) holder).myAskHelperItem.setText(MyAskDataArray.get(position).helper + " 명");

        //지원자수 ( 서버에서  세왔어야지 )
        ((RowCell) holder).myAskApplyCountItem.setText(MyAskDataArray.get(position).applyHelperCount);

        // 진행 상태
        if (MyAskDataArray.get(position).onGoingState.equals("0")) {
            ((RowCell) holder).myAskState.setText("모집중");
            ((RowCell) holder).myAskState.setTextColor(Color.BLUE);
        } else if (MyAskDataArray.get(position).onGoingState.equals("1")) {
            ((RowCell) holder).myAskState.setText("진행중");
            ((RowCell) holder).myAskState.setTextColor(Color.GREEN);
        } else if (MyAskDataArray.get(position).onGoingState.equals("2")) {
            ((RowCell) holder).myAskState.setText("마감");
            ((RowCell) holder).myAskState.setTextColor(Color.RED);
        }


        // 성별
        if (MyAskDataArray.get(position).gender.equals("남자")) {
            ((RowCell) holder).myAskGenderItem.setImageResource(R.drawable.man);
            // Glide.with(context).load(R.drawable.man).into(((RowCell) holder).gender);
        } else if (MyAskDataArray.get(position).gender.equals("여자")) {
            ((RowCell) holder).myAskGenderItem.setImageResource(R.drawable.girl);
            //Glide.with(context).load(R.drawable.girl).into(((RowCell) holder).gender);
        } else {
            // 성별 무관 ( 디폴트 이미지 )
            ((RowCell) holder).myAskGenderItem.setImageResource(R.drawable.genderdefault);
            // Glide.with(context).load(R.drawable.genderdefault).into(((RowCell) holder).gender);
        }


        //레이아웃 클릭 이벤트 _   지원자들 보여주기
        ((RowCell) holder).myAskItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(MyAskDataArray.get(position).applyHelperCount) > 0) {
                    showApplyHelpers(MyAskDataArray.get(position).key, MyAskDataArray.get(position).helper, MyAskDataArray.get(position).acceptHelpCount,position);
                    gUa.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "지원자가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return MyAskDataArray.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder {

        public TextView myAskMakeDateItem, myAskSdateItem, myAskEdateItem,
                myAskHelperItem, myAskTitleItem, myAskLocationItem, myAskAddressItem, myAskPayItem, myAskApplyCountItem, myAskState;
        public ImageView myAskGenderItem;
        public LinearLayout myAskItemLayout;

        public RowCell(View view) {
            super(view);
            myAskMakeDateItem = (TextView) view.findViewById(R.id.myAskMakeDateItem);
            myAskSdateItem = (TextView) view.findViewById(R.id.myAskSdateItem);
            myAskEdateItem = (TextView) view.findViewById(R.id.myAskEdateItem);
            myAskTitleItem = (TextView) view.findViewById(R.id.myAskTitleItem);
            myAskLocationItem = (TextView) view.findViewById(R.id.myAskLocationItem);
            myAskAddressItem = (TextView) view.findViewById(R.id.myAskAddressItem);
            myAskPayItem = (TextView) view.findViewById(R.id.myAskPayItem);
            myAskApplyCountItem = (TextView) view.findViewById(R.id.myAskApplyCountItem);
            myAskHelperItem = (TextView) view.findViewById(R.id.myAskHelperItem);

            myAskState = (TextView) view.findViewById(R.id.myAskState);

            myAskGenderItem = (ImageView) view.findViewById(R.id.myAskGenderItem);
            myAskItemLayout = (LinearLayout) view.findViewById(R.id.myAskItemLayout);

        }
    }

    // ( 다이얼 로그 )레이아웃 클릭 이벤트 _   지원자들 보여주기
    public void showApplyHelpers(String key, String totalCount, String acceptCount, final int position) {

        // fcm 리스트 비워주기
        fcmList.clear();

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.my_ask_apply_user_list, null);


        // 리사이클러뷰
        RecyclerView reView = (RecyclerView) mView.findViewById(R.id.ApplyUserListRecyclerView);

        // 서버에서 데이터 가지고오기
        getApplyUserData(key);

        // 어댑터 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reView.setLayoutManager(layoutManager);


        // totalCount 를  모집인원 수에서 - 수락한 인원 빼기
        totalCount = String.valueOf(Integer.parseInt(totalCount) - Integer.parseInt(acceptCount));
        gUa = new getApplyUserDataAdapter(AuD, context, totalCount, key, loginUserId,loginUserNick,MyAskDataArray.get(position).title);
        reView.setAdapter(gUa);
        gUa.notifyDataSetChanged();

        Button apply_List_ok_Btn = (Button)mView.findViewById(R.id.apply_List_ok_Btn);
        apply_List_ok_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 수락 또는 거절의 모든 최종 결과를 가지고 여기서  서버에 전송을 하게 된다.

                Log.d("수락한 놈들", "" + getAcceptContentNumAndUserId);

                // 확인시 수락한 아이디들을 static으로 선언된 변수에 담아서 한번에 서버로 보낸다.
                if (!TextUtils.isEmpty(getAcceptContentNumAndUserId)) {
                    setAccept(position);
                }

                Log.d("거절한 놈들", "" + getDinyContentNumAndUserId);
                if (!TextUtils.isEmpty(getDinyContentNumAndUserId)) {
                    setDiny(position);
                }

                //node FCM 전송
                Log.d("FCM 어레이 리스트 크기",""+fcmList.size());


                for ( int i = 0; i < fcmList.size(); i++){
                    String dataString;
                    dataString = fcmList.get(i).loginUserId+"@"+fcmList.get(i).targetId+"@"+ fcmList.get(i).title+"@"+fcmList.get(i).result+"@";
                    so.emit("applyResult",dataString);
                }

                getAcceptContentNumAndUserId = null;
                getDinyContentNumAndUserId = null;
                dialog.dismiss();

            }
        });

        Button apply_List_cancel_Btn = (Button)mView.findViewById(R.id.apply_List_cancel_Btn);
        apply_List_cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAcceptContentNumAndUserId = null;
                getDinyContentNumAndUserId = null;
                dialog.dismiss();
            }
        });




        aBuilder.setView(mView);
        dialog = aBuilder.create();
        dialog.show();
    }

    // ( 서버 연결 )
    public void getApplyUserData(String key) {
        class getApplyUserDataHttp extends AsyncTask<Void, Void, String> {

            String result;
            ProgressDialog dialog = new ProgressDialog(context);
            String key;

            public getApplyUserDataHttp(String key) {
                this.key = key;
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 로드중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad + "/getMyApplyUserData.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------

                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST

                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 서버로 값 전달.
                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------


                    StringBuffer buffer = new StringBuffer();
                    buffer.append("key").append("=").append(key);                 // php 변수에 값 대입

                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    result = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d("헬프리스트 메인 ",""+result);

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }

                if (s.equals("없음")) {
                } else {
                    Log.d("프래그먼트 다이얼로그 정보들 ", "" + s);
                    // 가지고온 데이터들을 나눈다.
                    divideGetMyAskData(s);
                    gUa.notifyDataSetChanged();
                }

            }
        }

        getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(key);
        getDataAsk.execute();
    }

    public void divideGetMyAskData(String result) {
        AuD.clear();
        firstFilter = result.split("!"); // 게시글 별로 나누기 위함
        for (int i = 0; i < firstFilter.length; i++) {
            seccondFilter = firstFilter[i].split("@");

            // 로그인 모드, URL , 유저 닉네임 순서,
            getApplyuserData = new getApplyuserData(seccondFilter[0], seccondFilter[1], seccondFilter[2],
                    seccondFilter[3], seccondFilter[4], seccondFilter[5], seccondFilter[6], seccondFilter[7], seccondFilter[8], seccondFilter[9], seccondFilter[10], false); // 객체 생성

            AuD.add(getApplyuserData); // 어레이 리스트에 객체 추가.
        }
    }

    // 확인 버튼시 ( 서버에 수락한 사람들의 아이디를 전송한다 ), 거절당한 사람의 아이디도 전송한다, 모집인원과 비교한다,
    // 다이얼로그 확인 버튼  수락 아이디 이벤트
    public void setAccept(int position) {

        String allData = getAcceptContentNumAndUserId;
        String[] splitData = allData.split("!");

        String contentNum = splitData[0];
        String id = splitData[1];

        String [] count = id.split("@");
        int arrSize = count.length;

        Log.d("수락 결과  제목 번호 ", " " + contentNum);
        Log.d("수락 당한 아이디들 ", "" + id);

        int a = Integer.parseInt(MyAskDataArray.get(position).helper);
        int b = Integer.parseInt(MyAskDataArray.get(position).acceptHelpCount);
        int c = b+arrSize;

        // 모집인원과  수락한 인원을 비교하여 그 수가 맞게 되면,  onGoingState 를 진행중으로 변경한다.
        if ( a == c ){
            //  모집인원  ==  ( 수락인원  + 추가로 한 인원 )
            // 모집중 - > 진행중으로 변경.
            MyAskDataArray.get(position).onGoingState = "1";
            notifyItemChanged(position);
        }else{

        }

        setApplyDB(contentNum, id, "1");
    }

    // 다이얼로그 확인 버튼 거절 아이디 이벤트
    public void setDiny(int position) {
        String allData = getDinyContentNumAndUserId;

        String[] splitData = allData.split("!");
        String contentNum = splitData[0];
        String id = splitData[1];

        Log.d("거절 결과  제목 번호 ", " " + contentNum);
        Log.d("거절 당한 아이디들 ", "" + id);

        setApplyDB(contentNum, id, "2");
        getAcceptContentNumAndUserId = null;
        getDinyContentNumAndUserId = null;


    }

    public void setApplyDB(String key, String id, String mode) {
        class getApplyDinySetHttp extends AsyncTask<Void, Void, String> {

            String result;
            ProgressDialog dialog = new ProgressDialog(context);
            String key;
            String id;
            String mode;


            public getApplyDinySetHttp(String key, String id, String mode) {
                this.key = key;
                this.id = id;
                this.mode = mode;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("데이터 로드중");
                dialog.setCanceledOnTouchOutside(false); // 바깥 터치 안되게
                dialog.setCancelable(false); // 뒤로가기로 캔슬시키는거 안되게
                dialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------
                    URL url = new URL(ipad + "/applyDinySet.php");
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                    //--------------------------
                    //   전송 모드 설정 - 기본적인 설정이다
                    //--------------------------

                    huc.setDefaultUseCaches(false);
                    huc.setDoInput(true);                         // 서버에서 읽기 모드 지정
                    huc.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                    huc.setRequestMethod("POST");         // 전송 방식은 POST

                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                    // 서버로 값 전달.
                    //--------------------------
                    //   서버로 값 전송
                    //--------------------------


                    StringBuffer buffer = new StringBuffer();
                    buffer.append("key").append("=").append(key).append("&");                 // php 변수에 값 대입
                    buffer.append("id").append("=").append(id).append("&");
                    buffer.append("mode").append("=").append(mode);


                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "UTF-8");  // 안드에서 php로 보낼때 UTF8로 해야지 한글이 안깨진다.
//                    OutputStreamWriter outStream = new OutputStreamWriter(huc.getOutputStream(), "EUC-KR");
                    PrintWriter writer = new PrintWriter(outStream);
                    writer.write(buffer.toString());
                    writer.flush();


                    //--------------------------
                    //   서버에서 전송받기
                    //--------------------------
                    InputStreamReader tmp = new InputStreamReader(huc.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                        builder.append(str);                     // View에 표시하기 위해 라인 구분자 추가
                    }
                    result = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d("헬프리스트 메인 ",""+result);

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {

                }
                Log.d("gggg", "" + s);

            }
        }

        getApplyDinySetHttp getApplyDinySetHttp = new getApplyDinySetHttp(key, id, mode);
        getApplyDinySetHttp.execute();
    }

}
