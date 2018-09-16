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

package de.omnikryptec.postprocessing.main;

import org.jdom2.Element;
import org.lwjgl.opengl.GL11;

import de.codemakers.serialization.XMLable;

public class RenderTarget implements XMLable {

    public final int target;
    public final int extended;

    public RenderTarget(int target) {
        this(target, GL11.GL_RGBA8);
    }

    public RenderTarget(int target, int extended) {
        this.target = target;
        this.extended = extended;
    }

    @Override
    public final Element toXML() {
        return new Element(getClass().getSimpleName()).setAttribute("target", "" + target).setAttribute("extended", "" + extended);
    }

}
