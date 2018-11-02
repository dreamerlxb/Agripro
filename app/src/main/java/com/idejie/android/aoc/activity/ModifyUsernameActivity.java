package com.idejie.android.aoc.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.UserApplication;
import com.idejie.android.aoc.model.UserModel;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ModifyUsernameActivity extends AppCompatActivity {

    private UserApplication userApplication;

    private EditText newUsername;

    private ImageView backImg;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);

        userApplication = (UserApplication) getApplication();

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyUsernameActivity.this.finish();
            }
        });

        newUsername = (EditText) findViewById(R.id.user_name_txt);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyName();
            }
        });
    }

    private void modifyName() {
        if (TextUtils.isEmpty(newUsername.getText())) {
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        UserModelRepository userModelRepository = UserModelRepository.getInstance(this,userApplication.getAccessToken());
        UserModel userModel = userApplication.getUser();
        userModel.setName(newUsername.getText().toString());

        Map<String,Object> params = new HashMap<>();
        params.put("id",userModel.getUserId());
        params.put("name",newUsername.getText().toString());

        showLoadingDialog();
        userModelRepository.invokeStaticMethod("upsert", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadingDialog.dismiss();
                Log.i("response",response.toString());
                Toast.makeText(ModifyUsernameActivity.this,"昵称修改成功",Toast.LENGTH_SHORT).show();
                ModifyUsernameActivity.this.finish();
            }

            @Override
            public void onError(Throwable t) {
                loadingDialog.dismiss();
                Log.i("response",t.toString());
            }
        });
    }

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
}

