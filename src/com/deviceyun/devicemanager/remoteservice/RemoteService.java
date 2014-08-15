package com.deviceyun.devicemanager.remoteservice;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.deviceyun.yunos.remote.vo.Device;

public interface RemoteService {
	@GET("/devices")
	List<Device> getUserDevices(@Query("userId") String userId);
}
