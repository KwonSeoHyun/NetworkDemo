package com.example.iot2.network;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmstory.vo.Plant;

import java.net.URLEncoder;
import java.util.List;

public class PlantListAdapter extends BaseAdapter {

    private List<Plant> mPlants;
    private Context mActivityContext;
    private int mResourceId;

    private ImageLoader imageLoader;

    private String basePath = "http://172.16.6.100:8087/farmstory/resources/upload-image/plant-info/";

    public PlantListAdapter(List<Plant> mPlants, Context mActivityContext, int mResourceId) {
        this.mPlants = mPlants;
        this.mActivityContext = mActivityContext;
        this.mResourceId = mResourceId;

        imageLoader = new ImageLoader(mActivityContext);
    }

    @Override
    public int getCount() {
        return mPlants.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mPlants.get(position).getPlaNo();
    }

    //목록에 표시될 화면 요소 한단위를 만드는 메서드
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(mActivityContext, mResourceId, null);
        }

        Plant plant = mPlants.get(position);

        ImageView imageView = view.findViewById(R.id.plant_image);
        TextView plantName = view.findViewById(R.id.plant_name);
        plantName.setText(plant.getPlaName());
        TextView plantType = view.findViewById(R.id.plant_type);
        plantType.setText(plant.getPlaType());

        //Bitmap Download
        String imagePath = plant.getPliImg();
        String encodedPath = imagePath;
        try {
            encodedPath = URLEncoder.encode(imagePath, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        imageView.setTag(basePath + encodedPath);
        imageLoader.displayImage(basePath + encodedPath, (Activity)mActivityContext, imageView);

        return view;
    }
}
