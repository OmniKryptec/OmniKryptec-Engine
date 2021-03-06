/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;

public class CLDevice {
    
    private final CLCapabilities deviceCaps;
    private final CLPlatform parent;
    private final long device;
    
    CLDevice(final long id, final CLPlatform parent) {
        this.device = id;
        this.parent = parent;
        this.deviceCaps = CL.createDeviceCapabilities(id, parent.getPlatCaps());
    }
    
    public CLCapabilities getDeviceCaps() {
        return this.deviceCaps;
    }
    
    public long getID() {
        return this.device;
    }
    
    public CLPlatform getParent() {
        return this.parent;
    }
    
}
