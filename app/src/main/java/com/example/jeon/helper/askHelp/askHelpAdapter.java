package com.example.jeon.helper.askHelp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;

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
 * Created by JEON on 2018-05-01.
 */

public class askHelpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<askHelpContent> dataArray = new ArrayList<>();
    public Context context;
    public String userId;
    public String loginMode;

    String result2;
    ip ip = new ip();
    String ipad = ip.getIp();


    //생성자
    public askHelpAdapter(ArrayList<askHelpContent> dataArray,Context context,String userId,String loginMode){
        this.dataArray = dataArray;
        this.context = context;
        this.userId = userId;
        this.loginMode = loginMode;
    }

    //레이아웃을 불러오는 함수
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ask_help_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // 날짜 제목 주소 수고비 성별 지도 포토
        //photo나 map의 값이 1이면 값이 있는것  ( 이미지 보여주기 ) 0이면 없는것 ( 이미지 숨기기 )

        //------------------------------------------텍스트 ----------------------------------------
        //날짜
        ((RowCell)holder).date.setText(dataArray.get(position).date);
        //제목
        ((RowCell)holder).title.setText(dataArray.get(position).title);
        //주소
        ((RowCell)holder).address.setText(dataArray.get(position).address);
        //수고비
        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String payForMal = dataArray.get(position).pay;
        payForMal = dc.format(Double.parseDouble(payForMal)).toString();

        ((RowCell)holder).pay.setText(payForMal);
        //시작일
        ((RowCell)holder).sdate.setText(dataArray.get(position).sdate);
        //종료일
        ((RowCell)holder).edate.setText(dataArray.get(position).edate);

        //------------------------------------------이미지 -----------------------------------------
        // (사전처리)

        // 성별
        if( dataArray.get(position).gender.equals("남자")){
            ((RowCell)holder).gender.setImageResource(R.drawable.man);
           // Glide.with(context).load(R.drawable.man).into(((RowCell) holder).gender);
        }else if ( dataArray.get(position).gender.equals("여자")){
            ((RowCell)holder).gender.setImageResource(R.drawable.girl);
            //Glide.with(context).load(R.drawable.girl).into(((RowCell) holder).gender);
        }else{
            // 성별 무관 ( 디폴트 이미지 )
           ((RowCell)holder).gender.setImageResource(R.drawable.genderdefault);
           // Glide.with(context).load(R.drawable.genderdefault).into(((RowCell) holder).gender);
        }

        //지도
        if(  dataArray.get(position).map.equals("1")){
            // 값이 있다면
            ((RowCell)holder).map.setImageResource(R.drawable.itemmap);
            //글라이더 사용
            //Glide.with(context).load(R.drawable.itemmap).into(((RowCell) holder).map);
        }else{
            // 값이 없다면.
            ((RowCell)holder).map.setVisibility(View.INVISIBLE);
        }

        //사진
        if(  dataArray.get(position).photo.equals("1")){
            // 값이 있다면
            ((RowCell)holder).photo.setImageResource(R.drawable.itemphoto);
            //Glide.with(context).load(R.drawable.itemphoto).into(((RowCell) holder).photo);
        }else{
            // 값이 없다면.
            ((RowCell)holder).photo.setVisibility(View.INVISIBLE);
        }

        // 아이템 전체를 클릭하려구 했기 때문에, 레이아웃 자체를 클릭 이벤트를 준다.
        ((RowCell)holder).lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 단순 클릭시 보기
                Intent gotoEditAskHelp = new Intent(context,showMyAskHelp.class);
                gotoEditAskHelp.putExtra("userId",userId);  // 유저 아이디
                gotoEditAskHelp.putExtra("loginMode",loginMode);  // 로그인 모드
                gotoEditAskHelp.putExtra("title",dataArray.get(position).title);  // 제목
                gotoEditAskHelp.putExtra("date",dataArray.get(position).date);
                gotoEditAskHelp.putExtra("key",dataArray.get(position).contentKey);

                //startActivityForResult 를 쓰기위해서 앞에 클래스로 형변환한 context를 사용.
                ((askHalpMainActivity)context).startActivityForResult(gotoEditAskHelp,2222);

            }
        });

        ((RowCell)holder).lay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 다이얼 로그를 띄워서 삭제인지 결정하게 할 것이다.
                if (dataArray.get(position).state.equals("0")){
                    showDialog(context,position);
                }else{
                    Toast.makeText(context, "이미 지원자가 있는 경우 \n삭제 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    // ( 다이얼 로그 )
    public void showDialog(final Context context, final int ItmePosition){
        AlertDialog.Builder itemOption = new AlertDialog.Builder(context);
        itemOption.setTitle("게시글을 삭제 하시겠습니까?");

        itemOption.setPositiveButton("취소",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 아무것도 없음
            }
        });


        itemOption.setNegativeButton("삭제",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 데이터를 서버에 전송해서 해당하는 데이터를 삭제 후 갱신?
                getAskHelpData(ItmePosition);
            }
        });
        itemOption.show();   // 실행
    }

    // ( 서버 호출 ) : 삭제 또는 수정시 사용
    public void getAskHelpData(int itemPosition){

        class getAddHelpDataToHttp extends AsyncTask<Void,Void,String> {
            int itemPosition;
            public  getAddHelpDataToHttp (int itemPosition){
                this.itemPosition = itemPosition;
            }


            @Override
            protected String doInBackground(Void... voids) {
                try {

                    //--------------------------
                    //   URL 설정하고 접속하기
                    //--------------------------

                    URL url = new URL(ipad+"/removeAskHelp.php");
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
                    buffer.append("loginMode").append("=").append(loginMode).append("&");
                    buffer.append("userId").append("=").append(userId).append("&");
                    buffer.append("contentKey").append("=").append(dataArray.get(itemPosition).contentKey);


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
                   result2 = builder.toString();                       // 전송결과를 전역 변수에 저장

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result2;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Log.d("///////////",result);

                dataArray.remove(itemPosition);
                notifyDataSetChanged();
            }
        }
        getAddHelpDataToHttp getAddHelp = new getAddHelpDataToHttp(itemPosition);
        getAddHelp.execute();


    }

    // 사이즈만큼 반복해서 뿌려줄 것이다.
    @Override
    public int getItemCount() {

        // 사이즈만큼 반복해서 뿌려줄 것이다.
        return dataArray.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder  {

        public TextView date,title,address,pay,sdate,edate;
        public ImageView gender,photo,map;
        public LinearLayout lay;

        public RowCell(View view) {
            super(view);
            //날짜 제목 주소 수고비
            date = (TextView) view.findViewById(R.id.makeDate);
            title = (TextView) view.findViewById(R.id.askHelpItemTitle);
            address = (TextView) view.findViewById(R.id.askHelpItemAddress);
            pay = (TextView) view.findViewById(R.id.payItem);
            sdate =(TextView)view.findViewById(R.id.sdateItem);
            edate = (TextView)view.findViewById(R.id.edateItem);

            // 성별 사진 지도
            gender = (ImageView) view.findViewById(R.id.itemGender);
            photo  = (ImageView) view.findViewById(R.id.itemPhoto);
            map = (ImageView) view.findViewById(R.id.itemMap);

            //레이아웃
            lay = (LinearLayout)view.findViewById(R.id.itemLayout);


        }
    }
}
