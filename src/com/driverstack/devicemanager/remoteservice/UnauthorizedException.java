package com.driverstack.devicemanager.remoteservice;

public class UnauthorizedException extends RuntimeException {
	public UnauthorizedException(Throwable throwable) {
		super(throwable);
	}
}
