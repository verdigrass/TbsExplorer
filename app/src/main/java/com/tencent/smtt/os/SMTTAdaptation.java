package com.tencent.smtt.os;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SMTTAdaptation {
	public final static int SDK_10 	= 1;
	public final static int SDK_11 	= 2;
	public final static int SDK_15 	= 3;
	public final static int SDK_16 	= 4;
	public final static int SDK_20 	= 5;
	public final static int SDK_201 = 6;
	public final static int SDK_21 	= 7;
	public final static int SDK_22 	= 8;
	public final static int SDK_232 = 9;
	public final static int SDK_234 = 10;
	public final static int SDK_30 	= 11;
	public final static int SDK_31 	= 12;
	public final static int SDK_32 	= 13;
	public final static int SDK_402 = 14;
	public final static int SDK_404 = 15;
	public final static int SDK_41 	= 16;
	public final static int SDK_42 	= 17;
	public final static int SDK_43  = 18;
	public final static int SDK_44  = 19;
	public final static int SDK_50  = 21;
	public final static int SDK_51  = 22;
	public final static int SDK_60  = 23;
	public final static int SDK_70  = 24;

	public static KeyCharacterMap getKeyCharacterMap(){
		if(Build.VERSION.SDK_INT < SDK_30){
			//适配android3.0以下的KeyCharacterMap
			return KeyCharacterMap.load(KeyCharacterMap.BUILT_IN_KEYBOARD);
		}
		else if(Build.VERSION.SDK_INT <= SDK_42){
			//适配android3.0到android4.2KeyCharacterMap
			return KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
		} 
    	
		return null;
	}
	
	public static InputMethodManager getInputMethodManager(Context context){
		InputMethodManager imm = null;
		int sdk = Build.VERSION.SDK_INT;
		/*if(sdk == SDK_42){
			imm = InputMethodManager.peekInstance();//为什么在4.2中找不到呢?
		}
		else*/{
	        imm = (InputMethodManager)
	        		context.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		
		return imm;
	}

	public static void setTranslationY(View view, float translationY) {
		if (view == null)
			return;

		if (android.os.Build.VERSION.SDK_INT < SMTTAdaptation.SDK_30) {
			Object top = view.getTag();
			if (top != null && top instanceof Integer) {
				view.offsetTopAndBottom(((Integer) top).intValue() + (int) translationY
						- view.getTop());
			}
		} else {
			view.setTranslationY(translationY);
		}
	}

	public static boolean invokeCanvasIsHardwareAccelerated(Canvas canvas) {
		if (Build.VERSION.SDK_INT >= SDK_30) {
			return canvas.isHardwareAccelerated();
		} else {
			return false;
		}
	}

	public static Bitmap getBitmap(Canvas canvas) {
		if (canvas == null) {
			return null;
		}

		try {
			Field field = Canvas.class.getDeclaredField("mBitmap");
			field.setAccessible(true);
			return (Bitmap) field.get(canvas);
		} catch (Throwable ignore) {
			ignore.printStackTrace();
			return null;
		}
	}

	private static boolean sHasGot = false;
	private static boolean sIsBuildForArch64 = false;

	public static boolean getIsBuildForArch64() {
		if (!sHasGot) {
			sIsBuildForArch64 = nativeGetIsBuildForArch64();
			sHasGot = true;
		}

		return sIsBuildForArch64;
	}

	public static String getBuildMessage() {
		String msg = "null";
		if (!sHasGot) {
			msg = nativeGetBuildMessage();
			sHasGot = true;
		}

		return msg;
	}

	private static native boolean nativeGetIsBuildForArch64();

	// for test @grass
	private static native String nativeGetBuildMessage();

	public static int getScreenWidth(Context context) {
		try {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			return wm.getDefaultDisplay().getWidth();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	private static Method sViewRootImplAttachFunctorMethod = null;
	private static Method sViewRootImplInvokeFunctorMethod = null;
	private static Method sViewRootImplDetachFunctorIMethod = null;
	private static Method sViewRootImplDetachFunctorJMethod = null;
	private static Method sHardwareCanvasCallDrawGLFunctionIMethod = null;
	private static Method sHardwareCanvasCallDrawGLFunctionJMethod = null;
	private static Method sHardwareCanvasCallDrawGLFunction2JMethod = null;
	private static Method sDisplayListCanvasCallDrawGLFunction2JMethod = null;
	private static Method sDisplayListCanvasDrawGLFunctor2Method = null;
	private static Method sBluetoothManagerGetAdapterMethod = null;
	private static Method sSurfaceTextureReleaseTexImageMethod = null;
	private static Method sViewIsAttachedToWindowMethod = null;
	private static Method sViewExecuteHardwareActionMethod = null;

	private static Method getMethod(int apiLevel, String className, String methodName,
                                    Class<?> types[]) {
		if (Build.VERSION.SDK_INT < apiLevel) {
			return null;
		}

		Method method = null;
		try {
			method = Class.forName(className).getMethod(methodName, types);
		} catch (NoSuchMethodException e) {
		} catch (ClassNotFoundException e) {
		}
		return method;
	}

	static {
		{
			Class<?> parameterTypes[] = { Integer.TYPE };
			sViewRootImplAttachFunctorMethod = getMethod(SDK_41, "android.view.ViewRootImpl",
					"attachFunctor", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Long.TYPE, Boolean.TYPE };
			sViewRootImplInvokeFunctorMethod = getMethod(SDK_50, "android.view.ViewRootImpl",
					"invokeFunctor", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Integer.TYPE };
			sViewRootImplDetachFunctorIMethod = getMethod(SDK_41, "android.view.ViewRootImpl",
					"detachFunctor", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Long.TYPE };
			sViewRootImplDetachFunctorJMethod = getMethod(SDK_50, "android.view.ViewRootImpl",
					"detachFunctor", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Integer.TYPE };
			sHardwareCanvasCallDrawGLFunctionIMethod = getMethod(SDK_30,
					"android.view.HardwareCanvas", "callDrawGLFunction", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Long.TYPE };
			sHardwareCanvasCallDrawGLFunctionJMethod = getMethod(SDK_50,
					"android.view.HardwareCanvas", "callDrawGLFunction", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Long.TYPE };
			sHardwareCanvasCallDrawGLFunction2JMethod = getMethod(SDK_51,
					"android.view.HardwareCanvas", "callDrawGLFunction2", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Long.TYPE };
			sDisplayListCanvasCallDrawGLFunction2JMethod = getMethod(SDK_51,
					"android.view.DisplayListCanvas", "callDrawGLFunction2", parameterTypes);
		}

		{
			Class<?> parameterTypes[] = { Long.TYPE, Runnable.class };
			sDisplayListCanvasDrawGLFunctor2Method = getMethod(SDK_70,
					"android.view.DisplayListCanvas", "drawGLFunctor2", parameterTypes);
		}

		{
			sBluetoothManagerGetAdapterMethod = getMethod(SDK_43,
					"android.bluetooth.BluetoothManager", "getAdapter", (Class<?>[]) null);
		}

		{
			sSurfaceTextureReleaseTexImageMethod = getMethod(SDK_44,
					"android.graphics.SurfaceTexture", "releaseTexImage", (Class<?>[]) null);
		}

		{
			sViewIsAttachedToWindowMethod = getMethod(SDK_44, "android.view.View",
					"isAttachedToWindow", (Class<?>[]) null);
		}

		{
			Class<?> parameterTypes[] = { Runnable.class };
			sViewExecuteHardwareActionMethod = getMethod(SDK_44, "android.view.View",
					"executeHardwareAction", parameterTypes);
		}
	}

	public static void invokeFunctor(Object viewRootImpl, long functor, boolean waitForCompletion) {
		if (Build.VERSION.SDK_INT >= SMTTAdaptation.SDK_50) {
			if (sViewRootImplInvokeFunctorMethod != null) {
				try {
					sViewRootImplInvokeFunctorMethod.invoke(viewRootImpl, functor,
							waitForCompletion);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (Build.VERSION.SDK_INT >= SMTTAdaptation.SDK_41) {
			if (sViewRootImplAttachFunctorMethod != null) {
				try {
					sViewRootImplAttachFunctorMethod.invoke(viewRootImpl, (int) functor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void detachFunctor(Object viewRootImpl, long functor) {
		if (sViewRootImplDetachFunctorIMethod != null) {
			try {
				sViewRootImplDetachFunctorIMethod.invoke(viewRootImpl, (int) functor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sViewRootImplDetachFunctorJMethod != null) {
			try {
				sViewRootImplDetachFunctorJMethod.invoke(viewRootImpl, functor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int callDrawGLFunction(Object canvas, long functor) {
		Object result = null;
		if (sHardwareCanvasCallDrawGLFunctionIMethod != null) {
			try {
				result = sHardwareCanvasCallDrawGLFunctionIMethod.invoke(canvas, (int) functor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sHardwareCanvasCallDrawGLFunctionJMethod != null) {
			try {
				result = sHardwareCanvasCallDrawGLFunctionJMethod.invoke(canvas, functor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sHardwareCanvasCallDrawGLFunction2JMethod != null) {
			try {
				result = sHardwareCanvasCallDrawGLFunction2JMethod.invoke(canvas, functor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sDisplayListCanvasCallDrawGLFunction2JMethod != null) {
			try {
				result = sDisplayListCanvasCallDrawGLFunction2JMethod.invoke(canvas, functor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (result instanceof Boolean) {
			boolean b = (Boolean) result;
			return b ? 0 : 2;
		} else if (result instanceof Integer) {
			return (Integer) result;
		}
		return 2;
	}

	public static void drawGLFunctor2(Object canvas, long functor, Runnable releasedRunnabl) {
		try {
			sDisplayListCanvasDrawGLFunctor2Method.invoke(canvas, functor, releasedRunnabl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean viewExecuteHardwareAction(View view, Runnable runnable) {
		if (sViewExecuteHardwareActionMethod == null)
			return false;
		try {
			return (Boolean) sViewExecuteHardwareActionMethod.invoke(view, runnable);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
