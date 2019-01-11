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

package de.omnikryptec.libapi.opengl.shader;

public abstract class GLUniform {

    private final String name;
    private int location;
    private final boolean isfound = false;

    protected GLUniform(final String name) {
        this.name = name;
    }

    protected int getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + " Location: " + this.location;
    }
    
    public boolean isFound() {
        return this.isfound;
    }

}
