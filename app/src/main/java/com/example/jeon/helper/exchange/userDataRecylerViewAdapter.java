package com.example.jeon.helper.exchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.addAskHelp;
import com.example.jeon.helper.helpList.myAskAdapter;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.kakaoLogin.GlobalApplication;
import com.example.jeon.helper.selectMenu;
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
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import static com.example.jeon.helper.exchange.exchangerRecyclerViewAdapter.exchangerCount;

/**
 * Created by JEON on 2018-05-17.
 */

public class userDataRecylerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String loginUserid;
    String loginUserNick;

    Context context;
    ArrayList<getApplyUserDataInExchange> AllAuD = new ArrayList<>();
    String pay;
    String key;
    String title;


    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();

    // 평가후기
    String score;
    String comment;
    View mView;


    Socket so;


    public userDataRecylerViewAdapter(Context context, ArrayList<getApplyUserDataInExchange> allAuD,String pay,String key,String title,String loginUserid,Socket so,String loginUserNick) {
        this.context = context;
        AllAuD = allAuD;
        this.pay = pay;
        this.key = key;
        this.title =title;
        this.loginUserid = loginUserid;
        this.so = so;
        this.loginUserNick = loginUserNick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_user_data_recycler_view_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Log.d("닉네임",AllAuD.get(position).userNick);
        Log.d("성별",AllAuD.get(position).gender);
        Log.d("지역",AllAuD.get(position).location);
        Log.d("이미지 프로필",AllAuD.get(position).profilePath);


        ((RowCell)holder).ExchangeApplyUserNick.setText(AllAuD.get(position).userNick);
        ((RowCell)holder).ExchangeApplyLocation.setText(AllAuD.get(position).location);


        String money;

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        money = dc.format(Double.parseDouble(pay));
        ((RowCell)holder).ExchangePayItem.setText(money);

        if (AllAuD.get(position).gender.equals("남자")){
            Glide.with(context).load(R.drawable.man).into(((RowCell)holder).ExchangeApplyUserGender);
        }else if (AllAuD.get(position).gender.equals("여자")){
            Glide.with(context).load(R.drawable.girl).into(((RowCell)holder).ExchangeApplyUserGender);
        }

        // 프로필 이미지
        if(AllAuD.get(position).profilePath.equals("0")){

        }else {
            if(AllAuD.get(position).profilePath.contains("http://k.kakaocdn.net")){
                Glide.with(context).load((AllAuD.get(position).profilePath)).into(((RowCell)holder).ExchangeApplyUserProfile);
            }else{
                Glide.with(context).load(ipad+"/"+(AllAuD.get(position).profilePath)).into(((RowCell)holder).ExchangeApplyUserProfile);
            }
        }

        // 정산 버튼
        if(AllAuD.get(position).exChangeState.equals("0")){
            ((RowCell)holder).paySand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnEvent(holder,position);
                }
            });
        }else if(AllAuD.get(position).exChangeState.equals("1")){
            // 이미 정산이 된경우에는 완료
            ((RowCell)holder).paySand.setBackgroundColor(Color.GREEN);
            ((RowCell)holder).paySand.setText("완료");
            exchangerCount = exchangerCount+1;

        }

        ((RowCell)holder).applyUserListItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoSelect = new Intent(context,selectMenu.class);
                gotoSelect.putExtra("targetId",AllAuD.get(position).userId);
                gotoSelect.putExtra("loginUserId",loginUserid);
                ((exchangeMainActivity)context).startActivity(gotoSelect);
            }
        });

    }

    @Override
    public int getItemCount() {
        return AllAuD.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder {

        public TextView ExchangeApplyUserNick, ExchangeApplyLocation, ExchangePayItem;
        public ImageView ExchangeApplyUserProfile,ExchangeApplyUserGender;
        public Button paySand;
        public LinearLayout applyUserListItemLayout;


        public RowCell(View view) {
            super(view);
            ExchangeApplyUserNick = (TextView) view.findViewById(R.id.ExchangeApplyUserNick);
            ExchangeApplyLocation = (TextView) view.findViewById(R.id.ExchangeApplyLocation);
            ExchangePayItem = (TextView) view.findViewById(R.id.ExchangePayItem);

            ExchangeApplyUserProfile = (ImageView)view.findViewById(R.id.ExchangeApplyUserProfile);
            ExchangeApplyUserGender = (ImageView)view.findViewById(R.id.ExchangeApplyUserGender);
            paySand = (Button)view.findViewById(R.id.paySand);
            applyUserListItemLayout = (LinearLayout)view.findViewById(R.id.applyUserListItemLayout);
        }

    }

    //정산 버튼
    public void btnEvent( RecyclerView.ViewHolder holder,int position){

        ((RowCell)holder).paySand.setBackgroundColor(Color.BLUE);
        ((RowCell)holder).paySand.setText("완료");
        ((RowCell)holder).paySand.setBackgroundColor(Color.GREEN);

        exchangerCount = exchangerCount+1;

        setAccount(AllAuD.get(position).userId,key,pay,title);
        notifyDataSetChanged();
        assessmentDialog(AllAuD.get(position).userNick,AllAuD.get(position).userId,AllAuD.get(position).profilePath,loginUserid,AllAuD.get(position).gender);


        // 여기에서 보내주면 되겟구만 그려 .

        String dataString = AllAuD.get(position).userNick+"@"+AllAuD.get(position).userId+"@"+loginUserid+"@"+loginUserNick+"@";
        so.emit("exchangeNoti",dataString);

    }

    // ( 서버에 전송 )  송금
    public void setAccount(String id,String key,String pay,String title){
            class getApplyUserDataHttp extends AsyncTask<Void, Void, String> {

                String result;
                ProgressDialog dialog = new ProgressDialog(context);
                String key;
                String id; // 받을 사람
                String pay;
                String title;

                public getApplyUserDataHttp(String id, String key, String pay,String title) {
                    this.key = key;
                    this.id = id;
                    this.pay = pay;
                    this.title = title;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("송금중");
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
                        URL url = new URL(ipad +"/sendMoney.php");
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
                        buffer.append("id").append("=").append(id).append("&");
                        buffer.append("loginUserid").append("=").append(loginUserid).append("&");  // php 변수에 값 대입
                        buffer.append("key").append("=").append(key).append("&");
                        buffer.append("pay").append("=").append(pay).append("&");
                        buffer.append("title").append("=").append(title);


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

                    if ( s.equals("정산완료")){
                        Toast.makeText(context, "송금완료", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("돈 보내기 ",""+s);
                    }

                }
            }

            getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(id,key,pay,title);
            getDataAsk.execute();
        }


    //( 송금 이후 다이얼로그 ) 평가및 평점 받는 다이얼로그
    public void assessmentDialog(String nick, final String id, String profile, final String loginUserid,String gender){


        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.assessment_dialog,null);


        ImageView assess_profile = mView.findViewById(R.id.assessment_Profile_item);
        TextView assess_Nick = mView.findViewById(R.id.assessment_NickName_item);
        EditText assess_score = mView.findViewById(R.id.assessment_Score_item);

        // 프로필 이미지
        if(profile.equals("0")){

        }else {
            if(profile.contains("http://k.kakaocdn.net")){
                Glide.with(context).load(profile).into(assess_profile);
            }else{
                Glide.with(context).load(ipad+"/"+profile).into(assess_profile);
            }
        }

        //닉네임
        assess_Nick.setText(nick);

        //성별 적용
        ImageView assessment_Gender_item = mView.findViewById(R.id.assessment_Gender_item);
        if(gender.equals("남자")){
            assessment_Gender_item.setImageResource(R.drawable.man);
        }else if (gender.equals("여자")){
            assessment_Gender_item.setImageResource(R.drawable.girl);
        }


        // 평점 받기 ( 숫자만받기, 빈공백인지 확인하기 )

        // 숫자만 받기
        InputFilter filterNum = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[1-5]+$");
                if(!ps.matcher(source).matches()){
                    Toast.makeText(context, "평점은 1~ 5점 까지 입니다.", Toast.LENGTH_SHORT).show();
                    return "";
                }
                return null;
            }
        };

        assess_score.setFilters(new InputFilter[]{filterNum});


        aBuilder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText assess_score = mView.findViewById(R.id.assessment_Score_item);
                EditText assess_comment = mView.findViewById(R.id.assessment_Comment);
                score = assess_score.getText().toString();
                comment = assess_comment.getText().toString();


                if (TextUtils.isEmpty(score) || TextUtils.isEmpty(comment)){
                    Toast.makeText(context, "입력되지 않은 데이터가 있습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(context, "후기 등록 완료", Toast.LENGTH_SHORT).show();
                    Log.d("후기 남긴 아이디",""+loginUserid);
                    Log.d("후기 남긴 대상",""+id);
                    Log.d("후기 평점",""+score);
                    Log.d("후기 코멘트",""+comment);

                    //서버에 전송
                    setAssess(id,comment,score,loginUserid,dialog,0);

                    //dialog.dismiss();

                    //node 알림 ㄱㄱ
                    String dataString  = loginUserid+"@"+id+"@"+loginUserNick;
                    so.emit("commentNoti",dataString);

                }
            }
        });

        aBuilder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setAssess(id,comment,score,loginUserid,dialog,2);
            }
        });



        aBuilder.setView(mView);
        AlertDialog dialog = aBuilder.create();
        dialog.show();

    }

    //  ( 서버에 전송  후기글 등록 )
    public void setAssess(String id, String comment, String score ,String loginUserid,DialogInterface dialog,int mode){
        class getApplyUserDataHttp extends AsyncTask<Void, Void, String> {

            String result;
            ProgressDialog dialog = new ProgressDialog(context);
            String id;
            String comment; // 받을 사람
            String loginUserid;
            String score;
            DialogInterface dialog2;
            int mode;

            public getApplyUserDataHttp(String id, String comment, String score, String loginUserid, DialogInterface dialog2,int mode) {
                this.id = id;
                this.comment = comment;
                this.loginUserid = loginUserid;
                this.score = score;
                this.dialog2 = dialog2;
                this.mode =mode;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("송금중");
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
                    URL url = new URL(ipad +"/setAssessment.php");
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
                    buffer.append("id").append("=").append(id).append("&");
                    buffer.append("comment").append("=").append(comment).append("&");  // php 변수에 값 대입
                    buffer.append("loginUserid").append("=").append(loginUserid).append("&");
                    buffer.append("mode").append("=").append(mode).append("&");
                    buffer.append("score").append("=").append(score);


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

                Log.d("평가 결과 ",""+s);

                if ( s.equals("완료")){
                    Toast.makeText(context, "평가 완료", Toast.LENGTH_SHORT).show();
                    dialog2.dismiss();
                }else if(s.equals("확인")){
                    Toast.makeText(context, "평가 취소", Toast.LENGTH_SHORT).show();
                    dialog2.dismiss();
                }

            }
        }

        getApplyUserDataHttp getDataAsk = new getApplyUserDataHttp(id,comment,score,loginUserid,dialog,mode);
        getDataAsk.execute();

    }


}
