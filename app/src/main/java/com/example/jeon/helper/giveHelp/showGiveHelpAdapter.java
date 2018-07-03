package com.example.jeon.helper.giveHelp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;
import com.example.jeon.helper.askHelp.showAskHelpBigImage;
import com.example.jeon.helper.askHelp.showMyAskHelp;
import com.example.jeon.helper.ip;

import java.util.ArrayList;

/**
 * Created by JEON on 2018-05-09.
 */

public class showGiveHelpAdapter extends PagerAdapter {
    public LayoutInflater inflater;
    public Context context;
    public ArrayList<String> images = new ArrayList<>();

    ip ip = new ip();
    String ipad = ip.getIp();

    //생성자
    public showGiveHelpAdapter(Context context,ArrayList<String> images){
        this.context = context;
        this.images = images;
    }

    // 아이템 갯수
    @Override
    public int getCount() {
        return images.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    //실질적으로 뿌려주는 곳
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.show_give_help_image_slider,container,false);

        ImageView sliderImage = (ImageView)v.findViewById(R.id.showGiveHelp_ImgaeSlide);
        final String a = ipad+"/"+images.get(position);
        Glide.with(context).load(a).into(sliderImage);

        // 보고있는 이미지 페이지
        TextView startPo = (TextView)v.findViewById(R.id.showGiveHelp_pageStart);
        startPo.setText(String.valueOf(position+1));

        // 이미지 총 페이지수
        TextView endPo = (TextView)v.findViewById(R.id.showGiveHelp_pageEnd);
        endPo.setText(String.valueOf(images.size()));

        container.addView(v);

        // 이미지크게 보여주기
        sliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showBig = new Intent(context,showAskHelpBigImage.class);
                showBig.putExtra("path",a);
                ((showGiveHelp)context).startActivity(showBig);
            }
        });


        return v;
    }


    // 아이템 파괴
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();// 컨테이너가 틀렷음을 입증하는 메소드  ( 해석이 이럼.. )
    }
}
