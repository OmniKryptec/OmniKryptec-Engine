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

package de.omnikryptec.animation.ColladaParser.dataStructures;

import de.omnikryptec.resource.objConverter.Vertex;
import org.joml.Vector3f;

public class AnimatedVertex extends Vertex {

    private final VertexSkinData weightsData;

    public AnimatedVertex(int index, Vector3f position, VertexSkinData weightsData) {
        super(index, position);
        this.weightsData = weightsData;
    }

    public VertexSkinData getWeightsData() {
        return weightsData;
    }

}