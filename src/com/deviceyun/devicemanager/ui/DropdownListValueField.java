package com.deviceyun.devicemanager.ui;

public class DropdownListValueField<T> implements ValueField {
	private DropdownList dropdownList;

	public DropdownListValueField(DropdownList dropdownList) {
		this.dropdownList = dropdownList;
	}

	@Override
	public String getValue() {
		return dropdownList.getSelectedObjectId();
	}

}
