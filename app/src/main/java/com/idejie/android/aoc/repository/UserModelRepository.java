package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.FavourModel;
import com.idejie.android.aoc.model.LikeModel;
import com.idejie.android.aoc.model.UserModel;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.User;
import com.strongloop.android.loopback.callbacks.JsonObjectParser;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shandongdaxue on 16/8/28.
 */

public class UserModelRepository extends com.strongloop.android.loopback.UserRepository<UserModel> {

    public UserModelRepository() {
        super("user",UserModel.class);
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem2 = new RestContractItem("/users/:id/avatar", "GET");
        contract.addItem(restContractItem2, className + ".usersAvatar");

        RestContractItem restContractItem3 = new RestContractItem("/users/:id/likeNews/:likeNewsId", "GET");
        contract.addItem(restContractItem3, className + ".likeNews");

        //favorNews
        RestContractItem restContractItem4 = new RestContractItem("/users/:id/favorNews/:favorNewsId", "GET");
        contract.addItem(restContractItem4, className + ".favorNews");

        RestContractItem restContractItem5 = new RestContractItem("/users", "PUT");
        contract.addItem(restContractItem5, className + ".upsert");

        RestContractItem restContractItem6 = new RestContractItem("/users/sendSMS", "POST");
        contract.addItem(restContractItem6, className + ".sendSMS");

        RestContractItem restContractItem7 = new RestContractItem("/users/createUser", "POST");
        contract.addItem(restContractItem7, className + ".createUser");
        // resetPwdBySMS

        RestContractItem restContractItem8 = new RestContractItem("/users/resetPwdBySMS", "POST");
        contract.addItem(restContractItem8, className + ".resetPwdBySMS");

        //resetPwd
        RestContractItem restContractItem9 = new RestContractItem("/users/resetPwd", "POST");
        contract.addItem(restContractItem9, className + ".resetPwd");

        //uploadAvatar
        RestContractItem restContractItem10 = new RestContractItem("/users/uploadAvatar", "POST");
        contract.addItem(restContractItem10, className + ".uploadAvatar");
        return contract;
    }


    public static UserModelRepository getInstance(Context context,String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
//        adapter
        if (!TextUtils.isEmpty(accessToken)) {
            adapter.setAccessToken(accessToken);
        }
        UserModelRepository newRepo=adapter.createRepository(UserModelRepository.class);

        return newRepo;
    }

    public void findUserById(int userId){

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put("include","avatar");

        params.put("id", userId);
        params.put("filter",filter);


        invokeStaticMethod("findById", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("findUserById",response.toString());
            }

            @Override
            public void onError(Throwable t) {
                Log.i("findUserById",t.toString());
            }
        });
    }
//用户是否点赞了该条新闻
    public void likeNews(int userId, int likeNewsId, final ObjectCallback<LikeModel> likeModelObjectCallback){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id",userId);
        params.put("likeNewsId",likeNewsId);

        this.invokeStaticMethod("likeNews", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("likeNews",response.toString());
                likeModelObjectCallback.onSuccess(null);
            }

            @Override
            public void onError(Throwable t) {
                Log.i("likeNews",t.toString());
                likeModelObjectCallback.onError(t);
            }
        });
    }
    //用户是否收藏了该条新闻
    public void favorNews(int userId, int likeNewsId, final ObjectCallback<FavourModel> favourModelObjectCallback){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id",userId);
        params.put("favorNewsId",likeNewsId);

        this.invokeStaticMethod("favorNews", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("favorNews",response.toString());
                favourModelObjectCallback.onSuccess(null);
            }

            @Override
            public void onError(Throwable t) {
                Log.i("favorNews",t.toString());
                favourModelObjectCallback.onError(t);
            }
        });
    }
}