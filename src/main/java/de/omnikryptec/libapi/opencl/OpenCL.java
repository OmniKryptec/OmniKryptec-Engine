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

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;
import org.lwjgl.opencl.CL20;
import org.lwjgl.opencl.CL21;
import org.lwjgl.opencl.CL22;
import org.lwjgl.system.MemoryStack;

import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Util;
//TODO better OpenCL integration into the engine
public class OpenCL {
    
    private static final Class<?>[] constantsClasses = { CL10.class, CL12.class, CL20.class, CL21.class, CL22.class };
    static IntBuffer tmpBuffer;
    private static OpenCL instance;
    
    private HashMap<Integer, CLPlatform> createdPlatforms = new HashMap<>();
    private PointerBuffer platforms;
    
    static {
        tmpBuffer = BufferUtils.createIntBuffer(1);
    }
    
    public static void create() {
        if(isInitialized()) {
            throw new IllegalStateException("Already initialized");
        }
        try {
            if (CL.getFunctionProvider() == null) {
                CL.create();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        instance = new OpenCL();
        Logger.getLogger(OpenCL.class).info("Created OpenCL context");
    }
    
    public static OpenCL instance() {
        if (!isInitialized()) {
            throw new IllegalStateException("OpenCL is not initialized");
        }
        return instance;
    }
    
    public static boolean isInitialized() {
        return instance != null;
    }
    
    public static void shutdown() {
        CLKernel.cleanup();
        CLProgram.cleanup();
        CLMemory.cleanup();
        CLCommandQueue.cleanup();
        CLContext.cleanup();
        CL.destroy();
        instance = null;
    }
    
    private OpenCL() {
        createPlatformData();
    }
    
    private void createPlatformData() {
        CL10.clGetPlatformIDs(null, tmpBuffer);
        assert tmpBuffer.get(0) != 0;
        platforms = BufferUtils.createPointerBuffer(tmpBuffer.get(0));
        CL10.clGetPlatformIDs(platforms, (IntBuffer) null);
    }
    
    public PointerBuffer getPlatforms() {
        return platforms;
    }
    
    public int getPlatformCount() {
        return platforms.capacity();
    }
    
    public CLPlatform getNextGLInterOpPlatform() {
        for (int i = 0; i < getPlatformCount(); i++) {
            CLPlatform p = getPlatform(i);
            if (p.canGLInterop()) {
                return p;
            }
        }
        throw new OpenCLException("No platform with GL inter-op found!");
    }
    
    public CLPlatform getPlatform(final int platformInd) {
        if (!createdPlatforms.containsKey(platformInd)) {
            createdPlatforms.put(platformInd, new CLPlatform(platforms.get(platformInd)));
        }
        return createdPlatforms.get(platformInd);
    }
    
    public static void checked(int i) {
        if (i != CL10.CL_SUCCESS) {
            Util.stripStacktrace(new OpenCLException(searchConstants(i)), 1);
        }
    }
    
    public static String searchConstants(final int i) {
        for (final Class<?> c : constantsClasses) {
            final Field[] fields = c.getFields();
            for (final Field f : fields) {
                try {
                    if (i == f.getInt(null)) {
                        return f.getName();
                    }
                } catch (final IllegalArgumentException e) {
                } catch (final IllegalAccessException e) {
                }
            }
        }
        throw new IllegalArgumentException("Constant with value '" + i + "' not found");
    }
}
