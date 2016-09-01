package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.SortModel;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by slf on 16/8/30.
 */

public class GradeRepository extends ModelRepository<GradeModel> {

    public GradeRepository() {
        super("grade", GradeModel.class);
    }


}