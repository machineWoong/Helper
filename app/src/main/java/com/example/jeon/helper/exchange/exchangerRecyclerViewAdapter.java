package com.example.jeon.helper.exchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.helpList.getApplyUserDataAdapter;
import com.example.jeon.helper.helpList.getApplyuserData;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;
import com.github.nkzawa.socketio.client.Socket;

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

/**
 * Created by JEON on 2018-05-17.
 */

public class exchangerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String loginUserId;
    String loginUserNick;

    ArrayList<exchangeContent> exchangeData = new ArrayList<>();
    Context context;

    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();


    // 지원자들의 정보
    ArrayList<getApplyUserDataInExchange> AllAuD = new ArrayList<>();
    getApplyUserDataInExchange getApplyuserData;
    String[] firstFilter;
    String[] seccondFilter;
    View mView;
    userDataRecylerViewAdapter adapter;
    AlertDialog dialog;

    static int exchangerCount = 0;// 정산하기 누른 사람의 수

    Socket so;

    public exchangerRecyclerViewAdapter(ArrayList<exchangeContent> exchangeData, Context context, String loginUserId,Socket so,String loginUserNick) {
        this.exchangeData = exchangeData;
        this.context = context;
        this.loginUserId = loginUserId;
        this.so = so;
        this.loginUserNick = loginUserNick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_recyclerview_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((RowCell) holder).exchangeMakeDateItem.setText(exchangeData.get(position).makeDate);
        ((RowCell) holder).exchangeTitleItem.setText(exchangeData.get(position).title);
        ((RowCell) holder).exchangeApplyHelperCountItem.setText(exchangeData.get(position).applyeHelper);


        String money;

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        money = dc.format(Double.parseDouble(exchangeData.get(position).pay)).toString();
        ((RowCell) holder).exchangePayItem.setText(money);


        Log.d("정산상태좀 보자 ", exchangeData.get(position).state);

        if (exchangeData.get(position).state.equals("0")) {
            ((RowCell) holder).exchangeStateItem.setText("정산대기");
            ((RowCell) holder).exchangeStateItem.setTextColor(Color.RED);


        } else if (exchangeData.get(position).state.equals("1")) {
            ((RowCell) holder).exchangeStateItem.setText("정산완료");
            ((RowCell) holder).exchangeStateItem.setTextColor(Color.BLUE);
        }


        // 지원자 정산하기 위해서 보는 다이얼로그 띄우기
        ((RowCell) holder).exchangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버에서 지원자들 데이터 받기
                getApplyUserData(exchangeData.get(position).key, position);
                //Log.d("정산 상태",""+exchangeData.get(position).state);

            }
        });

    }

    @Override
    public int getItemCount() {
        return exchangeData.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {

        public TextView exchangeMakeDateItem, exchangeTitleItem, exchangeApplyHelperCountItem, exchangePayItem, exchangeStateItem;

        public LinearLayout exchangeLayout;

        public RowCell(View view) {
            super(view);
            exchangeMakeDateItem = (TextView) view.findViewById(R.id.exchangeMakeDateItem);
            exchangeTitleItem = (TextView) view.findViewById(R.id.exchangeTitleItem);
            exchangeApplyHelperCountItem = (TextView) view.findViewById(R.id.exchangeApplyHelperCountItem);
            exchangePayItem = (TextView) view.findViewById(R.id.exchangePayItem);
            exchangeStateItem = (TextView) view.findViewById(R.id.exchangeStateItem);

            exchangeLayout = (LinearLayout) view.findViewById(R.id.exchangeLayout);
        }
    }

    // ( 다이얼 로그 )레이아웃 클릭 이벤트 _   지원자들 보여주기
    public void showApplyHelpers(final String key, String pay, String title, final int position) {

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.exchange_user_data_recycler_view, null);


        // 리사이클러뷰
        RecyclerView reView = (RecyclerView) mView.findViewById(R.id.showApplyUserDataRecyclerView);


        // 어댑터 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reView.setLayoutManager(layoutManager);


        adapter = new userDataRecylerViewAdapter(context, AllAuD, pay, key, title, loginUserId,so,loginUserNick);
        reView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button exchange_User_list_Btn = (Button) mView.findViewById(R.id.exchange_User_list_Btn);
        exchange_User_list_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();

                // exchangeCount를 가지고  지원자수랑 비교후 동일하면, 정산완료,  아니면,  정산중  그리고 다시  초기화 해주기
                setState(position);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });



        aBuilder.setView(mView);
        dialog = aBuilder.create();
        dialog.show();
    }


    // ( 서버 연결 ) 다이얼로그에 뿌려줄 지원자들 데이터 받기
    public void getApplyUserData(String key, final int position) {
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
                    URL url = new URL(ipad + "/getExchangeApplyUserData.php");
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

                Log.d("@@@@@@@@@@@",""+result);
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
                    Toast.makeText(context, "도움 진행중이 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {
                    divideGetMyAskData(s, position);
                }

            }
        }

        getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(key);
        getDataAsk.execute();
    }

    public void divideGetMyAskData(String result, int position) {

        AllAuD.clear();
        firstFilter = result.split("!"); // 게시글 별로 나누기 위함
        for (int i = 0; i < firstFilter.length; i++) {
            seccondFilter = firstFilter[i].split("@");
            // 로그인 모드, URL , 유저 닉네임 순서,
            getApplyuserData = new getApplyUserDataInExchange(seccondFilter[0], seccondFilter[1], seccondFilter[2],
                    seccondFilter[3], seccondFilter[4], seccondFilter[5]); // 객체 생성

            AllAuD.add(getApplyuserData); // 어레이 리스트에 객체 추가.
        }

//        for ( int i= 0 ; i < AllAuD.size(); i++){
//            Log.d("정산하기 지원자 데이터 받기",""+AllAuD.get(i).userNick);
//        }


        //다 받아온 상태에서  다이얼로그를 띄우자
        showApplyHelpers(exchangeData.get(position).key, exchangeData.get(position).pay, exchangeData.get(position).title, position);


    }

    public void setState(int position) {

        int a = Integer.parseInt(exchangeData.get(position).applyeHelper);
        if (a <= exchangerCount) {
            exchangeData.get(position).state = "1";
            notifyItemChanged(position);
        }
        exchangerCount = 0;

    }

}
