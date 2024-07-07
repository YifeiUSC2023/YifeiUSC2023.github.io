package com.example.my_application;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbarMain;
    private Toolbar toolbarSearch;
    private TextView tvDate; // 成员变量，用于存储日期 TextView 的引用
    private ConstraintLayout.LayoutParams dateLayoutParams; // 用于修改约束的参数
    private double currentPrice;
    private double nowPrice;
    private String ticker;
    private String symbol;
    private double balance;
    private ProgressBar progressBar;
    private View mainContent;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置初始的 Toolbar 作为 ActionBar
        toolbarMain = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarMain);
        // 确保主 Toolbar 不显示返回箭头
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


        progressBar = findViewById(R.id.progressBar);
        mainContent = findViewById(R.id.mainContent);  // 假设这是你的主内容区域的ID
        mainContent.setVisibility(View.GONE);
        loadYourData();




        // 找到搜索 Toolbar，这将用于稍后切换
        toolbarSearch = findViewById(R.id.toolbar_search);
        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示搜索 Toolbar
                showSearchToolbar();
            }
        });

        // 启用返回键
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }



















//        显示日期
        tvDate = findViewById(R.id.tvDate);
        // 获取日期 TextView 的当前布局参数
        dateLayoutParams = (ConstraintLayout.LayoutParams) tvDate.getLayoutParams();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles")); // 设置为洛杉矶时区
        String currentDate = dateFormat.format(new Date());
        tvDate.setText(currentDate);

//用于实现清楚图标的逻辑，清除搜索框内的text内容
        AutoCompleteTextView searchField = findViewById(R.id.search_field);
        ImageView clearSearch = findViewById(R.id.clear_search);

        // 为清除图标设置点击监听器
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除搜索框文本
                searchField.setText("");
            }
        });


        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 获取用户输入的股票代码
                    symbol = searchField.getText().toString().toUpperCase();
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    // 将用户输入的股票代码传递给SearchResultsActivity
                    intent.putExtra("symbol", symbol);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        String urlTemplate = "http://10.0.2.2:3000/api/name/%1$s";
        String url = "http://10.0.2.2:3000/api/favourites";
//        String url = "https://myappp-418801.uc.r.appspot.com/api/favourites";
        String url2 = "http://10.0.2.2:3000/api/quote/" + symbol;
//        String url2 = "https://myappp-418801.uc.r.appspot.com/api/quote/" + symbol;

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 根据用户输入的currentsymbol获取数据
                String url = String.format(urlTemplate, s.toString());

                // 发送请求
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    // 解析响应
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONArray results = jsonResponse.getJSONArray("result");
//                                    ArrayList<String> symbolsAndDescriptions = new ArrayList<>();
                                    ArrayList<StockItem> stockItems = new ArrayList<>();

                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject item = results.getJSONObject(i);
                                        String symbol = item.getString("symbol");
                                        String type = item.optString("type", "");
                                        String description = item.getString("description");
//                                        if (!symbol.contains(".")) {
//                                            symbolsAndDescriptions.add(symbol + " | " + description);
//                                        }
                                        if (!symbol.contains(".")) {
                                            stockItems.add(new StockItem(symbol, description));
                                        }

                                    }


