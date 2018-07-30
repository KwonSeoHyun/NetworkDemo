package com.example.iot2.network;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.media.CamcorderProfile.get;

public class ImageActivity extends AppCompatActivity {

    Button mSearchButton;
    EditText mSearchText;
    GridView mImageGridView;

    ArrayList<Image> images = new ArrayList<>();
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mSearchText = (EditText)findViewById(R.id.image_search_text);
        mSearchButton = (Button)findViewById(R.id.image_search_button);
        mImageGridView = (GridView)findViewById(R.id.image_gridview);

        adapter = new ImageAdapter(this, images, 0);
        mImageGridView.setAdapter(adapter);

        mImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Image image = images.get(i);

                //Activity 호출 1
//                Intent intent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(image.getImage()));
//                startActivity(intent);

                //Activity 호출 2
                Intent intent = new Intent(ImageActivity.this,
                        ImageDetailActivity.class);
                intent.putExtra("image-path", image.getImage());
                startActivity(intent);
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현재 표시되고 있는 소프트키보드 숨기기
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);

                adapter.setImageSize(mImageGridView.getColumnWidth());

                Thread thread = new Thread() {
                    public void run() {
                        //요청 정보 구성 -> GET 방식의 URL 문자열 만들기
                        String apiKey = "d37ef2fed0ee523406720015b67af946";
                        String format = "json"; //json or xml
                        String searchText = mSearchText.getText().toString();
                        try {
                            //한글과 같은 문자셋을 url 형식으로 인코딩
                            searchText = URLEncoder.encode(searchText, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String urlString = String.format("https://apis.daum.net/search/image?apikey=%s&q=%s&result=20&output=%s", apiKey, searchText, format);

                        try {
                            URL url = new URL(urlString); // 요청을 관리하는 객체 만들기
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //연결 만들기

                            int responseCode = conn.getResponseCode(); //요청 보내기 + 응답 수신 + 응답 코드 반환
                            if (responseCode == HttpURLConnection.HTTP_OK) {

                                if (format.equals("xml")) {
                                    processXmlResult(conn);
                                } else {
                                    processJsonResult(conn);
                                }

                                ImageActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //데이터가 갱신되었으므로 화면 갱신 (어댑터뷰 갱신)
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        });
    }

    private void processJsonResult(HttpURLConnection conn) {

        images.clear();

        try {
            //JSON 문자열 -> 객체 트리로 변환하는 변환기 만들기
            InputStream is = conn.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            JsonParser parser = new JsonParser();

            //변환 처리 -> JsonElement 반환
            JsonElement je = parser.parse(reader);

            //객체 탐색
            JsonObject doc = je.getAsJsonObject();
            JsonObject root = doc.getAsJsonObject("channel"); // channel : { ... } 요소 탐색
            JsonArray items = root.getAsJsonArray("item"); // item: [ ... ] 요소 탐색

            Gson gson = new Gson();
            for (int i = 0; i < items.size(); i++) {
                JsonObject element = items.get(i).getAsJsonObject();
                Image image = gson.fromJson(element, Image.class); // JSON 객체를 VO 객체로 직접 변환
                image.setTitle(
                        image.getTitle()
                                .replace("&lt;b&gt", "")
                                .replace("&lt;/b&gt", ""));//<b>title</b> -> title
                images.add(image);
            }
        } catch (Exception ex) {
        }

    }

    private void processXmlResult(HttpURLConnection conn) throws ParserConfigurationException, SAXException, IOException {

        //XML -> 객체 트리로 변환하는 변환기(파서) 만들기
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        //실제 변환 수행 -> 객체 트리를 반환
        Document doc = builder.parse(conn.getInputStream());

        images.clear();

        NodeList list = doc.getElementsByTagName("item");//<item> 엘리먼트 요청

        for (int i = 0; i <list.getLength(); i++) {
            Element element = (Element)list.item(i);
            Image image = new Image();
            NodeList children = element.getChildNodes();//하위 엘리먼트 목록 반환
            String title = element.getElementsByTagName("title").item(0).getTextContent();
            image.setTitle(title.replace("<b>", "").replace("</b>", ""));
            image.setLink(element.getElementsByTagName("link").item(0).getTextContent());
            image.setImage(element.getElementsByTagName("image").item(0).getTextContent());
            image.setThumbnail(element.getElementsByTagName("thumbnail").item(0).getTextContent());
            image.setPubDate(element.getElementsByTagName("pubDate").item(0).getTextContent());
            image.setWidth(Integer.parseInt(element.getElementsByTagName("width").item(0).getTextContent()));
            image.setHeight(Integer.parseInt(element.getElementsByTagName("height").item(0).getTextContent()));
            image.setCp(element.getElementsByTagName("cp").item(0).getTextContent());
            images.add(image);
        }
    }
}
