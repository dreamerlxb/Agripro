package com.idejie.android.aoc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.tools.LocalDisplay;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvinceTDialog extends Dialog {
    View localView;
    private Handler han;
    private TextView provinceTxt;
    private TagFlowLayout cityFlowLayout;

    private ArrayMap<String, List<RegionModel>> chinaMap;
    private List<RegionModel> cities;

    public ProvinceTDialog(Context context, Handler han, ArrayMap<String, List<RegionModel>> chinaMap) {
        super(context);
        this.han = han;
        this.chinaMap = chinaMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = LocalDisplay.getScreenWidthPixels(getContext());
        Window window = getWindow();//这部分是设置dialog宽高的，宽高是我从sharedpreferences里面获取到的。之前程序启动的时候有获取
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        window.setAttributes(lp);

        localView = getLayoutInflater().inflate(R.layout.dialog_province, null);
        cityFlowLayout = (TagFlowLayout) localView.findViewById(R.id.city_flow_layout);
        provinceTxt = (TextView) localView.findViewById(R.id.province_txt);
        provinceTxt.setVisibility(View.GONE);
        localView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_to_top));

        setContentView(localView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().getAttributes().gravity = Gravity.CENTER;

        initData();
    }

    private void initData() {
        final List<String> provinces = new ArrayList<>(chinaMap.keySet());
        cityFlowLayout.setAdapter(new TagAdapter<String>(provinces) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.single_txt_item, parent, false);
                tv.setText(chinaMap.keyAt(position));
                return tv;
            }
        });
        cityFlowLayout.setTag(0);
        cityFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                int tag = (int) parent.getTag();
                if (tag == 0) { //省 直辖市 港澳台
                    cities = chinaMap.valueAt(position);
                    String province = chinaMap.keyAt(position);
                    Log.i("Province", province);
                    if (cities.size() == 1) {
                        Message mess = new Message();
                        mess.obj = cities.get(0);
                        han.sendMessage(mess);
                        ProvinceTDialog.this.dismiss();
                    } else { // 市区
                        provinceTxt.setVisibility(View.VISIBLE);
                        provinceTxt.setText(province);
                        cityFlowLayout.setTag(1);
                        cityFlowLayout.setAdapter(new TagAdapter<RegionModel>(cities) {
                            @Override
                            public View getView(FlowLayout parent, int position, RegionModel o) {
                                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.single_txt_item, parent, false);
                                tv.setText(o.getCity());
                                return tv;
                            }
                        });
                    }
                } else {
                    Message mess = new Message();
                    mess.obj = cities.get(position);
                    han.sendMessage(mess);
                    ProvinceTDialog.this.dismiss();
                }
                return true;
            }
        });
    }
}