package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.Model.PriceModel;
import com.strongloop.android.loopback.ModelRepository;


/**
 * Created by slf on 16/8/28.
 */

public class PriceRepository extends ModelRepository<PriceModel> {

    public PriceRepository() {
        super("price", PriceModel.class);
    }
}
