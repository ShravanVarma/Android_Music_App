package com.examples.Services.MusicClient;

import java.util.ArrayList;
import java.util.List;

import com.examples.Services.MusicCommon.MusicOptions;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private MusicOptions mMusicOptionsService;
	private boolean mIsBound;
	protected static final String TAG = "KeyServiceUser";
	boolean bind;
	List<String> dbRecords = new ArrayList<String>();
	IntentFilter bFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Get the button instances
		final Button play1Button = (Button) findViewById(R.id.play1);
		final Button play2Button = (Button) findViewById(R.id.play2);
		final Button play3Button = (Button) findViewById(R.id.play3);
		final Button pauseButton = (Button) findViewById(R.id.pause);
		final Button listButton = (Button) findViewById(R.id.list);
		final Button stopButton = (Button) findViewById(R.id.stop);
		//Set the text of Pause/Resume button
		pauseButton.setTag(0);
		pauseButton.setText("Pause");
		
		try{
		//Intent to bind the service
		Intent intent = new Intent(MusicOptions.class.getName());
		//Set the package name and class name to the intent
		intent.setClassName("com.examples.Services.MusicService", "com.examples.Services.MusicService.MusicOptionsImpl");
		//bind the service to the activity
		bind = bindService(intent,this.mConnection, Context.BIND_AUTO_CREATE);
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
		//Create the intent filter for broadcast receiver
		bFilter = new IntentFilter("com.examples.Services.Song_Done");
		//Register the receiver
		registerReceiver(MyReceiver, bFilter);
		
		listButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(mIsBound){
						//Retrieve all the SQLite records 
						dbRecords = mMusicOptionsService.retrieveList();
						//create the bundle
						Bundle bundle = new Bundle();
						//Put the retrieved records in the bundle
						bundle.putStringArrayList("records", (ArrayList<String>)dbRecords);
						//create an intent for second activity
						Intent i = new Intent(getApplicationContext(),Transactions.class);
						//pass the bundle to the intent
						i.putExtras(bundle);
						//start the activity
						startActivity(i);
					}
						
				}catch (Exception e){
					Log.e(TAG, e.toString());
				}
				
			}
		});
		
		play1Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(mIsBound){
						//Set the pause button text to "Pause" when play is clicked
						pauseButton.setTag(0);
						pauseButton.setText("Pause");
						//Call the service to play the song 1
						mMusicOptionsService.play1(1);
					}
						
				}catch (Exception e){
					Log.e(TAG, e.toString());
				}
				
			}
		});
		
		play2Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(mIsBound){
						//Set the pause button text to "Pause" when play is clicked
						pauseButton.setTag(0);
						pauseButton.setText("Pause");
						//Call the service to play song 2
						mMusicOptionsService.play1(2);		
					}
						
				}catch (Exception e){
					Log.e(TAG, e.toString());
				}
				
			}
		});
		
		play3Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(mIsBound){
						//Set the pause button text to "Pause" when play is clicked
						pauseButton.setTag(0);
						pauseButton.setText("Pause");
						//call the service to play the song 3
						mMusicOptionsService.play1(3);						
					}
						
				}catch (Exception e){
					Log.e(TAG, e.toString());
				}
				
			}
		});
		
		pauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final int status = (Integer) pauseButton.getTag();
				try{
					if(mIsBound){
						if(status == 0 && mMusicOptionsService.isPlaying()){
							//call the service to pause the song
							mMusicOptionsService.pause();
							//If status is 0 which means pause then change to resume when hit on pause
							pauseButton.setTag(1);
							pauseButton.setText("Resume");
						}else if(status == 1){
							//call the service to resume the song
							mMusicOptionsService.resume();
							//If status is 1 which means resume then change to pause when hit on resume
							pauseButton.setTag(0);
							pauseButton.setText("Pause");
						}
								
					}
						
				}catch (Exception e){
					Log.e(TAG, e.toString());
				}
				
			}
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(mIsBound){
						//Set the text to pause
						pauseButton.setTag(0);
						pauseButton.setText("Pause");
						//call the service to stop the song
						mMusicOptionsService.stop();
					}
						
				}catch (Exception e){
					Log.e(TAG, e.toString());
				}
				
			}
		});
		
	}
	
	
	private final BroadcastReceiver MyReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			//Listen for the broadcast which is sent by the service
			if(intent.getAction().equalsIgnoreCase("com.examples.Services.Song_Done"))
            {
				//get the present application context
				Context context1 = getApplicationContext();
				// Set the text for the toast
				String text = "Song completed";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context1, text, duration);
				//show the toast
				toast.show();
            }

		}
		
	};
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		if(isFinishing()){
		//unregister the service
		unregisterReceiver(MyReceiver);
		if (mIsBound) {
			try{
				//delete all the database records
				mMusicOptionsService.deleteData();
				//stop the music player
				mMusicOptionsService.stopPlayer();
				
			}catch(Exception e){
				Log.e(TAG, e.toString());
			}
			//unbind the service
			unbindService(this.mConnection);

		}
		}

		
	}
	
	private final ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName classname, IBinder iservice) {
			//get the stub as interface
			mMusicOptionsService = MusicOptions.Stub.asInterface(iservice);
			//set is bound to true
			mIsBound = true;
			Log.i("bound", "Service connected");
			
			
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			//on service disconnected, set the service to null and is bound to false
			mMusicOptionsService = null;
			mIsBound = false;
			
		}
	};

}
