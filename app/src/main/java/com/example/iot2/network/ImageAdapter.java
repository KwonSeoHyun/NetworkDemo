package com.example.iot2.network;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import static android.media.CamcorderProfile.get;

public class ImageAdapter extends BaseAdapter {

    Context context;
    List<Image> images;
    int resId;

    ImageLoader imageLoader;

    int imageSize;
    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public ImageAdapter(Context context, List<Image> images, int resId) {
        this.context = context;
        this.images = images;
        this.resId = resId;

        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = new ImageView(context);
            view.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize));
            ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        //Bitmap Download
        String imagePath = images.get(i).getThumbnail();
        view.setTag(imagePath);
        imageLoader.displayImage(
                imagePath, (Activity)context, (ImageView)view);

        return view;
    }
}
