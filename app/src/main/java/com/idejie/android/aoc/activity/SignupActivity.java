package com.idejie.android.aoc.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.idejie.android.aoc.utils.LoadingDialog;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;


public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private static final int REQUEST_READ_CONTACTS = 0;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView,mPasswordRepeat;
    private EditText editCode;
    private Button codeBtn;

    Dialog loadingDialog;
    TimerTask timerTask;
    Timer timer;
    Handler timerHandler;
    int length = 60 * 1000;

    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        codeBtn= (Button) findViewById(R.id.button);
        codeBtn.setOnClickListener(this);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.signupPhone);
        populateAutoComplete();
        editCode= (EditText) findViewById(R.id.code);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordRepeat = (EditText) findViewById(R.id.password2);
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        timerHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                length -= 1000;
                codeBtn.setText(length / 1000 + "秒");
                if (length < 0) {
                    codeBtn.setEnabled(true);
                    codeBtn.setText("获取验证码");
                    clearTimer();
                    length = 60 * 1000;
                }
            };
        };
    }

    /**
     * 初始化时间
     */
    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timerHandler.sendEmptyMessage(1);
            }
        };
    }

    /***
     * 开始倒计时
     */
    private void startTimer() {
        //发送验证码倒计时
        initTimer();
        codeBtn.setText(60 + "秒");
        codeBtn.setEnabled(false);
        timer.schedule(timerTask, 0, 1000);
    }
    /**
     * 清除倒计时
     */
    private void clearTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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

    /**
     * Callback received when a permissions request has been completed.
     */
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
        String confirmPwd = mPasswordRepeat.getText().toString();
        String code = editCode.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailView.requestFocus();
            Toast.makeText(SignupActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return;
        } else if(!email.matches("^[1]+[3,4,5,7,8]+\\d{9}$")) {
            mEmailView.requestFocus();
            Toast.makeText(SignupActivity.this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.requestFocus();
            Toast.makeText(SignupActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        } else if(password.length() < 4) {
            mPasswordView.requestFocus();
            Toast.makeText(SignupActivity.this,"密码太短",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPwd)){
            mPasswordRepeat.requestFocus();
            Toast.makeText(SignupActivity.this,"您两次输入的密码不一致，请重新输入密码",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(code)) {
            editCode.requestFocus();
            Toast.makeText(SignupActivity.this,"请填写验证码",Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        // 在这里写
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("zoneCode",  "86");
        params.put("password",  password);
        params.put("mobileNumber", email);
        params.put("code",code);

        UserModelRepository userModelRepository = UserModelRepository.getInstance(this,null);
        userModelRepository.invokeStaticMethod("createUser", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadingDialog.dismiss();
                if(response.optInt("statusCode") == 401) {
                    Toast.makeText(SignupActivity.this,"手机号已存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SignupActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                finish();
            } // 您的密码填写有误，请重新填写

            @Override
            public void onError(Throwable t) {
                Log.i("Register",t.toString());
                loadingDialog.dismiss();
                Toast.makeText(SignupActivity.this,"注册失败，请重试",Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                String phone = mEmailView.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignupActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                    mEmailView.requestFocus();
                    return;
                } else if(!phone.matches("^[1]+[3,4,5,7,8]+\\d{9}$")) {
                    Toast.makeText(SignupActivity.this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
                    mEmailView.requestFocus();
                    return;
                }

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("zoneCode",  "86");
                params.put("mobileNumber", phone);

                UserModelRepository userModelRepository = UserModelRepository.getInstance(this,null);
                userModelRepository.invokeStaticMethod("sendSMS", params, new Adapter.JsonObjectCallback() {
                    @Override
                    public void onSuccess(JSONObject response) { // 401手机号已存在 200发送成功 400频率过快
                        Log.i("SendSMS success", response.toString());
                        int statusCode = response.optInt("statusCode");
                        if(statusCode == 200) {
                            Toast.makeText(SignupActivity.this,"已发送",Toast.LENGTH_SHORT).show();
                        } else if(statusCode == 400) {
                            Toast.makeText(SignupActivity.this,"频率过快，请稍后重试",Toast.LENGTH_SHORT).show();
                        }
                    } // 您的密码填写有误，请重新填写

                    @Override
                    public void onError(Throwable t) {
                        Log.i("SendSMS error",t.toString());
                        Toast.makeText(SignupActivity.this,"请求失败，请检查网络后，重试",Toast.LENGTH_SHORT).show();
                    }
                });

                startTimer();

                break;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        clearTimer();
        super.onDetachedFromWindow();
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

