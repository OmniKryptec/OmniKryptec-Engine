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

package de.omnikryptec.graphics.shader.base.uniform;

import org.lwjgl.opengl.GL20;

public class UniformInt extends Uniform {

    private int currentValue;
    private boolean used = false;

    public UniformInt(String name) {
        super(name);
    }

    public void loadInt(int value) {
        if (isFound() && (!used || currentValue != value)) {
            GL20.glUniform1i(super.getLocation(), value);
            used = true;
            currentValue = value;
        }
    }

}
