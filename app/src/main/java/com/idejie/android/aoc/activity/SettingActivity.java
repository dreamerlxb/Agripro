package com.idejie.android.aoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.UserApplication;
import com.idejie.android.aoc.model.UserModel;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.idejie.android.aoc.utils.DataCleanManager;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MODIFY_NAME = "modify_name";
    public static final int RESULT_CODE = 3002;
    private TextView modifyPwdTxt;
    private TextView modifyNameTv;
    private TextView cacheTxt;
    private View clearCacheView;
    private ImageView backImg;
    private UserApplication userApplication;
    private boolean isModifyName;

    private Dialog loadingDialog;
    private Dialog modifyNameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        userApplication = (UserApplication) getApplication();

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.putExtra(MODIFY_NAME, isModifyName);
                SettingActivity.this.setResult(RESULT_CODE, in);
                SettingActivity.this.finish();
            }
        });

        modifyPwdTxt = (TextView) findViewById(R.id.modify_pwd_txt);
        modifyPwdTxt.setOnClickListener(this);

        modifyNameTv = (TextView) findViewById(R.id.modify_name_txt);
        modifyNameTv.setOnClickListener(this);

        cacheTxt = (TextView) findViewById(R.id.clear_cache_txt);
        clearCacheView = findViewById(R.id.clear_cache);
        clearCacheView.setOnClickListener(this);

        try {
            cacheTxt.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            Log.i("清除缓存", e.toString());
            e.printStackTrace();
        }
    }

    private Runnable clearCache = new Runnable() {
        @Override
        public void run() {
            DataCleanManager.clearAllCache(SettingActivity.this);
            try {
                String size = DataCleanManager.getTotalCacheSize(SettingActivity.this);
                Message msg = new Message();
                msg.obj = size;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            cacheTxt.setText(msg.obj.toString());
            Toast.makeText(SettingActivity.this, "缓存清除完成", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_pwd_txt:
                startActivity(new Intent(this, ModifyPwdActivity.class));
                break;
            case R.id.modify_name_txt:
                modifyUserName();
                break;
            case R.id.clear_cache:
                Log.i("清除缓存", "========");
                dialog();
                break;
        }
    }

    private void modifyUserName() {
        if (modifyNameDialog != null) {
            modifyNameDialog.show();
            return;
        }
        modifyNameDialog = new Dialog(this, R.style.comment_dialog);
        Window window = modifyNameDialog.getWindow();//这部分是设置dialog宽高的，宽高是我从sharedpreferences里面获取到的。之前程序启动的时候有获取
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setContentView(R.layout.modify_name_dialog);

        final EditText contentTxt = (EditText) modifyNameDialog.findViewById(R.id.user_name_txt);
        final Button sentBtn = (Button) modifyNameDialog.findViewById(R.id.email_sign_in_button);
        sentBtn.setBackgroundResource(R.color.gray_light);
        sentBtn.setClickable(false);
        contentTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    sentBtn.setBackgroundResource(R.color.gray_light);
                    sentBtn.setClickable(false);
                } else {
                    sentBtn.setBackgroundResource(R.color.colorPrimary);
                    sentBtn.setClickable(true);
                }
            }
        });

        sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyNameDialog.dismiss();

                UserModelRepository userModelRepository = UserModelRepository.getInstance(SettingActivity.this, userApplication.getAccessToken());
                UserModel userModel = userApplication.getUser();
                userModel.setName(contentTxt.getText().toString());

                Map<String,Object> params = new HashMap<>();
                params.put("id",userModel.getUserId());
                params.put("name",contentTxt.getText().toString());

                showLoadingDialog();
                userModelRepository.invokeStaticMethod("upsert", params, new Adapter.JsonObjectCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        loadingDialog.dismiss();
                        isModifyName = true;
                        Log.i("response",response.toString());
                        Toast.makeText(SettingActivity.this,"昵称修改成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable t) {
                        loadingDialog.dismiss();
                        Log.i("response",t.toString());
                        Toast.makeText(SettingActivity.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        modifyNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        contentTxt.setFocusable(true);
                        contentTxt.setFocusableInTouchMode(true);
                        contentTxt.requestFocus();
                        InputMethodManager inputManager =
                                (InputMethodManager)contentTxt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(contentTxt, 0);
                    }
                }, 100);
            }
        });
        modifyNameDialog.show();
    }


    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确任清除缓存吗？");
        builder.setTitle("提示");

//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                new Thread(clearCache).start();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        AlertDialog alertDialog = builder.create();

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(clearCache).start();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        Button btn2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            View v2 = getLayoutInflater().inflate(R.layout.loading_dialog2, null);// 得到加载view
            LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.dialog_view2);

            loadingDialog = new Dialog(this, R.style.loading_dialog);
            loadingDialog.setContentView(layout2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        loadingDialog.show();
    }
}
