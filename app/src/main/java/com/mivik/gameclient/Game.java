package com.mivik.gameclient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class Game implements SocketManager.Receiver {
	private static final Logger L = Logger.getLogger(Game.class);

	public static final int PREPARE_STATE_UNPREPARED = 0, PREPARE_STATE_PREPARED = 1, PREPARE_STATE_MASTER = 2;

	public static final char SPLITTER = '¨';
	public static final int HEARTBEAT_TIMEOUT = 5;
	public static final int VERSION = 10;

	private SocketManager manager;
	private UserState state = UserState.OFFLINE;
	private GameImpl impl;
	private boolean isMaster;
	private int heartbeatCount = 0;
	private final byte[] heartbeatLock = new byte[0];
	private String userName, roomName;
	private StateUpdateListener listener;
	private String id;

	public Game(String host, int port) {
		manager = new SocketManager(host, port);
		manager.setReceiver(this);
	}

	public Game(InetAddress addr, int port) {
		manager = new SocketManager(addr, port);
		manager.setReceiver(this);
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setAddress(String host, int port) {
		manager.setAddress(host, port);
	}

	public void setAddress(InetAddress addr, int port) {
		manager.setAddress(addr, port);
	}

	public UserState getUserState() {
		return state;
	}

	public void leave() {
		send("Leave");
	}

	public String getUserName() {
		return userName;
	}

	public String getRoomName() {
		return roomName;
	}

	public StateUpdateListener getStateUpdateListener() {
		return listener;
	}

	public void setStateUpdateListener(StateUpdateListener listener) {
		if (this.listener == listener) return;
		if ((this.listener = listener) != null) listener.onStateUpdate(state);
	}

	public void setGameImplementation(GameImpl impl) {
		this.impl = impl;
	}

	public GameImpl getGameImplementation() {
		return impl;
	}

	public boolean isRoomMaster() {
		return isMaster;
	}

	public void send(String msg) {
		send(msg, true);
	}

	public void send(String msg, boolean showErr) {
		if (!msg.startsWith("Beat")) L.info("Send: " + msg);
		manager.send(msg + SPLITTER, showErr);
	}

	public static String[] split(String msg, char c) {
		int st = 0, ind;
		ArrayList<String> ret = new ArrayList<>();
		while ((ind = msg.indexOf(c, st)) != -1) {
			ret.add(msg.substring(st, ind));
			st = ind + 1;
		}
		ret.add(msg.substring(st));
		return ret.toArray(new String[0]);
	}

	public static String[] trim(String[] arr) {
		ArrayList<String> tmp = new ArrayList<>(arr.length);
		for (int i = 0; i < arr.length; i++) {
			String t = arr[i].trim();
			if (!t.isEmpty()) tmp.add(t);
		}
		return tmp.toArray(new String[0]);
	}

	@Override
	public boolean shouldReceive() {
		return state != UserState.OFFLINE;
	}

	@Override
	public void onReceive(String msg) {
		String[] parsed = split(msg, SPLITTER);
		for (int i = 0; i < parsed.length; i++) if (!parsed[i].isEmpty()) execute(parsed[i]);
	}

	public void execute(String cmd) {
		if (cmd.isEmpty()) return;
		if (!cmd.startsWith("Beat")) L.info("Exec: " + cmd);
		int ind = cmd.indexOf('|');
		if (ind == -1) return;
		String pre = cmd.substring(0, ind);
		String after = cmd.substring(ind + 1);
		String[] arg = split(after, '|');
		switch (pre) {
			case "Clear":
				impl.clearChats();
				break;
			case "Start":
				if (!(isMaster && state == UserState.ROOM)) break;
				impl.onStartGame();
				break;
			case "Chat":
				if (arg.length == 1) impl.showChat(arg[0], false);
				else impl.showChat(arg[0], arg[1].charAt(0) == '1');
				break;
			case "Msgbox":
				impl.alert(after);
				break;
			case "Exit":
				impl.setLabelLogin(after);
				logout();
				break;
			case "Select":
				if (after.isEmpty()) break;
				impl.onSelect(arg[0], arg[1]);
				break;
			case "Center": {
				setState(UserState.CENTER);
				impl.setRoomList(trim(arg));
				break;
			}
			case "Chatable":
				impl.setChatEnabled(after.charAt(0) == '1');
				break;
			case "List":
				impl.setUserList(trim(arg));
				break;
			case "Observe":
				impl.setObserverCount(Integer.parseInt(arg[0]));
				break;
			case "Room":
				setState(UserState.ROOM);
				roomName = arg[0];
				impl.onJoinRoom(arg[0]);
				break;
			case "Content":
				impl.setGameContent(after);
				break;
			case "Game":
				setState(UserState.GAME);
				impl.setGameTitle(arg[0]);
				break;
			case "Timer":
				impl.resetTimer(Integer.parseInt(arg[0]), arg[1]);
				break;
			case "Beat":
				send("Beat|" + after, false);
				synchronized (heartbeatLock) {
					heartbeatCount = 0;
				}
				break;
		}
	}

	public void select(int ind) {
		send("Select|" + ind);
	}

	public int prepare(int cur) {
		impl.setPrepareEnabled(false);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					L.error("Failed to sleep", e);
				}
				impl.setPrepareEnabled(true);
			}
		}.start();
		switch (cur) {
			case PREPARE_STATE_UNPREPARED:
				send("Prepare|True");
				impl.setExitRoomEnabled(false);
				return PREPARE_STATE_PREPARED;
			case PREPARE_STATE_PREPARED:
				send("Prepare|False");
				impl.setExitRoomEnabled(true);
				return PREPARE_STATE_UNPREPARED;
			case PREPARE_STATE_MASTER:
				send("Start");
				return PREPARE_STATE_MASTER;
		}
		return -1;
	}

	public void chat(String msg) {
		send("Chat|" + msg.replace(SPLITTER, '-').replace('|', '/').replace("[\\u0000-\\u001F\\u007F-\\u00A0]", ""));
	}

	private void setState(UserState state) {
		this.state = state;
		if (listener != null) listener.onStateUpdate(state);
	}

	public void login(final String name) {
		if (state != UserState.OFFLINE) return;
		if (!impl.isLoginEnabled()) return;
		impl.setLoginEnabled(false);
		userName = name;
		new Thread() {
			@Override
			public void run() {
				try {
					manager.connect();
					send("Login|" + VERSION + '|' + userName.replace(SPLITTER, '-').replace('|', '/') + '|' + id);
					byte[] buf = new byte[1025];
					int read = manager.getInputStream().read(buf);
					String str = new String(buf, 0, read, manager.getCharset());
					if (str.contains("Center")) {
						onReceive(str);
						heartbeatCount = 0;
						startHeartbeat();
						manager.startReceiving();
						impl.setTitle("Game Center - " + userName);
					} else if (str.startsWith("Exit|")) onReceive(str);
					else onReceive("Exit|未知信息：" + str.replace("Error|", ""));
				} catch (IOException e) {
					execute("Exit|" + e.getMessage());
				} finally {
					impl.setLoginEnabled(true);
				}
			}
		}.start();
	}

	public void createRoom() {
		isMaster = true;
		send("Create");
		impl.setExitRoomEnabled(true);
	}

	public void joinRoom(int ind) {
		isMaster = false;
		send("Join|" + ind);
		impl.setExitRoomEnabled(true);
	}

	private void startHeartbeat() {
		new Thread() {
			@Override
			public void run() {
				while (state != UserState.OFFLINE) {
					synchronized (heartbeatLock) {
						if (++heartbeatCount >= HEARTBEAT_TIMEOUT) execute("Exit|连接超时");
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						L.error("Failed to sleep", e);
					}
				}
			}
		}.start();
	}

	public void logout() {
		if (state == UserState.OFFLINE) return;
		int step = 0;
		out:
		while (true) {
			try {
				switch (step) {
					case 0:
						impl.setTitle("GameClient");
					case 1:
						send("Exit", false);
					case 2:
						setState(UserState.OFFLINE);
						break out;
				}
			} catch (Throwable t) {
				L.error("Failed to exit", t);
			}
		}
	}

	public interface StateUpdateListener {
		void onStateUpdate(UserState state);
	}
}