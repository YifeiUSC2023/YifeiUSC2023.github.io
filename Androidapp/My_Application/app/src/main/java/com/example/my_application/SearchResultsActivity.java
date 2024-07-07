package com.example.my_application;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;



public class SearchResultsActivity extends AppCompatActivity {
    private String symbol;
    private String name;
    private int numberOfShares;
    private double changeInPrice;
    private AlertDialog dialog;
    private double currentPrice = 1.0;

    private int quantity;
    private List<Double> prices = new ArrayList<>();
    private List<Long> times = new ArrayList<>();
    WebView webView;
    private ImageView chartHourImageView;
    private ImageView chartHistoricalImageView;
    WebView webView2;
    WebView webView3;
    WebView webView4;
    private double balance;

    private final boolean[] isStarFilled = {false};
    private JSONObject stockInfo = new JSONObject();
    private JSONObject quoteInfo = new JSONObject();
    private final JSONObject postData = new JSONObject();
    private ProgressBar progressBar;
    private View mainContent;
    private TextView companyNameTextView;
    private ImageView companyLogoImageView;
    private TextView companyStockTextView;
    private WebView chartWebView;
    private ImageView chart_historical;
    private ImageView chart_hour;
    private TextView portfolioTextView;
    private ConstraintLayout portfolioDetailsContainer;
    private TextView statsTextView;
    private TextView openPriceTextView;
    private TextView highPriceTextView;
    private TextView lowPriceTextView;
    private TextView prevPriceTextView;
    private TextView aboutTitleTextView;
    private TextView ipoStartTextView;
    private TextView industryTextView;
    private TextView webpageTextView;
    private TextView companyPeersTextView;

    private HorizontalScrollView horizontalScrollView;
    private TextView InsightTextView;
    private TextView titleTextView;
    private TableLayout tableTextView;
    private WebView histogramChartWebView;
    private WebView curveChartWebView;
    private TextView NewsTextView;
    private CardView newsCardView;
    private TextView newsTitleTextView;
    private ScrollView scrollview2;
    private View chart_hour_underline;
//    private RecyclerView recyclerView;
//    private NewsAdapter adapter;
//    private List<NewsItem> newsList; // 将 newsList 声明为成员变量






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


//        recyclerView = findViewById(R.id.newsRecyclerView); // 获取 RecyclerView 实例
//        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 设置布局管理器
//        newsList = new ArrayList<>(); // 假设您已经填充了这个列表
//        adapter = new NewsAdapter(this, newsList); // 创建适配器实例
//        recyclerView.setAdapter(adapter); // 设置适配器

        companyNameTextView = findViewById(R.id.companyNameTextView);
        companyLogoImageView = findViewById(R.id.companyLogoImageView);
        companyStockTextView = findViewById(R.id.companyStockTextView);
        chartWebView = findViewById(R.id.chartWebView);
        chart_historical = findViewById(R.id.chart_historical);
        chart_hour = findViewById(R.id.chart_hour);
        portfolioTextView = findViewById(R.id.portfolioTextView);
        portfolioDetailsContainer = findViewById(R.id.portfolioDetailsContainer);
        statsTextView = findViewById(R.id.statsTextView);
        openPriceTextView = findViewById(R.id.openPriceTextView);
        highPriceTextView = findViewById(R.id.highPriceTextView);
        lowPriceTextView = findViewById(R.id.lowPriceTextView);
        prevPriceTextView = findViewById(R.id.prevPriceTextView);
        aboutTitleTextView = findViewById(R.id.aboutTitleTextView);
        ipoStartTextView = findViewById(R.id.ipoStartTextView);
        industryTextView = findViewById(R.id.industryTextView);
        webpageTextView = findViewById(R.id.webpageTextView);
        companyPeersTextView = findViewById(R.id.companyPeersTextView);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        InsightTextView = findViewById(R.id.InsightTextView);
        titleTextView = findViewById(R.id.titleTextView);
        tableTextView = findViewById(R.id.tableTextView);
        histogramChartWebView = findViewById(R.id.histogramChartWebView);
        curveChartWebView = findViewById(R.id.curveChartWebView);
        NewsTextView = findViewById(R.id.NewsTextView);
        newsCardView = findViewById(R.id.newsCardView);
        newsTitleTextView = findViewById(R.id.newsTitleTextView);
        scrollview2 = findViewById(R.id.scrollview2);
        chart_hour_underline = findViewById(R.id.chart_hour_underline);

        companyNameTextView.setVisibility(View.GONE);
        companyLogoImageView.setVisibility(View.GONE);
        companyStockTextView.setVisibility(View.GONE);
        chartWebView.setVisibility(View.GONE);
        chart_historical.setVisibility(View.GONE);
        chart_hour.setVisibility(View.GONE);
        portfolioTextView.setVisibility(View.GONE);
        portfolioDetailsContainer.setVisibility(View.GONE);
        statsTextView.setVisibility(View.GONE);
        openPriceTextView.setVisibility(View.GONE);
        highPriceTextView.setVisibility(View.GONE);
        lowPriceTextView.setVisibility(View.GONE);
        prevPriceTextView.setVisibility(View.GONE);
        aboutTitleTextView.setVisibility(View.GONE);
        ipoStartTextView.setVisibility(View.GONE);
        industryTextView.setVisibility(View.GONE);
        webpageTextView.setVisibility(View.GONE);
        companyPeersTextView.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.GONE);
        InsightTextView.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        tableTextView.setVisibility(View.GONE);
        histogramChartWebView.setVisibility(View.GONE);
        curveChartWebView.setVisibility(View.GONE);
        NewsTextView.setVisibility(View.GONE);
        newsCardView.setVisibility(View.GONE);
        newsTitleTextView.setVisibility(View.GONE);
        scrollview2.setVisibility(View.GONE);
        chart_hour_underline.setVisibility(View.GONE);


        progressBar = findViewById(R.id.progressBar);


        loadYourData();




        chartHourImageView = findViewById(R.id.chart_hour);
        chartHistoricalImageView = findViewById(R.id.chart_historical);
        View chartHourUnderline = findViewById(R.id.chart_hour_underline);
        View historicalUnderline = findViewById(R.id.historical_underline);

        webView = findViewById(R.id.chartWebView);
        webView2 = findViewById(R.id.chartHistoricalWebview);
        webView3 = findViewById(R.id.histogramChartWebView);
        webView4 = findViewById(R.id.curveChartWebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/chart.html");

        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl("file:///android_asset/chart2.html");

        webView3.getSettings().setJavaScriptEnabled(true);
        webView3.loadUrl("file:///android_asset/chart3.html");

        webView4.getSettings().setJavaScriptEnabled(true);
        webView4.loadUrl("file:///android_asset/chart4.html");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadData(); // 确保页面加载完成后加载数据
            }
        });

        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadData2(); // 确保页面加载完成后加载数据
            }
        });

        webView3.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadData3(); // 确保页面加载完成后加载数据
            }
        });

        webView4.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadData4(); // 确保页面加载完成后加载数据
            }
        });





        //获取数据
        symbol = getIntent().getStringExtra("symbol");
        if (symbol != null) {
            symbol = symbol.toUpperCase(); // 将股票代码转换为大写
        } else {
            symbol = ""; // 确保 symbol 不为 null
        }
