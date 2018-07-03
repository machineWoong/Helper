package com.example.jeon.helper.noti_table;

import android.app.Activity;
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
import com.example.jeon.helper.main_BestHelper_RecyclerViewAdater;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-06-12.
 */

public class noti_List_Adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    ArrayList<noti_Content> ncArray = new ArrayList<>();
    String loginUserId;
    String loginUserNick;

    String [] filter;

    public noti_List_Adapter(Context context, ArrayList<noti_Content> ncArray, String loginUserId, String loginUserNick) {
        this.context = context;
        this.ncArray = ncArray;
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_list_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        // 모드
        ((RowCell)holder).noti_item_mode.setText( ncArray.get(position).notiMode);

        // 제목
        ((RowCell)holder).noti_item_title.setText( ncArray.get(position).title);

        // 작성일
        filter = ncArray.get(position).makeDate.split("!");
        ((RowCell)holder).noti_item_makeDate.setText( filter[1]);

        // 작성자
        ((RowCell)holder).noti_item_makeNick.setText( ncArray.get(position).makeNick);

        ((RowCell)holder).noti_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 번호의 객체를 넘겨준다.
                Intent gotoShowNoti = new Intent(context,show_noti.class);
                gotoShowNoti.putExtra("loginUserId",loginUserId);
                gotoShowNoti.putExtra("loginUserNick",loginUserNick);
                gotoShowNoti.putExtra("data",ncArray.get(position));
                ((noti_List_Main)context).startActivity(gotoShowNoti);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ncArray.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {


        public TextView noti_item_mode,noti_item_title,noti_item_makeDate,noti_item_makeNick;
        public LinearLayout noti_item_layout;

        public RowCell(View view) {
            super(view);
            noti_item_mode = (TextView) view.findViewById(R.id.noti_item_mode);
            noti_item_title = (TextView) view.findViewById(R.id.noti_item_title);
            noti_item_makeDate = (TextView) view.findViewById(R.id.noti_item_makeDate);
            noti_item_makeNick = (TextView) view.findViewById(R.id.noti_item_makeNick);

            noti_item_layout = (LinearLayout)view.findViewById(R.id.noti_item_layout);
        }
    }

}
