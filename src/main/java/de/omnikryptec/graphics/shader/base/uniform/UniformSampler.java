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

public class UniformSampler extends Uniform {

    private int currentValue;
    private boolean used = false;

    public UniformSampler(final String name) {
        super(name);
    }

    public void loadTexUnit(final int texUnit) {
        if (isFound() && (!this.used || this.currentValue != texUnit)) {
            GL20.glUniform1i(super.getLocation(), texUnit);
            this.used = true;
            this.currentValue = texUnit;
        }
    }

}
