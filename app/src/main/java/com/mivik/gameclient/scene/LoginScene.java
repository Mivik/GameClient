package com.mivik.gameclient.scene;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.mivik.argon.widget.MButton;
import com.mivik.argon.widget.MEditText;
import com.mivik.argon.widget.UI;
import com.mivik.gameclient.G;
import com.mivik.gameclient.Game;
import com.mivik.gameclient.Logger;
import com.mivik.gameclient.R;

public class LoginScene extends Scene {
	private static final Logger L = Logger.getLogger(LoginScene.class);

	private Game game;

	private LinearLayoutCompat Root;
	private MEditText NameEdit;
	private MButton LoginButton;

	public LoginScene(Activity cx, Game game) {
		super(cx);
		this.game = game;
		initialize();
	}

	private void initialize() {
		Root = new LinearLayoutCompat(cx);
		Root.setOrientation(LinearLayoutCompat.VERTICAL);
		Root.setGravity(Gravity.CENTER);

		NameEdit = new MEditText(cx);
		NameEdit.setHint(R.string.username);
		NameEdit.setText(G._UserName);
		NameEdit.setGravity(Gravity.CENTER);
		NameEdit.setColor(Color.WHITE);
		NameEdit.setContentColor(UI.getPrimaryColor(cx, MButton.DEFAULT_BACKGROUND_COLOR));
		{
			LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(-1, -2);
			para.leftMargin = para.rightMargin = dp2px(60);
			para.bottomMargin = dp2px(10);
			Root.addView(NameEdit, para);
		}
		LoginButton = new MButton(cx);
		LoginButton.setText(R.string.login);
		LoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = NameEdit.getEditableText().toString();
				if (name.length() == 0) {
					Toast.makeText(cx, R.string.name_cannot_be_empty, Toast.LENGTH_SHORT).show();
					return;
				}
				G.setUserName(name);
				game.login(name);
			}
		});
		Root.addView(LoginButton, -2, -2);
	}

	@Override
	public void loadMenu(Menu menu) {
		menu.clear();
		menu.add(0, 0, 0, R.string.server_setting);
	}

	@Override
	public void onMenuItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				showServerSetting();
				break;
		}
	}

	private void showServerSetting() {
		LinearLayoutCompat root = new LinearLayoutCompat(cx);
		root.setOrientation(LinearLayoutCompat.VERTICAL);
		final AppCompatEditText edHost = new AppCompatEditText(cx);
		edHost.setText(G._GameHost);
		edHost.setHint(R.string.server_host);
		root.addView(edHost, -1, -2);
		final AppCompatEditText edPort = new AppCompatEditText(cx);
		edPort.setText(Integer.toString(G._GamePort));
		edPort.setHint(R.string.server_port);
		edPort.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		root.addView(edPort, -1, -2);
		new AlertDialog.Builder(cx).setTitle(R.string.server_setting).setView(root).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String host = edHost.getEditableText().toString();
				if (host.length() == 0) {
					edHost.setHint(R.string.cannot_be_empty);
					UI.preventDismiss((AlertDialog) dialog);
					return;
				}
				final String portString = edPort.getEditableText().toString();
				if (portString.length() == 0) {
					edPort.setHint(R.string.cannot_be_empty);
					UI.preventDismiss((AlertDialog) dialog);
					return;
				}
				int port;
				try {
					port = Integer.parseInt(portString);
				} catch (Throwable t) {
					L.error("Failed to parse port", t);
					edPort.setHint(R.string.input_illegal);
					UI.preventDismiss((AlertDialog) dialog);
					return;
				}
				UI.forceDismiss((AlertDialog) dialog);
				G.setGameHost(host);
				G.setGamePort(port);
				game.setAddress(host, port);
			}
		}).setNegativeButton(R.string.cancel, null).setCancelable(true).show();
	}

	public void setLabelLogin(String content) {
		NameEdit.setError(content);
	}

	public void setLoginEnabled(boolean flag) {
		LoginButton.setEnabled(flag);
	}

	public boolean isLoginEnabled() {
		return LoginButton.isEnabled();
	}

	@Override
	public View getView() {
		return Root;
	}
}