package com.deviceyun.mydevices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ActionBarActivity {
	List<Map<String, String>> planetsList = new ArrayList<Map<String, String>>();
	SimpleAdapter simpleAdpt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initList();

		// We get the ListView component from the layout
		ListView lv = (ListView) findViewById(R.id.listViewDevice);

		// This is a simple adapter that accepts as parameter
		// Context
		// Data list
		// The row layout that is used during the row creation
		// The keys used to retrieve the data
		// The View id used to show the data. The key number and the view id
		// must match
		simpleAdpt = new SimpleAdapter(this, planetsList,
				android.R.layout.simple_list_item_1, new String[] { "planet" },
				new int[] { android.R.id.text1 });

		lv.setAdapter(simpleAdpt);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initList() {
		// We populate the planets

		planetsList.add(createPlanet("planet", "电灯"));
		planetsList.add(createPlanet("planet", "电视机"));
		planetsList.add(createPlanet("planet", "热水器"));
		

	}

	private HashMap<String, String> createPlanet(String key, String name) {
		HashMap<String, String> planet = new HashMap<String, String>();
		planet.put(key, name);

		return planet;
	}
}