//
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(symbol);

        RequestQueue queue = Volley.newRequestQueue(this);
        // 构造URL，使用传递来的股票符号

        String url = "http://10.0.2.2:3000/api/stock/" + symbol;
        String url2 = "http://10.0.2.2:3000/api/quote/" + symbol;
        String url3 = "http://10.0.2.2:3000/api/peers/" + symbol;
        String url4 = "http://10.0.2.2:3000/api/insider-sentiment/" + symbol;
        String url5 = "http://10.0.2.2:3000/api/news/" + symbol;
        String url6 = "http://10.0.2.2:3000/api/favourites";
        String url7 = "http://10.0.2.2:3000/api/favourites/" + symbol;
        String url8 = "http://10.0.2.2:3000/api/transactions";

//        String url = "https://myappp-418801.uc.r.appspot.com/api/stock/" + symbol;
//        String url2 = "https://myappp-418801.uc.r.appspot.com/api/quote/" + symbol;
//        String url3 = "https://myappp-418801.uc.r.appspot.com/api/peers/" + symbol;
//        String url4 = "https://myappp-418801.uc.r.appspot.com/api/insider-sentiment/" + symbol;
//        String url5 = "https://myappp-418801.uc.r.appspot.com/api/news/" + symbol;
//        String url6 = "https://myappp-418801.uc.r.appspot.com/api/favourites";
//        String url7 = "https://myappp-418801.uc.r.appspot.com/api/favourites/" + symbol;
//        String url8 = "https://myappp-418801.uc.r.appspot.com/api/transactions";













        ImageView starBorder = findViewById(R.id.star_border);
        ImageView fullStar = findViewById(R.id.full_star);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 处理响应
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            stockInfo = new JSONObject(response);
                            name = jsonObject.getString("name");
                            postData.put("stockInfo", stockInfo);
                            String ipoDate = jsonObject.getString("ipo");
                            String industry = jsonObject.getString("finnhubIndustry");
                            String webpage = jsonObject.getString("weburl");
                            String logo = jsonObject.getString("logo");

                            TextView nameTextView = findViewById(R.id.nameTextView);
                            // 设置表格文本
                            nameTextView.setText(name);

                            //加载logo
                            ImageView companyLogoImageView = findViewById(R.id.companyLogoImageView);
                            // 使用Glide加载图片
                            Glide.with(SearchResultsActivity.this) // 正确传入Activity Context
                                    .load(logo) // 传入图片URL
                                    .override(180, 180)
                                    .into(companyLogoImageView); // 传入目标ImageView

                            SpannableString symbolSpannable = new SpannableString(symbol + "\n");
                            symbolSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            symbolSpannable.setSpan(new RelativeSizeSpan(1.0f), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 默认大小

                            // 设置name的样式
                            SpannableString nameSpannable = new SpannableString(name);
                            nameSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            nameSpannable.setSpan(new RelativeSizeSpan(0.9f), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 稍微小一点的字体

                            // 组合两部分
                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            builder.append(symbolSpannable);
                            builder.append(nameSpannable);

                            TextView textView = findViewById(R.id.companyNameTextView);
                            textView.setText(builder);

                            //ipo date
                            TextView ipoStartTextView = findViewById(R.id.ipoStartTextView);

                            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            // 创建另一个SimpleDateFormat对象用于格式化日期为新的格式
                            SimpleDateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

                            // 解析字符串为Date对象
                            Date date = originalFormat.parse(ipoDate);
                            // 使用目标格式化对象格式化日期
                            String formattedDate = targetFormat.format(date);

                            String ipoStartDateText = getString(R.string.ipo_start_date_label) + createSpaces(12)+formattedDate; // 'dynamicDate' 是您动态获取的日期数据
                            ipoStartTextView.setText(ipoStartDateText);

                            TextView industryTextView = findViewById(R.id.industryTextView);
                            String industryText = getString(R.string.industry) + createSpaces(24)+industry; // 'dynamicDate' 是您动态获取的日期数据
                            industryTextView.setText(industryText);

//

//                            TextView webpageTextView = findViewById(R.id.webpageTextView);
//                            String webpageText = getString(R.string.webpage) + createSpaces(22)+webpage;
//                            webpageTextView.setText(webpageText);
//// 下面是手动设置点击事件的方式
//                            webpageTextView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webpage));
//                                    startActivity(browserIntent);
//                                }
//                            });
//
//// 设置文本下划线和文字颜色来模仿链接的样式
//                            webpageTextView.setPaintFlags(webpageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//                            webpageTextView.setTextColor(Color.BLUE);

                            TextView webpageTextView = findViewById(R.id.webpageTextView);
                            String labelText = getString(R.string.webpage);

                            String webpageText = labelText + createSpaces(21) + webpage;

                            SpannableString spannableString = new SpannableString(webpageText);

// 为网址设置点击事件和样式
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View view) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webpage));
                                    startActivity(browserIntent);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(true); // 为链接部分设置下划线
                                    ds.setColor(Color.BLUE);   // 为链接部分设置颜色
                                }
                            };

                            // 应用ClickableSpan到SpannableString
                            int start = webpageText.indexOf(webpage);
                            int end = start + webpage.length();
                            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            // 设置TextView可点击并应用SpannableString
                            webpageTextView.setText(spannableString);
                            webpageTextView.setMovementMethod(LinkMovementMethod.getInstance()); // 让链接可点击

                            // 确保其他文本部分（"Webpage"）没有样式
                            webpageTextView.setLinkTextColor(Color.TRANSPARENT); // 透明色意味着没有颜色变化


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, error -> {
                    // 处理错误
                    Log.d("Volley", "Error: " + error.toString());
                });

        // 将请求添加到RequestQueue


        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        quoteInfo = new JSONObject(response);
                        postData.put("quoteInfo", quoteInfo);
                        currentPrice = jsonObject.getDouble("c");
                        double highPrice = jsonObject.getDouble("h");
                        double lowPrice = jsonObject.getDouble("l");
                        double openPrice = jsonObject.getDouble("o");
                        double previousPrice = jsonObject.getDouble("pc");
                        double percentageChange = jsonObject.getDouble("dp");
                        changeInPrice = jsonObject.getDouble("d");

                        String formattedCurrentPrice = String.format(Locale.US,"%.2f", currentPrice);
                        String formattedHighPrice = String.format(Locale.US,"%.2f", highPrice);
                        String formattedLowPrice = String.format(Locale.US,"%.2f", lowPrice);
                        String formattedOpenPrice = String.format(Locale.US,"%.2f", openPrice);
                        String formattedPreviousPrice = String.format(Locale.US,"%.2f", previousPrice);
                        String formattedPercentageChange = String.format(Locale.US,"%.2f", percentageChange);
                        String formattedChangeInPrice = String.format(Locale.US,"%.2f", changeInPrice);

                        SpannableString priceSpannable = new SpannableString("$"+formattedCurrentPrice + "\n");
                       // priceSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                       // priceSpannable.setSpan(new RelativeSizeSpan(1.0f), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 默认大小

                        // 设置的样式
                        SpannableString changeSpannable = new SpannableString(createSpaces(3)+"$"+formattedChangeInPrice+"("+formattedPercentageChange+"%)");
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



                        changeSpannable.setSpan(new ForegroundColorSpan(color), 0, changeSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        changeSpannable.setSpan(new RelativeSizeSpan(0.9f), 0, changeSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 组合两部分
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(priceSpannable);
                        builder.append(changeSpannable);

                        TextView textView = findViewById(R.id.companyStockTextView);
                        textView.setText(builder);

                        TextView openPriceTextView = findViewById(R.id.openPriceTextView);
                        String openPriceText = "Open Price: "+"$"+formattedOpenPrice; // 'dynamicDate' 是您动态获取的日期数据
                        openPriceTextView.setText(openPriceText);

                        TextView highPriceTextView = findViewById(R.id.highPriceTextView);
                        String highPriceText = "High Price: "+"$"+formattedHighPrice; // 'dynamicDate' 是您动态获取的日期数据
                        highPriceTextView.setText(highPriceText);

                        TextView lowPriceTextView = findViewById(R.id.lowPriceTextView);
                        String lowPriceText = "Low Price: "+"$"+formattedLowPrice; // 'dynamicDate' 是您动态获取的日期数据
                        lowPriceTextView.setText(lowPriceText);

                        TextView prevPriceTextView = findViewById(R.id.prevPriceTextView);
                        String prevPriceText = "Prev. close: "+"$"+formattedPreviousPrice; // 'dynamicDate' 是您动态获取的日期数据
                        prevPriceTextView.setText(prevPriceText);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Error: " + error.toString());
            }
        });

        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray peersJsonArray = new JSONArray(response);
                            LinearLayout peersContainer = findViewById(R.id.peersContainer);
                            peersContainer.removeAllViews(); // 清除之前的视图

                            for (int i = 0; i < peersJsonArray.length(); i++) {
                                String peerName = peersJsonArray.getString(i);
                                TextView peerTextView = new TextView(SearchResultsActivity.this);

                                SpannableString spannablePeerName = new SpannableString(peerName + ",");
                                spannablePeerName.setSpan(new UnderlineSpan(), 0, spannablePeerName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannablePeerName.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannablePeerName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                peerTextView.setText(spannablePeerName);


                                peerTextView.setPadding(8, 8, 8, 8); // 根据需要调整padding


                                peerTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 创建Intent以启动新的Activity
                                        Intent intent = new Intent(SearchResultsActivity.this, SearchResultsActivity.class);
                                        intent.putExtra("symbol", peerName.trim()); // 假设"symbol"是SearchResultsActivity接受的关键字
                                        startActivity(intent);
                                    }
                                });

                                peersContainer.addView(peerTextView);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Error: " + error.toString());
            }
        });

        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, url4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray dataArray = jsonResponse.getJSONArray("data");

                            double positiveMSPR = 0.0;
                            double negativeMSPR = 0.0;
                            double totalMSPR = 0.0;
                            double positiveChange = 0.0;
                            double negativeChange = 0.0;
                            double totalChange = 0.0;

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                double mspr = item.getDouble("mspr");
                                double change = item.getDouble("change");

                                if (mspr > 0) {
                                    positiveMSPR += mspr;
                                } else {
                                    negativeMSPR += mspr;
                                }
                                if (change > 0) {
                                    positiveChange += change;
                                } else {
                                    negativeChange += change;
                                }
                            }
                            totalMSPR = positiveMSPR + negativeMSPR;
                            totalChange = positiveChange + negativeChange;


                            String formattedPositiveMSPR = String.format(Locale.getDefault(), "%.2f", positiveMSPR);
                            String formattedNegativeMSPR = String.format(Locale.getDefault(), "%.2f", negativeMSPR);
                            String formattedTotalMSPR = String.format(Locale.getDefault(), "%.2f", totalMSPR);
                            String formattedNegativeChange = String.format(Locale.getDefault(), "%.2f", negativeChange);
                            String formattedPositiveChange = String.format(Locale.getDefault(), "%.2f", positiveChange);
                            String formattedTotalChange = String.format(Locale.getDefault(), "%.2f", totalChange);



                            TextView ttMSPR = findViewById(R.id.totalMSPR);
                            TextView ttChange = findViewById(R.id.totalChange);
                            TextView poMSPR = findViewById(R.id.positiveMSPR);
                            TextView neMSPR = findViewById(R.id.negativeMSPR);
                            TextView poChange = findViewById(R.id.positiveChange);
                            TextView neChange = findViewById(R.id.negativeChange);
                            ttMSPR.setText(formattedTotalMSPR);
                            poMSPR.setText(formattedPositiveMSPR);
                            neMSPR.setText(formattedNegativeMSPR);
                            ttChange.setText(formattedTotalChange);
                            poChange.setText(formattedPositiveChange);
                            neChange.setText(formattedNegativeChange);


                        }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Volley", "Error: " + error.toString());
                        }
                    });
        StringRequest stringRequest5 = new StringRequest(Request.Method.GET, url5,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            JSONArray newsArray = new JSONArray(response);
//                            ArrayList<JSONObject> validNews = new ArrayList<>();
//                            ArrayList<NewsItem> newItems = new ArrayList<>();
//
//                            for (int i = 0; i < newsArray.length()&&newItems.size()<19; i++) {
//                                JSONObject newsObject = newsArray.getJSONObject(i);
//                                String image = newsObject.optString("image");
//                                String title = newsObject.optString("headline");
//
//                                // 检查image和title是否非空
//                                if (!image.isEmpty() && !title.isEmpty()) {
//                                    if (validNews.isEmpty()) {  // 如果 validNews 还没有存储任何新闻，存储第一个有效新闻
//                                        JSONObject validNewsItem = new JSONObject();
//                                        validNewsItem.put("source", newsObject.optString("source"));
//                                        validNewsItem.put("datetime", newsObject.optInt("datetime"));
//                                        validNewsItem.put("url", newsObject.optString("url"));
//                                        validNewsItem.put("title", title);
//                                        validNewsItem.put("image", image);
//                                        validNewsItem.put("summary", newsObject.optString("summary"));
//                                        validNews.add(validNewsItem);
//                                    } else {  // 存储接下来的最多19条新闻到 newsList
//                                        NewsItem newsItem = new NewsItem(
//                                                title,
//                                                image,
//                                                newsObject.optString("summary"),
//                                                newsObject.optString("source"),
//                                                newsObject.optInt("datetime"),
//                                                newsObject.optString("url")
//                                        );
//                                        if (newItems.size() < 19) {  // 确保最多只添加19条新闻
//                                            newItems.add(newsItem);
//                                        }
//                                    }
//                                }
//                            }
//
//                            newsList.clear(); // 清空旧数据
//                            newsList.addAll(newItems); // 添加新数据
//                            adapter.notifyDataSetChanged(); // 通知数据已更改
























                            JSONArray newsArray = new JSONArray(response);
                            ArrayList<JSONObject> validNews = new ArrayList<>();
                            for (int i = 0; i < newsArray.length() && validNews.size() < 20; i++) {
                                JSONObject newsObject = newsArray.getJSONObject(i);
                                String image = newsObject.optString("image");
                                String title = newsObject.optString("headline");
                                // 检查image和title是否非空
                                if (!image.isEmpty() && !title.isEmpty()) {
                                    // 创建新的JSONObject来保存所需的信息
                                    JSONObject validNewsItem = new JSONObject();
                                    validNewsItem.put("source", newsObject.optString("source"));
                                    validNewsItem.put("datetime", newsObject.optInt("datetime"));
                                    validNewsItem.put("url", newsObject.optString("url"));
                                    validNewsItem.put("title", title);
                                    validNewsItem.put("image", image);
                                    validNewsItem.put("summary", newsObject.optString("summary"));

                                    // 添加到列表中
                                    validNews.add(validNewsItem);
                                }
                            }
                            // 现在 validNews 包含了最多20个有有效image和title的news对象
                            // TODO: 更新UI或处理数据
                            ImageView firstNews = findViewById(R.id.newsImageView);
                            TextView firstText = findViewById(R.id.newsTitleTextView);
                            if (!validNews.isEmpty()) {
                                // 从列表中获取第一个新闻对象
                                JSONObject firstNewsItem = validNews.get(0);

                                // 获取新闻标题、图片和来源
                                String title = firstNewsItem.optString("title");
                                String image = firstNewsItem.optString("image");
                                String source = firstNewsItem.optString("source");


                                long epochTime = firstNewsItem.optLong("datetime");

                                long currentTime = System.currentTimeMillis() / 1000L; // 转换为秒

                                long timeElapsedInSeconds = currentTime - epochTime;
                                String timeElapsedString;
                                if (timeElapsedInSeconds < 60) {
                                    timeElapsedString = timeElapsedInSeconds + "seconds ago";
                                } else if (timeElapsedInSeconds < 3600) {
                                    long minutes = timeElapsedInSeconds / 60;
                                    timeElapsedString = minutes + (minutes == 1 ? " minute ago" : " minutes ago");
                                } else if (timeElapsedInSeconds < 86400) {
                                    long hours = timeElapsedInSeconds / 3600;
                                    timeElapsedString = hours + (hours == 1 ? " hour ago" : " hours ago");
                                } else {
                                    long days = timeElapsedInSeconds / 86400;
                                    timeElapsedString = days + (days == 1 ? " day ago" : " days ago");
                                }


                                String sourceText = source + createSpaces(5) + timeElapsedString;
                                String text = sourceText + "\n" + title;
                                SpannableString intro = new SpannableString(text);



                                int sourceColor = Color.GRAY; // 灰色

                                StyleSpan boldSpanForSource = new StyleSpan(Typeface.BOLD); // 加粗样式
                                RelativeSizeSpan sizeSpanForSource = new RelativeSizeSpan(1.1f); // 字体放大1.1倍
                                intro.setSpan(boldSpanForSource, 0, sourceText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                intro.setSpan(new ForegroundColorSpan(sourceColor), 0, sourceText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                intro.setSpan(sizeSpanForSource, 0, sourceText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

// 为title设置样式
                                int titleColor = Color.BLACK; // 黑色
                                StyleSpan boldSpanForTitle = new StyleSpan(Typeface.BOLD); // 加粗样式
                                RelativeSizeSpan sizeSpanForTitle = new RelativeSizeSpan(1.1f); // 字体放大1.5倍
                                int titleStart = sourceText.length() + 1; // +2 是因为冒号和换行符
                                int titleEnd = titleStart + title.length();
                                intro.setSpan(boldSpanForTitle, titleStart, titleEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                intro.setSpan(sizeSpanForTitle, titleStart, titleEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                intro.setSpan(new ForegroundColorSpan(titleColor), titleStart, titleEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                                // 设置新闻标题和来源到 TextView
                                firstText.setText(intro);

                                // 使用Glide库来加载图片到 ImageView
                                Glide.with(SearchResultsActivity.this) // 确保这里的 this 指向的是 Context 对象，如果在Fragment中，应该用 getActivity() 或 getContext()
                                        .load(image)
                                        .into(firstNews);


                                //以上是第一条news，下面是剩下19条
                                LinearLayout container = findViewById(R.id.newsContainer);

                                // 循环遍历除第一条之外的所有新闻
                                for (int i = 1; i < validNews.size(); i++) {
                                    JSONObject newsItem = validNews.get(i);

                                    // Inflate news item layout
                                    View newsView = LayoutInflater.from(SearchResultsActivity.this).inflate(R.layout.news_item, container, false);

                                    // Set title and image
                                    TextView titleTextView = newsView.findViewById(R.id.newsTitleTextView);
                                    ImageView imageView = newsView.findViewById(R.id.newsImageView);

                                    String title2 = newsItem.optString("title");
                                    String image2 = newsItem.optString("image");
                                    String source2 = newsItem.optString("source");
                                    String summary2 = newsItem.optString("summary");
                                    String url2 = newsItem.optString("url");

                                    long epochTime2 = firstNewsItem.optLong("datetime");
                                    long EpochTime2 = epochTime2* 1000L;

                                    Date date = new Date(EpochTime2);

                                    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
                                    formatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles")); // 设置为洛杉矶时区

                                    String formattedDate = formatter.format(date);

                                    long currentTime2 = System.currentTimeMillis() / 1000L; // 转换为秒

                                    long timeElapsedInSeconds2 = currentTime2 - epochTime2;
                                    String timeElapsedString2;
                                    if (timeElapsedInSeconds < 60) {
                                        timeElapsedString2 = timeElapsedInSeconds2 + "seconds ago";
                                    } else if (timeElapsedInSeconds < 3600) {
                                        long minutes = timeElapsedInSeconds2 / 60;
                                        timeElapsedString2 = minutes + (minutes == 1 ? " minute ago" : " minutes ago");
                                    } else if (timeElapsedInSeconds2 < 86400) {
                                        long hours = timeElapsedInSeconds2 / 3600;
                                        timeElapsedString2 = hours + (hours == 1 ? " hour ago" : " hours ago");
                                    } else {
                                        long days = timeElapsedInSeconds2 / 86400;
                                        timeElapsedString2 = days + (days == 1 ? " day ago" : " days ago");
                                    }

                                    String sourceText2 = source2 + createSpaces(5) + timeElapsedString2;
                                    String text2 = sourceText2 + "\n" + title2;
                                    SpannableString intro2 = new SpannableString(text2);

                                    StyleSpan boldSpanForSource2 = new StyleSpan(Typeface.BOLD); // 加粗样式
                                    RelativeSizeSpan sizeSpanForSource2 = new RelativeSizeSpan(1.1f); // 字体放大1.1倍
                                    intro2.setSpan(boldSpanForSource2, 0, sourceText2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    intro2.setSpan(new ForegroundColorSpan(sourceColor), 0, sourceText2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    intro2.setSpan(sizeSpanForSource2, 0, sourceText2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                                    StyleSpan boldSpanForTitle2 = new StyleSpan(Typeface.BOLD); // 加粗样式
                                    RelativeSizeSpan sizeSpanForTitle2 = new RelativeSizeSpan(1.1f); // 字体放大1.5倍
                                    int titleStart2 = sourceText2.length() + 1; // +2 是因为冒号和换行符
                                    int titleEnd2 = titleStart2 + title2.length();
                                    intro2.setSpan(boldSpanForTitle2, titleStart2, titleEnd2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    intro2.setSpan(sizeSpanForTitle2, titleStart2, titleEnd2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    intro2.setSpan(new ForegroundColorSpan(titleColor), titleStart2, titleEnd2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                                    titleTextView.setText(intro2);

                                    Glide.with(SearchResultsActivity.this).load(image2)
                                            .transform(new RoundedCorners(32))
                                            .override(100,100)
                                            .into(imageView);

                                    // Add the news view to the container
                                    newsView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // 创建和显示对话框
                                            Dialog dialog = new Dialog(SearchResultsActivity.this);
                                            dialog.setContentView(R.layout.dialog_news_details);

                                            // 获取并设置对话框中的控件
                                            TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);


                                            SpannableString spannableTitle = new SpannableString(source2 + "\n" + formattedDate);
                                            spannableTitle.setSpan(new ForegroundColorSpan(Color.BLACK), 0, source2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannableTitle.setSpan(new RelativeSizeSpan(1.5f), 0, source2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 1.5f是字体放大的倍数

// 为timeElapsedString2设置样式
                                            int startIndex = source2.length() + 1; // +1因为换行符
                                            spannableTitle.setSpan(new ForegroundColorSpan(Color.GRAY), startIndex, spannableTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            spannableTitle.setSpan(new RelativeSizeSpan(0.8f), startIndex, spannableTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 1.0f保持字体大小不变
                                            // 假设您已经从 newsItem 中提取了所需的详细信息
                                            dialogTitle.setText(spannableTitle);

                                            TextView newsTitle = dialog.findViewById(R.id.newsTitle);


                                            SpannableString titleSpannable = new SpannableString(title2);
                                            titleSpannable.setSpan(new RelativeSizeSpan(1.2f), 0, title2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            titleSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, title2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            newsTitle.setText(titleSpannable);


                                            TextView summaryView = dialog.findViewById(R.id.summaryView);
                                            SpannableString summarySpannable = new SpannableString(summary2);
                                            summarySpannable.setSpan(new RelativeSizeSpan(0.95f), 0, summary2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            summaryView.setText(summarySpannable);




                                            ImageView chromeIcon = dialog.findViewById(R.id.chromeIcon);
                                            chromeIcon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // 这里写上点击Chrome图标后要执行的动作
                                                    // 例如，打开一个网页链接
                                                     // 实际的网址
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(url2));
                                                    startActivity(intent);
                                                }
                                            });


                                            ImageView twitterIcon = dialog.findViewById(R.id.twitterIcon);
                                            twitterIcon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // 新闻的标题和链接
                                                    String tweetText = "Check out this Link:"; // 这里可以自定义您想分享的文本


                                                    // 构建Twitter分享的URL
                                                    String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(tweetText) + "&url=" + Uri.encode(url2);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                                                    startActivity(intent);
                                                }
                                            });

                                            ImageView facebookIcon = dialog.findViewById(R.id.facebookIcon);
                                            facebookIcon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // 新闻的标题和链接
                                                    String newsTitle = "Say something about this..."; // 适当设置您的新闻标题
                                                    ; // 这里替换成新闻的实际URL

                                                    // 构建Facebook分享的URL
                                                    String facebookUrl = "https://www.facebook.com/sharer/sharer.php?u=" + Uri.encode(url2) + "&t=" + Uri.encode(newsTitle);

                                                    // 启动一个新的浏览器或已有的浏览器标签来打开URL
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                                                    startActivity(intent);
                                                }
                                            });
                                            dialog.show();
                                        }
                                    });





                                    container.addView(newsView);








                                }





                            } else {
                                // 没有有效的新闻数据，可以选择隐藏这些视图或者设置默认的文本/图片
                                firstText.setText("No news available");
                            }









                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Error: " + error.toString());
            }
        });

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url6, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // 遍历收藏夹数组
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject favourite = response.getJSONObject(i);
                                JSONObject stockInfo = favourite.getJSONObject("stockInfo");
                                String ticker = stockInfo.getString("ticker");
                                if (ticker.equals(symbol)) { // 'symbol' 应该是你要比对的股票代码变量
                                    // 如果找到了匹配的 ticker，显示实心星星
                                    fullStar.setVisibility(View.VISIBLE);
                                    starBorder.setVisibility(View.GONE);
                                    break;
                                }
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

        // 添加请求到请求队列
        Volley.newRequestQueue(SearchResultsActivity.this).add(jsonArrayRequest);



        TextView shares = findViewById(R.id.information1);
        TextView AvgCost = findViewById(R.id.information2);
        TextView TotalCost = findViewById(R.id.information3);
        TextView change = findViewById(R.id.information4);
        TextView marketValue = findViewById(R.id.information5);



        // 创建GET请求
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, url8, null,
                response -> {
                    try {
                        // 第一个对象总是余额
                        JSONObject balanceObject = response.getJSONObject(0);
                        balance = balanceObject.getDouble("balance");
                        Log.d("API Request", "Balance: " + balance);



                        String currentTicker = symbol;  // 当前股票代码，根据实际情况设置或获取


                        // 检查后续对象以找到匹配的股票信息
                        for (int i = 1; i < response.length(); i++) {
                            JSONObject stockObject = response.getJSONObject(i);
                            if (stockObject.getString("ticker").equals(currentTicker)) {
                                String description = stockObject.getString("description");
                                quantity = stockObject.getInt("quantity");
                                double totalCost = stockObject.getDouble("totalCost");
                                double avgCost = stockObject.getDouble("avgCost");

                                double difference = avgCost-avgCost;

                                // 更新UI
                                shares.setText(String.format(Locale.US, "Shares Owned:       " +"%d", quantity));
                                AvgCost.setText(String.format(Locale.US, "Avg. Cost/Share:    $%.2f", avgCost));
                                TotalCost.setText(String.format(Locale.US, "Total Cost:               $%.2f", totalCost));
                                change.setText(String.format(Locale.US, "Change:                    $%.2f", difference));
                                marketValue.setText(String.format(Locale.US, "Market Value:          $%.2f", avgCost));


                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("API Request", "Error: " + error.toString()));

        // 添加请求到请求队列
//        requestQueue.add(jsonArrayRequest2);
        Volley.newRequestQueue(SearchResultsActivity.this).add(jsonArrayRequest2);


















        




        queue.add(stringRequest);
        queue.add(stringRequest2);
        queue.add(stringRequest3);
        queue.add(stringRequest4);
        queue.add(stringRequest5);



        chartHourImageView.setSelected(true); // 默认选中
        chartHourUnderline.setVisibility(View.VISIBLE);

        chartHourImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHourImageView.setSelected(true);
                chartHistoricalImageView.setSelected(false);
                chartHourUnderline.setVisibility(View.VISIBLE); // 显示hour chart下划线
                historicalUnderline.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView2.setVisibility(View.GONE);


                // 加载小时数据
            }
        });

        chartHistoricalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHistoricalImageView.setSelected(true);
                chartHourImageView.setSelected(false);
                historicalUnderline.setVisibility(View.VISIBLE); // 显示historical chart下划线
                chartHourUnderline.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                webView2.setVisibility(View.VISIBLE);
                // 加载历史数据
            }
        });









        starBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!isStarFilled[0]) {
                    // If current is hollow, after clicking, show solid
                starBorder.setVisibility(View.GONE);
                fullStar.setVisibility(View.VISIBLE);
                Toast.makeText(SearchResultsActivity.this, symbol+" is added to favorites", Toast.LENGTH_SHORT).show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url6, postData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // 请求成功，处理响应
                                Log.d("PostRequest", "Response: " + response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // 请求失败，处理错误
                                Log.e("PostRequest", "Error: " + error.toString());
                            }
                        });
                Volley.newRequestQueue(SearchResultsActivity.this).add(jsonObjectRequest);
                isStarFilled[0] = true;



