/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCapabilities;

public class CLPlatform {
    
    private static HashMap<Integer, CLDevice> createdDevices = new HashMap<>();
    
    private final CLCapabilities platformCaps;
    private PointerBuffer devices;
    
    private final PointerBuffer ctxProps;
    private final long platform;
    private int devs = -1;
    
    CLPlatform(final long id) {
        this.platform = id;
        this.platformCaps = CL.createPlatformCapabilities(id);
        this.ctxProps = BufferUtils.createPointerBuffer(3);
        this.ctxProps.put(0, CL10.CL_CONTEXT_PLATFORM).put(2, 0);
        this.ctxProps.put(1, id);
    }
    
    public boolean canGLInterop() {
        return this.platformCaps.cl_khr_gl_sharing || this.platformCaps.cl_APPLE_gl_sharing;
    }
    
    public CLCapabilities getPlatCaps() {
        return this.platformCaps;
    }
    
    public long getID() {
        return this.platform;
    }
    
    public PointerBuffer getCTXProps() {
        return this.ctxProps;
    }
    
    public CLPlatform createDeviceData(final DeviceType... deviceTypes) {
        final int clDeviceFilter = DeviceType.toInt(deviceTypes);
        CL10.clGetDeviceIDs(this.platform, clDeviceFilter, null, OpenCL.tmpBuffer);
        this.devs = OpenCL.tmpBuffer.get(0);
        this.devices = BufferUtils.createPointerBuffer(this.devs);
        CL10.clGetDeviceIDs(this.platform, clDeviceFilter, this.devices, (IntBuffer) null);
        return this;
    }
    
    public PointerBuffer getDevices() {
        return this.devices;
    }
    
    public int getDevicesSize() {
        return this.devs;
    }
    
    public CLDevice getDevice(final int deviceInd) {
        if (!createdDevices.containsKey(deviceInd)) {
            if (deviceInd >= this.devices.capacity() || deviceInd < 0) {
                throw new IndexOutOfBoundsException();
            }
            createdDevices.put(deviceInd, new CLDevice(this.devices.get(deviceInd), this));
        }
        return createdDevices.get(deviceInd);
    }
    
}