//                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                                            MainActivity.this,
//                                            android.R.layout.simple_dropdown_item_1line,
//                                            symbolsAndDescriptions
//                                    );
                                    StockAdapter adapter = new StockAdapter(MainActivity.this, stockItems);

                                    // 设置适配器到AutoCompleteTextView
                                    searchField.setAdapter(adapter);

                                    searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            StockItem item = (StockItem) parent.getItemAtPosition(position);
                                            searchField.setText(item.symbol); // 这将设置 AutoCompleteTextView 的文本只为 symbol
                                        }
                                    });





                                    // 确保下拉列表是可见的
                                    searchField.showDropDown();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 处理错误情况
                        error.printStackTrace();
                    }
                });

                // 将请求添加到请求队列
                Volley.newRequestQueue(MainActivity.this).add(request);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject quoteInfo = new JSONObject(response);
                        nowPrice = jsonObject.getDouble("c");
                        double percentageChange = jsonObject.getDouble("dp");
                        double changeInPrice = jsonObject.getDouble("d");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Error: " + error.toString());
            }
        });
        Volley.newRequestQueue(MainActivity.this).add(stringRequest2);










        LinearLayout llFavoritesContainer = findViewById(R.id.llFavoritesContainer);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            llFavoritesContainer.removeAllViews();

                            // 遍历收藏夹数组
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject favourite = response.getJSONObject(i);
                                JSONObject stockInfo = favourite.getJSONObject("stockInfo");
                                JSONObject quoteInfo = favourite.getJSONObject("quoteInfo");

                                // 从 JSON 对象中获取信息
                                ticker = stockInfo.getString("ticker");
                                String name = stockInfo.getString("name");
                                currentPrice = quoteInfo.getDouble("c");
                                double percentageChange = quoteInfo.getDouble("dp");
                                double changeInPrice = quoteInfo.getDouble("d");


                                String formattedPercentageChange = String.format(Locale.US,"%.2f", percentageChange);
                                String formattedChangeInPrice = String.format(Locale.US,"%.2f", changeInPrice);
                                SpannableString changeSpannable = new SpannableString("   $"+formattedChangeInPrice+"("+formattedPercentageChange+"%)");


                                // 动态创建视图并设置数据
                                View favoriteItemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.favorite_item, llFavoritesContainer, false);
                                TextView tvTicker = favoriteItemView.findViewById(R.id.tickerTextView);
                                TextView tvName = favoriteItemView.findViewById(R.id.nameTextView);
                                TextView tvCurrentPrice = favoriteItemView.findViewById(R.id.currentPriceTextView);
                                TextView tvChangePercentage = favoriteItemView.findViewById(R.id.changePercentageTextView);

                                tvTicker.setText(ticker);
                                tvName.setText(name);
                                tvCurrentPrice.setText(String.format(Locale.US, "$%.2f", currentPrice));



                                int color;
                                Drawable drawable;
                                int drawableRes;
                                if (changeInPrice > 0) {
                                    color = Color.parseColor("#1cb51a");  // 绿色表示价格上涨
                                    drawableRes = R.drawable.trending_up;
                                } else if (changeInPrice < 0) {
                                    color = Color.RED;    // 红色表示价格下跌
                                    drawableRes = R.drawable.trending_down;
                                } else {
                                    color = Color.BLACK;  // 黑色表示没有变化
                                    drawableRes = 0;
                                }
                                if (drawableRes != 0) {
                                    drawable = ContextCompat.getDrawable(getApplicationContext(), drawableRes);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                                    changeSpannable.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                tvChangePercentage.setText(changeSpannable);
                                tvChangePercentage.setTextColor(color);



                                favoriteItemView.setOnTouchListener(new View.OnTouchListener() {
                                    private float downX, upX;

                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                downX = event.getX();
                                                return true;  // 允许其他事件如 ACTION_UP 继续被处理
                                            case MotionEvent.ACTION_UP:
                                                upX = event.getX();
                                                float deltaX = downX - upX;
                                                if (deltaX > 100) {
                                                    // 如果用户左滑超过100像素，则视为尝试删除
                                                    showDeleteOption(v);
//                                                    slideToDelete(favoriteItemView,deleteIcon);
                                                }
                                                return true;
                                        }
                                        return false;
                                    }

                                    private void showDeleteOption(View card) {
                                        ImageView deleteView = card.findViewById(R.id.delete_view);
                                        deleteView.setVisibility(View.VISIBLE);
                                        card.animate()
                                                .translationX(-deleteView.getWidth())  // 将卡片向左滑动以显示删除按钮
                                                .setDuration(300)
                                                .start();

                                        deleteView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                removeCard(card,ticker);
                                            }
                                        });
                                    }


                                    // 当你想开始动画时调用这个方法

                                });




















                                // 将创建的视图添加到 LinearLayout 容器中
                                llFavoritesContainer.addView(favoriteItemView);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Request", "Error: " + error.toString());
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonArrayRequest);


        String url8 = "http://10.0.2.2:3000/api/transactions";
