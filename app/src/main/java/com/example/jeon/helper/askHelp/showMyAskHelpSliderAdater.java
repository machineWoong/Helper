package com.example.jeon.helper.askHelp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.ip;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-02.
 */

public class showMyAskHelpSliderAdater extends PagerAdapter {

    // 넘어와야할 데이터들
    public LayoutInflater inflater;
    public Context context;
    public ArrayList<String> images = new ArrayList<>();
    public String a;

    public int count;

    ip ip = new ip();
    String ipad = ip.getIp();


    //생성자
    public showMyAskHelpSliderAdater (Context context,ArrayList<String> images,int count){
        this.context = context;
        this.images = images;
        this.count =count;
    }

    @Override
    public int getCount() {
        // 이미지들의 갯수
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // 안되면   view == ((View)object); 로 수정
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.show_my_ask_help_image_slider,container,false);
        ImageView sliderImage = (ImageView)v.findViewById(R.id.show_My_Ask_Help_Slider);

        // 글라이더 사용 부분  ( 포지션에 따라서 달라지도록 )
        //Glide.with(context).load(images.get(position)).into(sliderImage);
        final String a = ipad+"/"+images.get(position);
        Glide.with(context).load(a).into(sliderImage);

        // 보고있는 이미지 페이지
        TextView startPo = (TextView)v.findViewById(R.id.show_My_Ask_Help_Tv1);
        startPo.setText(String.valueOf(position+1));

        // 이미지 총 페이지수
        TextView endPo = (TextView)v.findViewById(R.id.show_My_Ask_Help_Tv2);
        endPo.setText(String.valueOf(count));

        container.addView(v);

        // 이미지크게 보여주기
        sliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showBig = new Intent(context,showAskHelpBigImage.class);
                showBig.putExtra("path",a);
                ((showMyAskHelp)context).startActivity(showBig);
            }
        });

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
