package com.idejie.android.aoc.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.UserApplication;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class ModifyPwdActivity extends AppCompatActivity implements TextWatcher{

    private UserApplication userApplication;

    private EditText oldPwd;
    private EditText newPwd1;
    private EditText newPwd2;

    private ImageView backImg;
    private Button mEmailSignInButton;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_psword);
        userApplication = (UserApplication) getApplication();

        oldPwd = (EditText) findViewById(R.id.old_pwd);
        oldPwd.addTextChangedListener(this);
        newPwd1 = (EditText) findViewById(R.id.password_1);
        newPwd1.addTextChangedListener(this);
        newPwd2 = (EditText) findViewById(R.id.password_2);
        newPwd2.addTextChangedListener(this);

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPwdActivity.this.finish();
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setEnabled(false);
        mEmailSignInButton.setBackgroundResource(R.color.gray_light);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyPwd();
            }
        });
    }

    private void modifyPwd(){
        if(newPwd1.getText().length() < 4) {
            Toast.makeText(this,"密码太短",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(newPwd1.getText(), newPwd2.getText())) {
            Toast.makeText(this,"两次密码不同，请确认",Toast.LENGTH_SHORT).show();
            return;
        }

        UserModelRepository userModelRepository = UserModelRepository.getInstance(this, userApplication.getAccessToken());
        Map<String,Object> params = new HashMap<>();
        params.put("id", userApplication.getUser().getUserId());
        params.put("newPwd",newPwd1.getText());
        params.put("oldPwd",oldPwd.getText());
        params.put("username", userApplication.getUser().getUserName());
        showLoadingDialog();
        userModelRepository.invokeStaticMethod("resetPwd", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadingDialog.dismiss();
                int statusCode = response.optInt("statusCode");
                if (statusCode == 200) {
                    String tokenId = response.optString("tokenId");
                    userApplication.setAccessToken(tokenId);
                    //新建一个线程保存数据
                    new Thread(saveUserRunnable).start();
                    Toast.makeText(ModifyPwdActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    ModifyPwdActivity.this.finish();
                } else if(statusCode == 402) {
                    Toast.makeText(ModifyPwdActivity.this,"原密码错误",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ModifyPwdActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(ModifyPwdActivity.this,"超时，请重试！！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 新建一个线程保存信息
    private Runnable saveUserRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginUser", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("accessToken",userApplication.getAccessToken());
            editor.commit();
        }
    };

    private void showLoadingDialog(){
        if (loadingDialog == null) {
            View v2 = getLayoutInflater().inflate(R.layout.loading_dialog2, null);// 得到加载view
            LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.dialog_view2);

            loadingDialog = new Dialog(this,R.style.loading_dialog);
            loadingDialog.setContentView(layout2,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        loadingDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(oldPwd.getText())
                && !TextUtils.isEmpty(newPwd1.getText())
                && !TextUtils.isEmpty(newPwd2.getText())) {
            mEmailSignInButton.setEnabled(true);
            mEmailSignInButton.setBackgroundResource(R.color.colorPrimary);
        }
    }
}

