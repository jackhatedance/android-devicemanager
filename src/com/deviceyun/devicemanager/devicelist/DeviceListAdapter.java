package com.deviceyun.devicemanager.devicelist;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deviceyun.yunos.remote.vo.Device;

public class DeviceListAdapter  extends ArrayAdapter<Device> {

	public DeviceListAdapter(Context context, int resource, List<Device> objects) {
		super(context, resource, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(android.R.layout.simple_list_item_1, null);
			 
		}

		Device item = getItem(position);
		if (item != null) {
			// My layout has only one TextView
			TextView itemView = (TextView) view.findViewById(android.R.id.text1);
			if (itemView != null) {
				// do whatever you want with your string and long
				itemView.setText(String.format("%s %s", item.getLocation(),
						item.getName()));
			}
		}

		return view;
	}
}
