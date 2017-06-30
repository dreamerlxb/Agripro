package com.idejie.android.aoc.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.RegionModel;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;


public class SelectCityFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TagFlowLayout cityFlowLayout;
    private ArrayMap<String, List<RegionModel>> chinaMap;
    private List<RegionModel> cities;
    private ImageView backImg;

    private String mParam1;
    private String mParam2;


    public SelectCityFragment() {

    }

    public static SelectCityFragment newInstance(String param1, String param2) {
        SelectCityFragment fragment = new SelectCityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_city, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backImg = (ImageView) view.findViewById(R.id.back_img);
        backImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                getActivity().finish();
                break;
        }
    }
}
