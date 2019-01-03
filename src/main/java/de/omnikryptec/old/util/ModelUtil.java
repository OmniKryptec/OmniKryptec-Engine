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

package de.omnikryptec.old.util;

import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.model.Model.VBO_TYPE;
import de.omnikryptec.old.resource.model.VertexArrayObject;

public class ModelUtil {

    private static final int QUAD_VERTEX_COUNT = 4;
    // private static final float[] QUAD_VERTICES = {0, 0, 1, 0, 1, 1, 0, 1};
    private static final float[] QUAD_VERTICES = { -1, 1, -1, -1, 1, 1, 1, -1 };
    private static final float[] QUAD_TEX_COORDS = { 0, 0, 0, 1, 1, 1, 1, 0 };
    private static final int[] QUAD_INDICES = { 0, 3, 1, 1, 3, 2 };

    public static Model generateQuad() {
	return generateQuad(VBO_TYPE.NONE);
    }

    public static Model generateQuad(VBO_TYPE type) {
	return generateQuad(QUAD_TEX_COORDS, type);
    }

    public static Model generateQuad(float[] texcoords, VBO_TYPE type) {
	VertexArrayObject vao = VertexArrayObject.create();
	vao.storeDataf(QUAD_INDICES, QUAD_VERTEX_COUNT, QUAD_VERTICES, texcoords);
	return new Model("", vao, type);
    }

}
