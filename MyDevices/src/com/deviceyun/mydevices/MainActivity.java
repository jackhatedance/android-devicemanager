package com.deviceyun.mydevices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.deviceyun.devicemanager.R;

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

		// React to user clicks on item
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view,
					int position, long id) {

				// We know the View is a TextView so we can cast it
				TextView clickedView = (TextView) view;

				Toast.makeText(
						MainActivity.this,
						"Item with id [" + id + "] - Position [" + position
								+ "] - Planet [" + clickedView.getText() + "]",
						Toast.LENGTH_SHORT).show();

			}
		});

		// we register for the contextmneu       
		registerForContextMenu(lv);
	}

	// We want to create a context Menu when the user long click on an item
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

		// We know that each row in the adapter is a Map
		HashMap map = (HashMap) simpleAdpt.getItem(aInfo.position);

		menu.setHeaderTitle("Options for " + map.get("planet"));
		menu.add(1, 1, 1, "Details");
		menu.add(1, 2, 2, "Delete");

	}
	
	// This method is called when user selects an Item in the Context menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    // Implements our logic
	    Toast.makeText(this, "Item id ["+itemId+"]", Toast.LENGTH_SHORT).show();
	    return true;
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
