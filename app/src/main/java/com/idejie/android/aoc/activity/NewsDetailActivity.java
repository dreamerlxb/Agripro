package com.idejie.android.aoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.UserApplication;
import com.idejie.android.aoc.callback.CustomCallback;
import com.idejie.android.aoc.model.FavourModel;
import com.idejie.android.aoc.model.LikeModel;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.repository.CommentRepository;
import com.idejie.android.aoc.repository.FavourRepository;
import com.idejie.android.aoc.repository.LikeRepository;
import com.idejie.android.aoc.repository.NewsRepository;
import com.idejie.android.aoc.widget.LeftDrawableButton;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

// 新闻详情
public class NewsDetailActivity extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_time;
    private WebView webView;

    private SimpleDateFormat dateFormat;

    private ImageView backImg;
    private ImageView shareImg;
    private LeftDrawableButton commentBtn;
    private ImageButton likeBtn;
    private ImageButton favoriteBtn;

    private TextView commentTxt;
    private UserApplication userApplication;

    public final static String EXTRA="EXTRA";
    private NewsModel newsModel;

    private int position; //主要记录该位置的数据是否发生变化，若发生变化，则返回时更新数据

    private LikeModel likeModel = null;
    private FavourModel favourModel = null;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        userApplication = (UserApplication) getApplication();

        Intent intent = getIntent();
        newsModel = (NewsModel) intent.getSerializableExtra("news");
        position = intent.getIntExtra("position",0); //判断是哪一个news

        tv_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_title.setText(newsModel.getTitle());

        webView = (WebView) findViewById(R.id.Content);
        webView.loadDataWithBaseURL(null,newsModel.getContent(),"text/html","UTF-8",null);
        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕，内容将自动缩放
        webSettings.setSupportZoom(true);
        webSettings.setTextZoom(350);
        webView.setWebChromeClient(new WebChromeClient());

        tv_time = (TextView) findViewById(R.id.tv_detail_time);
        tv_time.setText(dateFormat.format(newsModel.getLastUpdated()));

        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("position",position);

                intent1.putExtra("likeCount",newsModel.getLikersCount());
                intent1.putExtra("commentCount",newsModel.getCommentsCount());
                NewsDetailActivity.this.setResult(22000,intent1);

                finish();
            }
        });

        shareImg = (ImageView) findViewById(R.id.share_img);
