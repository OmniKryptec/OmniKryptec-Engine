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

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.HashMap;

public class OpenCL {
    
    private static final Class<?>[] constantsClasses = {CL10.class, CL12.class, CL20.class, CL21.class, CL22.class};
    static MemoryStack memStack;
    static IntBuffer tmpBuffer;
    private static HashMap<Integer, CLPlatform> createdPlatforms = new HashMap<>();
    private static PointerBuffer platforms;
    
    static {
        memStack = MemoryStack.stackPush();
        tmpBuffer = memStack.mallocInt(1);
    }
    
    private static void createPlatformData() {
        CL10.clGetPlatformIDs(null, tmpBuffer);
        assert tmpBuffer.get(0) != 0;
        platforms = memStack.mallocPointer(tmpBuffer.get(0));
        CL10.clGetPlatformIDs(platforms, (IntBuffer) null);
    }
    
    public static PointerBuffer getPlatforms() {
        return platforms;
    }
    
    public static CLPlatform getPlatform(int platformInd) {
        if (!createdPlatforms.containsKey(platformInd)) {
            createdPlatforms.put(platformInd, new CLPlatform(platforms.get(platformInd)));
        }
        return createdPlatforms.get(platformInd);
        
    }
    
    public static void cleanup() {
        CLKernel.cleanup();
        CLProgram.cleanup();
        CLMemory.cleanup();
        CLCommandQueue.cleanup();
        CLContext.cleanup();
        CL.destroy();
    }
    
    public static void create() {
        try {
            CL.create();
        } catch (Exception e) {
        }
        createPlatformData();
    }
    
    public static String searchConstants(int i) {
        for (Class<?> c : constantsClasses) {
            Field[] fields = c.getFields();
            for (Field f : fields) {
                try {
                    if (i == f.getInt(null)) {
                        return f.getName();
                    }
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
        return "ERROR 404: Constant not found";
    }
}
