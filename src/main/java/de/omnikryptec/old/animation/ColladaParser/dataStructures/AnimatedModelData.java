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

import de.omnikryptec.old.resource.loader.ResourceObject;

/**
 * Contains the extracted data for an animated model, which includes the mesh
 * data, and skeleton (joints heirarchy) data.
 *
 * @author Karl
 *
 */
public class AnimatedModelData implements ResourceObject {

    private final String name;
    private final SkeletonData joints;
    private final MeshData mesh;

    public AnimatedModelData(String name, MeshData mesh, SkeletonData joints) {
	this.name = name;
	this.joints = joints;
	this.mesh = mesh;
    }

    public SkeletonData getJointsData() {
	return joints;
    }

    public MeshData getMeshData() {
	return mesh;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public ResourceObject delete() {
	return this;
    }

}
