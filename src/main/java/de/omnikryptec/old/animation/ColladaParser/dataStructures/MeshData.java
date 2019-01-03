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

import de.omnikryptec.old.resource.objConverter.ModelData;

/**
 * This object contains all the mesh data for an animated model that is to be
 * loaded into the VAO.
 *
 * @author Karl
 *
 */
public class MeshData extends ModelData {

    private int[] jointIds;
    private float[] vertexWeights;

    public MeshData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, int[] jointIds,
	    float[] vertexWeights) {
	super(vertices, textureCoords, normals, normals, indices, 0);
	this.jointIds = jointIds;
	this.vertexWeights = vertexWeights;
    }

    public MeshData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices,
	    int[] jointIds, float[] vertexWeights) {
	super(vertices, textureCoords, normals, tangents, indices, 0);
	this.jointIds = jointIds;
	this.vertexWeights = vertexWeights;
    }

    public int[] getJointIds() {
	return jointIds;
    }

    public float[] getVertexWeights() {
	return vertexWeights;
    }

}
