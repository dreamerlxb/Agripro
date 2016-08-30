package com.idejie.android.aoc.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.LoginActivity;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.IndicatorViewPager;

/**
 * Created by shandongdaxue on 16/8/11.
 */
public class MeFragment extends LazyFragment implements View.OnClickListener{
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
    public static final String INTENT_INT_INDEX = "intent_int_index";
    private String tabName;
    private int index;
    private Activity activity;
    private Context context;
    private View view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_me, container,
                false);
        init();
        return view;
    }

    private void init() {
        TextView tvLogin= (TextView) view.findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);

        RelativeLayout layoutGuanzhu = (RelativeLayout) view.findViewById(R.id.layout_me_guanzhu);
        layoutGuanzhu.setOnClickListener(this);

        RelativeLayout layoutPrice = (RelativeLayout) view.findViewById(R.id.layout_me_price);
        layoutPrice.setOnClickListener(this);

        RelativeLayout layoutComment = (RelativeLayout) view.findViewById(R.id.layout_me_comment);
        layoutComment.setOnClickListener(this);

        RelativeLayout layoutMall = (RelativeLayout) view.findViewById(R.id.layout_me_mall);
        layoutMall.setOnClickListener(this);

        RelativeLayout layoutTui = (RelativeLayout) view.findViewById(R.id.layout_me_tuiguang);
        layoutTui.setOnClickListener(this);

        RelativeLayout layoutSet = (RelativeLayout) view.findViewById(R.id.layout_me_set);
        layoutSet.setOnClickListener(this);

        Button exit = (Button) view.findViewById(R.id.exitButton);
        exit.setOnClickListener(this);
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        Log.d("cccc", "Fragment所在的Activity onResume, onResumeLazy " + this);
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        Log.d("cccc", "Fragment 显示 " + this);
    }

    @Override
    protected void onFragmentStopLazy() {
        super.onFragmentStopLazy();
        Log.d("cccc", "Fragment 掩藏 " + this);
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
        Log.d("cccc", "Fragment所在的Activity onPause, onPauseLazy " + this);
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        Log.d("cccc", "Fragment View将被销毁 " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("cccc", "Fragment 所在的Activity onDestroy " + this);
    }

    @Override
    public void onClick(View view) {
//        Toast.makeText(activity,"hh"+view.toString(), Toast.LENGTH_LONG).show();
        switch (view.getId()){
            case  R.id.tv_login:
                Intent intent=new Intent(activity, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }



}
