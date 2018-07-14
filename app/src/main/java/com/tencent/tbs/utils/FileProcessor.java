package com.tencent.tbs.utils;

import android.content.Context;
import android.system.Os;
import android.util.Log;

import com.tencent.smtt.export.external.SpeedyDexClassLoader;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by grasshuang on 2018/6/28.
 */

public class FileProcessor {

    final static int PROT_READ = 0x1;     /* Page can be read.  */
    final static int PROT_WRITE = 0x2;     /* Page can be written.  */
    final static int PROT_EXEC = 0x4;     /* Page can be executed.  */
    final static int PROT_NONE = 0x0;     /* Page can not be accessed.  */

    final static int MAP_SHARED = 0x01;  /* Share changes.  */
    final static int MAP_PRIVATE = 0x02;  /* Changes are private.  */

    final static int MAP_FIXED = 0x10;  /* Interpret addr exactly.  */
    final static int MAP_FILE = 0;
    final static int MAP_ANONYMOUS = 0x20;  /* Don't use a file.  */
    final static int MAP_ANON = MAP_ANONYMOUS;

    final static int MAP_DENYWRITE = 0x0800;  /* ETXTBSY */
    final static int MAP_FOOBAR = 0x0800;  /* ETXTBSY */


    final static String LOGTAG = "FileProcessor";


    public static void handleDexFile(String path) {

        String method_name = Thread.currentThread().getStackTrace()[2].getMethodName();

        SpeedyDexClassLoader classLoader = new SpeedyDexClassLoader(path,
                null,
                null,
                FileProcessor.class.getClass().getClassLoader());

        Class<?> tmp = null;
        try {
            tmp = classLoader.loadClass("com.tencent.tbs.sdk.extension.TbsSDKExtension");
        } catch (Throwable t) {
            t.printStackTrace();
        }

        Log.i(LOGTAG, "[" + method_name + "]  classLoader: " + classLoader
                + ", class: " + tmp);

    }


    public static void handleElfFile(FileDescriptor fd) {


        FileInputStream fileStream = new FileInputStream(fd);

        StringBuilder builder = new StringBuilder();

        byte[] buffer = new byte[1024];

        try {

            while (true) {

                int number_of_bytes = fileStream.read(buffer);

                if (number_of_bytes > 0) {

                    builder.append(new String(buffer));
                    builder.append("\n");

                    Log.i(LOGTAG, "reading file...");

                } else {
                    break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int address = 0;
        int byteCount = 1024;
        int prot = PROT_READ | PROT_EXEC;
        int offset = 0;

        try {
            long ret = Os.mmap(address, byteCount,
                    prot,
                    MAP_FIXED,
                    fd, offset);

            Log.i(LOGTAG, "Os.mmap result: " + ret);

        } catch (Throwable t) {
            Log.i(LOGTAG, "Os.mmap exception: " + Log.getStackTraceString(t));
        }

        Log.e(LOGTAG, "@tiny, content=" + builder.toString());
    }

    public static boolean copyFileTo(Context context, String input, String dest) {

        File file = new File(dest);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return copyFileTo(context, input, file);
    }


    public static boolean copyFileTo(Context context, String input, File dest) {

        Log.i(LOGTAG, "copyAssetsFileTo  input=" + input);

        boolean ret = false;
        InputStream ins = null;
        FileOutputStream fos = null;

        try {
            ins = new FileInputStream(input);
            fos = new FileOutputStream(dest);

            int len = -1;
            byte[] data = new byte[32 * 1024];
            while ((len = (ins.read(data))) != -1) {
                fos.write(data, 0, len);
            }

            fos.flush();
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }

            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ins = null;
            }
        }

        return ret;

    }
}
