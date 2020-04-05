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

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;
import org.lwjgl.opencl.CL20;
import org.lwjgl.opencl.CL21;
import org.lwjgl.opencl.CL22;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.util.Util;

public class OpenCL {

    private static final Class<?>[] constantsClasses = { CL10.class, CL12.class, CL20.class, CL21.class, CL22.class };
    private static boolean created;
    static IntBuffer tmpBuffer;

    private final HashMap<Integer, CLPlatform> createdPlatforms = new HashMap<>();
    private PointerBuffer platforms;

    static {
        tmpBuffer = BufferUtils.createIntBuffer(1);
    }

    /**
     * engine-intern. Use {@link LibAPIManager} instead.
     *
     * @see de.omnikryptec.libapi.exposed.LibAPIManager
     */
    public void shutdown() {
        CLKernel.cleanup();
        CLProgram.cleanup();
        CLMemory.cleanup();
        CLCommandQueue.cleanup();
        CLContext.cleanup();
        CL.destroy();
    }

    /**
     * engine-intern. Use {@link LibAPIManager} instead.
     *
     * @see de.omnikryptec.libapi.exposed.LibAPIManager
     */
    public OpenCL() {
        if (created) {
            throw new IllegalStateException("OpenCL has already been created");
        }
        createPlatformData();
    }

    private void createPlatformData() {
        CL10.clGetPlatformIDs(null, tmpBuffer);
        assert tmpBuffer.get(0) != 0;
        this.platforms = BufferUtils.createPointerBuffer(tmpBuffer.get(0));
        CL10.clGetPlatformIDs(this.platforms, (IntBuffer) null);
    }

    public PointerBuffer getPlatforms() {
        return this.platforms;
    }

    public int getPlatformCount() {
        return this.platforms.capacity();
    }

    public CLPlatform getNextGLInterOpPlatform() {
        for (int i = 0; i < getPlatformCount(); i++) {
            final CLPlatform p = getPlatform(i);
            if (p.canGLInterop()) {
                return p;
            }
        }
        throw new OpenCLException("No platform with GL inter-op found!");
    }

    public CLPlatform getPlatform(final int platformInd) {
        if (!this.createdPlatforms.containsKey(platformInd)) {
            this.createdPlatforms.put(platformInd, new CLPlatform(this.platforms.get(platformInd)));
        }
        return this.createdPlatforms.get(platformInd);
    }

    public static void checked(final int i) {
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
