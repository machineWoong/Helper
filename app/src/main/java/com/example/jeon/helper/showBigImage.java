package com.example.jeon.helper;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class showBigImage extends Activity {

    int number;
    ArrayList<String> imageArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시계창 없애기
        setContentView(R.layout.activity_show_big_image);

        // 이미지 정보 가져오기
        getImages();

        // 이미지 뿌려주기
        showImage();
    }

    public void getImages(){
        imageArray = getIntent().getStringArrayListExtra("imageArray");
        number = getIntent().getIntExtra("imageNumber",0);
        showLg();
    }

    public void showLg(){
        for ( int i = 0; i < imageArray.size();i++){
            Log.d("어레이 리스트 ","어레이 리스트 !!  : "+imageArray.get(i));
        }
    }
    public void showImage(){
        ImageButton show = (ImageButton)findViewById(R.id.showImage);
        Glide.with(this).load(imageArray.get(number)).into(show);
    }
}
