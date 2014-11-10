package com.deviceyun.devicemanager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.chainsaw.Main;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropdownList<T> {

	public static final String NULL_KEY = "null";

	protected ObjectToIdValue objectToIdValue;

	protected List<T> objects = new ArrayList<T>();
	protected List<String> names = new ArrayList<String>();
	protected Map<String, T> map = new HashMap<String, T>();

	private Spinner spinner;

	public DropdownList(Context context, int resource, List<T> objects,
			Spinner spinner, ObjectToIdValue objectToIdValue) {
		init(context, resource, objects, spinner, objectToIdValue);
	}

	private DropdownList(Context context, int resource, List<T> objects,
			T nullObject, Spinner spinner, ObjectToIdValue objectToIdValue) {
		List objects2 = new ArrayList<T>();
		objects2.add(nullObject);
		objects2.addAll(objects);

		init(context, resource, objects2, spinner, objectToIdValue);
	}

	private void init(Context context, int resource, List<T> objects,
			Spinner spinner, ObjectToIdValue objectToIdValue) {

		this.objects = objects;
		this.objectToIdValue = objectToIdValue;

		for (T t : objects) {

			String id = objectToIdValue.getId(t);
			String name = objectToIdValue.getName(t);
			names.add(name);
			map.put(id, t);
		}

		this.spinner = spinner;

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				resource, names);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(dataAdapter);
	}

	public boolean containsId(String id) {
		return map.containsKey(id);
	}

	/**
	 * 
	 * @param id
	 * @return false if no such ID
	 */
	public void setSelectedObjectById(String id) {
		T t = map.get(id);
		int index = objects.indexOf(t);
		spinner.setSelection(index);
	}

	public String getSelectedObjectId() {
		int pos = spinner.getSelectedItemPosition();

		if (pos != android.widget.AdapterView.INVALID_POSITION) {
			T t = objects.get(pos);
			String id = objectToIdValue.getId(t);
			return id;
		} else
			return null;
	}

	public void selectFirstItem(){
		spinner.setSelection(0);
	}
}
