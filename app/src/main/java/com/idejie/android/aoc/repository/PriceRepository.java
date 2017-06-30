package com.idejie.android.aoc.repository;

import android.content.Context;
import android.text.TextUtils;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.PriceModel;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.adapters.RestContract;
import com.strongloop.android.remoting.adapters.RestContractItem;


/**
 * Created by slf on 16/8/28.
 */

public class PriceRepository extends ModelRepository<PriceModel> {

    public PriceRepository() {
        super("price", PriceModel.class);
    }

    public static PriceRepository getInstance(Context context, String accessToken){
        RestAdapter adapter = new RestAdapter(context, context.getString(R.string.mh_base_url));
        if (!TextUtils.isEmpty(accessToken)){
            adapter.setAccessToken(accessToken);
        }
        PriceRepository newRepo=adapter.createRepository(PriceRepository.class);

        return newRepo;
    }

    @Override
    public RestContract createContract() {
        RestContract contract = super.createContract();
        String className = getClassName();

        RestContractItem restContractItem2 = new RestContractItem("/prices", "POST");
        contract.addItem(restContractItem2, className + ".create");

        RestContractItem restContractItem4 = new RestContractItem("/prices", "GET");
        contract.addItem(restContractItem4, className + ".all");

        RestContractItem restContractItem3 = new RestContractItem("/prices/:id", "DELETE");
        contract.addItem(restContractItem3, className + ".deleteById");

        return contract;
    }
}
