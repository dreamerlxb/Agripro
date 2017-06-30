package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.LikeModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sxb on 16/10/14.
 */

public class LikeRepository extends ModelRepository<LikeModel> {
    public LikeRepository() {
        super("like",LikeModel.class);
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem2 = new RestContractItem("/Likes", "POST");
        contract.addItem(restContractItem2, className + ".create");

        RestContractItem restContractItem3 = new RestContractItem("/Likes/:id", "DELETE");
        contract.addItem(restContractItem3, className + ".deleteById");


        return contract;
    }

    public static LikeRepository getInstance(Context context){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        LikeRepository newRepo=adapter.createRepository(LikeRepository.class);

        return newRepo;
    }

    public static LikeRepository getInstance(Context context,String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        if (!TextUtils.isEmpty(accessToken)){
            adapter.setAccessToken(accessToken);
        }
        LikeRepository newRepo=adapter.createRepository(LikeRepository.class);

        return newRepo;
    }

    private void count(int newsId){
        Map<String,Object> parmas = new HashMap<>();
        Map<String,Object> where = new HashMap<>();

        where.put("newsId",newsId);

        parmas.put("where",where);
        this.invokeStaticMethod("count", parmas, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("LikeRepository s",response.toString());
            }

            @Override
            public void onError(Throwable t) {
                Log.i("LikeRepository e",t.toString());
            }
        });
    }

    public void createLike(int likerId, int newsId, final ObjectCallback<LikeModel> likeModelObjectCallback){
        Map<String,Object> parmas = new HashMap<>();
        parmas.put("likerId",likerId);
        parmas.put("newsId",newsId);
        invokeStaticMethod("create", parmas, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                LikeModel likeModel = gson.fromJson(response.toString(),LikeModel.class);
                Double id = (Double) likeModel.getId();
                likeModel.setLikeId(id.intValue());

                likeModelObjectCallback.onSuccess(likeModel);
            }

            @Override
            public void onError(Throwable t) {
                likeModelObjectCallback.onError(t);
            }
        });
    }

    public void findLike(int likerId, int newsId, final ObjectCallback<LikeModel> likeModelObjectCallback){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> filter = new HashMap<>();
        Map<String,Object> where = new HashMap<>();

        where.put("likerId",likerId);
        where.put("newsId",newsId);
        filter.put("where",where);
        params.put("filter",filter);

        invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                if (response==null || response.length() == 0){
                    likeModelObjectCallback.onSuccess(null);
                } else {
                    JSONObject jsonObject = response.optJSONObject(0);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    LikeModel likeModel = gson.fromJson(jsonObject.toString(),LikeModel.class);
                    Double id = (Double) likeModel.getId();
                    likeModel.setLikeId(id.intValue());

                    likeModelObjectCallback.onSuccess(likeModel);
                }
            }

            @Override
            public void onError(Throwable t) {
                likeModelObjectCallback.onError(t);
            }
        });
    }



    public void delLike(int likeId, final Adapter.JsonObjectCallback jsonObjectCallback){
        Map<String,Object> params = new HashMap<>();
        params.put("id",likeId);
        invokeStaticMethod("deleteById", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                jsonObjectCallback.onSuccess(response);
            }

            @Override
            public void onError(Throwable t) {
                jsonObjectCallback.onError(t);
            }
        });
    }
}
