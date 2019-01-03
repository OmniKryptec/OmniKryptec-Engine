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

package de.omnikryptec.graphics.shader.base.uniform;

public abstract class Uniform {

    private static final int NOT_FOUND = -1;

    private final String name;
    private int location;
    private boolean muted = false;
    private final boolean isfound = false;

    protected Uniform(final String name) {
        this(name, false);
    }

    protected Uniform(final String name, final boolean mute) {
        this.name = name;
        this.muted = mute;
    }

    // protected void storeUniformLocation(Shader shader) {
    // location = GL20.glGetUniformLocation(shader.getId(), name);
    // if(location == NOT_FOUND) {
    // isfound = false;
    // if (!muted /*&& Logger.isDebugMode()*/) {
    // System.out.println(shader.getName() + ": No uniform variable called " + name
    // + " found!"/*, LogLevel.WARNING*/);
    // }
    // }else {
    // isfound = true;
    // }
    // }

    protected int getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + " Location: " + this.location;
    }

    public Uniform mute() {
        this.muted = true;
        return this;
    }

    public boolean isFound() {
        return this.isfound;
    }

}
