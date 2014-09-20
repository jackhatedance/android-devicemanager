package com.deviceyun.devicemanager.utils;

import java.util.Locale;

import android.content.Context;

public class Utils {

	public static Locale getLocale(Context context){
		return context.getResources().getConfiguration().locale;
	}
}
