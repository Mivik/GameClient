package com.mivik.gameclient;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

public class LoginDialog extends AlertDialog {
	private AppCompatEditText NameEdit;

	public LoginDialog(Context cx) {
		super(cx);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setTitle(cx.getString(R.string.login));
		NameEdit = new AppCompatEditText(cx);
		NameEdit.setHint(R.string.username);
		setButton(BUTTON_POSITIVE, cx.getString(R.string.confirm), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}

	public Button getLoginBtuton() {
		return getButton(BUTTON_POSITIVE);
	}
}
