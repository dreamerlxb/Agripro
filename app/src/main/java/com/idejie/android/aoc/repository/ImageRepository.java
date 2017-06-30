package com.idejie.android.aoc.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.ImageModel;
import com.idejie.android.aoc.model.PriceModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by slf on 16/8/28.
 */

public class ImageRepository extends ModelRepository<ImageModel> {

    public ImageRepository() {
        super("image", ImageModel.class);
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

        RestContractItem restContractItem2 = new RestContractItem("/images", "POST");
        contract.addItem(restContractItem2, className + ".create");

        RestContractItem restContractItem3 = new RestContractItem("/images", "PUT");
        contract.addItem(restContractItem3, className + ".save");

        return contract;
    }

    public static ImageRepository getInstance(Context context){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        ImageRepository newRepo=adapter.createRepository(ImageRepository.class);

        return newRepo;
    }

    public static ImageRepository getInstance(Context context,String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        adapter.setAccessToken(accessToken);
        ImageRepository newRepo=adapter.createRepository(ImageRepository.class);

        return newRepo;
    }

    public void findImageById(int id, final ImageModelCallBack imageModelCallBack){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        invokeStaticMethod("findById", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .create();
                ImageModel imageModel = gson.fromJson(response.toString(),ImageModel.class);
                imageModelCallBack.onSuccess(tag,imageModel);
            }

            @Override
            public void onError(Throwable t) {
                imageModelCallBack.onError(t);
            }
        });
    }

    public interface ImageModelCallBack  {
        void onSuccess(Object tag, ImageModel object);
        void onError(Throwable t);
    }
}
