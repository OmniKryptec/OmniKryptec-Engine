package de.omnikryptec.opencl.core;

import org.lwjgl.opencl.CL10;

import java.util.ArrayList;
import java.util.List;

public enum DeviceType {
	CPU(CL10.CL_DEVICE_TYPE_CPU), GPU(CL10.CL_DEVICE_TYPE_GPU), DEFAULT(CL10.CL_DEVICE_TYPE_DEFAULT),
	ACCELERATOR(CL10.CL_DEVICE_TYPE_ACCELERATOR), ALL(CL10.CL_DEVICE_TYPE_ALL), UNKNOWN(-2);

	public final int CL_INT;

	private DeviceType(int clint) {
		this.CL_INT = clint;
	}

	public static DeviceType[] toTypes(int in) {
		List<DeviceType> ts = new ArrayList<>();
		if(in!=DeviceType.UNKNOWN.CL_INT) {
			for (int i = 0; i < DeviceType.values().length; i++) {
				if (DeviceType.values()[i].CL_INT > 0) {
					if ((in & DeviceType.values()[i].CL_INT) != 0) {
						ts.add(DeviceType.values()[i]);
					}
						
				}
			}
		}
		if(ts.isEmpty()) {
			ts.add(UNKNOWN);
		}
		return ts.toArray(new DeviceType[ts.size()]);
	}
	
	public static DeviceType toType(int i) {
		return toTypes(i)[0];
	}
}