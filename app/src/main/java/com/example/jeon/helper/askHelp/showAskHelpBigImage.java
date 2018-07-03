package com.example.jeon.helper.askHelp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jeon.helper.R;

public class showAskHelpBigImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ask_help_big_image);

        getShowAskHelpImage();
    }

    public void getShowAskHelpImage(){
        String imagePath = getIntent().getStringExtra("path");

        ImageView show = (ImageView)findViewById(R.id.showAskHelpBigImage);
        Glide.with(this).load(imagePath).into(show);

    }
}
