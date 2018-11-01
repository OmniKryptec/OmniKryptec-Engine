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

import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

public class CLContext {
    
    private static List<CLContext> contexts = new ArrayList<>();
    
    private CLContextCallback contextCB;
    private long context;
    
    public CLContext(CLDevice device) {
        context = CL10.clCreateContext(device.getParent().getCTXProps(), device.getID(), contextCB = CLContextCallback.create((errinfo, private_info, cb, user_data) -> {
            System.err.println("[LWJGL] cl_context_callback");
            System.err.println("\tInfo: " + MemoryUtil.memUTF8(errinfo));
        }), MemoryUtil.NULL, null);
    }
    
    public static void cleanup() {
        for (CLContext c : contexts) {
            CL10.clReleaseContext(c.getID());
        }
    }
    
    public long getID() {
        return context;
    }
    
    CLContextCallback getCB() {
        return contextCB;
    }
}
