package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by slf on 16/8/30.
 */

public class RegionRepository extends ModelRepository<RegionModel> {

    public RegionRepository() {
        super("region", RegionModel.class);
    }


}