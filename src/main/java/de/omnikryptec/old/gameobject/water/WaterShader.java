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

package de.omnikryptec.old.gameobject.water;

import de.omnikryptec.old.shader.base.Shader;

/**
 * WaterShader
 *
 * @author Panzer1119
 */
public class WaterShader extends Shader {

    private static final String SHADER_FOLDER = "/de/omnikryptec/old/gameobject/water/";

    /* VertexShader Uniforms */
    /*
     * public static final UniformMatrix transformationMatrix = new
     * UniformMatrix("transformationMatrix"); public static final UniformMatrix
     * projectionMatrix = new UniformMatrix("projectionMatrix"); public static final
     * UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
     */

    /* FragmentShader Uniforms */
    /*
     * public static final UniformSampler backgroundTexture = new
     * UniformSampler("backgroundTexture"); public static final UniformSampler
     * rTexture = new UniformSampler("rTexture"); public static final UniformSampler
     * gTexture = new UniformSampler("gTexture"); public static final UniformSampler
     * bTexture = new UniformSampler("bTexture"); public static final UniformSampler
     * blendMap = new UniformSampler("blendMap");
     */

    public WaterShader() {
	super(WaterShader.class.getResourceAsStream(SHADER_FOLDER + "waterVertexShader.txt"),
		WaterShader.class.getResourceAsStream(SHADER_FOLDER
			+ "waterFragmentShader.txt")/*
						     * , "position", "textureCoordinates", "normal", "tangents",
						     * transformationMatrix, projectionMatrix, viewMatrix,
						     * backgroundTexture, rTexture, gTexture, bTexture, blendMap
						     */);
	start();
	/*
	 * backgroundTexture.loadTexUnit(0); rTexture.loadTexUnit(1);
	 * gTexture.loadTexUnit(2); bTexture.loadTexUnit(3); blendMap.loadTexUnit(4);
	 */
    }

}
