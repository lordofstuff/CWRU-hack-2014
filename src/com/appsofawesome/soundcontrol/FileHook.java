package com.appsofawesome.soundcontrol;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class FileHook implements IXposedHookLoadPackage{

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if(!lpparam.packageName.equals("android")) {
			return;
		}
		XposedBridge.log("We are in android");
		
		
		
		//findAndHookMethod("android.os.Environment", lpparam.classLoader, "getDirectory", String.class, String.class, hook);
		
	}
	
	XC_MethodHook hook = new XC_MethodHook() {
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			XposedBridge.log("We are inside the method!");
			param.args[1] = "/system/media/audio/ringtones/";
			//param.
		}

		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable {

		}
	};

}
