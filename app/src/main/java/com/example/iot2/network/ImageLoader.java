package com.example.iot2.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

//Cache 사용 기능 제공 (Memory Cache, SD Card Cache)
//Lazy Loading 방식 사용 (처음에는 임시 이미지 사용 -> 다운로드 끝나면 이미지 교체)
public class ImageLoader {

    private Context context;

    //Memory Cache로 사용할 Map 객체
    private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

    //File Cache 경로 (SD Card 사용)
    private File cacheDir;

    public ImageLoader(Context context) {
        this.context = context;
        //Image 다운로드를 처리하는 작업 쓰레드의 우선 순위 설정 (낮게)
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

        cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "lazy-list");

        if (!cacheDir.exists())
            cacheDir.mkdirs();//SD 카드에 만들어짐.//2번째 방법
    }

    //임시 이미지 리소스 아이디 (실제 이미지가 다운로드 되기 전에 사용할 이미지)
    final int stub_id = R.mipmap.ic_launcher;

    /************************************************
     * 외부에서 호출될 이미지 다운로드 명령 메서드
     * @param url 이미지 경로
     * @param activity 실행 중인 액티비티
     * @param imageView 이미지가 표시될 (적용될) 위젯
     ***********************************************/
    public void displayImage(String url, Activity activity, ImageView imageView) {

        if (url == null || url.length() == 0) {//이미지 경로가 존재하지 않는 경우
            imageView.setImageResource(stub_id);//임시 이미지 설정
            return;//작업 종료
        }

        if (cache.containsKey(url))//memory cache에서 이미지 찾기
            imageView.setImageBitmap(cache.get(url));
        else {
            queuePhoto(url, activity, imageView);//이미지 처리 작업 생성 및 작업 큐에 추가 //download
            imageView.setImageResource(stub_id);//임시 이미지 지정
        }
    }

    private void queuePhoto(String url, Activity activity, ImageView imageView) {
        //지정된 이미지 뷰에 해당하는 작업이 등록되어 있다면 제거
        photosQueue.Clean(imageView);

        PhotoToLoad p = new PhotoToLoad(url, imageView);//작업 생성

        synchronized (photosQueue.photosToLoad) {
            photosQueue.photosToLoad.push(p);//작업 큐에 작업 등록
            photosQueue.photosToLoad.notifyAll();//작업 큐 관리 쓰레드 깨우기
        }

        //작업 큐 관리 쓰레드가 시작되지 않은 경우 작업 큐 관리 쓰레드 시작
        if (photoLoaderThread.getState() == Thread.State.NEW)
            photoLoaderThread.start();
    }

    private Bitmap getBitmap(String url) {
        //URL을 (빨리 읽을 수 있는)변형문자열(hashcode)로 만들어서 파일 객체 만들기
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);

        //파일 캐시에서 파일 찾기 (파일이 있다면 반환... 끝)
        if (f.exists()) {
            Bitmap b = decodeFile(f);//최적화된 이미지 파일 반환
            if (b != null) {
                return b;
            }
        }
        //네트워크에서 다운로드
        try {
            Bitmap bitmap = null;
            InputStream is = new URL(url).openStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);//파일 캐시에 파일 저장
            os.close();

            bitmap = decodeFile(f); //최적화된 이미지 파일 반환
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //파일 최적화 작업 수행
    private Bitmap decodeFile(File f) {
        try {
            //이미지 크기 분석 (이미지를 메모리에 로딩하지 않고 분석)
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //최적화된 이미지 확대/축소 비율 계산
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //계산된 비율로 이미지 생성
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //작업 클래스
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    PhotosQueue photosQueue = new PhotosQueue();

    public void stopThread() {
        photoLoaderThread.interrupt();
    }

    //작업 큐 클래스
    class PhotosQueue {
        private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

        //같은 ImageView에 대해 등록된 작업이 있다면 제거
        public void Clean(ImageView image) {
            for (int j = 0; j < photosToLoad.size(); ) {
                if (photosToLoad.get(j).imageView == image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }

    //작업 큐 관리 쓰레드
    class PhotosLoader extends Thread {
        public void run() {
            try {
                while (true) {
                    //현재 큐에 처리할 작업이 없으면 쉬는 시간 (깨울 때까지 잠자기)
                    if (photosQueue.photosToLoad.size() == 0) {
                        synchronized (photosQueue.photosToLoad) {
                            photosQueue.photosToLoad.wait();//notify 호출에 의해 재동작
                        }
                    } else {
                        PhotoToLoad photoToLoad;
                        synchronized (photosQueue.photosToLoad) {
                            //작업 큐에서 작업 꺼내기
                            photoToLoad = photosQueue.photosToLoad.pop();
                        }
                        //작업의 url을 이용해서 이미지 생성 (파일에서 읽기 or download)
                        Bitmap bmp = getBitmap(photoToLoad.url);

                        //메모리 캐시에 저장
                        cache.put(photoToLoad.url, bmp);

                        //작업에 저장된 이미지 뷰를 찾아서 이미지 설정 (runOnUiThread 사용)
                        Object tag = photoToLoad.imageView.getTag();
                        if (tag != null && ((String) tag).equals(photoToLoad.url)) {
                            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView);
                            //Activity a = (Activity) photoToLoad.imageView.getContext();
                            Activity a = (Activity)context;
                            a.runOnUiThread(bd);//UI 쓰레드에서 작업 실행
                        }
                    }
                    if (Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    PhotosLoader photoLoaderThread = new PhotosLoader();

    //UI 쓰레드에서 이미지뷰에 이미지를 설정 처리를 위해 사용하는 클래스
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;

        public BitmapDisplayer(Bitmap b, ImageView i) {
            bitmap = b;
            imageView = i;
        }

        public void run() {
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        cache.clear();

        File[] files = cacheDir.listFiles();
        for (File f : files)
            f.delete();
    }

}
