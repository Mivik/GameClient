package com.mivik.gameclient;

import android.content.Context;
import android.content.SharedPreferences;

public class G {
	private static SharedPreferences S;

	public static String _GameHost;
	public static int _GamePort;
	public static String _UserName;

	static void initialize(Context cx) {
		S = cx.getSharedPreferences("config", Context.MODE_PRIVATE);
		_GameHost = S.getString("GameHost", "s1.dhwpcs.com");
		_GamePort = S.getInt("GamePort", 11033);
		_UserName = S.getString("UserName", "");
	}

	public static void setGameHost(String host) {
		S.edit().putString("GameHost", _GameHost = host).apply();
	}

	public static void setGamePort(int port) {
		S.edit().putInt("GamePort", _GamePort = port).apply();
	}

	public static void setUserName(String name) {
		S.edit().putString("UserName", _UserName = name).apply();
	}

	public static int getColorByName(String color, int def) {
		final int[] COLORS = {0xFF82FF82, 0xFFFF8282, 0xFF8282FF};
		try {
			int ind = Integer.parseInt(color);
			if (ind < COLORS.length) return COLORS[ind];
		} catch (Throwable t) {
		}
		switch (color) {
			case "Orange":
				return 0xFFFF8000;
			case "Red":
				return 0xFFFF0000;
			case "Blue":
				return 0xFF0080FF;
		}
		return def;
	}
}