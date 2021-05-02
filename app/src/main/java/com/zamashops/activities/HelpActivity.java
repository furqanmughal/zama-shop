package com.zamashops.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class HelpActivity extends AppCompatActivity {

    Context context = HelpActivity.this;
    WebView mWebView;
    ProgressBar progressBar;

    String webUrl = "http://zamashops.com/help.php";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("Help");


        mWebView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

//        mWebView.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                progressBar.setVisibility(View.VISIBLE);
//                progressBar.setProgress(0);
//            }
//
//            public void onPageFinished(WebView view, String url) {
//
//                progressBar.setVisibility(View.GONE);
//                progressBar.setProgress(100);
//            }
//
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//
//                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            }
//        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                progressBar.setProgress(progress); //Make the bar disappear after URL is loaded
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.loadUrl(webUrl);


//
//        final TextView txt_term_condition = findViewById(R.id.txt_term_condition);
//
//
//        HashMap<String, String> params = new HashMap<>();
//        CustomVolley.getInsertPOSTData(context, params, "help.php", new VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//
//
//                try {
//
//                    boolean error = result.getBoolean("error");
//                    if(error){
//
//                    }else{
//                        String user_data = result.getString("result");
//                        txt_term_condition.setText(Html.fromHtml(user_data));
//
//                    }
//
//
//                } catch (JSONException e) {
////                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//
//            }
//        });



    }
}