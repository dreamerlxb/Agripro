package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.model.SortModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;

/**
 * Created by slf on 16/8/30.
 */

public class SortRepository extends ModelRepository<SortModel> {

    public SortRepository() {
        super("sort", SortModel.class);
    }

    public static SortRepository getInstance(Context context, String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        if (!TextUtils.isEmpty(accessToken)){
            adapter.setAccessToken(accessToken);
        }
        SortRepository newRepo=adapter.createRepository(SortRepository.class);

        return newRepo;
    }
}