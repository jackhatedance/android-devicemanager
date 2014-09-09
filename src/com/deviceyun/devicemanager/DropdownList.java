package com.deviceyun.devicemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropdownList<T> {

	protected ObjectToIdValue objectToIdValue;

	protected List<T> objects = new ArrayList<T>();
	protected List<String> names = new ArrayList<String>();
	protected Map<String, T> map = new HashMap<String, T>();

	private Spinner spinner;

	public DropdownList(Context context, int resource, List<T> objects,
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

	public void setSelectedObjectById(String id) {
		T t = map.get(id);
		int index = objects.indexOf(t);
		spinner.setSelection(index);
	}

	public String getSelectedObjectId() {
		T t = (T) spinner.getSelectedItem();
		String id = objectToIdValue.getId(t);
		return id;
	}
}
