package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.model.SortModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.callbacks.ListCallback;

/**
 * Created by slf on 16/8/30.
 */

public class SortRepository extends ModelRepository<SortModel> {

    public SortRepository() {
        super("sort", SortModel.class);
    }


}