package com.tencent.smtt.export.external;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.BaseDexClassLoader;

/**
 * Created by grasshuang on 2018/6/29.
 */

public class SpeedyDexClassLoader extends BaseDexClassLoader
{
    public SpeedyDexClassLoader(String dexPath, File optimizedDirectory,
                                String libraryPath, ClassLoader parent) {
        super(dexPath, null, libraryPath, parent);
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    public URL findResource(String name) {
        return super.findResource(name);
    }

    public Enumeration<URL> findResources(String name) {
        return super.findResources(name);
    }

    public synchronized Package getPackage(String name) {
        return super.getPackage(name);
    }

    public Package definePackage(String name, String specTitle, String specVersion, String specVendor,
                                 String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
        return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
    }

    public Package[] getPackages() {
        return super.getPackages();
    }

    public Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(className, resolve);
    }
}
