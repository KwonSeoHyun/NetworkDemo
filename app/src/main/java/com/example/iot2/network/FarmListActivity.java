package com.example.iot2.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.farmstory.vo.Plant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FarmListActivity extends AppCompatActivity {

    private ListView mPlantListView;
    private PlantListAdapter mPlantListAdapter; //Adapter에 data + xml파일 만들어줘야함
    private List<Plant> mPlants;
    private TextView mLogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_list); //XML 내용을 읽어서 화면으로 사용

        mLogText = findViewById(R.id.log);

        mPlants = new ArrayList<>();
        mPlantListAdapter = new PlantListAdapter(mPlants, this, R.layout.plant_item_view);

        mPlantListView = findViewById(R.id.plant_list);

        mPlantListView.setAdapter(mPlantListAdapter);

        loadPlants();
    }

    private void loadPlants() {

        Thread t = new Thread() {
            public void run() {
                try {
                    int x = (int)(Math.random() * 900) + 100;
                    String y = String.valueOf((int)(Math.random() * 900) + 100);
                    String serverUrl = String.format("http://172.16.6.100:8087/farmstory/mobile_plant_list.action?x=%d&y=%s", x, y);

                    URL url = new URL(serverUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    final int responseCode = con.getResponseCode();

                    if (responseCode == 200) {  //정상 응답인 경우
                        processResult(con.getInputStream()); //응답받은내용을 처리하세요
                        //processResult1(con.getInputStream());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //show error message
                                Toast.makeText(getApplicationContext(),
                                        "error " + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
        t.start();

    }

    private void processResult(InputStream inputStream) {

        mPlants.clear();

        try {
            //JSON 문자열 -> 객체 트리로 변환하는 변환기 만들기
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            Plant[] plants = gson.fromJson(reader, Plant[].class); //JSON -> 객체

            for (Plant plant : plants) {
                mPlants.add(plant);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //데이터가 변경되었으니 목록을 갱신해서 표시하세요
                    mPlantListAdapter.notifyDataSetChanged();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processResult1(InputStream is) throws IOException {
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        final StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();
        mLogText.post(new Runnable() {
            @Override
            public void run() {
                mLogText.setText(response.toString());
            }
        });
    }
}
