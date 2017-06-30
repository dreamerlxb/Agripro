package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.FavourModel;
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

public class FavourRepository extends ModelRepository<FavourModel> {
    public FavourRepository() {
        super("favour",FavourModel.class);
    }

//    public static FavourRepository getInstance(Context context){
//        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
//        FavourRepository newRepo=adapter.createRepository(FavourRepository.class);
//
//        return newRepo;
//    }

    public static FavourRepository getInstance(Context context, String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        if (!TextUtils.isEmpty(accessToken)){
            adapter.setAccessToken(accessToken);
//            adapter.
        }
        FavourRepository newRepo=adapter.createRepository(FavourRepository.class);
//        newRepo.getAdapter().;
        return newRepo;
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem2 = new RestContractItem("/Favours", "POST");
        contract.addItem(restContractItem2, className + ".create");

        RestContractItem restContractItem3 = new RestContractItem("/Favours/:id", "DELETE");
        contract.addItem(restContractItem3, className + ".deleteById");


        return contract;
    }

    public void createFavour(int favorerId, int newsId, final ObjectCallback<FavourModel> favourModelObjectCallback){
        Map<String,Object> params = new HashMap<>();
        params.put("favorerId",favorerId);
        params.put("newsId",newsId);
        invokeStaticMethod("create", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                FavourModel favourModel = gson.fromJson(response.toString(),FavourModel.class);
                Double id = (Double) favourModel.getId();
                favourModel.setFavourId(id.intValue());

                favourModelObjectCallback.onSuccess(favourModel);
            }

            @Override
            public void onError(Throwable t) {
                favourModelObjectCallback.onError(t);
            }
        });
    }

    public void findFavour(int favorerId, int newsId, final ObjectCallback<FavourModel> favourModelObjectCallback){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> filter = new HashMap<>();
        Map<String,Object> where = new HashMap<>();

        where.put("favorerId",favorerId);
        where.put("newsId",newsId);
        filter.put("where",where);
        params.put("filter",filter);

        invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                if (response==null || response.length() == 0){
                    favourModelObjectCallback.onSuccess(null);
                } else {
                    JSONObject jsonObject = response.optJSONObject(0);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    FavourModel favourModel = gson.fromJson(jsonObject.toString(),FavourModel.class);
                    Double id = (Double) favourModel.getId();
                    favourModel.setFavourId(id.intValue());

                    favourModelObjectCallback.onSuccess(favourModel);
                }
            }

            @Override
            public void onError(Throwable t) {
                favourModelObjectCallback.onError(t);
            }
        });
    }

    public void delFavour(int favorerId, final Adapter.JsonObjectCallback jsonObjectCallback){
        Map<String,Object> params = new HashMap<>();
        params.put("id",favorerId);
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
