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

package de.omnikryptec.old.physics;

import org.dyn4j.geometry.Rectangle;

import de.omnikryptec.old.util.ConverterUtil;

public class AdvancedRectangle extends Rectangle {

    public AdvancedRectangle(float w, float h) {
	this(ConverterUtil.convertToPhysics2D(w), ConverterUtil.convertToPhysics2D(h));
    }

    public AdvancedRectangle(double w, double h) {
	super(w, h);
    }

}
