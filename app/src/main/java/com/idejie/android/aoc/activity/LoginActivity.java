package com.idejie.android.aoc.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.application.UserApplication;
import com.idejie.android.aoc.model.ImageModel;
import com.idejie.android.aoc.model.UserModel;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.idejie.android.aoc.utils.LoadingDialog;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,View.OnClickListener {

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_CODE = 3003;
    public static final int RESULT_CODE = 3004;

    private AutoCompleteTextView mEmailView;
    private UserApplication userApplication;
    private EditText mPasswordView;

    private TextView tvSignup,tvBackup;
    private String accessToken;
    private int userId;

    Dialog loadingDialog;

    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userApplication= (UserApplication) getApplication();
        mEmailView = (AutoCompleteTextView) findViewById(R.id.phone);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        tvSignup = (TextView) findViewById(R.id.tv_signup);
        tvSignup.setOnClickListener(this);
        tvBackup= (TextView) findViewById(R.id.tv_backup);
        tvBackup.setOnClickListener(this);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailView.requestFocus();
            Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return;
        } else if(!email.matches("^[1]+[3,4,5,7,8]+\\d{9}$")) {
            mEmailView.requestFocus();
            Toast.makeText(this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            mPasswordView.requestFocus();
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        } else if(password.length() < 4) {
            mPasswordView.requestFocus();
            Toast.makeText(this,"密码太短",Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username",  "86-"+email);
        params.put("password",  password);

        params.put("include","avatar");

        UserModelRepository userModelRepository = UserModelRepository.getInstance(this,null);
        userModelRepository.invokeStaticMethod("login", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadingDialog.dismiss();
                userId = response.optInt("userId");
                accessToken = response.optString("id");
                JSONObject user = response.optJSONObject("user");
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                UserModel userModel = gson.fromJson(user.toString(),UserModel.class);
                userModel.setUserId(userId);

                userApplication.setUser(userModel);
                userApplication.setAccessToken(accessToken);

                Log.d("Login Res",response.toString());

                //新建一个线程保存数据

                Intent intent = new Intent();
                intent.putExtra("OK", true);
                LoginActivity.this.setResult(RESULT_CODE, intent);
                finish();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginUser", MODE_PRIVATE);
                        //getBoolean(String key, boolean defValue) 获取键isFirst的值，若无此键则获取默认值（第一次打开程序的时候没有isFirst这个键
                        UserApplication ua = (UserApplication) getApplication();
                        int userId = sharedPreferences.getInt("userId",-1);
                        if (userId == ua.getUser().getUserId()) {
                            return;
                        } else { //重新保存数据
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("userId",ua.getUser().getUserId());
                            editor.putString("accessToken",ua.getAccessToken());
                            editor.putString("userName",ua.getUser().getName());
                            editor.commit();
                        }
                    }
                }).start();
            } // 您的密码填写有误，请重新填写

            @Override
            public void onError(Throwable t) {
                Log.d("Login Error",t.toString());
                loadingDialog.dismiss();
                Toast.makeText(LoginActivity.this, "手机号或密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private Runnable saveUserRunnable = new Runnable() {
//        @Override
//        public void run() {
//            SharedPreferences sharedPreferences = getSharedPreferences("LoginUser", MODE_PRIVATE);
//            //getBoolean(String key, boolean defValue) 获取键isFirst的值，若无此键则获取默认值（第一次打开程序的时候没有isFirst这个键
//            int userId = sharedPreferences.getInt("userId",-1);
//            if (userId == userApplication.getUser().getUserId()) {
//                return;
//            } else { //重新保存数据
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt("userId",userApplication.getUser().getUserId());
//                editor.putString("accessToken",userApplication.getAccessToken());
//                editor.putString("userName",userApplication.getUser().getName());
//                editor.commit();
//            }
//        }
//    };


    /**
     * Shows the progress UI and hides the login form.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.createLoadingDialog(this, "");
        }
        loadingDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.tv_signup:
              Intent intent=new Intent(LoginActivity.this, SignupActivity.class);
              startActivity(intent);
              break;
          case R.id.tv_backup:
              startActivity(new Intent(LoginActivity.this,BackUpActivity.class));
              break;
      }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

