package com.idejie.android.aoc.activity;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.application.UserApplication;
import com.idejie.android.aoc.dialog.PriceItemDialog;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.strongloop.android.remoting.adapters.Adapter;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class MyPriceActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, PriceItemDialog.OnItemClickListener {

    private ImageView backImg;
    private ListView listView;
    private SwipeRefreshLayout myPriceRefresh;
    private MyPriceListAdapter adapter;

    private UserApplication userApplication;

    private Button refreshBtn;

    private PriceItemDialog priceItemDialog;
    private int selectedPosition;

    int skip = 0;

    private View listFooterView;
    private ProgressBar progressBar;
    private TextView progressTxt;
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_price);

        userApplication = (UserApplication) getApplication();
        initViews();

        adapter = new MyPriceListAdapter(this);
        listView.setAdapter(adapter);

        requestData(true);
    }

    private void initViews() {
        backImg= (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView= (ListView) findViewById(R.id.my_price_list);

        listFooterView = LayoutInflater.from(this).inflate(R.layout.loading_more, null);
        listFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressTxt.setText("加载中...");
                isFirstLoad = false;
                requestData(true);
            }
        });
        listView.addFooterView(listFooterView);
        progressBar = (ProgressBar) listFooterView.findViewById(R.id.loading_more_progress);
        progressTxt = (TextView) listFooterView.findViewById(R.id.loading_more_txt);

        myPriceRefresh = (SwipeRefreshLayout) findViewById(R.id.my_price_refresh);
        myPriceRefresh.setOnRefreshListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                if (priceItemDialog == null) {
                    priceItemDialog = new PriceItemDialog(MyPriceActivity.this);
                    priceItemDialog.setOnItemClickListener(MyPriceActivity.this);
                }
                priceItemDialog.show();
            }
        });

        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLoad = true;
                myPriceRefresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressTxt.setText("加载中...");
                requestData(false);
            }
        });
    }

    private void requestData(final boolean isLoadMore){
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();

        where.put("userId",userApplication.getUser().getUserId());
        filterMap.put("where",where);

        List<String> list = new ArrayList<>();
        list.add("sort");
        list.add("grade");
        list.add("region");

        filterMap.put("include",list);
        filterMap.put("limit", 10);
        filterMap.put("skip", skip);
        filterMap.put("order","priceDate DESC"); //按priceDate排序

        Gson gson1 = new Gson();
        String filterStr =  gson1.toJson(filterMap);

        params.put("filter",filterStr);

        PriceRepository priceRepository = PriceRepository.getInstance(this,userApplication.getAccessToken());
        priceRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                int len = response.length();
                skip += len;
                if (isLoadMore || isFirstLoad) {
                    if (len > 0) {
                        progressTxt.setText("点我加载");
                    } else {
                        progressTxt.setText("没有了！！");
                    }
                    myPriceRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                } else {
                    myPriceRefresh.setRefreshing(false);
                }
                if (len > 0) {
                    Gson gson = CommonUtil.getGSON();
                    List<PriceModel> priceModels = new ArrayList<PriceModel>(len);

                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);
                        PriceModel priceModel = gson.fromJson(jsonObject.toString(),PriceModel.class);
                        priceModels.add(priceModel);
                    }
                    myPriceRefresh.setVisibility(View.VISIBLE);
                    if (isLoadMore) {
                        adapter.addItems(priceModels, true);
                    } else {
                        adapter.addItems(priceModels, false);
                    }
                    adapter.notifyDataSetChanged();
                } else { //没有数据
                    if (isFirstLoad) { // 如果是第一次加载。那么直接将列表隐藏
                        myPriceRefresh.setVisibility(View.GONE);
                    } else {
                        myPriceRefresh.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                myPriceRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                progressTxt.setText("点击加载");
                if (isFirstLoad) {
                    myPriceRefresh.setVisibility(View.GONE);
                }
                Toast.makeText(MyPriceActivity.this, "网络出问题了，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        skip = 0;
        isFirstLoad = true;
        requestData(false);
    }

    @Override
    public void onItemClick(View v, int index) {
        priceItemDialog.dismiss();
        PriceRepository priceRepository = PriceRepository.getInstance(this,userApplication.getAccessToken());
        PriceModel priceModel = (PriceModel) adapter.getItem(selectedPosition);
        Map<String, Object> params = new HashMap<>();
        Log.i("Price Del ID", priceModel.getId().toString());
        params.put("id", priceModel.getId());
        priceRepository.invokeStaticMethod("deleteById", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("Price Del", response.toString());
                if (response.optInt("count") == 1) {
                    adapter.delItem(selectedPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private class MyPriceListAdapter extends BaseAdapter {

        private List<PriceModel> favourList;
        private Context context;
        private SimpleDateFormat dateFormat;

        public MyPriceListAdapter(Context context) {
            this.context = context;
            favourList = new ArrayList<>();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        }

        public void addItems(List<PriceModel> list,boolean append) {
            if (!append) {
                favourList.clear();
            }
            favourList.addAll(list);
        }

        @Override
        public int getCount() {
            return favourList.size();
        }

        @Override
        public Object getItem(int position) {
            return favourList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_my_upload_list,null);
                viewHolder = new ViewHolder();

                viewHolder.city = (TextView) convertView.findViewById(R.id.text_region);
                viewHolder.sortGrade = (TextView) convertView.findViewById(R.id.text_sort_grade);
                viewHolder.price = (TextView) convertView.findViewById(R.id.text_price);
                viewHolder.amount = (TextView) convertView.findViewById(R.id.text_amount);
                viewHolder.marketName = (TextView) convertView.findViewById(R.id.text_market_name);
                viewHolder.upTime = (TextView) convertView.findViewById(R.id.text_up_time);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            PriceModel model = favourList.get(position);
            if (TextUtils.isEmpty(model.getRegion().getCity())){
                viewHolder.city.setText(String.format("%s", model.getRegion().getProvince() ));
            } else {
                viewHolder.city.setText(String.format("%s%s", model.getRegion().getProvince(),model.getRegion().getCity()));
            }

            String gradeName = (model.getGrade() == null)?"":model.getGrade().getName();
            String sortName = (model.getSort() == null)?"":model.getSort().getSubName();
            viewHolder.sortGrade.setText(sortName+"  "+gradeName);

            viewHolder.price.setText(String.format("%s 元/kg", model.getPrice())); // 价格
            viewHolder.amount.setText(String.format("%s kg", model.getTurnover()));// 成交量
            viewHolder.marketName.setText(model.getMarketName());
            viewHolder.upTime.setText(dateFormat.format(model.getPriceDate()));
//
            return convertView;
        }

        public void delItem(int selectedPosition) {
            favourList.remove(selectedPosition);
        }

        class ViewHolder {
            TextView city;
            TextView sortGrade;
            TextView price;
            TextView amount;
            TextView marketName;
            TextView upTime;
        }
    }
}
