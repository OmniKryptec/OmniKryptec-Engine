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

package de.omnikryptec.old.renderer.d3;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import de.omnikryptec.old.gameobject.Entity;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.resource.model.AdvancedModel;
import de.omnikryptec.old.resource.model.Model;
import de.omnikryptec.old.resource.model.TexturedModel;
import de.omnikryptec.old.shader.base.Shader;
import de.omnikryptec.old.shader.base.ShaderGroup;
import de.omnikryptec.old.shader.base.ShaderPack;
import de.omnikryptec.old.shader.files.render.ForwardMeshShader;
import de.omnikryptec.old.util.FrustrumFilter;
import de.omnikryptec.old.util.Instance;
import de.omnikryptec.old.util.KeyArrayHashMap;
import de.omnikryptec.old.util.logger.LogLevel;
import de.omnikryptec.old.util.logger.Logger;
import de.omnikryptec.util.data.Color;

/**
 * renders with per-pixel light.
 * 
 * @author pcfreak9000
 *
 */
public class ForwardMeshRenderer extends Renderer {

    public static final int INSTANCED_DATA_LENGTH = 20;
    private static final int INSTANCES_PER_DRAWCALL = Instance.getGameSettings().getMaxInstancesPerDrawcall();

    public ForwardMeshRenderer() {
	super(new ShaderPack(new ShaderGroup(new ForwardMeshShader(true)).addShader(1, new ForwardMeshShader(false))));
	RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private Entity entity;
    private long vertcount = 0;
    private Model model;

    @Override
    public long render(AbstractScene3D s, KeyArrayHashMap<AdvancedModel, List<Entity>> entities, Shader b,
	    FrustrumFilter f) {
	if (!OmniKryptecEngine.instance().getDisplayManager().getSettings().isLightForwardAllowed()
		&& Logger.isDebugMode()) {
	    Logger.log("Forward light is not enabled. Will not render.", LogLevel.WARNING);
	    return 0;
	}
	vertcount = 0;
	for (AdvancedModel advancedModel : entities.keysArray()) {
	    if (advancedModel == null || !(advancedModel instanceof TexturedModel)) {
		if (Logger.isDebugMode()) {
		    Logger.log("Wrong renderer for AdvancedModel set! (" + advancedModel + ")", LogLevel.WARNING);
		}
		continue;
	    }
	    model = advancedModel.getModel();
	    b.onModelRenderStart(advancedModel);
	    stapel = entities.get(advancedModel);
	    for (int j = 0; j < stapel.size(); j += INSTANCES_PER_DRAWCALL) {
		newRender(s, j, advancedModel, f);
	    }
	    b.onModelRenderEnd(advancedModel);
	}
	return vertcount;
    }

    private FloatBuffer buffer;
    private int pointer;
    private int count;
    private float[] array;
    private int instances;

    private void newRender(AbstractScene3D s, int offset, AdvancedModel amodel, FrustrumFilter f) {
	instances = Math.min(stapel.size(), INSTANCES_PER_DRAWCALL + offset);
	array = new float[Math.min(stapel.size(), INSTANCES_PER_DRAWCALL) * INSTANCED_DATA_LENGTH];
	pointer = 0;
	count = 0;
	for (int j = offset; j < instances; j++) {
	    entity = stapel.get(j);
	    if (entity.isRenderingEnabled()) {
		if (f.intersects(entity, true)) {
		    updateArray(entity.getTransformation(), entity.getColor(), array);
		    count++;
		}
	    }
	}
	if (buffer == null || buffer.capacity() < array.length) {
	    buffer = BufferUtils.createFloatBuffer(array.length);
	}
	model.getUpdateableVBO().updateData(array, buffer);
	GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, amodel.getModel().getVao().getIndexCount(),
		GL11.GL_UNSIGNED_INT, 0, count);
	vertcount += model.getModelData().getVertexCount() * count;
    }

//enable the uniforms in the shader
//**
//	private void oldRender(boolean onlyRender, Scene s){
//		for (int j = 0; j < stapel.size(); j++) {
//			entity = stapel.get(j);
//			if (entity.isActive() && RenderUtil.inRenderRange(entity, s.getCamera())) {
//				if (!onlyRender) {
//					entity.doLogic0();
//				}
//				shader.transformation.loadMatrix(entity.getTransformationMatrix());
//				shader.colmod.loadVec4(entity.getColor().getVector4f());
//				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVao().getIndexCount(),
//						GL11.GL_UNSIGNED_INT, 0);
//				vertcount += model.getModelData().getVertexCount();
//			}
//		}
//	}
    private void updateArray(Matrix4f transformationMatrix, Color color, float[] array) {
	storeMatrixData(transformationMatrix, array);
	array[pointer++] = color.getR();
	array[pointer++] = color.getG();
	array[pointer++] = color.getB();
	array[pointer++] = color.getA();
    }

    private void storeMatrixData(Matrix4f matrix, float[] vboData) {
	vboData[pointer++] = matrix.m00();
	vboData[pointer++] = matrix.m01();
	vboData[pointer++] = matrix.m02();
	vboData[pointer++] = matrix.m03();
	vboData[pointer++] = matrix.m10();
	vboData[pointer++] = matrix.m11();
	vboData[pointer++] = matrix.m12();
	vboData[pointer++] = matrix.m13();
	vboData[pointer++] = matrix.m20();
	vboData[pointer++] = matrix.m21();
	vboData[pointer++] = matrix.m22();
	vboData[pointer++] = matrix.m23();
	vboData[pointer++] = matrix.m30();
	vboData[pointer++] = matrix.m31();
	vboData[pointer++] = matrix.m32();
	vboData[pointer++] = matrix.m33();
    }

}
