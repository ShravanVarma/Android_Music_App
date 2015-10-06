package com.examples.Services.MusicService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.examples.Services.MusicCommon.MusicOptions;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.format.Time;

public class MusicOptionsImpl extends Service {
	
	MediaPlayer mPlayer = new MediaPlayer();
	int length;
	public int status;
	public int currentState = 100;
	DataHandler handler;
	List<String> dbRecords = null;
		
	//Implement the stub for this object
	private final MusicOptions.Stub mBinder = new MusicOptions.Stub(){
		
			public String getDate(){
				//create a calendar instance
				Calendar calendar = Calendar.getInstance();
				int thisYear = calendar.get(Calendar.YEAR);
				int thisMonth = calendar.get(Calendar.MONTH)+1;
				int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
				//put the date in Month/Day/Year format
				String date = Integer.toString(thisMonth)+"/"+Integer.toString(thisDay)+"/"+Integer.toString(thisYear);
				return date;
			}
			
			public String getTime(){
				//get the calendar instance
				Calendar c = Calendar.getInstance();
				//create an instance for HH:MM:SS format
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				String time = sdf.format(c.getTime());
				return time;
			}
			
			public String getCurrState(int num){
				String str = new String();
				//Get the current state based on the num value set
				if(num == 0){
					str = "Playing song "+status; 
				}else if(num == 1){
					str = "Playing song "+status;
				}else if(num == 2){
					str = "Playing song "+status;
				}else if(num == 3){
					str = "Paused while playing song "+status;
				}else if(num == 4){
					str = "Resumed the song "+status;
				}else if(num == 5){
					str = "Stopped the song "+status;
				}else{
					str = "First operation"; 
				}
				return str;
			}
			@Override
			public void play1(int a) throws RemoteException {
				if(a==1){
					//Stop the player if its already playing
					if(mPlayer.isPlaying()){
						mPlayer.stop();
					}
					mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ohohoh);
					
					handler = new DataHandler(getBaseContext());
					handler.open();
					//insert the data into the database
					handler.insertData(getDate(), getTime(),"Play","1", getCurrState(currentState));
					handler.close();
					//set the statuses
					currentState = 0;
					status =1;
				}
				else if(a==2){
					if(mPlayer.isPlaying()){
						mPlayer.stop();
					}
					mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.iloveyou);	
					
					handler = new DataHandler(getBaseContext());
					handler.open();
					//insert the data 
					handler.insertData(getDate(), getTime(),"Play","2", getCurrState(currentState));
					handler.close();
					//set the statuses
					currentState = 1;
					status =2;
					
				}
				else{
					if(mPlayer.isPlaying()){
						mPlayer.stop();
					}
					mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.arrayhermajesty);
					
					handler = new DataHandler(getBaseContext());
					handler.open();
					//insert the data
					handler.insertData(getDate(), getTime(),"Play","3", getCurrState(currentState));
					handler.close();
					//set the statuses
					currentState = 2;
					status =3;
				}
			
				
				mPlayer.start();
				mPlayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						//On song complete, send a broadcast across the device
						Intent intent = new Intent();
						intent.setAction("com.examples.Services.Song_Done");
						sendBroadcast(intent);
						
					}
				});
				
			}

			@Override
			public void pause() throws RemoteException {
				//Pause the song
				mPlayer.pause();
				//get the current position where the song is stopped
				length = mPlayer.getCurrentPosition();
				handler = new DataHandler(getBaseContext());
				handler.open();
				//insert the data
				long id = handler.insertData(getDate(), getTime(),"Pause",Integer.toString(status), getCurrState(currentState));
				handler.close();
				//set the status
				currentState = 3;
			}

			@Override
			public void resume() throws RemoteException {
				//go to the duration where the song is stopped
				mPlayer.seekTo(length);
				//start the song from the duration set
				mPlayer.start();
				handler = new DataHandler(getBaseContext());
				handler.open();
				//insert the data
				handler.insertData(getDate(), getTime(),"Resume",Integer.toString(status), getCurrState(currentState));
				handler.close();
				//set the status
				currentState = 4;
			}

			@Override
			public void stop() throws RemoteException {
				//stop the player
				mPlayer.stop();
				handler = new DataHandler(getBaseContext());
				handler.open();
				//insert the data
				handler.insertData(getDate(), getTime(),"Stop",Integer.toString(status), getCurrState(currentState));
				handler.close();
				//set the status
				currentState = 5;
			}

			@Override
			public void stopPlayer() throws RemoteException {
				//release the media player
				mPlayer.release();
			}

			@Override
			public int getStatus() throws RemoteException {
				//get the status as which song is being played
				return status;
			}

			@Override
			public List<String> retrieveList() throws RemoteException {
				//create a new arraylist
				dbRecords = new ArrayList<String>();
				handler = new DataHandler(getBaseContext());
				handler.open();
				//get the data through the cursor until there is a next row
				Cursor c = handler.returnData();
				if(c.moveToFirst()){
					do{
						dbRecords.add("Date:"+c.getString(0)+" "+"Time:"+c.getString(1)+" "+"Req Kind:"+c.getString(2)+" "+"Clip No:"+c.getString(3)+" "+"Current State:"+c.getString(4));
					}while(c.moveToNext());
					
				}
				handler.close();
				//return the records in a List<String>
				return dbRecords;
			}

			@Override
			public void deleteData() throws RemoteException {
				handler = new DataHandler(getBaseContext());
				handler.open();
				//delete all the data
				handler.deleteData();
				handler.close();
				
			}

			@Override
			public boolean isPlaying() throws RemoteException {
				// check if some song is playing
				return mPlayer.isPlaying();
			}


			
		};

	@Override
	public IBinder onBind(Intent intent) {
		//return the binder object
		return mBinder;
	}
	


}
