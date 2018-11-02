package com.idejie.android.aoc.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.UserApplication;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.model.CommentModel;
import com.idejie.android.aoc.repository.CommentRepository;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

// 新闻评论
public class NewsCommentActivity extends AppCompatActivity {

    private ImageView backBtn; //back_img
    private ListView commentList; //comment_title
    private CommentListAdapter commentListAdapter;
    private int newsId;
    private UserApplication userApplication;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_comment);
        userApplication = (UserApplication) getApplication();

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));

        backBtn = (ImageView) findViewById(R.id.back_img);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newsId = getIntent().getIntExtra("newsId",0);

        commentList = (ListView) findViewById(R.id.comment_list);

        commentListAdapter = new CommentListAdapter(this);
        commentList.setAdapter(commentListAdapter);

        requestDate();
    }

    private void requestDate(){
        CommentRepository commentRepository = CommentRepository.getInstance(this, userApplication.getAccessToken());
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();
        Map<String, Object> include = new HashMap<String, Object>();
        Map<String, Object> scope = new HashMap<String, Object>();
        scope.put("include","avatar");
        include.put("relation","author");
        include.put("scope",scope);

        where.put("newsId",newsId);
        filter.put("where",where);
        filter.put("include",include);
        filter.put("order","created DESC");
        params.put("filter", filter);
        /**
         * {
         *      filter:{
         *           where:{
         *               newsId:12,
         *               //......
         *           },
         *           include:""
         *      }
         * }
         *
         */
        commentRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("Comment",response.toString());
                if (response.length() > 0) {
                    Gson gson = CommonUtil.getGSON();

                    List<CommentModel> commentModelList = new ArrayList<CommentModel>(response.length());
                    for (int i = 0 ; i< response.length(); i++) {
                        JSONObject jsonObject = response.optJSONObject(i);
                        CommentModel commentModel = gson.fromJson(jsonObject.toString(),CommentModel.class);
                        commentModelList.add(commentModel);
                    }

                    commentListAdapter.addItems(commentModelList);
                    commentListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.i("Comment",t.toString());
            }
        });
    }


    private class CommentListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<CommentModel> comments;

        SparseBooleanArray imagsMap = new SparseBooleanArray();

        public CommentListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            comments = new ArrayList<>();
        }

        public void addItems(List<CommentModel> list){
            comments.addAll(list);
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommentItemViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.comment_list_item,null);

                viewHolder = new CommentItemViewHolder();
                viewHolder.iconImg = (ImageView) convertView.findViewById(R.id.comment_user_icon);
                viewHolder.timeTxt = (TextView) convertView.findViewById(R.id.comment_time);
                viewHolder.titleTxt = (TextView) convertView.findViewById(R.id.comment_title);
                viewHolder.contentTxt = (TextView) convertView.findViewById(R.id.comment_content);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CommentItemViewHolder) convertView.getTag();
            }

            CommentModel model = comments.get(position);
            viewHolder.timeTxt.setText(simpleDateFormat.format(model.getCreated()));
            viewHolder.contentTxt.setText(model.getContent());
            viewHolder.titleTxt.setText(model.getAuthor().getName() == null ? "" : model.getAuthor().getName());
//
//            if (!imagsMap.get(position,false)) {
//                ImageRepository imageRepository = ImageRepository.getInstance(NewsCommentActivity.this);
//                imageRepository.setTag(position);
//                imageRepository.findImageById(model.getAuthor().getImageId(), new ImageRepository.ImageModelCallBack() {
//                    @Override
//                    public void onSuccess(Object tag, ImageModel object) {
//                        CommentModel model1 = comments.get((Integer) tag);
//                        model1.getAuthor().setAvatar(object);
//                        imagsMap.put((int)tag,true);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        Log.i("findById",t.toString());
//                    }
//                });
//            } else {
//            }
            if (model.getAuthor().getAvatar() != null) {
                Glide.with(NewsCommentActivity.this).load(model.getAuthor().getAvatar().getUrl()).into(viewHolder.iconImg);
            }

//            UserModelRepository userModelRepository = UserModelRepository.getInstance(NewsCommentActivity.this);
//            Map<String, Object> params = new HashMap<String, Object>();
////            Map<String, Object> filter = new HashMap<String, Object>();
////            Map<String, Object> where = new HashMap<String, Object>();
////
////            where.put("id",model.getAuthorID());
////            filter.put("where",where);
////            filter.put("include","avatar");
//            params.put("id", model.getAuthorId());
//            userModelRepository.invokeStaticMethod("usersAvatar", params, new Adapter.JsonObjectCallback() {
//                @Override
//                public void onSuccess(JSONObject response) {
//                    Log.i("usersAvatar",response.toString());
//                }
//
//                @Override
//                public void onError(Throwable t) {
//                    Log.i("usersAvatar",t.toString());
//                }
//            });

            return convertView;
        }

        class CommentItemViewHolder {
            ImageView iconImg;
            TextView titleTxt;
            TextView timeTxt;
            TextView contentTxt;
        }
    }
}
