package com.idejie.android.aoc.repository;

import com.idejie.android.aoc.model.UserModel;
import com.strongloop.android.loopback.User;

/**
 * Created by shandongdaxue on 16/8/28.
 */

public class UserModelRepository extends com.strongloop.android.loopback.UserRepository<UserModel> {

    public UserModelRepository() {
        super("user",UserModel.class);
    }

}