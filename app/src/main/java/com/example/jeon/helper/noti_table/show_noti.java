package com.example.jeon.helper.noti_table;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;

public class show_noti extends AppCompatActivity {
    noti_Content nC;
    com.example.jeon.helper.ip ip = new ip();
    String ipad = ip.getIp();
    String date;


    String loginUserId;
    String loginUserNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_noti);


        getdata();

        Button show_noti_okBtn = (Button)findViewById(R.id.show_noti_okBtn);
        show_noti_okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getdata(){
        nC = (noti_Content) getIntent().getSerializableExtra("data");
        loginUserId = getIntent().getStringExtra("loginUserId");
        loginUserNick = getIntent().getStringExtra("loginUserNick");


        // 데이터 세팅
        if ( nC.notiMode.equals("공지사항")){
            TextView show_noti_titleBar = (TextView)findViewById(R.id.show_noti_titleBar);
            show_noti_titleBar.setText("공지사항");
        }else{
            TextView show_noti_titleBar = (TextView)findViewById(R.id.show_noti_titleBar);
            show_noti_titleBar.setText("이벤트");
        }


        // 작성자
        TextView show_noti_makeNick = (TextView)findViewById(R.id.show_noti_makeNick);
        show_noti_makeNick.setText(nC.makeNick);

        date =  nC.makeDate.replace("!",".");

        // 작성날짜
        TextView show_noti_makeDate = (TextView)findViewById(R.id.show_noti_makeDate);
        show_noti_makeDate.setText(date);


        // 제목
        TextView show_noti_title = (TextView)findViewById(R.id.show_noti_title);
        show_noti_title.setText(nC.title);

        // 내용
        TextView show_noti_content = (TextView)findViewById(R.id.show_noti_content);
        show_noti_content.setText(nC.content);

        // 이미지
        if ( nC.image.equals("없음")){
            ImageView show_noti_image = (ImageView)findViewById(R.id.show_noti_image);
            show_noti_image.setVisibility(View.GONE);
        }else{
            ImageView show_noti_image = (ImageView)findViewById(R.id.show_noti_image);
            Glide.with(this).load(ipad+"/"+nC.image).into(show_noti_image);
        }

        // 수정 버튼
        if ( loginUserId.equals("admin")){
            Button show_noti_editBtn = (Button)findViewById(R.id.show_noti_editBtn);
            show_noti_editBtn.setVisibility(View.VISIBLE);
            show_noti_editBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 수정 페이지로 이동.
                    Intent gotoEdit = new Intent(show_noti.this,make_noti_page.class);
                    gotoEdit.putExtra("mode",2);
                    gotoEdit.putExtra("loginUserId",loginUserId);
                    gotoEdit.putExtra("loginUserNick",loginUserNick);
                    gotoEdit.putExtra("editData",nC);
                    startActivityForResult(gotoEdit,1111);

                }

            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1111){
            if(resultCode == RESULT_OK){
                //데이터 재셋팅.
                finish();
            }else{
                Toast.makeText(this, "수정 취소", Toast.LENGTH_SHORT).show();;
            }
        }
    }
}
