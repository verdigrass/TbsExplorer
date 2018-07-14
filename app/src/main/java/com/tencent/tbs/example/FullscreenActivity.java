package com.tencent.tbs.example;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.common.plugin.IQBPluginCallback;
import com.tencent.common.plugin.IQBPluginSystemCallback;
import com.tencent.common.plugin.QBPluginItemInfo;
import com.tencent.common.plugin.QBPluginSystem;
import com.tencent.smtt.export.external.DexClassLoaderProviderService;
import com.tencent.tbs.sdk.extension.partner.precheck.TbsCrashHandler;
import com.tencent.tbs.utils.FileProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import isolated.example.tiny.com.isolatedprocessdemo.IFDInterface;
import isolated.example.tiny.com.isolatedprocessdemo.ISolatedService;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity
implements  ServiceConnection {
    //content
    private FrameLayout mContentView;

    public Context mContext;

    private static String LOGTAG = "FullscreenActivity";

    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_fullscreen);

        mContentView = (FrameLayout) findViewById(R.id.fullscreen_content);


        ViewGroup linearLayout = prepareLayout();

        mContentView.addView(linearLayout);


        WEBVIEW_SO = new File(getNativeLibraryDir(mContext), "libtbs_sdk_extension_dex.jar.so");//TbsCrashHandler.LIBRARY_NAME);
    }

    public static String getNativeLibraryDir(Context context) {
        int sdkVer = android.os.Build.VERSION.SDK_INT;

        // api level 9
        if (sdkVer >= Build.VERSION_CODES.GINGERBREAD) {
            return context.getApplicationInfo().nativeLibraryDir;
        }
        // api level 4
        else if (sdkVer >= Build.VERSION_CODES.DONUT) {
            return context.getApplicationInfo().dataDir + "/lib";
        }

        return "/data/data/" + context.getPackageName() + "/lib";
    }


    // preparation
    private LinearLayout prepareLayout() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        mEdit = new EditText(mContext);
        mEdit.setText("http://v.qq.com");
        linearLayout.addView(mEdit);

        // 为什么变大写？
        Button btn1 = new Button(mContext);
        btn1.setText("openUrl");
        btn1.setAllCaps(false);

        // onClick
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setClass(mContext, ScrollingActivity.class);

                String url = mEdit.getText().toString();

                intent.putExtra("url", url);

                Log.e("grass", "startActivity: " + intent + "; url: " + url);

                startActivity(intent);
            }
        });

        // 为什么变大写？
        Button btn2 = new Button(mContext);
        btn2.setText("checkDexLoaderProviderService");
        btn2.setAllCaps(false);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long start = System.currentTimeMillis();
                Log.e("grass", "start: " + start);
                boolean ret = checkService(mContext);
                mEdit.setText("checkService: " + ret + "; cost: " + (System.currentTimeMillis() - start));
            }
        });


        // btn2
        Button btn3 = new Button(mContext);
        btn3.setText("startService");
        btn3.setAllCaps(false);

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long start = System.currentTimeMillis();
                Log.e("grass", "start: " + start);

                try {
                    startService();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                //mEdit.setText("checkService: " + ret + "; cost: " + (System.currentTimeMillis() - start));
            }
        });


        linearLayout.addView(btn1);

        linearLayout.addView(btn2);

        linearLayout.addView(btn3);

        return linearLayout;
    }

    private boolean checkService(Context context) {

        try {
//			ApplicationInfo appInfo = context.getApplicationInfo();
//			String pkg_name = appInfo.packageName;
            final PackageManager packageManager = context.getPackageManager();

            Intent intent = new Intent(context, DexClassLoaderProviderService.class);
            List<android.content.pm.ResolveInfo> resolveInfo = packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo.size() > 0) {
                // Service available
                return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }


    File WEBVIEW_SO = null;


    private void startService() throws Throwable {

        copyDex();

        Intent sercice = new Intent(this, ISolatedService.class);
        Bundle bundle = sercice.getExtras();
//            sercice.putExtra("fd", ParcelFileDescriptor.open(new File(this.getCacheDir(), "test.txt"), ParcelFileDescriptor.MODE_READ_ONLY));

        this.bindService(sercice,  this,    Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        IFDInterface fdInterface = IFDInterface.Stub.asInterface(iBinder);
        try {

            Log.i(LOGTAG, "WEBVIEW_SO: " + WEBVIEW_SO);
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(WEBVIEW_SO, ParcelFileDescriptor.MODE_READ_WRITE);


            fdInterface.onReceivedFD(pfd);


            File dev = mContext.getDir("dev", Context.MODE_PRIVATE);


            String path = dev.getAbsolutePath() + File.separator + "tbs_sdk_extension_dex.jar";

            fdInterface.onReceivedFD2(pfd, path);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }


    void copyDex() {

        File dev = mContext.getDir("dev", Context.MODE_PRIVATE);

        String tmpStaticTbsInstall = getNativeLibraryDir(mContext);

        String dest_file = dev.getAbsolutePath() + File.separator + "tbs_sdk_extension_dex.jar";

        boolean ret = FileProcessor.copyFileTo(mContext, tmpStaticTbsInstall + File.separator + "libtbs_sdk_extension_dex.jar.so",
                dest_file);

        if (ret) {
            Log.i(LOGTAG, "copyDex : " + ret + ", dest: " + new File(dest_file).exists());
            Log.i(LOGTAG, "file : " + dest_file + ", dest: " + new File(dest_file).exists());

            return;
        }

        // Get runtime
        Runtime runtime = Runtime.getRuntime();

        // Command template for making link
        StringBuilder cmdTemplate = new StringBuilder("ln -s ");
        cmdTemplate.append(tmpStaticTbsInstall);
        cmdTemplate.append(File.separator);
        cmdTemplate.append("%s");
        cmdTemplate.append(" ");
        cmdTemplate.append(dev.getAbsolutePath());
        cmdTemplate.append(File.separator);
        cmdTemplate.append("%s");

        // Make links for static tbs
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;

        try {
            String cmd = String.format(cmdTemplate.toString(),
                    "libtbs_sdk_extension_dex.jar.so",
                    "tbs_sdk_extension_dex.jar");

            Log.i(LOGTAG, "cmd: " + cmd);

            Process proc = runtime.exec(cmd.toString());

            // Fast finish link operation
            inputReader = new InputStreamReader(proc.getInputStream());
            bufferedReader = new BufferedReader(inputReader);
            while (bufferedReader.readLine() != null) ;

            Thread.sleep(3000);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}




