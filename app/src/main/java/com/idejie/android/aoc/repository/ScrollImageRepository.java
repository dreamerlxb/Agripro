package com.idejie.android.aoc.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.model.ScrollImageModel;
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
 * Created by slf on 16/8/30.
 */

public class ScrollImageRepository extends ModelRepository<ScrollImageModel> {

    public ScrollImageRepository() {
        super("scrollImage", ScrollImageModel.class);
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem5 = new RestContractItem("/scrollImages", "GET");
        contract.addItem(restContractItem5, className + ".all");

        return contract;
    }

    public static ScrollImageRepository getInstance(Context context){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        ScrollImageRepository newRepo=adapter.createRepository(ScrollImageRepository.class);

        return newRepo;
    }

    public static ScrollImageRepository getInstance(Context context,String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        adapter.setAccessToken(accessToken);
        ScrollImageRepository newRepo=adapter.createRepository(ScrollImageRepository.class);

        return newRepo;
    }

    public void all(final ListCallback<ScrollImageModel> scrollImageModelListCallback){
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();

        List<String> list = new ArrayList<>();
        list.add("image");
        list.add("news");

        where.put("enabled",true);
        filter.put("where",where);
        filter.put("order","order");
        filter.put("include",list);

        Gson gson1 = new Gson();
        String filterStr =  gson1.toJson(filter);

        params.put("filter", filterStr);

        invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("all scrollImage",response.toString());

                if (response.length() > 0){
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    List<ScrollImageModel> objects = new ArrayList<ScrollImageModel>(response.length());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.optJSONObject(i);
                        ScrollImageModel newsModel = gson.fromJson(jsonObj.toString(),ScrollImageModel.class);
                        Double id = (Double) newsModel.getId();
                        newsModel.setScrollImageId(id.intValue());
                        objects.add(newsModel);
                    }
                    scrollImageModelListCallback.onSuccess(objects);
                }
            }

            @Override
            public void onError(Throwable t) {
                scrollImageModelListCallback.onError(t);
                Log.i("all scrollImage",t.toString());
            }
        });
    }

    public void all(Map<String, Object> params, final ListCallback<ScrollImageModel> scrollImageModelListCallback){

        invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("all scrollImage",response.toString());

                if (response.length() > 0){
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    List<ScrollImageModel> objects = new ArrayList<ScrollImageModel>(response.length());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.optJSONObject(i);
                        ScrollImageModel newsModel = gson.fromJson(jsonObj.toString(),ScrollImageModel.class);
                        Double id = (Double) newsModel.getId();
                        newsModel.setScrollImageId(id.intValue());
                        objects.add(newsModel);
                    }
                    scrollImageModelListCallback.onSuccess(objects);
                }
            }

            @Override
            public void onError(Throwable t) {
                scrollImageModelListCallback.onError(t);
                Log.i("all scrollImage",t.toString());
            }
        });
    }
}