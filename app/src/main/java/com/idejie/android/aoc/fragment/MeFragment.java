package com.idejie.android.aoc.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.MyFollowActivity;
import com.idejie.android.aoc.activity.LoginActivity;
import com.idejie.android.aoc.activity.MallActivity;
import com.idejie.android.aoc.activity.MyCommentActivity;
import com.idejie.android.aoc.activity.MyPriceActivity;
import com.idejie.android.aoc.activity.SettingActivity;
import com.idejie.android.aoc.activity.TuiActivity;
import com.idejie.android.aoc.application.UserApplication;
import com.idejie.android.aoc.dialog.UploadDialog;
import com.idejie.android.aoc.fragment.utils.CircleImageView;
import com.idejie.android.aoc.model.ImageModel;
import com.idejie.android.aoc.repository.ImageRepository;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.idejie.android.aoc.utils.LoadingDialog;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.strongloop.android.remoting.adapters.Adapter;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shandongdaxue on 16/8/11.
 */
public class MeFragment extends LazyFragment implements View.OnClickListener{
    private UserApplication userApplication;
    private View view;
    private TextView textName,textScore;
    private Button exitBtn;
    private TextView loginTxt;

    Dialog loadingDialog;
    UploadDialog uploadDialog;
    private CircleImageView circleImageView;
    String avatarPath;
    long avatarLength;
    String avatarName;
    String avatarType;
    boolean isLoadAvatar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_me, container, false);
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userApplication= (UserApplication) getActivity().getApplication();
    }

    private void initViews() {
        loginTxt = (TextView) view.findViewById(R.id.tv_login);
        loginTxt.setOnClickListener(this);
        textName= (TextView) view.findViewById(R.id.tv_nickName);
        textScore= (TextView) view.findViewById(R.id.tv_score);

        view.findViewById(R.id.tv_guanzhu).setOnClickListener(this);
        view.findViewById(R.id.tv_set).setOnClickListener(this);
        view.findViewById(R.id.tv_comment).setOnClickListener(this);
        view.findViewById(R.id.tv_price).setOnClickListener(this);
        view.findViewById(R.id.tv_mall).setOnClickListener(this);
        view.findViewById(R.id.tv_app).setOnClickListener(this);

        circleImageView = (CircleImageView) view.findViewById(R.id.roundImageView);
        circleImageView.setOnClickListener(this);
        exitBtn = (Button) view.findViewById(R.id.exitButton);
        exitBtn.setOnClickListener(this);
    }
    private void loadData(){
//        Log.i("=======", userApplication.getUser().getAvatar().toString());
        if (userApplication.getUser() == null) {
            exitBtn.setVisibility(View.GONE);
            loginTxt.setVisibility(View.VISIBLE);

            textName.setText(String.format("昵称: %s", "未登录"));
            textScore.setText(String.format("积分: %d", 0));

            circleImageView.setImageResource(R.mipmap.face);
        } else {
            exitBtn.setVisibility(View.VISIBLE);
            loginTxt.setVisibility(View.GONE);

            textName.setText(String.format("昵称: %s", userApplication.getUser().getName()));
            textScore.setText(String.format("积分: %d", userApplication.getUser().getScore()));

            if (userApplication.getUser().getAvatar() != null) {
                Log.i("2=========", "here");
                Glide
                    .with(this)
                    .load(userApplication.getUser().getAvatar().getUrl())
                    .centerCrop()
                    .crossFade()
                    .into(circleImageView);
            } else {
                Log.i("3=========", "here");
                if (!isLoadAvatar) {
                    Log.i("4=========", "here");
                    fetchUserAvatar();
                }
            }
        }
    }

    private void fetchUserAvatar() {
        UserModelRepository userModelRepository = UserModelRepository.getInstance(this.getContext(), userApplication.getAccessToken());
        Map<String, Object> params = new HashMap<>();
        params.put("id", (int)userApplication.getUser().getId());
        userModelRepository.invokeStaticMethod("usersAvatar", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                isLoadAvatar = true;
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                ImageModel ima = gson.fromJson(response.toString(), ImageModel.class);
                userApplication.getUser().setAvatar(ima);
                Log.i("5==========", ima.getUrl());
                Glide.with(getContext())
                    .load(ima.getUrl())
                    .centerCrop()
                    .crossFade()
                    .into(circleImageView);
            }

            @Override
            public void onError(Throwable t) {
                Log.i("fetchUserAvatar error", t.toString());
            }
        });
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        Log.d("MeFragment", "Fragment 显示 " + this);
        loadData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_login) {
            this.startActivityForResult(new Intent(getContext(), LoginActivity.class),1000);
            return;
        }

        if (userApplication.getUser() == null) {
            Toast.makeText(this.getContext(),"未登录，请先登录",Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()){
            case R.id.tv_guanzhu:
                startActivity(new Intent(getContext(), MyFollowActivity.class));
                break;
            case R.id.tv_price:
                startActivity(new Intent(getContext(), MyPriceActivity.class));
                break;
            case R.id.tv_comment:
                startActivity(new Intent(getContext(), MyCommentActivity.class));
                break;
            case R.id.tv_mall:
                startActivity(new Intent(getContext(), MallActivity.class));
                break;
            case R.id.tv_app:
                startActivity(new Intent(getContext(), TuiActivity.class));
                break;
            case R.id.tv_set:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.exitButton:
                dialog();
                break;
            case R.id.roundImageView:
                GalleryConfig config = new GalleryConfig.Build()
                        .limitPickPhoto(1)
                        .singlePhoto(true)
                        .hintOfPick("this is pick hint")
                        .filterMimeTypes(new String[]{})
                        .build();
                Intent intent = new Intent(getContext(), GalleryActivity.class);
                intent.putExtra("GALLERY_CONFIG", config);
                this.startActivityForResult(intent, 1001);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("=====", requestCode + "  " + resultCode);
        if (requestCode == 1000 && resultCode == 2000) {
            //重新加载数据
            loadData();
        } else if(requestCode == 1001) {
            if (data !=null && data.getSerializableExtra(GalleryActivity.PHOTOS) != null) {
                List<String> photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
                avatarPath = photos.get(0);
                Log.i("Photo URL", avatarPath);
                showUploadProgress(true);
                try {
                    fetchQiniuToken(); // 获取 七牛token
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMsgToServer(String key) {
        showProgress(true);
//        UserModelRepository userRepo = UserModelRepository.getInstance(getContext(), userApplication.getAccessToken());
//        Map<String,Object> params =new HashMap<>();
//        params.put("userId", userApplication.getUser().getId());
//        params.put("imgKey", key);
//        params.put("imgName", avatarName);
//        params.put("imgType", avatarType);

        UserModelRepository userRepo = UserModelRepository.getInstance(getContext(), userApplication.getAccessToken());
        Map<String,Object> params =new HashMap<>();
        params.put("userId", userApplication.getUser().getId());
        params.put("imgKey", key);
        params.put("imgName", avatarName);
        params.put("imgType", avatarType);
        userRepo.invokeStaticMethod("uploadAvatar", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showProgress(false);
                Log.i("Avatar create", response.toString());
                JSONObject image = response.optJSONObject("image");
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                ImageModel ima = gson.fromJson(image.toString(), ImageModel.class);
                userApplication.getUser().setAvatar(ima);
                Glide
                    .with(getContext())
                    .load(ima.getUrl())
                    .centerCrop()
                    .crossFade()
                    .into(circleImageView);
            }

            @Override
            public void onError(Throwable t) {
                showProgress(false);
                Toast.makeText(getContext(), "图片上传失败，请重试", Toast.LENGTH_SHORT).show();
                Log.i("Avatar create", t.toString());
            }
        });
    }

    private void uploadAvatar(String token) throws FileNotFoundException {
        File file = new File(avatarPath);
        avatarLength = file.length();
        avatarName = file.getName();
        if (avatarPath.endsWith(".png")) {
            avatarType = "image/png";
        } else if(avatarPath.endsWith(".jpeg") || avatarPath.endsWith(".jpg")) {
            avatarType = "image/jpeg";
        }
        Log.i("Avatar", avatarName);
        Log.i("Avatar", avatarLength + "");
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, null, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                showUploadProgress(false);
                if(info.isOK()) {
                    Log.i("qiniu", "Upload Success");
                    sendMsgToServer(response.optString("key"));
                } else {
                    Toast.makeText(getContext(), "图片上传失败，请重试", Toast.LENGTH_SHORT).show();
                    Log.i("qiniu", "Upload Fail");
                }
                Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                uploadDialog.setPercentTxt(percent);
                Log.i("key", key + "");
                Log.i("percent", percent + "");
            }
        }, null));
    }

    private void fetchQiniuToken() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(getString(R.string.qiniu_token)).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("fetchQiniuToken e", e.toString());
                Toast.makeText(MeFragment.this.getContext(), "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String r = response.body().string();
                    Log.i("fetchQiniuToken", r);
                    try {
                        JSONObject jsonObject = new JSONObject(r);
                        String qiniuToken = jsonObject.optString("uptoken");
                        Log.i("fetchQiniuToken", qiniuToken);
                        uploadAvatar(qiniuToken);
                    } catch (JSONException e) {
                        showUploadProgress(false);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 确认退出吗
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("确定退出吗？");
        builder.setTitle("提示");

        AlertDialog alertDialog = builder.create();

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exit();
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
        btn.setTextColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        Button btn2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
    }

    private void exit() {
        showProgress(true);
        UserModelRepository userModelRepository = UserModelRepository.getInstance(getContext(),userApplication.getAccessToken());
        userModelRepository.invokeStaticMethod("logout", null, new Adapter.Callback() {
            @Override
            public void onSuccess(String response) {
                showProgress(false);
                userApplication.setUser(null);
                userApplication.setAccessToken(null);
                loadData();
                //新建一个线程保存数据
                new Thread(saveUserRunnable).start();
            }

            @Override
            public void onError(Throwable t) {
                showProgress(false);
                Log.i("Logout",t.toString());
            }
        });
    }

    private void showProgress(boolean show) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.createLoadingDialog(this.getContext(), "");
        }
        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    private void showUploadProgress(boolean show) {
        if (uploadDialog == null) {
            uploadDialog = new UploadDialog(this.getContext());
        }
        if (show) {
            uploadDialog.show();
        } else {
            uploadDialog.dismiss();
        }
    }

    //重新保存数据
    private Runnable saveUserRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginUser", MODE_PRIVATE);
            //重新保存数据
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userId",-1);
            editor.putString("accessToken","");
            editor.putString("userName","");
            editor.commit();
        }
    };
}
