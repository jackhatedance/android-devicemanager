package com.driverstack.devicemanager.ui.valuefield;

import android.widget.EditText;

public class EditTextValueField implements ValueField{
	private EditText editText;

	public EditTextValueField(EditText editText) {
		this.editText = editText;
	}
	@Override
	public String getValue() {
		return editText.getText().toString();
	}
	
}
