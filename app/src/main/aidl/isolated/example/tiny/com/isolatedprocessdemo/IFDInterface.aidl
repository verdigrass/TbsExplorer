// IFDInterface.aidl
package isolated.example.tiny.com.isolatedprocessdemo;

import android.os.ParcelFileDescriptor;

// Declare any non-default types here with import statements

interface IFDInterface {

    void onReceivedFD(in ParcelFileDescriptor fd);
    void onReceivedFD2(in ParcelFileDescriptor fd, in String path);
}
