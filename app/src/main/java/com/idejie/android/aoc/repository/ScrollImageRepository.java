package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.ScrollImageModel;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by slf on 16/8/30.
 */

public class ScrollImageRepository extends ModelRepository<ScrollImageModel> {

    public ScrollImageRepository() {
        super("scrollImage", ScrollImageModel.class);
    }


}