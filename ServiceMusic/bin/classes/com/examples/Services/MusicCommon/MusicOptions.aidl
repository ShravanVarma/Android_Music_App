package com.examples.Services.MusicCommon;

interface MusicOptions {
	void play1(int a);
	void pause();
	void resume();
	void stop();
	void stopPlayer();
	int getStatus();
	List<String> retrieveList();
	void deleteData();
	boolean isPlaying();
}