package com.idejie.android.aoc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.tools.LocalDisplay;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortDialog extends Dialog {
    View localView;
    private Handler han;

    private LinearLayout lin;
    //    private List<SortModel> sortModelList;
    private TagFlowLayout sortFlowLayout;

    Map<String, List<SortModel>> sortMap = new HashMap<>();
    private List<SortModel> selectedSortList; //选择的一级sort对应的二级所有sort


//    public SortDialog(Context context, Handler han) {
//        super(context);
//        this.han=han;
//        sortModelList = new ArrayList<>();
//    }

    public SortDialog(Context context, Handler han, Map<String, List<SortModel>> sortMap) {
        super(context);
        this.han = han;
        this.sortMap = sortMap;
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

        localView = getLayoutInflater().inflate(R.layout.dialog_sort, null);
        sortFlowLayout = (TagFlowLayout) localView.findViewById(R.id.sort_flow_layout);
        localView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_to_top));
        // 这句话起全屏的作用
        setContentView(localView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getWindow().getAttributes().gravity = Gravity.CENTER;

//        getSorts();
        initData();
    }

//    private void getSorts(){
//        SortRepository sortRepository = SortRepository.getInstance(getContext(),null);
//        sortRepository.findAll(new ListCallback<SortModel>() {
//            @Override
//            public void onSuccess(List<SortModel> objects) {
//                sortModelList = objects;
//
//                for (int i = 0; i < objects.size(); i++) {
//                    SortModel sortModel = objects.get(i);
//                    if (sortMap.containsKey(sortModel.getName())){
//                        List<SortModel> sortModels = sortMap.get(sortModel.getName());
//                        sortModels.add(sortModel);
//                    } else {
//                        List<SortModel> sortModels = new ArrayList<SortModel>();
//                        sortModels.add(sortModel);
//                        sortMap.put(sortModel.getName(),sortModels);
//                    }
//                }
//
//                initData();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                Log.d("test","Throwable..Objs..."+t.toString());
//            }
//        });
//    }

    private void initData() {
        final List<String> sorts0 = new ArrayList<String>(sortMap.keySet());

        sortFlowLayout.setAdapter(new TagAdapter<String>(sorts0) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.single_txt_item, parent, false);
                tv.setText(o);
                return tv;
            }
        });
        sortFlowLayout.setTag(0);

        sortFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                int tag = (int) parent.getTag();
                if (tag == 0) {
                    selectedSortList = sortMap.get(sorts0.get(position));

                    sortFlowLayout.setAdapter(new TagAdapter<SortModel>(selectedSortList) {
                        @Override
                        public View getView(FlowLayout parent, int position, SortModel o) {
                            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.single_txt_item, parent, false);
                            tv.setText(o.getSubName());
                            return tv;
                        }
                    });
                    sortFlowLayout.setTag(1);
                } else if (tag == 1) {
                    Message mess = new Message();
                    mess.obj = selectedSortList.get(position);
                    han.sendMessage(mess);

                    SortDialog.this.dismiss();
                } else {
                    return false;
                }

                return true;
            }
        });
    }

//    private void initView() {
//        lin= (LinearLayout) findViewById(R.id.line);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可选写的
//        lp.setMargins(10, 0, 10, 0);
//        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可选写的
//        lp1.setMargins(0,8,0,0);
//        LinearLayout linearLayout=null;
//        int j=0;
//        for (int i=0;i<sortModelList.size();i++){
//            if (i==0){
//                linearLayout=new LinearLayout(getContext());
//                linearLayout.setMinimumHeight(40);
//                linearLayout.setLayoutParams(lp1);
//                lin.addView(linearLayout);
//                final Button btn=new Button(getContext());
//                btn.setText(sortModelList.get(i).getName());
//                btn.setLayoutParams(lp);
//                btn.setTag(i);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int po = (int) view.getTag();
//                        Message mess=new Message();
//                        mess.obj = sortModelList.get(po);
//                        han.sendMessage(mess);
//
//                        SortDialog.this.dismiss();
//                    }
//                });
//                linearLayout.addView(btn);
//            }
//            if (i>0 && !sortModelList.get(i).getName().equals(sortModelList.get(i-1).getName())){
//                j++;
//                if (j%4==0){
//                    linearLayout=new LinearLayout(getContext());
//                    linearLayout.setMinimumHeight(40);
//                    linearLayout.setLayoutParams(lp1);
//                    lin.addView(linearLayout);
//                }
//                Button btn=new Button(getContext());
//                btn.setTag(i);
//                btn.setText(sortModelList.get(i).getName());
//                btn.setLayoutParams(lp);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int po = (int) view.getTag();
//                        Message mess=new Message();
//                        mess.obj = sortModelList.get(po);
//                        han.sendMessage(mess);
//                        SortDialog.this.dismiss();
//                    }
//                });
//                linearLayout.addView(btn);
//            }
//        }
//    }
}