//        shareImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showShare();
//            }
//        });

        commentTxt = (TextView) findViewById(R.id.comment_txt);
        commentTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userApplication.getUser() == null){
                    Toast.makeText(NewsDetailActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                    return;
                }
                postComment();  // 写评论
            }
        });

        commentBtn = (LeftDrawableButton) findViewById(R.id.comment_btn);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsModel.getCommentsCount() <= 0) {
                    Toast.makeText(NewsDetailActivity.this,"还没有评论",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(NewsDetailActivity.this,NewsCommentActivity.class);
                    intent.putExtra("newsId",newsModel.getNewsId());
                    startActivity(intent);
                }
            }
        });

        likeBtn = (ImageButton) findViewById(R.id.like_btn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userApplication.getUser() != null) {
                    likeAction(); // 发送请求
                } else {
                    Toast.makeText(NewsDetailActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });

        favoriteBtn = (ImageButton) findViewById(R.id.favorite_btn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userApplication.getUser() != null) {
                    favourAction(); // 发送请求
                } else {
                    Toast.makeText(NewsDetailActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (userApplication.getUser() != null) {
            like();
            favour();
        }
        if (newsModel.getCommentsCount() < 0) { //说明还未查询
            commentCount(); //评论个数
        } else { //说明已经查询
            commentBtn.setText(newsModel.getCommentsCount() == 0 ? "评论":(""+newsModel.getCommentsCount()));
        }
    }

//    private void showShare() {
//        ShareSDK.initSDK(this);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle("标题");
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("ShareSDK");
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//
//// 启动分享GUI
//        oks.show(this);
//    }

    /**
     * 发送评论
     * */
    private void postComment(){
        if (dialog != null) {
            dialog.show();
            return;
        }
        dialog = new Dialog(this, R.style.comment_dialog);
        Window window = dialog.getWindow();//这部分是设置dialog宽高的，宽高是我从sharedpreferences里面获取到的。之前程序启动的时候有获取
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setContentView(R.layout.comment_txt_view);

        final EditText contentTxt = (EditText) dialog.findViewById(R.id.comment_content);
        final Button sentBtn = (Button) dialog.findViewById(R.id.comment_send_btn);
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
                dialog.dismiss();
                Map<String,Object> params = new HashMap<String, Object>();
                params.put("content",contentTxt.getText().toString());
                params.put("newsId",newsModel.getNewsId());
                params.put("authorId",userApplication.getUser().getUserId());
                CommentRepository commentRepository = CommentRepository.getInstance(NewsDetailActivity.this,userApplication.getAccessToken());
                commentRepository.invokeStaticMethod("create", params, new Adapter.JsonObjectCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (response != null){
                            Log.i("create comment",response.toString());
                            newsModel.setCommentsCount(newsModel.getCommentsCount()+1);
                            commentBtn.setText(newsModel.getCommentsCount()+"");
                            Toast.makeText(NewsDetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i("create comment",t.toString());
                        Toast.makeText(NewsDetailActivity.this,"评论失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
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
        dialog.show();
    }

    // 点击关注
    private void likeAction(){
        likeBtn.setEnabled(false);
        LikeRepository likeRepository = LikeRepository.getInstance(this,userApplication.getAccessToken());
        if (likeModel != null){ //那么点击就是取消点赞
            likeRepository.delLike(likeModel.getLikeId(), new Adapter.JsonObjectCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    likeBtn.setEnabled(true);
                    int count = response.optInt("count");
                    if (count == 1){
                        likeModel = null;
                        newsModel.setLikersCount(newsModel.getLikersCount()-1);
                        likeBtn.setImageResource(R.drawable.ic_favorite_24dp);
                    }
                    Log.i("取消点赞",response.toString());
                }

                @Override
                public void onError(Throwable t) {
                    likeBtn.setEnabled(true);
                    Log.i("取消点赞",t.toString());
                }
            });
        } else { //否则就是点赞 //点赞就是创建一个Like对象
            likeRepository.createLike(userApplication.getUser().getUserId(), newsModel.getNewsId(), new ObjectCallback<LikeModel>() {
                @Override
                public void onSuccess(LikeModel object) {
                    likeBtn.setEnabled(true);
                    if (object != null){
                        likeModel = object;
                        newsModel.setLikersCount(newsModel.getLikersCount()+1);
                        likeBtn.setImageResource(R.drawable.ic_favorite_selected_24dp);
                    }
                    Log.i("点赞",object.toString());
                }

                @Override
                public void onError(Throwable t) {
                    likeBtn.setEnabled(true);
                    Log.i("点赞出错",t.toString());
                }
            });
        }
    }

    //点击收藏（关注）
    private void favourAction(){
        FavourRepository favourRepository = FavourRepository.getInstance(this,userApplication.getAccessToken());
        if (favourModel != null){
            favourRepository.delFavour(favourModel.getFavourId(), new Adapter.JsonObjectCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    int count = response.optInt("count");
                    if (count == 1){
                        favourModel = null;
                        newsModel.setLikersCount(newsModel.getFavoursCount()-1);
                        favoriteBtn.setImageResource(R.drawable.ic_like_24dp);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    Log.i("收藏出错1",t.toString());
                }
            });
        } else {
            favourRepository.createFavour(userApplication.getUser().getUserId(), newsModel.getNewsId(), new ObjectCallback<FavourModel>() {
                @Override
                public void onSuccess(FavourModel object) {
                    if (object != null){
                        favourModel = object;
                        newsModel.setLikersCount(newsModel.getFavoursCount()+1);
                        favoriteBtn.setImageResource(R.drawable.ic_like_selected_24dp);
                    }
                    Log.i("收藏2",object.toString());
                }

                @Override
                public void onError(Throwable t) {
                    Log.i("收藏2",t.toString());
                }
            });
        }
    }

    /*
    * 评论总个数
    * */
    private void commentCount(){
        NewsRepository newsRepository = NewsRepository.getInstance(this,userApplication.getAccessToken());
        newsRepository.newsCommentsCount(newsModel.getNewsId(), new CustomCallback(null) {
            @Override
            public void onSuccess(Object count) {
                newsModel.setCommentsCount((Integer) count);
                commentBtn.setText((int)count == 0 ? "评论":(""+count));
            }

            @Override
            public void onError(Throwable t) {
                Log.i("评论总个数",t.toString());
            }
        });
    }

    //用户是否点赞和收藏
    /** 改变Drawable的颜色
        Drawable leftDrawable =  btn.getCompoundDrawables()[0]; //getResources().getDrawable(R.mipmap.back);
        Drawable wrappedDrawable = DrawableCompat.wrap(leftDrawable);
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(Color.RED));
        btn.setCompoundDrawables(wrappedDrawable,null,null,null);
    * */
    //判断登陆用户是否点赞
    private void like(){
        LikeRepository likeRepository = LikeRepository.getInstance(NewsDetailActivity.this,userApplication.getAccessToken());
        likeRepository.findLike(userApplication.getUser().getUserId(), newsModel.getNewsId(), new ObjectCallback<LikeModel>() {
            @Override
            public void onSuccess(LikeModel object) {
                if (object != null){
                    likeBtn.setImageResource(R.drawable.ic_favorite_selected_24dp);

                    likeModel = object;
                } else {
                    likeModel = null;
                }
                Log.i("用户是否点赞",object.toString());
            }

            @Override
            public void onError(Throwable t) {
                likeBtn.setImageResource(R.drawable.ic_favorite_24dp);
                likeModel = null;
                Log.i("用户是否点赞",t.toString());
            }
        });
    }
    //判断登陆用户是否收藏(关注)
    private void favour(){
        FavourRepository favourRepository = FavourRepository.getInstance(this,userApplication.getAccessToken());
        favourRepository.findFavour(userApplication.getUser().getUserId(), newsModel.getNewsId(), new ObjectCallback<FavourModel>() {
            @Override
            public void onSuccess(FavourModel object) {
                if (object != null){
                    favoriteBtn.setImageResource(R.drawable.ic_like_selected_24dp);
                    favourModel = object;
                } else {
                    favourModel = null;
                }
                Log.i("用户是否收藏",object.toString());
            }

            @Override
            public void onError(Throwable t) {
                favoriteBtn.setImageResource(R.drawable.ic_like_24dp);
                favourModel = null;
                Log.i("用户是否收藏",t.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
        }
    }
}
