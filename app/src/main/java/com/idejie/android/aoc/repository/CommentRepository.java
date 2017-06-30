package com.idejie.android.aoc.repository;

import android.content.Context;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.CommentModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;

/**
 * Created by sxb on 16/10/13.
 */

public class CommentRepository extends ModelRepository<CommentModel> {

    public CommentRepository() {
        super("comment", CommentModel.class);
    }

    public static CommentRepository getInstance(Context context){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        CommentRepository newRepo=adapter.createRepository(CommentRepository.class);

        return newRepo;
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem2 = new RestContractItem("/comments", "POST");
        contract.addItem(restContractItem2, className + ".create");
        RestContractItem restContractItem3 = new RestContractItem("/comments/:id", "DELETE");
        contract.addItem(restContractItem3, className + ".deleteById");

        return contract;
    }


    public static CommentRepository getInstance(Context context,String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        adapter.setAccessToken(accessToken);
        CommentRepository newRepo=adapter.createRepository(CommentRepository.class);

        return newRepo;
    }
}