//                }
//                 else {
//                    // If current is solid, after clicking, show hollow
//                    starBorder.setVisibility(View.VISIBLE);
//                    fullStar.setVisibility(View.GONE);
//                    isStarFilled[0] = false;
//                }
            }
        });

        // If you want the fullStar to also respond to click events
        fullStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click solid, turn back hollow
                starBorder.setVisibility(View.VISIBLE);
                fullStar.setVisibility(View.GONE);
                Toast.makeText(SearchResultsActivity.this, symbol+" is removed from favorites", Toast.LENGTH_SHORT).show();

                StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, url7,
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
                Volley.newRequestQueue(SearchResultsActivity.this).add(deleteRequest);


                isStarFilled[0] = false;
            }
        });



        ImageButton buttonTrade = findViewById(R.id.buttonTrade);
        buttonTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建对话框构建器
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultsActivity.this);
                // 设置对话框内容


                // 通过LayoutInflater来加载一个xml布局文件作为一个View对象
                View dialogView = LayoutInflater.from(SearchResultsActivity.this).inflate(R.layout.dialog_trades, null);

                // 设置对话框的视图
                builder.setView(dialogView);
                dialog = builder.create();

                // 添加交易按钮的点击事件
                ImageButton btnBuy = dialogView.findViewById(R.id.btnBuy);
                ImageButton btnSell = dialogView.findViewById(R.id.btnSell);
                EditText etShares = dialogView.findViewById(R.id.etShares);
                TextView tvCalculation = dialogView.findViewById(R.id.tvCalculation);
                TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
                TextView tvCostInfo = dialogView.findViewById(R.id.tvCostInfo);


                tvDialogTitle.setText("Trade "+name+" shares");
                tvCostInfo.setText(String.format(Locale.US, "$%.2f to buy %s", balance, symbol));

                tvCalculation.setText("0*$"+currentPrice+"/share = $0.00");
                etShares.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!s.toString().isEmpty()) {
                            try {
                                numberOfShares = Integer.parseInt(s.toString());
                                double totalCost = numberOfShares * currentPrice;
                                String formattedText = String.format(Locale.US, "%d*$%.2f/share = $%.2f", numberOfShares, currentPrice, totalCost);
                                tvCalculation.setText(formattedText);
                            } catch (NumberFormatException e) {
                                tvCalculation.setText("Invalid input");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });




                // 设置按钮操作
                btnBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etShares.getText().toString().trim().isEmpty()) {
                            Toast.makeText(SearchResultsActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            numberOfShares = Integer.parseInt(etShares.getText().toString());
                            if (numberOfShares <= 0) {
                                // 输入的数量必须大于0
                                Toast.makeText(getApplicationContext(), "Cannot buy non-positive shares", Toast.LENGTH_SHORT).show();
                            }else if(balance<numberOfShares*currentPrice){
                                Toast.makeText(getApplicationContext(), "Not enough money to buy", Toast.LENGTH_SHORT).show();

                            }else {

                                String ticker = symbol; // 确保这个变量是当前类的成员或者通过某种方式得到
                                String description = name; // 同上
                                double price = currentPrice; // 这里是单价，同上

                                // 调用方法发送请求
                                sendPurchaseRequest(ticker, description, price, numberOfShares);
//                                showSuccessDialog("bought",numberOfShares);
                                dialog.dismiss();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(SearchResultsActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etShares.getText().toString().trim().isEmpty()) {
                            Toast.makeText(SearchResultsActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            numberOfShares = Integer.parseInt(etShares.getText().toString());
                            if (numberOfShares > quantity) {
                                // 如果输入的股票数量大于持有的股票数量
                                Toast.makeText(getApplicationContext(), "Not enough shares to sell", Toast.LENGTH_SHORT).show();
                            } else if (numberOfShares <= 0) {
                                // 输入的数量必须大于0
                                Toast.makeText(getApplicationContext(), "Cannot sell non-positive shares", Toast.LENGTH_SHORT).show();
                            } else {
                                String ticker = symbol; // 确保这个变量是当前类的成员或者通过某种方式得到
                                String description = name; // 同上
                                double price = currentPrice; // 这里是单价，同上

                                // 调用方法发送请求
                                processTransaction(ticker, description, price, numberOfShares);
//                                showSuccessDialog("sold",numberOfShares);
                                dialog.dismiss();
                            }
                        } catch (NumberFormatException e) {
                            // 如果输入的不是有效的整数，捕获异常并提示用户
                            Toast.makeText(getApplicationContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                // 创建并显示对话框

                dialog.show();
            }
        });



























        final OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("SearchResultsActivity", "Back pressed, finishing activity");
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理返回按钮的点击事件
        if (item.getItemId() == android.R.id.home) {
            // 结束当前 Activity，返回到 MainActivity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private String createSpaces(int numberOfSpaces) {
        return new String(new char[numberOfSpaces]).replace('\0', ' ');
    }

    private void loadData() {
        String url = "http://10.0.2.2:3000/api/polygon/" + symbol;
//        String url = "https://myappp-418801.uc.r.appspot.com/api/polygon/" + symbol;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        processResponse(response,symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Volley", "Error: " + error.toString())
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void processResponse(String response,String symbol) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray resultsArray = jsonResponse.getJSONArray("results");
        int length = resultsArray.length();
        int startIndex = Math.max(0, length - 20);

        List<Double> prices = new ArrayList<>();
        List<Long> times = new ArrayList<>();

        for (int i = startIndex; i < length; i++) {
            JSONObject result = resultsArray.getJSONObject(i);
            prices.add(result.getDouble("c"));
            times.add(result.getLong("t"));
        }

        // 调用JavaScript函数更新图表
        String script = "javascript:updateChart(" + new Gson().toJson(prices) + ", "
                + new Gson().toJson(times) + ", '" + symbol + "', " + changeInPrice + ");";
        webView.loadUrl(script);
    }

    private void loadData2() {
        String url = "http://10.0.2.2:3000/api/polygon2/" + symbol;
//        String url = "https://myappp-418801.uc.r.appspot.com/api/polygon2/" + symbol;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        processResponse2(response,symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Volley", "Error: " + error.toString())
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void processResponse2(String response,String symbol) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray resultsArray = jsonResponse.getJSONArray("results");

        List<List<Double>> ohlc = new ArrayList<>();
        List<List<Object>> volume = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject item = resultsArray.getJSONObject(i);
            double open = item.getDouble("o");
            double high = item.getDouble("h");
            double low = item.getDouble("l");
            double close = item.getDouble("c");
            long volumeVal = item.getLong("v");
            long time = item.getLong("t");

            // OHLC data for Highstock (timestamp, open, high, low, close)
            ohlc.add(Arrays.asList((double) time, open, high, low, close));

            // Volume data (timestamp, volume)
            volume.add(Arrays.asList((double) time, (double) volumeVal));
        }

//        updateChartWithData(ohlc, volume, symbol);
        String script = "javascript:updateChartWithData(" + new Gson().toJson(ohlc) + "," + new Gson().toJson(volume) + ",'" + symbol + "');";
        webView2.loadUrl(script);
    }

    private void loadData3() {
        String url = "http://10.0.2.2:3000/api/recommendation/" + symbol;
//        String url = "https://myappp-418801.uc.r.appspot.com/api/recommendation/" + symbol;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        processResponse3(response,symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Volley", "Error: " + error.toString())
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void processResponse3(String response,String symbol) throws JSONException {
        JSONArray dataArray = new JSONArray(response);

        ArrayList<String> periods = new ArrayList<>();
        ArrayList<Integer> strongSellData = new ArrayList<>();
        ArrayList<Integer> sellData = new ArrayList<>();
        ArrayList<Integer> holdData = new ArrayList<>();
        ArrayList<Integer> buyData = new ArrayList<>();
        ArrayList<Integer> strongBuyData = new ArrayList<>();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);

            // 提取每个时间段的数据
            String period = item.getString("period");
            int strongSell = item.getInt("strongSell");
            int sell = item.getInt("sell");
            int hold = item.getInt("hold");
            int buy = item.getInt("buy");
            int strongBuy = item.getInt("strongBuy");

            periods.add(period);
            strongSellData.add(strongSell);
            sellData.add(sell);
            holdData.add(hold);
            buyData.add(buy);
            strongBuyData.add(strongBuy);
        }

        // 现在你有了所有需要的数据列表，可以将它们传递给图表的设置方法
        //updateChart(periods, strongSellData, sellData, holdData, buyData, strongBuyData);
        String script = "javascript:updateChartWithData(" +
                new Gson().toJson(periods) + "," +
                new Gson().toJson(strongSellData) + "," +
                new Gson().toJson(sellData) + "," +
                new Gson().toJson(holdData) + "," +
                new Gson().toJson(buyData) + "," +
                new Gson().toJson(strongBuyData) + ",'" + symbol + "');";
        webView3.loadUrl(script);
    }

    private void loadData4() {
        String url = "http://10.0.2.2:3000/api/earnings/" + symbol;
//        String url = "https://myappp-418801.uc.r.appspot.com/api/earnings/" + symbol;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        processResponse4(response,symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("Volley", "Error: " + error.toString())
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void processResponse4(String response,String symbol) throws JSONException {
        JSONArray dataArray = new JSONArray(response);


        ArrayList<Double> actualData = new ArrayList<>();
        ArrayList<String> periodsData = new ArrayList<>();
        ArrayList<Double> estimateData = new ArrayList<>();
        ArrayList<Double> surpriseData = new ArrayList<>();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);

            double actual = item.getDouble("actual");
            String period = item.getString("period");
            double estimate = item.getDouble("estimate");
            double surprise = item.getDouble("surprise");

            actualData.add(actual);
            periodsData.add(period);
            estimateData.add(estimate);
            surpriseData.add(surprise);
        }
        String script = "javascript:updateChartWithData(" +
                new Gson().toJson(actualData) + "," +
                new Gson().toJson(periodsData) + "," +
                new Gson().toJson(estimateData) + "," +
                new Gson().toJson(surpriseData) + ",'" +
                symbol + "');";
        webView4.loadUrl(script);


    }


    public void sendPurchaseRequest(String ticker, String description, double price, int quantity) {
        String url = "http://10.0.2.2:3000/api/transactions"; // 您的API端点地址

//        String url = "https://myappp-418801.uc.r.appspot.com/api/transactions";

        // 创建请求的JSON对象
        JSONObject postData = new JSONObject();
        try {
            postData.put("ticker", ticker);
            postData.put("description", description);
            postData.put("price", price);
            postData.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 创建JSON对象请求
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    // 处理响应
                    try {
                        String message = response.getString("message");
                        showSuccessDialog("bought",numberOfShares);
//                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // 处理错误
                    Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                });

        // 将请求添加到请求队列
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    private void processTransaction(String ticker, String description, double price, int quantity) {
        String url = "http://10.0.2.2:3000/api/transactions";
//        String url = "https://myappp-418801.uc.r.appspot.com/api/transactions";

        JSONObject postData = new JSONObject();
        try {
            postData.put("ticker", ticker);
            postData.put("description", description);
            postData.put("price", price);
            postData.put("quantity", quantity);

             // 如果是卖出，数量为负



            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, postData,
                    response -> {
                        // 处理响应
                        showSuccessDialog("sold", numberOfShares);
//                        dialog.dismiss();
                        Log.d("Transaction", "Response: " + response.toString());
                        // 更新UI或其他逻辑
                    },
                    error -> {
                        // 请求失败
                        Log.e("Transaction", "Error: " + error.toString());
                        // 错误处理
                    });

            // 添加请求到队列
            Volley.newRequestQueue(this).add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showSuccessDialog(String operation, int numberOfShares) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_success);

        TextView tvMessage = dialog.findViewById(R.id.tvMessage2);
        tvMessage.setText("You have successfully " + operation + " " + numberOfShares + " shares of " + symbol + ".");

        MaterialButton btnDone = dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();


                String url8 = "http://10.0.2.2:3000/api/transactions";
//                String url8 = "https://myappp-418801.uc.r.appspot.com/api/transactions";





                TextView shares = findViewById(R.id.information1);
                TextView AvgCost = findViewById(R.id.information2);
                TextView TotalCost = findViewById(R.id.information3);
                TextView change = findViewById(R.id.information4);
                TextView marketValue = findViewById(R.id.information5);



                // 创建GET请求
                JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, url8, null,
                        response -> {
                            try {
                                // 第一个对象总是余额
                                JSONObject balanceObject = response.getJSONObject(0);
                                balance = balanceObject.getDouble("balance");
                                Log.d("API Request", "Balance: " + balance);



                                String currentTicker = symbol;  // 当前股票代码，根据实际情况设置或获取

                                boolean stockFound = false;
                                // 检查后续对象以找到匹配的股票信息
                                for (int i = 1; i < response.length(); i++) {
                                    JSONObject stockObject = response.getJSONObject(i);
                                    if (stockObject.getString("ticker").equals(currentTicker)) {
                                        String description = stockObject.getString("description");
                                        quantity = stockObject.getInt("quantity");
                                        double totalCost = stockObject.getDouble("totalCost");
                                        double avgCost = stockObject.getDouble("avgCost");

                                        double difference = avgCost-avgCost;

                                        // 更新UI
                                        shares.setText(String.format(Locale.US, "Shares Owned:       " +"%d", quantity));
                                        AvgCost.setText(String.format(Locale.US, "Avg. Cost/Share:    $%.2f", avgCost));
                                        TotalCost.setText(String.format(Locale.US, "Total Cost:               $%.2f", totalCost));
                                        change.setText(String.format(Locale.US, "Change:                    $%.2f", difference));
                                        marketValue.setText(String.format(Locale.US, "Market Value:          $%.2f", avgCost));

                                        stockFound = true;
                                        break;
                                    }
                                }
                                if (!stockFound) {
                                    shares.setText(String.format(Locale.US, "Shares Owned:       0"));
                                    AvgCost.setText(String.format(Locale.US, "Avg. Cost/Share:    $0.00"));
                                    TotalCost.setText(String.format(Locale.US, "Total Cost:               $0.00"));
                                    change.setText(String.format(Locale.US, "Change:                    $0.00"));
                                    marketValue.setText(String.format(Locale.US, "Market Value:          $0.00"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> Log.e("API Request", "Error: " + error.toString()));

                // 添加请求到请求队列
//        requestQueue.add(jsonArrayRequest2);
                Volley.newRequestQueue(SearchResultsActivity.this).add(jsonArrayRequest2);










            }
        });

        dialog.show();
    }


    private void loadYourData() {
        progressBar.setVisibility(View.VISIBLE);  // 显示进度条
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // 模拟数据加载
                SystemClock.sleep(3000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressBar.setVisibility(View.GONE);  // 隐藏进度条
                companyNameTextView.setVisibility(View.VISIBLE);
                companyLogoImageView.setVisibility(View.VISIBLE);  // 显示主内容
                companyStockTextView.setVisibility(View.VISIBLE);
                chartWebView.setVisibility(View.VISIBLE);
                chart_historical.setVisibility(View.VISIBLE);
                chart_hour.setVisibility(View.VISIBLE);
                portfolioTextView.setVisibility(View.VISIBLE);
                portfolioDetailsContainer.setVisibility(View.VISIBLE);
                statsTextView.setVisibility(View.VISIBLE);
                openPriceTextView.setVisibility(View.VISIBLE);
                highPriceTextView.setVisibility(View.VISIBLE);
                lowPriceTextView.setVisibility(View.VISIBLE);
                prevPriceTextView.setVisibility(View.VISIBLE);
                aboutTitleTextView.setVisibility(View.VISIBLE);
                ipoStartTextView.setVisibility(View.VISIBLE);
                industryTextView.setVisibility(View.VISIBLE);
                webpageTextView.setVisibility(View.VISIBLE);
                companyPeersTextView.setVisibility(View.VISIBLE);
                horizontalScrollView.setVisibility(View.VISIBLE);
                InsightTextView.setVisibility(View.VISIBLE);
                titleTextView.setVisibility(View.VISIBLE);
                tableTextView.setVisibility(View.VISIBLE);
                histogramChartWebView.setVisibility(View.VISIBLE);
                curveChartWebView.setVisibility(View.VISIBLE);
                NewsTextView.setVisibility(View.VISIBLE);
                newsCardView.setVisibility(View.VISIBLE);
                newsTitleTextView.setVisibility(View.VISIBLE);
                scrollview2.setVisibility(View.VISIBLE);
                chart_hour_underline.setVisibility(View.VISIBLE);
            }
        }.execute();
    }










}