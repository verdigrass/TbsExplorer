package isolated.example.tiny.com.isolatedprocessdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.system.Os;
import android.util.Log;

import com.tencent.smtt.export.external.SpeedyDexClassLoader;
import com.tencent.tbs.utils.FileProcessor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import dalvik.system.DexClassLoader;

public class ISolatedService extends Service {

    final String LOGTAG = "ISolatedService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    IBinder binder = new IFDInterface.Stub() {

        @Override
        public void onReceivedFD2(ParcelFileDescriptor pfd, String path) throws RemoteException {

            String method_name = Thread.currentThread().getStackTrace()[2].getMethodName();

            boolean exists = new File(path).exists();

            Log.i(LOGTAG, "[" + method_name + "]  pfd=" + pfd + ", path: " + path
                    + ", exists: " + exists);

            if (exists) {
                FileProcessor.handleDexFile(path);
            }
        }

        @Override
        public void onReceivedFD(ParcelFileDescriptor pfd) throws RemoteException {
            Log.e(LOGTAG, "@tiny,  pfd=" + pfd);

            FileDescriptor fd = pfd.getFileDescriptor();

            Log.e(LOGTAG, "@tiny,  file: " + fd);

            FileProcessor.handleElfFile(fd);
        }
    };


    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(LOGTAG, "@tiny, onStart");
        super.onStart(intent, startId);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LOGTAG, "@tiny, onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


}
