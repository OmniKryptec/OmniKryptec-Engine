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

package de.omnikryptec.libapi.opengl.buffer;

import org.lwjgl.opengl.GL15;

import de.omnikryptec.libapi.LibAPIManager;

import java.util.ArrayList;
import java.util.List;

public abstract class GLBuffer {

    private static final List<GLBuffer> all = new ArrayList<>();

    static {
        LibAPIManager.registerResourceShutdownHooks(() -> cleanup());
    }

    private final int pointer;
    private final int type;

    public GLBuffer(int type) {
        this.type = type;
        this.pointer = GL15.glGenBuffers();
        all.add(this);
    }

    private static void cleanup() {
        while (!all.isEmpty()) {
            all.get(0).deleteBuffer();
        }
    }

    public void deleteBuffer() {
        GL15.glDeleteBuffers(pointer);
        all.remove(this);
    }

    public int bufferId() {
        return pointer;
    }

    public int bufferType() {
        return type;
    }

    public void bindBuffer() {
        GL15.glBindBuffer(type, pointer);
    }

    @Deprecated
    public void unbindBuffer() {
        GL15.glBindBuffer(type, 0);
    }

}
