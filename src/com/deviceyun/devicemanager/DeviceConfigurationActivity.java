package com.deviceyun.devicemanager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.deviceyun.devicemanager.utils.Utils;
import com.driverstack.yunos.remote.vo.ConfigurationItem;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.DriverConfigurationDefinitionItem;

public class DeviceConfigurationActivity extends ActionBarActivity {

	private LinearLayout myLinearLayout;

	private RemoteService remoteService;
	private Device device;

	private Locale currentLocale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_configuration);

		currentLocale = Utils.getLocale(this);
		remoteService = RemoteServiceFactory.getRemoteService();

		device = (Device) getIntent().getExtras().get("device");

		List<DriverConfigurationDefinitionItem> defItems = remoteService
				.getDriverConfigurationDefinitionItems(device.getDriverId(),
						currentLocale.toString());

		List<ConfigurationItem> deviceConfigurationItems = remoteService
				.getDeviceConfiguration(device.getId());
		Map<String, ConfigurationItem> deviceConfigurationMap = new HashMap<String, ConfigurationItem>();
		for (ConfigurationItem ci : deviceConfigurationItems)
			deviceConfigurationMap.put(ci.getName(), ci);

		// add LInearLayout
		myLinearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

		for (DriverConfigurationDefinitionItem item : defItems) {
			ConfigurationItem configurationItem = deviceConfigurationMap
					.get(item.getName());

			View row = createRow(this, item, configurationItem);

			myLinearLayout.addView(row);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_configuration, menu);
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

	public View createRow(Context context,
			DriverConfigurationDefinitionItem definitionItem,
			ConfigurationItem configurationItem) {
		// add LInearLayout
		LinearLayout container = (LinearLayout) new LinearLayout(context);

		// add LayoutParams
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		container.setOrientation(LinearLayout.HORIZONTAL);

		// add textView
		TextView label = new TextView(this);
		label.setText(definitionItem.getDisplayName());
		label.setLayoutParams(params);

		EditText field = new EditText(this);
		field.setText(configurationItem.getValue());
		label.setLayoutParams(params);

		// add the textView and the Button to LinearLayout
		container.addView(label);
		container.addView(field);

		return container;
	}

}
