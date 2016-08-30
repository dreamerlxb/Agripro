package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.NewsModel;
import com.strongloop.android.loopback.ModelRepository;

/**
 * Created by shandongdaxue on 16/8/28.
 */

public class NewsRepository extends ModelRepository<NewsModel> {

    public NewsRepository() {
        super("news",NewsModel.class);
    }

}