package com.driverstack.devicemanager.activity;

import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
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

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.R.id;
import com.driverstack.devicemanager.R.layout;
import com.driverstack.devicemanager.R.menu;
import com.driverstack.devicemanager.activity.auth.ChangePasswordActivity;
import com.driverstack.devicemanager.activity.auth.LoginActivity;
import com.driverstack.devicemanager.activity.device.DeviceDetailActivity;
import com.driverstack.devicemanager.activity.support.BaseActionBarActivity;
import com.driverstack.devicemanager.activity.support.Constants;
import com.driverstack.devicemanager.preference.Settings;
import com.driverstack.devicemanager.remoteservice.RemoteService;
import com.driverstack.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.devicemanager.session.Session;
import com.driverstack.devicemanager.session.SessionManager;
import com.driverstack.devicemanager.ui.DeviceListAdapter;
import com.driverstack.devicemanager.utils.Utils;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.FunctionalDevice;
import com.driverstack.yunos.remote.vo.User;

public class MainActivity extends BaseActionBarActivity {

	private static int REQUEST_DEVICE_DETAIL = 1;

	private ListView deviceListView;
	DeviceListAdapter deviceAdapter;
	private List<Device> devices;

	public static String userId;

	private SessionManager sessionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sessionManager = new SessionManager(this);

		Session session = sessionManager.getSession();
		if (session != null) {
			userId = session.getString(Session.KEY_USERNAME);
			// test token, in case it is expired.
			String key = session.getString(Session.KEY_TOKEN_KEY);
			String secret = session.getString(Session.KEY_TOKEN_SECRET);

			Settings settings = new Settings(MainActivity.this);
			String url = settings.getEffectiveServerUrl();

			final RemoteService remoteService = new RemoteServiceFactory()
					.getRemoteService(url, key, secret);

			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {

					try {
						User user = remoteService.getUser(userId);
						return true;

					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);

					if (result)
						loadDeviceListData();
					else
						startLoginActivity();

				}

			}.execute();
		} else {
			startLoginActivity();
			return;
		}

	}

	private void loadDeviceListData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				devices = remoteService.getUserDevices(userId);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				initUI();

			}

		}.execute();
	}

	private void initUI() {
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

						final Intent intent = new Intent(
								Constants.ACTION_OPERATE);
						// intent.setType(type)
						final Device device = devices.get(position);

						new AsyncTask<Void, Void, Void>() {
							List<FunctionalDevice> functionalDevices;

							@Override
							protected Void doInBackground(Void... params) {

								functionalDevices = remoteService
										.getFunctionalDevices(device.getId(),
												currentLocale.toString());

								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								FunctionalDevice functionalDevice = functionalDevices.get(device
										.getDefaultFunctionalDeviceIndex());
								String type = String.format("%s/%s",
										functionalDevice.getOrganizationId(),
										functionalDevice.getArtifactId());

								intent.setType(type);
								intent.putExtra("functionalDevice",
										functionalDevice);

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
								super.onPostExecute(result);
							}

						}.execute();

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
		final Device device = (Device) deviceAdapter.getItem(aInfo.position);

		if (itemId == 1) {
			startDeviceDetailAcitivity(device);
		} else if (itemId == 2) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Delete Device?")
					.setMessage("Are you sure you want to delete this device?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									removeDevice(device);
								}

							}).setNegativeButton("No", null).show();

		}

		return true;
	}

	private void startDeviceDetailAcitivity(Device device) {
		Intent myIntent = new Intent(this, DeviceDetailActivity.class);
		myIntent.putExtra("device", device);
		startActivityForResult(myIntent, REQUEST_DEVICE_DETAIL);
	}

	private void startLoginActivity() {
		// user is not logged in redirect him to Login Activity
		Intent i = new Intent(this, LoginActivity.class);

		// Closing all the Activities from stack
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		startActivity(i);

		finish();
	}

	private void startSettingsActivity() {
		// user is not logged in redirect him to Login Activity
		Intent i = new Intent(this, SettingsActivity.class);

		// Staring Login Activity
		startActivity(i);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DEVICE_DETAIL) {
			if (resultCode == RESULT_OK) {
				refreshUI();
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
		switch (item.getItemId()) {
		case R.id.action_settings:
			startSettingsActivity();
			break;

		case R.id.action_new:
			Device newDevice = new Device();
			newDevice.setName("new device");
			startDeviceDetailAcitivity(newDevice);
			break;
		case R.id.action_change_password:

			Intent i = new Intent(this, ChangePasswordActivity.class);

			startActivity(i);

			break;
		case R.id.action_logout:

			logout();

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void logout() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SessionManager sessionManager = new SessionManager(
						MainActivity.this);
				Session session = sessionManager.getSession();
				if (session != null) {

					String key = session.getString(Session.KEY_TOKEN_KEY);
					String secret = session.getString(Session.KEY_TOKEN_SECRET);
					String url = session.getString(Session.KEY_SERVER_URL);

					RemoteService remoteService = RemoteServiceFactory
							.getRemoteService(url, key, secret);
					try {
						remoteService.destroyToken();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				sessionManager.destroySession();
				startLoginActivity();

			}

		}.execute();
	}

	private void removeDevice(final Device device) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				remoteService.deleteDevice(device.getId());

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				devices.remove(device);
				updateDeviceListView();

			}

		}.execute();
	}

	private void refreshUI() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				devices = remoteService.getUserDevices(userId);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				updateDeviceListView();
			}

		}.execute();
	}

	private void updateDeviceListView() {
		deviceAdapter.clear();

		for (Device d : devices)
			deviceAdapter.add(d);

		deviceAdapter.notifyDataSetChanged();
	}

}
