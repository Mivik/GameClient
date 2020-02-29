package com.mivik.gameclient.scene;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.mivik.argon.widget.MButton;
import com.mivik.gameclient.Game;
import com.mivik.gameclient.R;

import java.util.ArrayList;
import java.util.Arrays;

public class CenterScene extends Scene implements AdapterView.OnItemClickListener {
	private Game game;
	private ArrayList<String> data = new ArrayList<>();
	private ArrayAdapter<String> Adapter;
	private LinearLayoutCompat Root;
	private ListView RoomList;
	private MButton CreateButton;

	public CenterScene(Activity cx, Game game) {
		super(cx);
		this.game = game;
		initialize();
	}

	private void initialize() {
		Root = new LinearLayoutCompat(cx);
		Root.setOrientation(LinearLayoutCompat.VERTICAL);
		RoomList = new ListView(cx);
		Adapter = new ArrayAdapter<>(cx, android.R.layout.simple_list_item_1, data);
		RoomList.setAdapter(Adapter);
		RoomList.setOnItemClickListener(this);
		Root.addView(RoomList, createWeightParam(true));
		CreateButton = new MButton(cx);
		CreateButton.setText(R.string.create_room);
		CreateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.createRoom();
			}
		});
		{
			LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(-1, -2);
			para.leftMargin = para.rightMargin = para.bottomMargin = dp2px(20);
			para.bottomMargin = dp2px(10);
			Root.addView(CreateButton, para);
		}
	}

	@Override
	public void onSwitch(boolean in) {
		if (in) {
			data.clear();
			Adapter.notifyDataSetChanged();
		}
	}

	public void setRoomList(String[] arr) {
		data.clear();
		data.addAll(Arrays.asList(arr));
		Adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		game.joinRoom(position);
	}

	@Override
	public View getView() {
		return Root;
	}

	@Override
	public boolean onBackPressed() {
		data.clear();
		Adapter.notifyDataSetChanged();
		game.logout();
		return true;
	}
}