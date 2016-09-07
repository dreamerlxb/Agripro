package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.NewsModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.User;

/**
 * Created by shandongdaxue on 16/8/28.
 */

public class UserRepository extends com.strongloop.android.loopback.UserRepository<User> {

    public UserRepository() {
        super("user",User.class);
    }

}