//        String url8 = "https://myappp-418801.uc.r.appspot.com/api/transactions";

        LinearLayout llFavoritesContainer2 = findViewById(R.id.llFavoritesContainer2);
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, url8, null,
                response -> {
                    try {
                        // 第一个对象总是余额
                        JSONObject balanceObject = response.getJSONObject(0);
                        balance = balanceObject.getDouble("balance");
                        Log.d("API Request", "Balance: " + balance);

                        TextView tvBalance = findViewById(R.id.tvPortfolioDetail2);
                        tvBalance.setText(String.format(Locale.US, "Cash Balance\n$%.2f", balance));


                        String currentTicker = ticker;  // 当前股票代码，根据实际情况设置或获取
                        llFavoritesContainer2.removeAllViews();
                        Double netWorth = balance;

                        // 检查后续对象以找到匹配的股票信息
                        for (int i = 1; i < response.length(); i++) {
                            JSONObject stockObject = response.getJSONObject(i);

                            View favoriteItemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.favorite_item, llFavoritesContainer2, false);

                            String description = stockObject.getString("description");
                            String thisTicker = stockObject.getString("ticker");
                            int quantity = stockObject.getInt("quantity");
                            double totalCost = stockObject.getDouble("totalCost");
                            double avgCost = stockObject.getDouble("avgCost");
                            double difference = currentPrice-avgCost;
                            netWorth += totalCost;

                            TextView tickerTextView = favoriteItemView.findViewById(R.id.tickerTextView); // 确保你的favorite_item.xml有这个ID
                            tickerTextView.setText(thisTicker);

                            TextView nameTextView = favoriteItemView.findViewById(R.id.nameTextView);
                            nameTextView.setText(quantity+" shares");

                            TextView currentPriceTextView = favoriteItemView.findViewById(R.id.currentPriceTextView);
                            currentPriceTextView.setText(String.format(Locale.US, "$%.2f", totalCost));

                            TextView changePercentageTextView = favoriteItemView.findViewById(R.id.changePercentageTextView);
                            changePercentageTextView.setText("$0.00(0.00%)");


                            favoriteItemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 使用intent启动SearchResultsActivity，并传递股票代码
                                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                                    intent.putExtra("symbol", thisTicker);  // 注意变量作用域，确保thisTicker在此处可用
                                    startActivity(intent);
                                }
                            });
                            // 将填充的视图添加到LinearLayout
                            llFavoritesContainer2.addView(favoriteItemView);

                            TextView tvPortfolioDetail = findViewById(R.id.tvPortfolioDetail);


                            tvPortfolioDetail.setText(String.format(Locale.US, "Net Worth\n$%.2f", netWorth));











                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("API Request", "Error: " + error.toString()));

        // 添加请求到请求队列
