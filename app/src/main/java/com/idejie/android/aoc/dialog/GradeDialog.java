package com.idejie.android.aoc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.GradeRepository;
import com.idejie.android.aoc.tools.LocalDisplay;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeDialog extends Dialog {

    View localView;
    private Handler han;
    private TextView sortTxt;
    private List<GradeModel> gradeList;
    private SortModel sortModel;

    private TagFlowLayout gradeFlowLayout;

    public GradeDialog(Context context, Handler han, SortModel sortModel) {
        super(context);
        this.han = han;
        this.sortModel = sortModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = LocalDisplay.getScreenWidthPixels(getContext());
        Window window = getWindow();//这部分是设置dialog宽高的，宽高是我从sharedpreferences里面获取到的。之前程序启动的时候有获取
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

        localView = getLayoutInflater().inflate(R.layout.dialog_grade, null);
        gradeFlowLayout = (TagFlowLayout) localView.findViewById(R.id.grade_flow_layout);
        gradeFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                GradeModel gradeModel = gradeList.get(position);
                Log.d("test", "Throwable..Objs..." + gradeModel.toString());
                Message mess = new Message();
                mess.obj = gradeModel;
                han.sendMessage(mess);

                GradeDialog.this.dismiss();
                return true;
            }
        });

        localView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_to_top));

        sortTxt = (TextView) localView.findViewById(R.id.top_name);
        sortTxt.setText(sortModel.getName());

        setContentView(localView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        getRank();
    }

    private void getRank() { // 获取等级
        GradeRepository gradeRepository = GradeRepository.getInstance(getContext(), null);

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();

        where.put("sortId", sortModel.getId());
        filter.put("where", where);
        params.put("filter", filter);

        gradeRepository.invokeStaticMethod("all", params, new JsonArrayParser<GradeModel>(gradeRepository, new ListCallback<GradeModel>() {
            @Override
            public void onSuccess(List<GradeModel> objects) {
                if (objects.size() > 0) {
                    gradeList = objects;
                    gradeFlowLayout.setAdapter(new TagAdapter<GradeModel>(gradeList) {
                        @Override
                        public View getView(FlowLayout parent, int position, GradeModel o) {
                            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.single_txt_item, parent, false);
                            tv.setText(o.getName());
                            return tv;
                        }
                    });
                } else {
                    Message mess = new Message();
                    mess.obj = null;
                    han.sendMessage(mess);
                    GradeDialog.this.dismiss();
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test", "Throwable..Objs..." + t.toString());
            }
        }));
    }


//    private void initView() {
//
//        clearallpan = (RelativeLayout) findViewById(R.id.clearallpan);
//        lin= (LinearLayout) findViewById(R.id.line);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可选写的
//        lp.setMargins(10, 0, 10, 0);
//        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可选写的
//        lp1.setMargins(0,8,0,0);
//        LinearLayout linearLayout=null;
//        int j=0;
//        for (int i=0;i<objects.size();i++){
//            if (i==0){
//                linearLayout=new LinearLayout(getContext());
//                linearLayout.setMinimumHeight(40);
//                linearLayout.setLayoutParams(lp1);
//                lin.addView(linearLayout);
//            }
//            if (i>0&&objects.get(i).getSortId()==sortId){
//
//                if (j%4==0){
//                    linearLayout=new LinearLayout(context);
//                    linearLayout.setMinimumHeight(40);
//                    linearLayout.setLayoutParams(lp1);
//                    lin.addView(linearLayout);
//                }
//                final Button btn=new Button(context);
//                btn.setText(objects.get(i).getName());
//                btn.setLayoutParams(lp);
//                final int finalI = i;
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Message mess=new Message();
//                        mess.what= (int) objects.get(finalI).getId();
//                        mess.obj =btn.getText().toString();
//                        han.sendMessage(mess);
//                        dialog.dismiss();
//                    }
//                });
//                linearLayout.addView(btn);
//                j++;
//            }
//
//        }
//
//    }

}