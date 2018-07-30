package com.example.iot2.network;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MovieActivity extends AppCompatActivity {

    private Button mSearchButton;
    private EditText mSearchText;
    private TextView mTextView;
    private ListView mMovieList;

    private List<Movie> movies;     // 영화 정보 데이터 목록
    private MovieAdapter adapter;   //리스트뷰에 사용할 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mSearchButton = (Button)findViewById(R.id.search_button);
        mSearchText = (EditText)findViewById(R.id.search_text);
        mTextView = (TextView)findViewById(R.id.textView);
        mMovieList = (ListView)findViewById(R.id.movie_list);

        movies = new ArrayList<>();

        adapter = new MovieAdapter(
                this, R.layout.movieitem_view, movies);
        mMovieList.setAdapter(adapter);

        //ListView의 개별 항목을 터치했을 때 호출되는 메서드
        mMovieList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view,
                                    int position,
                                    long itemId) {

//                Movie movie = movies.get(position);
//                String link = movie.getLink();
//                if (link != null && link.length() > 0) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW,
//                            Uri.parse(link));
//                    startActivity(intent);
//                }
            }
        });

        //검색 버튼 클릭 이벤트 처리기 등록
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //네이버 오픈 API를 이용해서 데이터 조회 (네트워크 사용 + 쓰레드 기반)
                MovieRequestThread t = new MovieRequestThread();
                t.start();
            }
        });
    }

    class MovieRequestThread extends Thread {
        @Override
        public void run() {
            String clientId = "0ydX7qHGcXql39iKHwWy";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "Z6Rud5sNrb";//애플리케이션 클라이언트 시크릿값";
            try {
                String text = mSearchText.getText().toString();
                text = URLEncoder.encode(text, "UTF-8");
                String apiURL = String.format("https://openapi.naver.com/v1/search/movie.xml?query=%s&display=100&start=1", text);

                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                int responseCode = con.getResponseCode();

                //응답 결과를 문자열 형식으로 확인하는 메서드 (layout xml에서 scrollview의 height 수정)
                //processResult1(con, responseCode);

                if (responseCode == 200) {  //정상 응답인 경우
                    processResult2(con.getInputStream());
                } else {
                    //show error message
                    Toast.makeText(getApplicationContext(),
                            "error " + responseCode, Toast.LENGTH_SHORT).show();
                }
                
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void processResult2(InputStream istream) {
        try {
            //파서(xml을 객체 트리로 만드는 도구) 객체를 만드는 과정
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //파싱 : XML 문자열 -> 객체 트리
            Document doc = builder.parse(istream);//XML 읽기 -> 객체 트리 생성

            //final ArrayList<Movie> movies = new ArrayList<>();
            movies.clear();//기존 목록 제거

            //문서 내에 있는 <item> 엘리먼트 요청
            NodeList list = doc.getElementsByTagName("item");
            //빠른 for문 안 됨
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                Movie movie = new Movie();
                NodeList children = element.getChildNodes();//하위 엘리먼트 목록 반환
                String title = children.item(0).getTextContent();
                movie.setTitle(title.replace("<b>", "").replace("</b>", ""));
                movie.setLink(children.item(1).getTextContent());
                movie.setImage(children.item(2).getTextContent());
                movie.setSubtitle(children.item(3).getTextContent());
                movie.setPubDate(children.item(4).getTextContent());
                movie.setDirector(children.item(5).getTextContent());
                movie.setActor(children.item(6).getTextContent());//<- element.getChildNodes().item(6).getTextContent()
                movie.setUserRating(Float.parseFloat(element.getChildNodes().item(7).getTextContent()));//getTextContent()는 문자형이므로
                movies.add(movie);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Adapter가 사용하는 데이터를 갱신하는 방식
                    //Adapter에게 데이터 변경 통지 -> 화면 갱신
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processResult1(
            HttpURLConnection con, int responseCode) throws IOException {
        BufferedReader br;
        if(responseCode==200) { // 정상 호출
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {  // 에러 발생
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }
        String inputLine;
        final StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(response.toString());
            }
        });
    }
}
