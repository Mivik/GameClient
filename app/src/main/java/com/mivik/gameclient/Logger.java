package com.mivik.gameclient;

import android.util.Log;

public class Logger {
	public static final String T = "GameCenter";

	private String pre;

	private Logger() {
	}

	public static Logger getLogger(Class<?> clz) {
		Logger ret = new Logger();
		ret.pre = "[" + clz.getName() + "] ";
		return ret;
	}

	public void verbose(Object msg) {
		Log.v(T, pre + msg.toString());
	}

	public void verbose(Object msg, Throwable t) {
		Log.v(T, pre + msg.toString(), t);
	}

	public void info(Object msg) {
		Log.i(T, pre + msg.toString());
	}

	public void info(Object msg, Throwable t) {
		Log.i(T, pre + msg.toString(), t);
	}

	public void warn(Object msg) {
		Log.w(T, pre + msg.toString());
	}

	public void warn(Object msg, Throwable t) {
		Log.w(T, pre + msg.toString(), t);
	}

	public void error(Object msg) {
		Log.e(T, pre + msg.toString());
	}

	public void error(Object msg, Throwable t) {
		Log.e(T, pre + msg.toString(), t);
	}

	public void fatal(Object msg) {
		Log.wtf(T, pre + msg.toString());
	}

	public void fatal(Object msg, Throwable t) {
		Log.wtf(T, pre + msg.toString(), t);
	}
}
