package com.driverstack.devicemanager.activity.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.activity.MainActivity;
import com.driverstack.devicemanager.activity.support.BaseActionBarActivity;
import com.driverstack.devicemanager.ui.DropdownList;
import com.driverstack.devicemanager.ui.ObjectToIdValue;
import com.driverstack.devicemanager.ui.valuefield.DropdownListValueField;
import com.driverstack.devicemanager.ui.valuefield.EditTextValueField;
import com.driverstack.devicemanager.ui.valuefield.ValueField;
import com.driverstack.yunos.driver.config.ConfigurationItemPrimaryType;
import com.driverstack.yunos.driver.config.ConfigurationItemType;
import com.driverstack.yunos.remote.vo.ConfigurationItem;
import com.driverstack.yunos.remote.vo.DriverConfigurationDefinitionItem;
import com.driverstack.yunos.remote.vo.FunctionalDevice;

public class DeviceConfigurationActivity extends BaseActionBarActivity {

	public final static String EXTRA_DEVICE_ID = "deviceId";
	public final static String EXTRA_DRIVER_ID = "driverId";
	public final static String EXTRA_DEVICE_CONFIGURATION_ITEMS = "deviceConfigurationItems";

	private LinearLayout myLinearLayout;

	private String deviceId;
	private String driverId;

	private List<ConfigurationItem> deviceConfigurationItems;
	private Map<String, ConfigurationItem> deviceConfigurationMap;

	private Map<String, ValueField> fieldMap;

	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_configuration);

		deviceId = getIntent().getExtras().getString(
				DeviceConfigurationActivity.EXTRA_DEVICE_ID);
		driverId = getIntent().getExtras().getString(
				DeviceConfigurationActivity.EXTRA_DRIVER_ID);

		new AsyncTask<Void, Void, List<DriverConfigurationDefinitionItem>>() {

			@Override
			protected List<DriverConfigurationDefinitionItem> doInBackground(
					Void... params) {
				List<DriverConfigurationDefinitionItem> defItems = remoteService
						.getDriverConfigurationDefinitionItems(driverId,
								currentLocale.toString());
				return defItems;
			}

			@Override
			protected void onPostExecute(
					List<DriverConfigurationDefinitionItem> result) {
				initUI(result);
				super.onPostExecute(result);
			}

		}.execute();
	}

	private void initUI(List<DriverConfigurationDefinitionItem> defItems) {
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
				result.putExtra(EXTRA_DEVICE_CONFIGURATION_ITEMS,
						deviceConfigurationItems
								.toArray(new ConfigurationItem[0]));
				setResult(RESULT_OK, result);

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
			final DriverConfigurationDefinitionItem definitionItem,
			final ConfigurationItem configurationItem) {
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

		View field;

		if (definitionItem.getType().getType() == ConfigurationItemPrimaryType.DEVICE) {

			final Spinner spinner = new Spinner(this);

			final ConfigurationItemType cfgItemType = definitionItem.getType();
			new AsyncTask<Void, Void, Void>() {
				List<FunctionalDevice> fdOptions;

				@Override
				protected Void doInBackground(Void... params) {
					fdOptions = remoteService.getFunctionalDevices(
							MainActivity.userId, cfgItemType.getParameter(),
							currentLocale.toString());
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					DropdownList<FunctionalDevice> functionalDeviceDropdownList = new DropdownList<FunctionalDevice>(
							DeviceConfigurationActivity.this,
							android.R.layout.simple_spinner_item, fdOptions,
							spinner, new ObjectToIdValue<FunctionalDevice>() {
								@Override
								public String getId(FunctionalDevice obj) {

									return obj.getFullId();
								}

								@Override
								public String getName(FunctionalDevice obj) {
									return String.format("%s:%d(%s)",
											obj.getDeviceName(),
											obj.getIndex(),
											obj.getArtifactName());

								}
							});

					functionalDeviceDropdownList
							.setSelectedObjectById(configurationItem.getValue());

					ValueField valueField = new DropdownListValueField(
							functionalDeviceDropdownList);

					fieldMap.put(definitionItem.getName(), valueField);
					super.onPostExecute(result);
				}

			}.execute();

			field = spinner;

		} else {
			EditText edit = new EditText(this);
			edit.setText(configurationItem.getValue());
			label.setLayoutParams(params);

			field = edit;
			ValueField valueField = new EditTextValueField(edit);
			fieldMap.put(definitionItem.getName(), valueField);
		}
		// add the textView and the Button to LinearLayout
		container.addView(label);
		container.addView(field);

		return container;
	}

	private void updateModel() {
		for (ConfigurationItem ci : deviceConfigurationItems) {
			ValueField cf = fieldMap.get(ci.getName());
			ci.setValue(cf.getValue());
		}
	}

}
