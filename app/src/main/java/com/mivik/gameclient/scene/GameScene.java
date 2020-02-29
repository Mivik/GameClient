package com.mivik.gameclient.scene;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.mivik.argon.widget.MButton;
import com.mivik.argon.widget.MEditText;
import com.mivik.argon.widget.UI;
import com.mivik.gameclient.*;

import java.util.ArrayList;
import java.util.Locale;

public class GameScene extends Scene {
	private Game game;

	private FrameLayout RRoot;
	private LinearLayoutCompat Root, RoomTop, GameTop;
	private ScrollView Container;
	private AppCompatTextView Content, TextObserverCount, GameContent;
	private MEditText MessageEdit;
	private MButton SendButton;
	private int PrepareState = Game.PREPARE_STATE_UNPREPARED;
	private final ArrayList<User> data = new ArrayList<>();
	private UserAdapter Adapter = new UserAdapter(data);
	private ListView PlayerList;
	private boolean inGame = false;
	private String gameTitle;
	private MenuItem MIPrepare, MIExit;
	private int observerCount = 0;
	private CountDownCard countDown;

	public GameScene(Activity cx, Game game) {
		super(cx);
		this.game = game;
		initialize();
	}

	public void setObserverCount(int count) {
		TextObserverCount.setText(String.format(cx.getString(R.string.observer_count), observerCount = count));
	}

	@Override
	public void onSwitch(boolean in) {
		if (in) {
			gameTitle = null;
			setPrepareState(Game.PREPARE_STATE_UNPREPARED);
			clear();
		} else {
			MIPrepare = MIExit = null;
			countDown.stop();
		}
	}

	@Override
	public void loadMenu(Menu menu) {
		menu.clear();
		if (inGame) {
			MIPrepare = null;
			MIExit = menu.add(0, 1, 0, R.string.exit_room);
		} else {
			MIPrepare = menu.add(0, 0, 0, "");
			setPrepareState(PrepareState);
			MIExit = menu.add(0, 1, 0, R.string.exit_room);
		}
		menu.add(0, 2, 0, R.string.player_list);
		menu.add(0, 3, 0, R.string.room_properties);
	}

