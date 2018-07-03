package com.example.jeon.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-04-23.
 */

public class main_BestHelper_RecyclerViewAdater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<bestHelper> bestHelpersArrary;
    int bestHelpersArrarySize;
    Context context;

    String loginUserId;

    ip ip = new ip();
    String ipad = ip.getIp();

    // 나중에 데이터 베이스  ( userAccount DB에서 거래 완료 횟수 상위 10명을 뽑아서 리스트로 만든후  뿌려줄 예정입니다. )
    // 생성자를 만들어서 그안에 어레이 리스트를 만들면 되겟죠 ??
    public main_BestHelper_RecyclerViewAdater(ArrayList<bestHelper> bestHelpersArrary, int bestHelpersArrarySize,Context context,String loginUserId) { // 생성자
        this.bestHelpersArrary = bestHelpersArrary;
        this.bestHelpersArrarySize = bestHelpersArrarySize;
        this.context = context;
        this.loginUserId = loginUserId;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // XML 디자인 한 부분 적용


        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_recyclerview_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // position 값을 인덱스로 활용해도 되겠다.  0 부터 시작해서 겟 아이템 수까지

        // XML 디자인한 부분에 안에 내용 변경 ( 사진을 넣는 부분 이미지를 지정해주는 부분 : 여기에서 각 데이터마다 적용시키면 될듯 )

        try{

            // 일반 로그인
            if (bestHelpersArrary.get(position).loginMode.equals("1")) { // 일반 로그인 인경우
                if (!bestHelpersArrary.get(position).profileURL.equals("1")) { // 이미지가 있는 경우 이미지를 보여주고, 이미지가 없는 경우 디폴트 이미지
                    // 이미지가 없는경우 ( 디폴트 ) userProFileImage/wldnd.png
                    Glide.with(context).load(ipad+"/"+bestHelpersArrary.get(position).profileURL).into(((RowCell) holder).imageView);
                }else{
                    // 이미지가 없는경우
                }
                ((RowCell) holder).textView.setText(bestHelpersArrary.get(position).bestHelperNickName);
            }

            // 카카오 로그인
            else if (bestHelpersArrary.get(position).loginMode.equals("2")) { // 카카오 로그인 인 경우
                if ( !bestHelpersArrary.get(position).profileURL.equals("1")){ // 이미지가 없는 경우 기본
                    // 글라이드를 사용해야함.
                    if(bestHelpersArrary.get(position).profileURL.contains("http://k.kakaocdn.net")){
                        Glide.with(context).load(bestHelpersArrary.get(position).profileURL).into(((RowCell) holder).imageView);
                    }else{
                        Glide.with(context).load(ipad+"/"+bestHelpersArrary.get(position).profileURL).into(((RowCell) holder).imageView);
                    }
                }
                // ((RowCell) holder).imageView.setImageURI(Uri.parse(bestHelpersArrary.get(position).profileURL));
                ((RowCell) holder).textView.setText(bestHelpersArrary.get(position).bestHelperNickName);
            }

            ((RowCell) holder).bestLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickLayout(position,holder);
                }
            });

        }catch (Exception e){

        }

         //Log.d("어댑터 안입니다.", "사용자의 닉네임이 왔는지 확인 : " + bestHelpersArrary.get(position).bestHelperNickName);

    }

    @Override
    public int getItemCount() {
        // 아이템의 갯수 ( 아이템의 갯수 만큼 위의 메소드를 반복 한다 )
        // 나중에 리스트 뷰의 갯수를 넣을것이여 ㅋ

        return bestHelpersArrarySize;  // 어레이 리스크 크기 만큼.
    }

    private static class RowCell extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public LinearLayout bestLayout;

        public RowCell(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.best_item_ImageView);
            textView = (TextView) view.findViewById(R.id.best_item_TextView);
            bestLayout = (LinearLayout)view.findViewById(R.id.bestLayout);
        }
    }

    // 레이아웃 클릭 이벤트
    public void setClickLayout (int position,RecyclerView.ViewHolder holder){

        Intent gotoSelect = new Intent(context,selectMenu.class);
        gotoSelect.putExtra("loginUserId",loginUserId);
        gotoSelect.putExtra("targetId",bestHelpersArrary.get(position).id);
        ((HelperMain)context).startActivity(gotoSelect);

    }

}
