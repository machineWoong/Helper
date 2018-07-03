package com.example.jeon.helper;

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
import com.example.jeon.helper.noti_table.noti_Content;
import com.example.jeon.helper.noti_table.noti_List_Main;
import com.example.jeon.helper.noti_table.show_noti;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-06-12.
 */

public class main_noti_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    Context context;
    ArrayList<noti_Content> ncArray = new ArrayList<>();

    String loginUserId;
    String loginUserNick;

    String [] filter;


    public main_noti_Adapter(Context context, ArrayList<noti_Content> ncArray, String loginUserId, String loginUserNick) {
        this.context = context;
        this.ncArray = ncArray;
        this.loginUserId = loginUserId;
        this.loginUserNick = loginUserNick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_noti_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // 모드
        ((RowCell)holder).main_noti_item_mode.setText( ncArray.get(position).notiMode);

        // 제목
        ((RowCell)holder).main_noti_item_title.setText( ncArray.get(position).title);

        // 작성일
        filter = ncArray.get(position).makeDate.split("!");
        ((RowCell)holder).main_noti_item_makeDate.setText( filter[1]);


        ((RowCell)holder).main_noti_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 번호의 객체를 넘겨준다.
                Intent gotoShowNoti = new Intent(context,show_noti.class);
                gotoShowNoti.putExtra("loginUserId",loginUserId);
                gotoShowNoti.putExtra("loginUserNick",loginUserNick);
                gotoShowNoti.putExtra("data",ncArray.get(position));
                ((HelperMain)context).startActivity(gotoShowNoti);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ncArray.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {


        public TextView main_noti_item_mode,main_noti_item_title,main_noti_item_makeDate;
        public LinearLayout main_noti_item_layout;

        public RowCell(View view) {
            super(view);
            main_noti_item_mode = (TextView) view.findViewById(R.id.main_noti_item_mode);
            main_noti_item_title = (TextView) view.findViewById(R.id.main_noti_item_title);
            main_noti_item_makeDate = (TextView) view.findViewById(R.id.main_noti_item_makeDate);

            main_noti_item_layout = (LinearLayout)view.findViewById(R.id.main_noti_item_layout);
        }
    }
}
