package com.examples.Services.MusicClient;

import java.util.List;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Transactions extends ListActivity {
	
	private List<String> dbRecords;
	ArrayAdapter<String> myAdapter;
	ArrayAdapter<String> emptyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transactions);
		//get the records set in the bundle
		Bundle b = this.getIntent().getExtras();
		dbRecords = b.getStringArrayList("records");
		// initiate the list adapter
		myAdapter = new ArrayAdapter <String>(this,R.layout.row_layout,R.id.listText, dbRecords);
		// assign the list adapter
		setListAdapter(myAdapter);

	}
	

}
