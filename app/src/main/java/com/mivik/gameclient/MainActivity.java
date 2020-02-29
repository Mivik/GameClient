package com.mivik.gameclient;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.mivik.gameclient.scene.CenterScene;
import com.mivik.gameclient.scene.GameScene;
import com.mivik.gameclient.scene.LoginScene;
import com.mivik.gameclient.scene.Scene;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GameImpl, Game.StateUpdateListener {
	private static final Logger L = Logger.getLogger(MainActivity.class);

	private Game game;
	private Handler handler = new Handler(Looper.getMainLooper());
	private LoginScene loginScene;
	private CenterScene centerScene;
	private GameScene gameScene;
	private Scene currentScene;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		game = new Game(G._GameHost, G._GamePort);
		game.setGameImplementation(this);
		game.setStateUpdateListener(this);
		game.setID(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
		loginScene = new LoginScene(this, game);
		centerScene = new CenterScene(this, game);
		gameScene = new GameScene(this, game);
		setScene(loginScene);
	}

	public void setScene(Scene scene) {
		if (currentScene == scene) return;
		if (currentScene != null) currentScene.onSwitch(false);
		currentScene = scene;
		scene.onSwitch(true);
		setContentView(scene.getView());
		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (currentScene == null) return super.onPrepareOptionsMenu(menu);
		currentScene.loadMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (currentScene == null) return super.onOptionsItemSelected(item);
		currentScene.onMenuItemSelected(item);
		return true;
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void clearChats() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.clear();
			}
		});
	}

	@Override
	public void onSelect(final String a, final String b) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(MainActivity.this).setTitle(R.string.note).setMessage(R.string.select_one).setPositiveButton(b, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						game.select(1);
					}
				}).setNegativeButton(a, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						game.select(0);
					}
				}).setCancelable(false).show();
			}
		});
	}

	@Override
	public void onStartGame() {

	}

	@Override
	public void showChat(final String msg, final boolean bold) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.append(msg, bold);
			}
		});
	}

	@Override
	public void alert(String msg) {

	}

	@Override
	public void setLabelLogin(final String content) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				loginScene.setLabelLogin(content);
			}
		});
	}

	@Override
	public void setRoomList(final String[] list) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				centerScene.setRoomList(list);
			}
		});
	}

	@Override
	public void setUserList(final String[] list) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.setUserList(list);
			}
		});
	}

	@Override
	public void setChatEnabled(final boolean flag) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.setChatEnabled(flag);
			}
		});
	}

	@Override
	public void setObserverCount(final int cnt) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.setObserverCount(cnt);
			}
		});
	}

	@Override
	public void onJoinRoom(final String name) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				setTitle("GameCenter - " + name);
			}
		});
	}

	@Override
	public void setGameContent(final String content) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i("Unqwe", "content: " + content);
				gameScene.setGameContent(content);
			}
		});
	}

	@Override
	public void setGameTitle(final String title) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i("Unqwe", "title: " + title);
				gameScene.setGameTitle(title);
			}
		});
	}

	@Override
	public void resetTimer(final int time, final String color) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.resetTimer(time, color);
			}
		});
	}

	@Override
	public void setTitle(final String title) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				MainActivity.super.setTitle(title);
			}
		});
	}

	@Override
	public void setLoginEnabled(final boolean flag) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				loginScene.setLoginEnabled(flag);
			}
		});
	}

	@Override
	public boolean isLoginEnabled() {
		return loginScene.isLoginEnabled();
	}

	@Override
	public void setExitRoomEnabled(final boolean flag) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.setExitEnabled(flag);
			}
		});
	}

	@Override
	public void setPrepareEnabled(final boolean flag) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				gameScene.setPrepareEnabled(flag);
			}
		});
	}

	@Override
	public void onStateUpdate(final UserState state) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				switch (state) {
					case OFFLINE:
						setScene(loginScene);
						break;
					case CENTER:
						setScene(centerScene);
						break;
					case ROOM:
						setScene(gameScene);
						gameScene.onRoom();
						break;
					case GAME:
						gameScene.onGame();
						break;
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (currentScene == null || (!currentScene.onBackPressed())) super.onBackPressed();
	}
}