package com.mivik.gameclient;

import android.graphics.Color;
import androidx.core.util.ObjectsCompat;

public class User {
	public int color;
	public String name;
	public String desc;

	public User(String str) {
		String[] arr = Game.split(str, '/');
		color = G.getColorByName(arr[0], Color.BLACK);
		name = arr[1];
		desc = arr[2];
	}

	@Override
	public String toString() {
		return color + "/" + name + "/" + desc;
	}

	@Override
	public int hashCode() {
		return ObjectsCompat.hash(color, name, desc);
	}
}