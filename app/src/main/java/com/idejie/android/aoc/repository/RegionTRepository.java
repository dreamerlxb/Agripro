package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.Region;
import com.idejie.android.aoc.model.RegionModel;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by slf on 16/8/30.
 */

public class RegionTRepository extends ModelRepository<Region> {

    public RegionTRepository() {
        super("region", Region.class);
    }


}