package com.mivik.gameclient;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.mivik.argon.widget.UI;

import java.util.List;

public class UserAdapter extends BaseAdapter {
	private List<User> data;

	public UserAdapter(List<User> list) {
		this.data = list;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = createView(parent.getContext());
		LinearLayoutCompat layout = (LinearLayoutCompat) convertView;
		AppCompatTextView Name = (AppCompatTextView) layout.getChildAt(0), Desc = (AppCompatTextView) layout.getChildAt(1);
		User user = data.get(position);
		Name.setText(user.name);
		Name.setTextColor(user.color);
		Desc.setText(user.desc);
		return convertView;
	}

	private View createView(Context cx) {
		LinearLayoutCompat Root = new LinearLayoutCompat(cx);
		Root.setOrientation(LinearLayoutCompat.VERTICAL);
		{
			final int d = UI.dp2px(5);
			Root.setPadding(d, d, d, d);
		}
		AppCompatTextView Name = new AppCompatTextView(cx);
		Name.setTextAppearance(cx, androidx.appcompat.R.style.TextAppearance_AppCompat_Medium);
		Name.setTextColor(Color.BLACK);
		Root.addView(Name, -1, -2);
		AppCompatTextView Desc = new AppCompatTextView(cx);
		Desc.setTextAppearance(cx, androidx.appcompat.R.style.TextAppearance_AppCompat_Small);
		Desc.setTextColor(Color.GRAY);
		{
			LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(-1, -2);
			para.topMargin = UI.dp2px(5);
			Root.addView(Desc, para);
		}
		return Root;
	}
}