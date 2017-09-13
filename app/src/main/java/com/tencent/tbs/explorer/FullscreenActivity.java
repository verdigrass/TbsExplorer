package com.tencent.tbs.explorer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 500;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private static String HOME_URL = "http://www.baidu.com";

    //content
    private FrameLayout mContentView;

    private WebView mWebView;

    private Button home;

    private GestureDetector detector;
    private Button btn_back;
    private Button btn_forward;

    private View mControlsView;
    private boolean mVisible;

    private Context mContext;

    private static final String CONTENT = "<html>"
            + "<head>"
                    + "<script>"
                    + "function submit() {"
                    + "var cc = document.getElementById(\"content\").value; var ss = cc.indexOf(\"http\");"
                    + "if (ss == 0) {"
                    + "    location.href=cc;"
                    + "} else { location.href=\"https://www.baidu.com/s?wd=\" + cc; }"
                    + "}"
                    + "</script>"
            + "</head>"
            + "<body>"
            + "<br>"
            + "<br>"+ "<br>"+ "<br>"+ "<br>"+ "<br>"
            + "<br>"//+ "<form name=\"search\" class=\"search\" method=\"post\" action=\"\"><br>　　" +
            + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "<input type=\"text\" id=\"content\" class=\"search_in\" placeholder=\"Input URL/kewords to search\"  style=\"width:300px;height:30px\"/>\n"
            + "<br><br>"
            + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + "<input type=\"submit\" name=\"send\" class=\"search_butt\" value=\"GO\" onClick=\"submit();\" style=\"width:50px;height:30px\" />\n"
            //"</form>"
            + "<br>"+ "<br>"+ "<br>"+ "<br>"+ "<br>"
          +  "</body>"
          +  "</html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;

        mControlsView = findViewById(R.id.fullscreen_content_controls);

        mContentView = (FrameLayout) findViewById(R.id.fullscreen_content);

        initControls();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initControls() {

        mWebView = new WebView(this);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        //启用支持javascript
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });


        mContentView.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        home = (Button)findViewById(R.id.home);
        home.setAlpha(1.0f);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mWebView instanceof  WebView) {
                    mWebView.loadData(CONTENT, "text/html", "UTF-8");
                }

            }
        });

        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");


/*
        home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                GestureDetector detector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return super.onSingleTapUp(e);
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {
                        super.onShowPress(e);
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return super.onDown(e);
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        //showNativagtor();
                        Toast.makeText(mContext, "onMultiTouch #2", Toast.LENGTH_SHORT).show();

                        //return super.onDoubleTap(e);
                        return true;
                    }

                    @Override
                    public boolean onDoubleTapEvent(MotionEvent e) {
                        return super.onDoubleTapEvent(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        return super.onSingleTapConfirmed(e);
                    }

                    @Override
                    public boolean onContextClick(MotionEvent e) {
                        return super.onContextClick(e);
                    }
                });
                detector.onTouchEvent(event);

                return false;
            }
        });
*/

        btn_back = (Button)findViewById(R.id.go_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                }

            }
        });


        btn_forward = (Button)findViewById(R.id.go_forward);
        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mWebView != null && mWebView.canGoForward() ) {
                    mWebView.goForward();
                }

            }
        });


        //创建手势检测器
        detector = new GestureDetector(this,this);
    }


    @Override
    public void onBackPressed() {
        // 这里处理逻辑代码，大家注意：该方法仅适用于2.0或更新版的sdk

        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        }

        return;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);

        mControlsView.setVisibility(View.VISIBLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //mWebView.loadUrl(HOME_URL);
        //
        mWebView.loadData(CONTENT, "text/html", "UTF-8");
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            //}
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;

    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }
//
//    @Override
//    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//        return false;
//    }

    /**
     * 滑屏监测
     *
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float minMove = 120;         //最小滑动距离
        float minVelocity = 0;      //最小滑动速度
        float beginX = e1.getX();
        float endX = e2.getX();
        float beginY = e1.getY();
        float endY = e2.getY();

        if(beginX-endX>minMove&&Math.abs(velocityX)>minVelocity){   //左滑
            Toast.makeText(this,velocityX+"左滑",Toast.LENGTH_SHORT).show();
        }else if(endX-beginX>minMove&&Math.abs(velocityX)>minVelocity){   //右滑
            Toast.makeText(this,velocityX+"右滑",Toast.LENGTH_SHORT).show();
        }else if(beginY-endY>minMove&&Math.abs(velocityY)>minVelocity){   //上滑
            Toast.makeText(this,velocityX+"上滑",Toast.LENGTH_SHORT).show();
        }else if(endY-beginY>minMove&&Math.abs(velocityY)>minVelocity){   //下滑
            Toast.makeText(this,velocityX+"下滑",Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //将该activity上的触碰事件交给GestureDetector处理
    public boolean onTouchEvent(MotionEvent me){
        return detector.onTouchEvent(me);
    }


//
//    /**
//     * 连续点击事件监听器 可以用作双击事件
//     *
//     */
//    static abstract class OnMultiTouchListener implements OnTouchListener {
//        /**
//         * 上次 onTouch 发生的时间
//         */
//        private long lastTouchTime = 0;
//        /**
//         * 已经连续 touch 的次数
//         */
//        private AtomicInteger touchCount = new AtomicInteger(0);
//
//        private Runnable mRun = null;
//
//        public void removeCallback() {
//            if (mRun != null) {
//　　　　　　　  getMainLoopHandler().removeCallbacks(mRun);
//                mRun = null;
//            }
//        }
//
//        @Override
//        public boolean onTouch(final View v, final MotionEvent event) {
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                final long now = System.currentTimeMillis();
//                lastTouchTime = now;
//
//                touchCount.incrementAndGet();
//　　　　　　　//每点击一次就移除上一次的延迟任务，重新布置一个延迟任务
//                removeCallback();
//
//                mRun = new Runnable() {
//                    @Override
//                    public void run() {
//　　　　　　　　　　　　//两个变量相等,表示时隔 multiTouchInterval之后没有新的touch产生, 触发事件并重置touchCount
//                        if (now == lastTouchTime) {
//                            onMultiTouch(v, event, touchCount.get());
//                            touchCount.set(0);
//                        }
//                    }
//                };
//
//                postTaskInUIThread(mRun, getMultiTouchInterval());
//            }
//            return true;
//        }
//
//        /**
//         * 连续touch的最大间隔, 超过该间隔将视为一次新的touch开始， 默认是400，推荐值，也可以由客户代码指定
//         *
//         * @return
//         */
//        protected int getMultiTouchInterval() {
//            return 400;
//        }
//
//        /**
//         * 连续点击事件回调
//         *
//         * @param v
//         * @param event
//         * @param touchCount
//         *            连续点击的次数
//         * @return
//         */
//        public abstract void onMultiTouch(View v, MotionEvent event, int touchCount);
//    }
}


