package com.deviceyun.devicemanager;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import com.deviceyun.devicemanager.devicelist.DeviceListAdapter;
import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.deviceyun.devicemanager.utils.Utils;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.FunctionalDevice;

public class MainActivity extends ActionBarActivity {

	private RemoteService remoteService;
	private Locale currentLocale;

	private static int REQUEST_DEVICE_DETAIL = 1;
	
	
	private ListView deviceListView;
	DeviceListAdapter deviceAdapter;
	private List<Device> devices;

	public static String userId = "jackding";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		remoteService = RemoteServiceFactory.getRemoteService();
		currentLocale = Utils.getLocale(this);
		devices = remoteService.getUserDevices(userId);

		deviceListView = (ListView) findViewById(R.id.listViewDevice);
		deviceAdapter = new DeviceListAdapter(this,
				android.R.layout.simple_list_item_1, devices);

		deviceListView.setAdapter(deviceAdapter);

		// React to user clicks on item
		deviceListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parentAdapter,
							View view, int position, long id) {

						// We know the View is a TextView so we can cast it
						TextView clickedView = (TextView) view;

						Intent intent = new Intent(Constants.ACTION_OPERATE);
						// intent.setType(type)
						Device device = devices.get(position);
						List<FunctionalDevice> functionalDevices = remoteService
								.getFunctionalDevices(device.getId(),
										currentLocale.toString());
						FunctionalDevice functionalDevice = functionalDevices
								.get(device.getDefaultFunctionalDeviceIndex());
						String type = String.format("%s/%s",
								functionalDevice.getOrganizationId(),
								functionalDevice.getArtifactId());

						intent.setType(type);
						intent.putExtra("functionalDevice", functionalDevice);

						PackageManager packageManager = getPackageManager();
						List<ResolveInfo> activities = packageManager
								.queryIntentActivities(intent, 0);
						boolean isIntentSafe = activities.size() > 0;

						if (isIntentSafe)
							startActivity(intent);
						else
							Toast.makeText(
									MainActivity.this,
									"No available appications for this device, please download some applications from the store.",
									Toast.LENGTH_SHORT).show();

					}
				});

		// we register for the contextmneu
		registerForContextMenu(deviceListView);

	}

	// We want to create a context Menu when the user long click on an item
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

		// We know that each row in the adapter is a Map
		Device device = (Device) deviceAdapter.getItem(aInfo.position);

		menu.setHeaderTitle("Options for " + device.getName());
		menu.add(1, 1, 1, "Details");
		menu.add(1, 2, 2, "Delete");

	}

	// This method is called when user selects an Item in the Context menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Device device = (Device) deviceAdapter.getItem(aInfo.position);

		if (itemId == 1) {

			Intent myIntent = new Intent(this, DeviceDetailActivity.class);
			myIntent.putExtra("device", device);
			startActivityForResult(myIntent, REQUEST_DEVICE_DETAIL);
		}
		// Implements our logic
		Toast.makeText(this, "Item id [" + itemId + "]", Toast.LENGTH_SHORT)
				.show();
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DEVICE_DETAIL) {

			if (resultCode == RESULT_OK) {
				devices = remoteService.getUserDevices(userId);
				updateDeviceListView();
			}
		}
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
		} else if (id == R.id.action_new) {
			Toast.makeText(MainActivity.this, "add new device",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateDeviceListView() {
		deviceAdapter.clear();

		for (Device d : devices)
			deviceAdapter.add(d);

		deviceAdapter.notifyDataSetChanged();
	}

}
