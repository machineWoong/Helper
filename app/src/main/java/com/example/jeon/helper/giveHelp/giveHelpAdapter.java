package com.example.jeon.helper.giveHelp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.askHalpMainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-08.
 */

public class giveHelpAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<giveHelpContent> giveHelpData = new ArrayList<>();
    Context context;
    String loginUser;
    String gender;
    String loginUserNick;

    public giveHelpAdapter( ArrayList<giveHelpContent> giveHelpData ,Context context,String loginUser,String gender,String loginUserNick){
        this.giveHelpData = giveHelpData;
        this.context = context;
        this.loginUser = loginUser;
        this.gender = gender;
        this.loginUserNick = loginUserNick;
    }

    // 레이아웃을 불러오는 함수
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_give_help_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //------------------------------------------텍스트 ----------------------------------------
        // 게시일 날짜
         ((RowCell)holder).makeDate.setText(giveHelpData.get(position).makeTime);
        // 시작일
        ((RowCell)holder).sDate.setText(giveHelpData.get(position).sDate);
        // 종료일
        ((RowCell)holder).eDate.setText(giveHelpData.get(position).eDate);
        // 제목
        ((RowCell)holder).giveHelpTitle.setText(giveHelpData.get(position).title);
        // 주소
        ((RowCell)holder).address.setText(giveHelpData.get(position).address);
        // 닉네임
        ((RowCell)holder).nickName.setText(giveHelpData.get(position).nickName);

        // 수고비
        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(giveHelpData.get(position).pay)).toString();
        ((RowCell)holder).pay.setText(userHaveCashFormal);

        // 사람
        ((RowCell)holder).giveHelpHelper.setText(giveHelpData.get(position).helper+"명");


        //------------------------------------------이미지 -----------------------------------------
        // (사전처리)


        // 성별
        if( giveHelpData.get(position).gender.equals("남자")){
            ((RowCell)holder).giveHelpGender.setImageResource(R.drawable.man);
            // Glide.with(context).load(R.drawable.man).into(((RowCell) holder).gender);
        }else if ( giveHelpData.get(position).gender.equals("여자")){
            ((RowCell)holder).giveHelpGender.setImageResource(R.drawable.girl);
            //Glide.with(context).load(R.drawable.girl).into(((RowCell) holder).gender);
        }else{
            // 성별 무관 ( 디폴트 이미지 )
            ((RowCell)holder).giveHelpGender.setImageResource(R.drawable.genderdefault);
            // Glide.with(context).load(R.drawable.genderdefault).into(((RowCell) holder).gender);
        }


        //지도
        if(  giveHelpData.get(position).map.equals("1")){
            // 값이 있다면
            ((RowCell)holder).giveHelpMap.setImageResource(R.drawable.itemmap);
            //글라이더 사용
            //Glide.with(context).load(R.drawable.itemmap).into(((RowCell) holder).map);
        }else{
            // 값이 없다면.
            ((RowCell)holder).giveHelpMap.setVisibility(View.INVISIBLE);
        }


        //사진
        if(  giveHelpData.get(position).imageUrl.equals("1")){
            // 값이 있다면
            ((RowCell)holder).giveHelpPhoto.setImageResource(R.drawable.itemphoto);
            //Glide.with(context).load(R.drawable.itemphoto).into(((RowCell) holder).photo);
        }else{
            // 값이 없다면.
            ((RowCell)holder).giveHelpPhoto.setVisibility(View.INVISIBLE);
        }

        ((RowCell)holder).giveHelpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoShowGiveHelp = new Intent(context,showGiveHelp.class);
                gotoShowGiveHelp.putExtra("data",giveHelpData);
                gotoShowGiveHelp.putExtra("position",position);
                gotoShowGiveHelp.putExtra("loginUser",loginUser);
                gotoShowGiveHelp.putExtra("gender",gender);
                gotoShowGiveHelp.putExtra("loginUserNick",loginUserNick);

                ((giveHelpMainActivity)context).startActivityForResult(gotoShowGiveHelp,3333);
            }
        });

    }

    @Override
    public int getItemCount() {
        return giveHelpData.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder{

        public TextView makeDate,sDate,eDate,giveHelpTitle,address,nickName,pay,giveHelpHelper;
        public ImageView giveHelpGender,giveHelpPhoto,giveHelpMap;
        public LinearLayout giveHelpLayout;
        public RowCell(View view) {
            super(view);

            makeDate = (TextView) view.findViewById(R.id.giveHelpMakeDate);
            sDate = (TextView)view.findViewById(R.id.giveHelpSdate);
            eDate = (TextView)view.findViewById(R.id.giveHelpEdate);
            giveHelpTitle = (TextView)view.findViewById(R.id.giveHelpTitle);
            address = (TextView)view.findViewById(R.id.giveHelpAddress);
            nickName = (TextView)view.findViewById(R.id.giveHelpAskUserNick);
            pay = (TextView)view.findViewById(R.id.giveHelpPay);
            giveHelpHelper = (TextView)view.findViewById(R.id.giveHelpHelperCount);

            giveHelpGender = (ImageView)view.findViewById(R.id.giveHelpGender);
            giveHelpPhoto = (ImageView)view.findViewById(R.id.giveHelpPhoto);
            giveHelpMap = (ImageView)view.findViewById(R.id.giveHelpMap);
            giveHelpLayout = (LinearLayout)view.findViewById(R.id.giveHelpLayout);

        }
    }
}
