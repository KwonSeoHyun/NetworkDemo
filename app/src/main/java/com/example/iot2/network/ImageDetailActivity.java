package com.example.iot2.network;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView mFullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        mFullImage = (ImageView)findViewById(R.id.full_image);
        //이 Activity를 호출한 Activity가 전달한 Intent 반환
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("image-path");

        ImageLoader loader = new ImageLoader(this);
        mFullImage.setTag(imagePath);
        loader.displayImage(imagePath, this, mFullImage);
    }
}