	@Override
	public void onMenuItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				setPrepareState(game.prepare(PrepareState));
				break;
			case 1:
				onBackPressed();
				break;
			case 2:
				showPlayerList();
				break;
			case 3:
				showRoomProperties();
				break;
		}
	}

	public void showPlayerList() {
		ViewParent parent = PlayerList.getParent();
		if (parent != null && (parent instanceof ViewGroup)) ((ViewGroup) parent).removeView(PlayerList);
		new AlertDialog.Builder(cx).setTitle(R.string.player_list).setView(PlayerList).setPositiveButton(R.string.confirm, null).setCancelable(true).show();
	}

	public void showRoomProperties() {
		final String msg = String.format(Locale.getDefault(), cx.getString(R.string.room_properties_text), game.getRoomName(), gameTitle, data.size(), observerCount);
		new AlertDialog.Builder(cx).setTitle(R.string.room_properties).setMessage(msg).setPositiveButton(R.string.confirm, null).setCancelable(true).show();
	}

	public void onRoom() {
		RoomTop.setVisibility(View.VISIBLE);
		GameTop.setVisibility(View.GONE);
		inGame = false;
		cx.invalidateOptionsMenu();
	}

	public void onGame() {
		RoomTop.setVisibility(View.GONE);
		GameTop.setVisibility(View.VISIBLE);
		inGame = true;
		cx.invalidateOptionsMenu();
	}

	private void initialize() {
		RRoot = new FrameLayout(cx);
		Root = new LinearLayoutCompat(cx);
		Root.setOrientation(LinearLayoutCompat.VERTICAL);
		RoomTop = new LinearLayoutCompat(cx);
		RoomTop.setOrientation(LinearLayoutCompat.HORIZONTAL);
		Root.addView(RoomTop, -1, -2);
		GameTop = new LinearLayoutCompat(cx);
		GameTop.setOrientation(LinearLayoutCompat.VERTICAL);
		TextObserverCount = new AppCompatTextView(cx);
		TextObserverCount.setText(String.format(cx.getString(R.string.observer_count), 0));
		TextObserverCount.setGravity(Gravity.CENTER);
		GameTop.addView(TextObserverCount, -1, -2);
		Root.addView(GameTop, -1, -2);
		Container = new ScrollView(cx);
		Content = new AppCompatTextView(cx);
		Content.setTextColor(Color.GRAY);
		Container.setFillViewport(true);
		Container.addView(Content, -1, -1);
		{
			LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(-1, 0);
			para.weight = 1;
			final int d = dp2px(10);
			para.setMargins(d, d, d, d);
			Root.addView(Container, para);
		}
		GameContent = new AppCompatTextView(cx);
		GameContent.setTextAppearance(cx, androidx.appcompat.R.style.TextAppearance_AppCompat_Small);
		GameContent.setBackgroundColor(Color.WHITE);
		GameContent.setTextColor(UI.getPrimaryColor(cx, MButton.DEFAULT_BACKGROUND_COLOR));
		GameContent.setGravity(Gravity.CENTER);
		Root.addView(GameContent, -1, -2);
		setGameContent("");
		LinearLayoutCompat Bottom = new LinearLayoutCompat(cx);
		Bottom.setOrientation(LinearLayoutCompat.HORIZONTAL);
		MessageEdit = new MEditText(cx);
		MessageEdit.setColor(Color.WHITE);
		MessageEdit.setContentColor(UI.getPrimaryColor(cx, MButton.DEFAULT_BACKGROUND_COLOR));
		MessageEdit.setSingleLine();
		MessageEdit.setGravity(Gravity.START);
		{
			final int d = dp2px(10);
			MessageEdit.setPadding(d, d, d, d);
		}
		{
			LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(0, -2);
			para.weight = 0.8f;
			para.leftMargin = para.topMargin = para.bottomMargin = dp2px(10);
			para.rightMargin = para.leftMargin / 2;
			Bottom.addView(MessageEdit, para);
		}
		SendButton = new MButton(cx);
		SendButton.setText(R.string.msg_send);
		SendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String msg = MessageEdit.getEditableText().toString();
				if (msg.length() == 0) return;
				if (msg.trim().length() == 0) return;
				MessageEdit.getText().clear();
				game.chat(msg);
			}
		});
		{
			LinearLayoutCompat.LayoutParams para = new LinearLayoutCompat.LayoutParams(-2, -2);
			para.rightMargin = para.topMargin = para.bottomMargin = dp2px(10);
			para.leftMargin = para.rightMargin / 2;
			Bottom.addView(SendButton, para);
		}
		Root.addView(Bottom, -1, -2);
		PlayerList = new ListView(cx);
		PlayerList.setAdapter(Adapter);
		RRoot.addView(Root, -1, -1);
		countDown = new CountDownCard(cx);
		{
			FrameLayout.LayoutParams para = new FrameLayout.LayoutParams(-2, -2);
			para.rightMargin = para.topMargin = UI.dp2px(16);
			para.gravity = Gravity.RIGHT | Gravity.TOP;
			RRoot.addView(countDown, para);
		}
	}

	public void resetTimer(int sec, String color) {
		countDown.setColor(G.getColorByName(color, Color.WHITE));
		countDown.start(sec);
	}

	public String getGameContent() {
		return GameContent.getText().toString();
	}

	public void setGameContent(String gameContent) {
		GameContent.setText(gameContent);
		if (TextUtils.isEmpty(gameContent)) GameContent.setVisibility(View.GONE);
		else GameContent.setVisibility(View.VISIBLE);
	}

	public void setGameTitle(String title) {
		this.gameTitle = title;
	}

	public void setUserList(String[] list) {
		data.clear();
		for (String s : list) data.add(new User(s));
	}

	public void setPrepareEnabled(boolean flag) {
		if (MIPrepare != null) MIPrepare.setEnabled(flag);
	}

	public void setExitEnabled(boolean flag) {
		if (MIExit != null) MIExit.setEnabled(flag);
	}

	public void setPrepareState(int state) {
		if (MIPrepare == null) return;
		switch (PrepareState = state) {
			case Game.PREPARE_STATE_UNPREPARED:
				MIPrepare.setTitle(R.string.prepare);
				break;
			case Game.PREPARE_STATE_PREPARED:
				MIPrepare.setTitle(R.string.cancel_prepare);
				break;
			case Game.PREPARE_STATE_MASTER:
				MIPrepare.setTitle(R.string.start_game);
				break;
		}
	}

	public void setChatEnabled(boolean flag) {
		MessageEdit.setEnabled(flag);
		SendButton.setEnabled(flag);
	}

	public void clear() {
		Content.setText("");
	}

	public void append(String msg, boolean bold) {
		if (Content.getText().length() != 0) Content.append("\n");
		if (bold) {
			SpannableString str = new SpannableString(msg);
			str.setSpan(new BoldSpan(), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			Content.append(str);
		} else Content.append(msg);
		Container.post(new Runnable() {
			@Override
			public void run() {
				Container.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	public boolean onBackPressed() {
		String msg = null;
		if (game.isRoomMaster()) {
			if (data.size() > 1) msg = cx.getString(R.string.master_exit_confirm);
		} else if (game.getUserState() == UserState.GAME && (!gameTitle.contains("旁观")))
			msg = cx.getString(R.string.player_exit_confirm);
		if (msg != null) {
			new AlertDialog.Builder(cx).setTitle(R.string.note).setMessage(msg).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					game.leave();
				}
			}).setNegativeButton(R.string.cancel, null).setCancelable(true).show();
		} else game.leave();
		return true;
	}

	@Override
	public View getView() {
		return RRoot;
	}
}