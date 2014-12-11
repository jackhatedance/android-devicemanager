package com.driverstack.devicemanager.remoteservice;

import retrofit.http.POST;

public interface StatefulSwitchRemoteService {

	@POST("/devices/{deviceId}/{functionalDeviceIndex}/on")
	boolean on();

	@POST("/devices/{deviceId}/{functionalDeviceIndex}/off")
	boolean off();

	@POST("/devices/{deviceId}/{functionalDeviceIndex}/isOn")
	boolean isOn();

}
