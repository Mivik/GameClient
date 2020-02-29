package com.mivik.gameclient;

public interface GameImpl {
	void clearChats();

	void onStartGame();

	void showChat(String msg, boolean bold);

	void alert(String msg);

	void setLabelLogin(String content);

	void onSelect(String a, String b);

	void setRoomList(String[] list);

	void setUserList(String[] list);

	void setChatEnabled(boolean flag);

	void setObserverCount(int cnt);

	void onJoinRoom(String name); // Lambda 3

	void setGameContent(String content);

	void setGameTitle(String title);

	void resetTimer(int time, String color);

	void setTitle(String title);

	void setLoginEnabled(boolean flag);

	boolean isLoginEnabled();

	void setExitRoomEnabled(boolean flag);

	void setPrepareEnabled(boolean flag);
}