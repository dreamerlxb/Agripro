package com.idejie.android.aoc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.repository.NewsRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsDetailActivity extends AppCompatActivity {
    public final static String EXTRA="EXTRA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        RestAdapter adapter = new RestAdapter(getApplicationContext(), "http://211.87.227.214:3001/api");
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        NewsRepository newRepo=adapter.createRepository(NewsRepository.class);
        newRepo.findById(getIntent().getStringExtra(EXTRA), new ObjectCallback<NewsModel>() {
            @Override
            public void onSuccess(NewsModel object) {
                TextView tv_title = (TextView) findViewById(R.id.tv_detail_title);
                tv_title.setText(object.getTitle());
                DateFormat format =new SimpleDateFormat("yyyy-MM-dd H:m:s");
                try {
                    Date timeNews =format.parse(object.getLastUpdated());
                    TextView tv_time = (TextView) findViewById(R.id.tv_detail_time);
                    tv_time.setText(format.format(timeNews));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String customHtml =object.getContent();
                WebView webView = (WebView) findViewById(R.id.Content);
                webView.loadDataWithBaseURL(null,customHtml,"text/html","UTF-8",null);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                webView.setWebChromeClient(new WebChromeClient());
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }
}
