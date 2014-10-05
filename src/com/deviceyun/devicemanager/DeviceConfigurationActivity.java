package com.deviceyun.devicemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.deviceyun.devicemanager.ui.DropdownList;
import com.deviceyun.devicemanager.ui.ObjectToIdValue;
import com.deviceyun.devicemanager.ui.valuefield.DropdownListValueField;
import com.deviceyun.devicemanager.ui.valuefield.EditTextValueField;
import com.deviceyun.devicemanager.ui.valuefield.ValueField;
import com.deviceyun.devicemanager.utils.Utils;
import com.driverstack.yunos.driver.config.ConfigurationItemPrimaryType;
import com.driverstack.yunos.driver.config.ConfigurationItemType;
import com.driverstack.yunos.remote.vo.ConfigurationItem;
import com.driverstack.yunos.remote.vo.DriverConfigurationDefinitionItem;
import com.driverstack.yunos.remote.vo.FunctionalDevice;

public class DeviceConfigurationActivity extends ActionBarActivity {

	public final static String EXTRA_DEVICE_ID = "deviceId";
	public final static String EXTRA_DRIVER_ID = "driverId";
	public final static String EXTRA_DEVICE_CONFIGURATION_ITEMS = "deviceConfigurationItems";

	private LinearLayout myLinearLayout;

	private String deviceId;
	private String driverId;
	private RemoteService remoteService;
	

	private List<ConfigurationItem> deviceConfigurationItems;
	private Map<String, ConfigurationItem> deviceConfigurationMap;

	private Map<String, ValueField> fieldMap;

	private Locale currentLocale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_configuration);

		currentLocale = Utils.getLocale(this);
		remoteService = RemoteServiceFactory.getRemoteService();

		deviceId =  getIntent().getExtras().getString(DeviceConfigurationActivity.EXTRA_DEVICE_ID);
		driverId =  getIntent().getExtras().getString(DeviceConfigurationActivity.EXTRA_DRIVER_ID);

		List<DriverConfigurationDefinitionItem> defItems = remoteService
				.getDriverConfigurationDefinitionItems(driverId,
						currentLocale.toString());

		Object[] array = (Object[]) getIntent().getSerializableExtra(
				EXTRA_DEVICE_CONFIGURATION_ITEMS);
		deviceConfigurationItems = new ArrayList<ConfigurationItem>();
		for (Object ci : array)
			deviceConfigurationItems.add((ConfigurationItem) ci);

		deviceConfigurationMap = new HashMap<String, ConfigurationItem>();
		for (ConfigurationItem ci : deviceConfigurationItems)
			deviceConfigurationMap.put(ci.getName(), ci);

		fieldMap = new HashMap<String, ValueField>();

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
		} else if (id == R.id.action_accept) {

			updateModel();
			try {
				// saveModel();
				Intent result = new Intent();
				result.putExtra(
						EXTRA_DEVICE_CONFIGURATION_ITEMS,
						deviceConfigurationItems
								.toArray(new ConfigurationItem[0]));
				setResult(RESULT_OK,result);

				finish();
			} catch (Exception e) {
				Toast.makeText(DeviceConfigurationActivity.this,
						"saved failed:" + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}

			return true;
		} else if (id == R.id.action_cancel) {

			setResult(RESULT_CANCELED);

			finish();
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

		View field = null;
		ValueField valueField = null;

		if (definitionItem.getType().getType() == ConfigurationItemPrimaryType.DEVICE) {

			Spinner spinner = new Spinner(this);

			ConfigurationItemType cfgItemType = definitionItem.getType();
			List<FunctionalDevice> fdOptions = remoteService
					.getFunctionalDevices(MainActivity.userId,
							cfgItemType.getParameter(),
							currentLocale.toString());
			DropdownList<FunctionalDevice> functionalDeviceDropdownList = new DropdownList<FunctionalDevice>(
					this, android.R.layout.simple_spinner_item, fdOptions,
					spinner, new ObjectToIdValue<FunctionalDevice>() {
						@Override
						public String getId(FunctionalDevice obj) {

							return obj.getFullId();
						}

						@Override
						public String getName(FunctionalDevice obj) {
							return String.format("%s:%d(%s)",
									obj.getDeviceName(), obj.getIndex(),
									obj.getArtifactName());

						}
					});

			functionalDeviceDropdownList
					.setSelectedObjectById(configurationItem.getValue());

			field = spinner;
			valueField = new DropdownListValueField(
					functionalDeviceDropdownList);
		} else {
			EditText edit = new EditText(this);
			edit.setText(configurationItem.getValue());
			label.setLayoutParams(params);

			field = edit;
			valueField = new EditTextValueField(edit);

		}
		// add the textView and the Button to LinearLayout
		container.addView(label);
		container.addView(field);

		fieldMap.put(definitionItem.getName(), valueField);

		return container;
	}

	private void updateModel() {
		for (ConfigurationItem ci : deviceConfigurationItems) {
			ValueField cf = fieldMap.get(ci.getName());
			ci.setValue(cf.getValue());
		}
	}

	private void saveModel() {

		remoteService.updateDeviceConfiguration(deviceId,
				deviceConfigurationItems);

	}
}
