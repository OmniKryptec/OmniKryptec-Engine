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

package de.omnikryptec.old.animation.ColladaParser.dataStructures;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the extracted data for a single joint in the model. This stores the
 * joint's index, name, and local bind transform.
 *
 * @author Karl
 *
 */
public class JointData {

    public final int index;
    public final String nameId;
    public final Matrix4f bindLocalTransform;

    public final List<JointData> children = new ArrayList<>();

    public JointData(int index, String nameId, Matrix4f bindLocalTransform) {
	this.index = index;
	this.nameId = nameId;
	this.bindLocalTransform = bindLocalTransform;
    }

    public void addChild(JointData child) {
	children.add(child);
    }

}
