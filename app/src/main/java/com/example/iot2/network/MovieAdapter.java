package com.example.iot2.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MovieAdapter extends BaseAdapter {

    private Context context;
    private int resId;
    private List<Movie> movies;

    private ImageLoader loader;

    public MovieAdapter(Context context, int resId, List<Movie> movies) {
        this.movies = movies;
        this.resId = resId;
        this.context = context;
        loader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View convertView = view;
        if (convertView == null) {
            convertView = View.inflate(context, resId, null);
        }

        final Movie movie = movies.get(i);
        final View v = convertView;

        ((TextView)convertView.findViewById(R.id.title)).setText(
                movie.getTitle());
        ((RatingBar)convertView.findViewById(R.id.rating)).setRating(
                movie.getUserRating());

   		try {
			if (movie.getImage() == null || movie.getImage().length() == 0) {
				Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
				((ImageView)convertView.findViewById(R.id.image)).setImageBitmap(bm);
			} else {
                //이미지 다운로드 처리 (다른 쓰레드에서 처리)
//				Thread thread = new Thread() {
//					public void run() {
//						try {
//							URL url = new URL(movie.getImage());
//							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//							InputStream istream = conn.getInputStream();
//							//스트림을 이미지 객체로 변환
//							final Bitmap bm = BitmapFactory.decodeStream(istream);
//							istream.close();
//
//							((Activity) context).runOnUiThread(new Runnable() {
//								@Override
//								public void run() {
//                                    //수신된 이미지를 이미지에뷰에 적용
//									((ImageView) v.findViewById(R.id.image)).setImageBitmap(bm);
//								}
//							});
//
//						} catch (Exception ex) {}
//					}
//				};
//				thread.start();
//				thread.join(); //thread가 종료될 때까지 대기(이미지 다운로드가 끝날때까지 기다리기)//Thread를 돌리지만 동기방식으로

                //Lazy-Loading
                ImageView imageView =
                        (ImageView)convertView.findViewById(R.id.image);
                imageView.setTag(movie.getImage());
                loader.displayImage(
                        movie.getImage(), (Activity)context, imageView);
			}


		} catch (Exception ex) {}

        return convertView;
    }
}
