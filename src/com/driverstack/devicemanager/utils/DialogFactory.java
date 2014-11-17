package com.driverstack.devicemanager.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogFactory {
	 

	public static AlertDialog.Builder getAlertDialogBuilder(Context context, String title, String message,
			String buttonText, DialogInterface.OnClickListener onClickListener) {

		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);

		dlgAlert.setMessage(message);
		dlgAlert.setTitle(title);
		dlgAlert.setPositiveButton(buttonText, onClickListener);
		dlgAlert.setCancelable(true);
		return dlgAlert;
	}

	 
}
