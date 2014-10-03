package com.deviceyun.devicemanager.ui;

public interface ObjectToIdValue<T> {
	String getId(T obj);

	String getName(T obj);
}