//        requestQueue.add(jsonArrayRequest2);
        Volley.newRequestQueue(MainActivity.this).add(jsonArrayRequest2);























    }

















    public void onPoweredByFinnhubClicked(View view) {
        String url = "https://www.finnhub.io";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回键的处理
        if (item.getItemId() == android.R.id.home) {
            // 如果搜索 Toolbar 可见，则切换回主 Toolbar
            if (toolbarSearch.getVisibility() == View.VISIBLE) {
                showMainToolbar(); // 使用单独的方法来处理回到主 Toolbar 的逻辑
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 如果搜索 Toolbar 可见，则先切换回主 Toolbar
        if (toolbarSearch.getVisibility() == View.VISIBLE) {
            toolbarSearch.setVisibility(View.GONE);
            toolbarMain.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbarMain); // 重新设置主 Toolbar 为 ActionBar
        } else {
            super.onBackPressed();
        }
    }

    private void showSearchToolbar() {
        // 隐藏主 Toolbar 和其分割线
        toolbarMain.setVisibility(View.GONE);
        findViewById(R.id.toolbar_divider_main).setVisibility(View.GONE);
        // 显示搜索 Toolbar 和其分割线
        toolbarSearch.setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_divider_search).setVisibility(View.VISIBLE);
        setSupportActionBar(toolbarSearch);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // 不显示任何标题
        }
        dateLayoutParams.topToBottom = R.id.toolbar_search;
        tvDate.setLayoutParams(dateLayoutParams); // 应用新的布局参数
    }

    private void showMainToolbar() {
        toolbarSearch.setVisibility(View.GONE);
        findViewById(R.id.toolbar_divider_search).setVisibility(View.GONE);
        toolbarMain.setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_divider_main).setVisibility(View.VISIBLE);
        setSupportActionBar(toolbarMain); // 重新设置主 Toolbar 为 ActionBar

        // 确保返回箭头不显示，显示 "Stocks" 标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 不显示返回箭头
            getSupportActionBar().setTitle("Stocks"); // 显示 "Stocks" 标题
        }

        // 更新日期 TextView 的顶部约束
        dateLayoutParams.topToBottom = R.id.toolbar;
        tvDate.setLayoutParams(dateLayoutParams); // 应用新的布局参数
    }

    private void performSearch(String query) {
        // 暂时显示一个Toast消息
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("TICKER", query);
        startActivity(intent);
    }


    private void removeCard(View card,String symbol) {
        String url = "http://10.0.2.2:3000/api/favourites/"+symbol;
//        String url = "https://myappp-418801.uc.r.appspot.com/api/favourites/"+symbol;

        ViewGroup parent = (ViewGroup) card.getParent();


        if (parent != null) {
            parent.removeView(card);
            StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // 请求成功，处理响应
                            Log.d("DeleteRequest", "Response: " + response);
                            // 你可能想要在这里更改UI，比如移除收藏的标记等
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // 请求失败，处理错误
                    Log.e("DeleteRequest", "Error: " + error.toString());
                    // 你可能想要显示错误信息或执行其他错误处理操作
                }
            });
            Volley.newRequestQueue(MainActivity.this).add(deleteRequest);
        }
    }


    private void loadYourData() {
        progressBar.setVisibility(View.VISIBLE);  // 显示进度条
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // 模拟数据加载
                SystemClock.sleep(4000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressBar.setVisibility(View.GONE);  // 隐藏进度条
                mainContent.setVisibility(View.VISIBLE);  // 显示主内容
            }
        }.execute();
    }

    protected void onResume() {
        super.onResume();
        String url = "http://10.0.2.2:3000/api/favourites";
//        String url = "https://myappp-418801.uc.r.appspot.com/api/favourites";

        LinearLayout llFavoritesContainer = findViewById(R.id.llFavoritesContainer);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            llFavoritesContainer.removeAllViews();

                            // 遍历收藏夹数组
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject favourite = response.getJSONObject(i);
                                JSONObject stockInfo = favourite.getJSONObject("stockInfo");
                                JSONObject quoteInfo = favourite.getJSONObject("quoteInfo");

                                // 从 JSON 对象中获取信息
                                ticker = stockInfo.getString("ticker");
                                String name = stockInfo.getString("name");
                                currentPrice = quoteInfo.getDouble("c");
                                double percentageChange = quoteInfo.getDouble("dp");
                                double changeInPrice = quoteInfo.getDouble("d");


                                String formattedPercentageChange = String.format(Locale.US,"%.2f", percentageChange);
                                String formattedChangeInPrice = String.format(Locale.US,"%.2f", changeInPrice);
                                SpannableString changeSpannable = new SpannableString("   $"+formattedChangeInPrice+"("+formattedPercentageChange+"%)");


                                // 动态创建视图并设置数据
                                View favoriteItemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.favorite_item, llFavoritesContainer, false);
                                TextView tvTicker = favoriteItemView.findViewById(R.id.tickerTextView);
                                TextView tvName = favoriteItemView.findViewById(R.id.nameTextView);
                                TextView tvCurrentPrice = favoriteItemView.findViewById(R.id.currentPriceTextView);
                                TextView tvChangePercentage = favoriteItemView.findViewById(R.id.changePercentageTextView);

                                tvTicker.setText(ticker);
                                tvName.setText(name);
                                tvCurrentPrice.setText(String.format(Locale.US, "$%.2f", currentPrice));



                                int color;
                                Drawable drawable;
                                int drawableRes;
                                if (changeInPrice > 0) {
                                    color = Color.parseColor("#1cb51a");  // 绿色表示价格上涨
                                    drawableRes = R.drawable.trending_up;
                                } else if (changeInPrice < 0) {
                                    color = Color.RED;    // 红色表示价格下跌
                                    drawableRes = R.drawable.trending_down;
                                } else {
                                    color = Color.BLACK;  // 黑色表示没有变化
                                    drawableRes = 0;
                                }
                                if (drawableRes != 0) {
                                    drawable = ContextCompat.getDrawable(getApplicationContext(), drawableRes);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                                    changeSpannable.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                tvChangePercentage.setText(changeSpannable);
                                tvChangePercentage.setTextColor(color);



                                favoriteItemView.setOnTouchListener(new View.OnTouchListener() {
                                    private float downX, upX;

                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                downX = event.getX();
                                                return true;  // 允许其他事件如 ACTION_UP 继续被处理
                                            case MotionEvent.ACTION_UP:
                                                upX = event.getX();
                                                float deltaX = downX - upX;
                                                if (deltaX > 100) {
                                                    // 如果用户左滑超过100像素，则视为尝试删除
                                                    showDeleteOption(v);
//                                                    slideToDelete(favoriteItemView,deleteIcon);
                                                }
                                                return true;
                                        }
                                        return false;
                                    }

                                    private void showDeleteOption(View card) {
                                        ImageView deleteView = card.findViewById(R.id.delete_view);
                                        deleteView.setVisibility(View.VISIBLE);
                                        card.animate()
                                                .translationX(-deleteView.getWidth())  // 将卡片向左滑动以显示删除按钮
                                                .setDuration(300)
                                                .start();

                                        deleteView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                removeCard(card,ticker);
                                            }
                                        });
                                    }


                                    // 当你想开始动画时调用这个方法

                                });




                                // 将创建的视图添加到 LinearLayout 容器中
                                llFavoritesContainer.addView(favoriteItemView);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Request", "Error: " + error.toString());
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonArrayRequest);
        // 每次 MainActivity 重新可见时都调用 loadData() 刷新数据
        String url8 = "http://10.0.2.2:3000/api/transactions";
