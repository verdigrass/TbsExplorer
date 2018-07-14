package com.tencent.smtt.export.external;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class DexClassLoaderProviderService extends Service {

    private static final String LOGTAG = "dexloader";

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    // #01
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        Log.d(LOGTAG, "DexClassLoaderProviderService -- onCreate(" + ")");

        // Force load dex
//        DexClassLoaderProvider.setForceLoadDexFlag(true, this);
    }

    // #02
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOGTAG, "DexClassLoaderProviderService -- onStartCommand(" + intent + ")");

        if (intent == null) {
            return Service.START_STICKY;
        }

        ArrayList<String> data = intent.getStringArrayListExtra("dex2oat");

        if (data == null) {
            return Service.START_STICKY;
        }

        String dexName            = data.get(0);
        String dexPath            = data.get(1);
        String optimizedDirectory = data.get(2);
        String libraryPath        = data.get(3);

        Log.d(LOGTAG, "DexClassLoaderProviderService -- onStartCommand(" + dexName + ")");

        // Parent classloader
        ClassLoader parent = this.getClassLoader();
//
//        DexClassLoaderProvider.createDexClassLoader(dexPath, optimizedDirectory, libraryPath, parent, this.getApplicationContext());

        return Service.START_STICKY;
    }

    // #03
    @Override
    public void onDestroy() {
        Log.d(LOGTAG, "DexClassLoaderProviderService -- onDestroy(" + ")");

        System.exit(0);
    }



}
