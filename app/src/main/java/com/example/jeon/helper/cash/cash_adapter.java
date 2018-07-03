package com.example.jeon.helper.cash;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeon.helper.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-21.
 */

public class cash_adapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    ArrayList<cash_Content> accountList = new ArrayList<>();
    Context context;
    String loginUserNick;
    int mode;

    public cash_adapter(ArrayList<cash_Content> accountList, Context context,String loginUserNick,int mode) {
        this.accountList = accountList;
        this.context = context;
        this.loginUserNick = loginUserNick;
        this.mode = mode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 아이템을 디자인한 레이아웃을 불러오는 코드
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cash_input_recyclerview_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        ((RowCell)holder).receiveDate.setText(accountList.get(position).date);
        ((RowCell)holder).receiveTitle.setText(accountList.get(position).title);


        DecimalFormat dc = new DecimalFormat("###,###,###,###");
        String userCash = dc.format(Double.parseDouble(accountList.get(position).money)).toString();
        ((RowCell)holder).receiveMoney.setText(userCash);

        Log.d("들어온 모드는 무엇?","///"+mode);  // 필터링.

        Log.d("보낸사람",accountList.get(position).senderId);
        Log.d("받는 사람",accountList.get(position).targetNickName);


        // mode  = 0 전체 내역 보여주기  1 입금받은 내역 보기 2 출금 내역 보기
        if ( mode == 0){
            if(accountList.get(position).senderId.equals(loginUserNick) ){ // 아이디가 동일하다면 출금
                ((RowCell)holder).receiveMode.setText("출금");
                ((RowCell)holder).receiveMode.setTextColor(Color.RED);
                ((RowCell)holder).senderNickname.setText(accountList.get(position).targetNickName); // 이름이 뜰곳
            }else { // 다르다면 받음
                ((RowCell)holder).receiveMode.setText("입금");
                ((RowCell)holder).receiveMode.setTextColor(Color.BLUE);
                ((RowCell)holder).senderNickname.setText(accountList.get(position).senderId); // 이름이 뜰곳
            }
        }else if(mode == 1){  // 입금 받은 내역 보기
            ((RowCell)holder).receiveMode.setText("입금");
            ((RowCell)holder).receiveMode.setTextColor(Color.BLUE);
            ((RowCell)holder).senderNickname.setText(accountList.get(position).senderId);
        }else if(mode == 2){  // 출금한 내역
            ((RowCell)holder).receiveMode.setText("출금");
            ((RowCell)holder).receiveMode.setTextColor(Color.RED);
            ((RowCell)holder).senderNickname.setText(accountList.get(position).targetNickName);
        }

    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    // 뷰 홀더
    private static class RowCell extends RecyclerView.ViewHolder  {

        public TextView receiveDate,senderNickname,receiveTitle,receiveMode,receiveMoney;
        public RowCell(View view) {
            super(view);
            receiveDate = (TextView)view.findViewById(R.id.receiveDate);
            senderNickname= (TextView)view.findViewById(R.id.senderNickname);
            receiveTitle= (TextView)view.findViewById(R.id.receiveTitle);
            receiveMode= (TextView)view.findViewById(R.id.receiveMode);
            receiveMoney= (TextView)view.findViewById(R.id.receiveMoney);

        }
    }
}
