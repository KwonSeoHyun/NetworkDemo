package com.example.iot2.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHtmlActivity extends AppCompatActivity {

    private Button mReqHtml;
    private TextView mHtmlResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_html);

        mReqHtml = findViewById(R.id.req_html);
        mHtmlResult = findViewById(R.id.html_result);

        mReqHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HtmlRequestThread thread = new HtmlRequestThread();
                thread.start();

            }
        });

    }

    //모든 네트워크 코드는 별도의 쓰레드에서 실행되어야 합니다.
    class HtmlRequestThread extends Thread {
        @Override
        public void run() {
            try {
                //네트워크 연결을 관리하는 객체
                URL url = new URL("https://developers.google.com");
                //연결
                HttpURLConnection conn =
                        (HttpURLConnection)url.openConnection();

                int resultCode = conn.getResponseCode();//요청 + 응답
                if (resultCode == 200) { //정상 응답
                    InputStream is = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader breader = new BufferedReader(reader);
                    final StringBuilder sb = new StringBuilder(1024);
                    while (true) {
                        String line = breader.readLine();
                        if (line == null) break;
                        sb.append(line + "\r\n");
                    }
                    breader.close(); reader.close(); is.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHtmlResult.setText(sb.toString());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}