//        String url8 = "https://myappp-418801.uc.r.appspot.com/api/transactions";

        LinearLayout llFavoritesContainer2 = findViewById(R.id.llFavoritesContainer2);
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, url8, null,
                response -> {
                    try {
                        // 第一个对象总是余额
                        JSONObject balanceObject = response.getJSONObject(0);
                        balance = balanceObject.getDouble("balance");
                        Log.d("API Request", "Balance: " + balance);

                        TextView tvBalance = findViewById(R.id.tvPortfolioDetail2);
                        tvBalance.setText(String.format(Locale.US, "Cash Balance\n$%.2f", balance));


                        String currentTicker = ticker;  // 当前股票代码，根据实际情况设置或获取
                        llFavoritesContainer2.removeAllViews();
                        Double netWorth = balance;

                        // 检查后续对象以找到匹配的股票信息
                        for (int i = 1; i < response.length(); i++) {
                            JSONObject stockObject = response.getJSONObject(i);

                            View favoriteItemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.favorite_item, llFavoritesContainer2, false);

                            String description = stockObject.getString("description");
                            String thisTicker = stockObject.getString("ticker");
                            int quantity = stockObject.getInt("quantity");
                            double totalCost = stockObject.getDouble("totalCost");
                            double avgCost = stockObject.getDouble("avgCost");
                            double difference = currentPrice-avgCost;
                            netWorth += totalCost;

                            TextView tickerTextView = favoriteItemView.findViewById(R.id.tickerTextView); // 确保你的favorite_item.xml有这个ID
                            tickerTextView.setText(thisTicker);

                            TextView nameTextView = favoriteItemView.findViewById(R.id.nameTextView);
                            nameTextView.setText(quantity+" shares");

                            TextView currentPriceTextView = favoriteItemView.findViewById(R.id.currentPriceTextView);
                            currentPriceTextView.setText(String.format(Locale.US, "$%.2f", totalCost));

                            TextView changePercentageTextView = favoriteItemView.findViewById(R.id.changePercentageTextView);
                            changePercentageTextView.setText("$0.00(0.00%)");


                            favoriteItemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 使用intent启动SearchResultsActivity，并传递股票代码
                                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                                    intent.putExtra("symbol", thisTicker);  // 注意变量作用域，确保thisTicker在此处可用
                                    startActivity(intent);
                                }
                            });
                            // 将填充的视图添加到LinearLayout
                            llFavoritesContainer2.addView(favoriteItemView);

                            TextView tvPortfolioDetail = findViewById(R.id.tvPortfolioDetail);


                            tvPortfolioDetail.setText(String.format(Locale.US, "Net Worth\n$%.2f", netWorth));











                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("API Request", "Error: " + error.toString()));

        // 添加请求到请求队列
//        requestQueue.add(jsonArrayRequest2);
        Volley.newRequestQueue(MainActivity.this).add(jsonArrayRequest2);






















    }
}