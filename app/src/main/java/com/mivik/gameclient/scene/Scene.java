package com.mivik.gameclient.scene;

import android.app.Activity;
import android.content.res.Resources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.LinearLayoutCompat;

public abstract class Scene {
	protected Activity cx;

	public Scene(Activity cx) {
		this.cx = cx;
	}

	public abstract View getView();

	public static LinearLayoutCompat.LayoutParams createWeightParam(boolean vertical) {
		LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(0, 0);
		if (vertical) para.width = -1;
		else para.height = -1;
		para.weight = 1;
		return para;
	}

	public static int dp2px(float dp) {
		return (int) (0.5f + Resources.getSystem().getDisplayMetrics().density * dp);
	}

	public void loadMenu(Menu menu) {
		menu.clear();
	}

	public void onMenuItemSelected(MenuItem item) {
	}

	public boolean onBackPressed() {
		return false;
	}

	public void onSwitch(boolean in) {
	}
}