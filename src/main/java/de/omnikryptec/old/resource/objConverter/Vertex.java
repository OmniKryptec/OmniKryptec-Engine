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

package de.omnikryptec.old.resource.objConverter;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private static final int NO_INDEX = -1;

    private Vector3f position;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private Vertex duplicateVertex = null;
    private int index;
    private float length;
    private List<Vector3f> tangents = new ArrayList<>();
    private Vector3f averagedTangent = new Vector3f(0, 0, 0);

    public Vertex(int index, Vector3f position) {
	this.index = index;
	this.position = position;
	this.length = position.length();
    }

    public void addTangent(Vector3f tangent) {
	tangents.add(tangent);
    }

    // NEW
    public Vertex duplicate(int newIndex) {
	Vertex vertex = new Vertex(newIndex, position);
	vertex.tangents = this.tangents;
	return vertex;
    }

    public void averageTangents() {
	if (tangents.isEmpty()) {
	    return;
	}
	for (Vector3f tangent : tangents) {
	    averagedTangent.add(tangent);
	}
	averagedTangent.normalize();
    }

    public Vector3f getAverageTangent() {
	return averagedTangent;
    }

    public int getIndex() {
	return index;
    }

    public float getLengthSquared() {
	return length;
    }

    public float getLength() {
	return (float) Math.sqrt(getLengthSquared());
    }

    public boolean isSet() {
	return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
    }

    public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
	return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
    }

    public void setTextureIndex(int textureIndex) {
	this.textureIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex) {
	this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
	return position;
    }

    public int getTextureIndex() {
	return textureIndex;
    }

    public int getNormalIndex() {
	return normalIndex;
    }

    public Vertex getDuplicateVertex() {
	return duplicateVertex;
    }

    public void setDuplicateVertex(Vertex duplicateVertex) {
	this.duplicateVertex = duplicateVertex;
    }

}
