package com.deviceyun.devicemanager.remoteservice;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.driverstack.yunos.remote.vo.Device;

public interface RemoteService {
	@GET("/devices")
	List<com.driverstack.yunos.remote.vo.Device> getUserDevices(
			@Query("userId") String userId);

	@POST("/devices/update")
	public boolean updateDevice(@Body Device device);

	@GET("/devices")
	List<com.driverstack.yunos.remote.vo.Device> getDeviceConfiguration(
			@Query("userId") String userId);

	@GET("/vendors")
	List<com.driverstack.yunos.remote.vo.Vendor> getAllVendors(
			@Query("locale") String locale);

	@GET("/device_classes")
	List<com.driverstack.yunos.remote.vo.DeviceClass> getDeviceClasses(@Query("vendorId") String vendorId,
			@Query("locale") String locale);

	@GET("/vendors/{vendorId}/models")
	List<com.driverstack.yunos.remote.vo.Model> getModels(
			@Path("vendorId") String vendorId,
			@Query("deviceClassId") String deviceClassId,
			@Query("locale") String locale);

}
