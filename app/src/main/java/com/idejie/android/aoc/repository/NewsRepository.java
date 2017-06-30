package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.callback.CustomCallback;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.model.NewsModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shandongdaxue on 16/8/28.
 */

public class NewsRepository extends ModelRepository<NewsModel> {

    public NewsRepository() {
        super("new",NewsModel.class);
    }

    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem2 = new RestContractItem("/news/:id/comments/count", "GET");
        contract.addItem(restContractItem2, className + ".newsCommentsCount");

        RestContractItem restContractItem3 = new RestContractItem("/news/:id/likers/count", "GET");
        contract.addItem(restContractItem3, className + ".newsLikersCount");

        RestContractItem restContractItem4 = new RestContractItem("/news/fetchNews", "GET");
        contract.addItem(restContractItem4, className + ".fetchNews");

        RestContractItem restContractItem5 = new RestContractItem("/news", "GET");
        contract.addItem(restContractItem5, className + ".all");

        return contract;
    }

    public static NewsRepository getInstance(Context context,String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        if (!TextUtils.isEmpty(accessToken)) {
            adapter.setAccessToken(accessToken);
        }

        NewsRepository newRepo=adapter.createRepository(NewsRepository.class);
        return newRepo;
    }

    public void fetchNews(Map<String, Object> params, final ListCallback<NewsModel> listCallback) {
        this.invokeStaticMethod("fetchNews", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray jsonArray = response.optJSONArray("res");
                Gson gson = CommonUtil.getGSON();
                List<NewsModel> objects = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.optJSONObject(i);
                    NewsModel newsModel = gson.fromJson(jsonObj.toString(), NewsModel.class);
                    Double id = (Double) newsModel.getId();
                    newsModel.setNewsId(id.intValue());
                    objects.add(newsModel);
                }
                listCallback.onSuccess(objects);
            }

            @Override
            public void onError(Throwable t) {
                listCallback.onError(t);
            }
        });
    }

    public void newsCommentsCount(int newsId, final CustomCallback newsCountCallBack) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id",newsId);
        this.invokeStaticMethod("newsCommentsCount", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("newsCommentsCount",response.toString());
                int count = response.optInt("count");
                newsCountCallBack.onSuccess(count);
            }

            @Override
            public void onError(Throwable t) {
                Log.i("newsCommentsCount",t.toString());
                newsCountCallBack.onError(t);
            }
        });
    }

    public void newsLikersCount(int newsId, final CustomCallback newsCountCallBack) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id",newsId);
        this.invokeStaticMethod("newsLikersCount", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("newsLikersCount",response.toString());
                int count = response.optInt("count");
                newsCountCallBack.onSuccess(count);
            }

            @Override
            public void onError(Throwable t) {
                Log.i("newsLikersCount",t.toString());
                newsCountCallBack.onError(t);
            }
        });
    }

    public void count(){

    }
}