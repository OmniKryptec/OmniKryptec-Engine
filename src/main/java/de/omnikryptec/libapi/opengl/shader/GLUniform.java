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

package de.omnikryptec.libapi.opengl.shader;

import de.omnikryptec.libapi.exposed.render.shader.Uniform;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;
import org.lwjgl.opengl.GL20;

public abstract class GLUniform implements Uniform {
    
    private final String name;
    private int location;
    private boolean isfound = false;
    
    protected GLUniform(final String name) {
        this.name = name;
    }
    
    protected void storeUniformLocation(final int programID) {
        this.location = GL20.glGetUniformLocation(programID, this.name);
        if (this.location == -1) {
            Logger.log(this.getClass(), LogType.Warning, "No uniform variable called " + this.name + " is being used!");
        } else {
            this.isfound = true;
        }
    }
    
    protected int getLocation() {
        return this.location;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ", Name: " + this.name + " Location: " + this.location;
    }
    
    @Override
    public boolean existsInCompilation() {
        return this.isfound;
    }
    
}
