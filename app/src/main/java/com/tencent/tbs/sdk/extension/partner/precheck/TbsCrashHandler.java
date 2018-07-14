package com.tencent.tbs.sdk.extension.partner.precheck;

import java.io.File;

public class TbsCrashHandler {
	
	private static final String LOGTAG = "CrashHandler";
	
	public static final String LIBRARY_NAME = "libtbs_main.so";
	
	
	TbsCrashHandler( String libraryPath ) {						
		System.load(libraryPath + File.separator + LIBRARY_NAME);
	}

	/**
	 * @attention 
	 * 		  Remember to "#define CRASH_TEST" in tbs_crash_hander.cc before use this API
	 * @param index
	 */
    public native static void nativeStartCrashTest(int index);


}
