package com.idejie.android.aoc.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.fragment.PriceFragment;
import com.idejie.android.aoc.fragment.SelectCityFragment;

public class SelectActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        int type = getIntent().getIntExtra(PriceFragment.SELECT_TYPE, 0);

        if(type == 0) {
            SelectCityFragment selectCityFragment = SelectCityFragment.newInstance("", "");
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.activity_select, selectCityFragment).commit();
        }
    }
}
