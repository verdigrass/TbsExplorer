package com.tencent.tbs.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.chromium.base.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScrollingActivity extends AppCompatActivity {


    private WebView mWebView = null;

    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;




        prepareWebview();


        Intent intent = getIntent();

        if (intent != null) {
            Bundle extras = intent.getExtras();
            String url = (String) extras.get("url");

            Log.i("ScrollingActivity", "onCreate -- url: " + url
                    + "; webview: " + mWebView);

            if (!TextUtils.isEmpty(url)) {
                mWebView.loadUrl(url);
            }
        }

    }

    void prepareWebview() {

        mWebView = new WebView(mContext);

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setDisplayZoomControls(false);
        webSetting.setMediaPlaybackRequiresUserGesture(false);

        mWebView.setWebViewClient( new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //return super.shouldOverrideUrlLoading(view, url);

                mWebView.loadUrl(url);

                return true;
            }
        });

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        BufferedReader reader = null;
                        try {

                            StringBuilder sb = new StringBuilder();

                            InputStream stream = mContext.getResources().openRawResource(R.raw.distiller);

                            Log.i("ScrollingActivity", "distiller: " + stream);


                            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

                            String line = null;
                            while ( ( line = reader.readLine() ) != null) {
                                sb.append( line );
                            }

                            mJsString = sb.toString();

                            Log.i("ScrollingActivity", "getJS: " + mJsString);

                        } catch (Throwable t) {
                            t.printStackTrace();
                        } finally {

                            try {
                                if (reader != null) {
                                    reader.close();
                                }
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    }
                }

        ).start();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Add JavaScript...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mWebView.evaluateJavascript(mJsString, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("ScrollingActivity", "evaluateJavascript result: " + value);
                    }
                });
            }
        });

        FrameLayout scrollView = (FrameLayout) findViewById(R.id.scrollView);

        scrollView.addView(mWebView);
    }

    private String mJsString = null;

    @Override
    protected void onNewIntent(Intent intent) {

        Log.i("ScrollingActivity", "onNewIntent: " + intent
                + "; webview: " + mWebView);

        super.onNewIntent(intent);

    }
}
