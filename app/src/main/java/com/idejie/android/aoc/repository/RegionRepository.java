package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;

/**
 * Created by slf on 16/8/30.
 */

public class RegionRepository extends ModelRepository<RegionModel> {

    public RegionRepository() {
        super("region", RegionModel.class);
    }

    public static RegionRepository getInstance(Context context, String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        if (!TextUtils.isEmpty(accessToken)){
            adapter.setAccessToken(accessToken);
        }
        RegionRepository newRepo=adapter.createRepository(RegionRepository.class);

        return newRepo;
    }
}