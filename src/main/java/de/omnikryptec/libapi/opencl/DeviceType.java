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

import org.lwjgl.opencl.CL10;

import java.util.ArrayList;
import java.util.List;

public enum DeviceType {
    CPU(CL10.CL_DEVICE_TYPE_CPU), GPU(CL10.CL_DEVICE_TYPE_GPU), DEFAULT(CL10.CL_DEVICE_TYPE_DEFAULT),
    ACCELERATOR(CL10.CL_DEVICE_TYPE_ACCELERATOR), ALL(CL10.CL_DEVICE_TYPE_ALL), UNKNOWN(-2);
    
    public final int CL_INT;
    
    private DeviceType(final int clint) {
        this.CL_INT = clint;
    }
    
    public static DeviceType[] toTypes(final int in) {
        final List<DeviceType> ts = new ArrayList<>();
        if (in != DeviceType.UNKNOWN.CL_INT) {
            for (int i = 0; i < DeviceType.values().length; i++) {
                if (DeviceType.values()[i].CL_INT > 0) {
                    if ((in & DeviceType.values()[i].CL_INT) != 0) {
                        ts.add(DeviceType.values()[i]);
                    }
                    
                }
            }
        }
        if (ts.isEmpty()) {
            ts.add(UNKNOWN);
        }
        return ts.toArray(new DeviceType[ts.size()]);
    }
    
    public static DeviceType toType(final int i) {
        return toTypes(i)[0];
    }
}