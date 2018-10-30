/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.libapi.opencl;

import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCapabilities;

public class CLPlatform {

    private static HashMap<Integer, CLDevice> createdDevices = new HashMap<>();

    private CLCapabilities platformCaps;
    private PointerBuffer devices, ctxProps;
    private long platform;
    private int devs = -1;

    CLPlatform(long id) {
	this.platform = id;
	platformCaps = CL.createPlatformCapabilities(id);
	ctxProps = OpenCL.memStack.mallocPointer(3);
	ctxProps.put(0, CL10.CL_CONTEXT_PLATFORM).put(2, 0);
	ctxProps.put(1, id);
    }

    public CLCapabilities getPlatCaps() {
	return platformCaps;
    }

    public long getID() {
	return platform;
    }

    public PointerBuffer getCTXProps() {
	return ctxProps;
    }

    public CLPlatform createDeviceData(int clDeviceFilter) {
	CL10.clGetDeviceIDs(platform, clDeviceFilter, null, OpenCL.tmpBuffer);
	devs = OpenCL.tmpBuffer.get(0);
	devices = OpenCL.memStack.mallocPointer(devs);
	CL10.clGetDeviceIDs(platform, clDeviceFilter, devices, (IntBuffer) null);
	return this;
    }

    public PointerBuffer getDevices() {
	return devices;
    }

    public int getDevicesSize() {
	return devs;
    }

    public CLDevice getDevice(int deviceInd) {
	if (!createdDevices.containsKey(deviceInd)) {
	    createdDevices.put(deviceInd, new CLDevice(devices.get(deviceInd), this));
	}
	return createdDevices.get(deviceInd);

    }

}
