package com.deviceyun.devicemanager;

public interface ObjectToIdValue<T> {
	String getId(T obj);

	String getName(T obj);
}
