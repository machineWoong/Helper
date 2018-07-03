package com.example.jeon.helper.helpList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.askHalpMainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class myGiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<getMyGiveDataContent> MyGiveDataArray = new ArrayList<>();
    Context context;
    String loginId;
    public myGiveAdapter( ArrayList<getMyGiveDataContent> MyGiveDataArray ,Context context ,String loginId) {
        this.MyGiveDataArray = MyGiveDataArray;
        this.context = context;
        this.loginId =loginId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_give_item, parent, false);
        return new myGiveAdapter.RowCell(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        // 상태 진행중 마감 수행중

        if (MyGiveDataArray.get(position).state.equals("0")){
            ((RowCell)holder).myGiveState.setText("수락대기중");
            ((RowCell)holder).myGiveState.setTextColor(Color.BLACK);
        }else if (MyGiveDataArray.get(position).state.equals("1")){
            ((RowCell)holder).myGiveState.setText("수행중");
            ((RowCell)holder).myGiveState.setTextColor(Color.BLUE);
        }else if (MyGiveDataArray.get(position).state.equals("2")){
            ((RowCell)holder).myGiveState.setText("거절됨");
            ((RowCell)holder).myGiveState.setTextColor(Color.RED);
        }else{
            ((RowCell)holder).myGiveState.setText("종료");
            ((RowCell)holder).myGiveState.setTextColor(Color.RED);
        }

        // 요청자 아이디
        ((RowCell)holder).myGiveAskUserId.setText(MyGiveDataArray.get(position).nickName);

        //시작일
        ((RowCell)holder).myGiveSdateItem.setText(MyGiveDataArray.get(position).sDate);
        //마감일
        ((RowCell)holder).myGiveEdateItem.setText(MyGiveDataArray.get(position).eDate);
        //제목
        ((RowCell)holder).myGiveTitleItem.setText(MyGiveDataArray.get(position).title);
        // 지역
        ((RowCell)holder).myGiveLocationItem.setText(MyGiveDataArray.get(position).location);
        // 주소
        ((RowCell)holder).myGiveAddressItem.setText(MyGiveDataArray.get(position).address);

        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userHaveCashFormal = dc.format(Double.parseDouble(MyGiveDataArray.get(position).pay)).toString();

        //소지금
        ((RowCell)holder).myGivePayItem.setText(userHaveCashFormal);

        // 레이아웃  ( 글정보( 키값 ) 를 전달할 것이다 )
        ((RowCell)holder).myGiveItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoDetailContent = new Intent(context,showDetailContent.class);
                gotoDetailContent.putExtra("dataContentNumber",MyGiveDataArray.get(position).key);
                gotoDetailContent.putExtra("loginId",loginId);
                gotoDetailContent.putExtra("makerId",MyGiveDataArray.get(position).id);
                ((helpListMain)context).startActivity(gotoDetailContent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return MyGiveDataArray.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder{

        public TextView myGiveState,myGiveSdateItem,myGiveEdateItem,myGiveTitleItem,myGiveLocationItem,myGiveAddressItem,myGivePayItem,myGiveAskUserId;
        public LinearLayout myGiveItemLayout;
        public RowCell(View view) {
            super(view);
            myGiveAskUserId = (TextView)view.findViewById(R.id.myGiveAskUserId);
            myGiveState = (TextView) view.findViewById(R.id.myGiveState);
            myGiveSdateItem = (TextView)view.findViewById(R.id.myGiveSdateItem);
            myGiveEdateItem = (TextView)view.findViewById(R.id.myGiveEdateItem);
            myGiveTitleItem = (TextView)view.findViewById(R.id.myGiveTitleItem);
            myGiveLocationItem = (TextView)view.findViewById(R.id.myGiveLocationItem);
            myGiveAddressItem = (TextView)view.findViewById(R.id.myGiveAddressItem);
            myGivePayItem = (TextView)view.findViewById(R.id.myGivePayItem);
            myGiveItemLayout = (LinearLayout)view.findViewById(R.id.myGiveItemLayout);
        }
    }

}
