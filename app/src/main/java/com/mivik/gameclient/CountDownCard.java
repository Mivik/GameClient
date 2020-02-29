package com.mivik.gameclient;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import com.mivik.argon.widget.MButton;
import com.mivik.argon.widget.UI;

public class CountDownCard extends MButton {
	private int seconds = -1;
	private Handler handler;
	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			synchronized (this) {
				--seconds;
			}
			update();
			synchronized (this) {
				if (seconds != -1) handler.postDelayed(this, 1000);
			}
		}
	};
	private boolean running = false;

	public CountDownCard(Context cx) {
		super(cx);
		setFocusable(false);
		setFocusableInTouchMode(false);
		setColor(UI.getPrimaryColor(cx, DEFAULT_BACKGROUND_COLOR));
		setContentColor(Color.WHITE);
		setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		handler = new Handler(Looper.getMainLooper());
		update();
	}

	private void update() {
		synchronized (this) {
			if (seconds == -1) setVisibility(View.GONE);
			else {
				setVisibility(View.VISIBLE);
				setText(Integer.toString(seconds));
			}
		}
	}

	public void start(int sec) {
		synchronized (this) {
			this.seconds = sec;
			if (running) return;
			running = true;
		}
		if (sec == -1) return;
		handler.postDelayed(runnable, 1000);
	}

	public void stop() {
		handler.removeCallbacks(runnable);
		synchronized (this) {
			this.seconds = -1;
		}
	}
}