package com.idejie.android.aoc.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.MapData;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.fragment.tab.SecondLayerFragment;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.repository.NewsRepository;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SearchNewsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backImg;

    private EditText searchTxt;
    private ImageButton searchClear;
    private ImageButton searchSubmit;

    private ListView searchResults;
    private View noDataView;

    private SearchListAdapter searchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_news);

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(this);

        noDataView = findViewById(R.id.no_data_view);

        searchTxt = (EditText) findViewById(R.id.search_keyword);
        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    searchClear.setVisibility(View.GONE);
                } else {
                    searchClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    searchListAdapter.clearItems();
                } else {
                    searchNews(s.toString());
                }
            }
        });

        searchClear = (ImageButton) findViewById(R.id.search_clear);
        searchSubmit = (ImageButton) findViewById(R.id.search_submit);
        searchSubmit.setOnClickListener(this);
        searchClear.setOnClickListener(this);


//        searchRefresh = (SwipeRefreshLayout) findViewById(R.id.search_refresh);
//        searchRefresh.setOnRefreshListener(this);
//        searchRefresh.setEnabled(true);

        searchResults = (ListView) findViewById(R.id.search_list);

        searchListAdapter = new SearchListAdapter(this);
        searchResults.setAdapter(searchListAdapter);

        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsModel newsModel = (NewsModel) searchListAdapter.getItem(position);
                Intent intent =new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("news",newsModel);
                intent.putExtras(bundle);
                intent.setClass(SearchNewsActivity.this,NewsDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        openKeyboard();
    }

    private void openKeyboard() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                searchTxt.setFocusable(true);
                searchTxt.setFocusableInTouchMode(true);
                searchTxt.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager)searchTxt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(searchTxt, 0);
            }
        }, 998);
    }

    private void searchNews(String keyword){
        NewsRepository newsRepository = NewsRepository.getInstance(this,null);
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> filter = new HashMap<>();
        Map<String,Object> where = new HashMap<>();
        Map<String,Object> like = new HashMap<>();

        like.put("like","%"+keyword+"%");

        where.put("title",like);

        filter.put("where",where);
        params.put("filter",filter);

        newsRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
//                Log.i("1",response.toString());
                if (response.length() > 0) {
                    Gson gson = CommonUtil.getGSON();
                    List<NewsModel> objects = new ArrayList<NewsModel>(response.length());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.optJSONObject(i);
                        NewsModel newsModel = gson.fromJson(jsonObj.toString(),NewsModel.class);
                        Double id = (Double) newsModel.getId();
                        newsModel.setNewsId(id.intValue());
                        objects.add(newsModel);
                    }
                    searchListAdapter.updateItems(objects);
                    searchListAdapter.notifyDataSetChanged();
                    noDataView.setVisibility(View.GONE);
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchNewsActivity.this, "未找到相关新闻", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.i("1",t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.search_submit:
                if (!TextUtils.isEmpty(searchTxt.getText())){
                    searchNews(searchTxt.getText().toString());
                } else {
                    Toast.makeText(this,"请输入关键字",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.search_clear:
                searchTxt.setText("");
                if (searchListAdapter.getCount() > 0) {
                    searchListAdapter.clearItems();
                }
                break;
        }
    }

    private class SearchListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<NewsModel> newsList;

        public SearchListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            newsList = new ArrayList<>();
        }

        public void addItems(List<NewsModel> newsList2){
            newsList.addAll(newsList2);
        }
        public void updateItems(List<NewsModel> newsList2){
            newsList.clear();
            newsList.addAll(newsList2);
        }

        public void clearItems(){
            newsList.clear();
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public Object getItem(int position) {
            return newsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1,null);
            } else {
                textView = (TextView) convertView;
            }
            NewsModel newsModel = newsList.get(position);
            textView.setText(newsModel.getTitle());

            return textView;
        }
    }
}
