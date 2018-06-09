package omnikryptec.gameobject.particlesV2;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.particles.ParticleSpawnArea.ParticleSpawnAreaType;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.graphics.OpenGL;
import omnikryptec.resource.loader.ResourceLoader;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.VertexBufferObject;
import omnikryptec.resource.texture.AtlasTexture;
import omnikryptec.resource.texture.ParticleAtlas;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.Maths;
import omnikryptec.util.ModelUtil;

public class ParticleRenderer {

	private static FrustrumFilter filter = new FrustrumFilter();

	private static int maxInstancesPerSys = 1_000_000;
	private static final int INSTANCE_DATA_LENGTH = 25;

	private static FloatBuffer buffer = BufferUtils.createFloatBuffer(maxInstancesPerSys * INSTANCE_DATA_LENGTH);

	private Model quad;
	private ParticleShader shader;

	private VertexBufferObject vbo;
	private int pointer = 0;

	private float[] vboData;
	private int oldsize = -1;

	private Camera curCam;

	private Vector3f curparpos;

	public ParticleRenderer() {
		quad = ModelUtil.generateQuad();
		vbo = VertexBufferObject.create(GL15.GL_ARRAY_BUFFER);
		vbo.addInstancedAttribute(quad.getVao(), 1, 4, INSTANCE_DATA_LENGTH, 0);
		vbo.addInstancedAttribute(quad.getVao(), 2, 4, INSTANCE_DATA_LENGTH, 4);
		vbo.addInstancedAttribute(quad.getVao(), 3, 4, INSTANCE_DATA_LENGTH, 8);
		vbo.addInstancedAttribute(quad.getVao(), 4, 4, INSTANCE_DATA_LENGTH, 12);
		vbo.addInstancedAttribute(quad.getVao(), 5, 4, INSTANCE_DATA_LENGTH, 16);
		vbo.addInstancedAttribute(quad.getVao(), 6, 1, INSTANCE_DATA_LENGTH, 20);
		vbo.addInstancedAttribute(quad.getVao(), 7, 4, INSTANCE_DATA_LENGTH, 21);
		shader = new ParticleShader();
	}

	private int count;
	private long globalCount;

	public void render(ParticleSimulationAttribute pos, int ps, Camera camera) {
		curCam = camera;
		if (buffer == null || buffer.capacity() != maxInstancesPerSys * INSTANCE_DATA_LENGTH) {
			buffer = BufferUtils.createFloatBuffer(maxInstancesPerSys * INSTANCE_DATA_LENGTH);
		}
		vboData = new float[INSTANCE_DATA_LENGTH];
		shader.start();
		shader.projMatrix.loadMatrix(curCam.getProjectionMatrix());
		filter.setCamera(camera);
		quad.getVao().bind(0, 1, 2, 3, 4, 5, 6, 7);
		globalCount = 0;
		ParticleAtlas tmpt = new ParticleAtlas(ResourceLoader.MISSING_TEXTURE, 2, BlendMode.ADDITIVE);
			bindTexture(tmpt);
			
			count = 0;
			for (int i=0; i<ps; i++) {
				if (count > maxInstancesPerSys) {
					break;
				}
				curparpos = new Vector3f(pos.get(i, 0),pos.get(i, 1),pos.get(i, 2));
				
					updateModelViewMatrix(curparpos, 1, 1, curCam.getViewMatrix(),
							vboData);
					updateTexCoordInfo(vboData);
					count++;
					globalCount++;
				
			}
			vbo.updateData(vboData, buffer);
			if (count > 0) {
				OpenGL.gl31drawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVao().getIndexCount(), count);
			}
	
		GraphicsUtil.blendMode(BlendMode.DISABLE);
	}

	public long getParticleCount() {
		return globalCount;
	}

	private void bindTexture(ParticleAtlas texture) {
		GraphicsUtil.blendMode(texture.getBlendMode());
		texture.getTexture().bindToUnitOptimized(0);
		shader.nrOfRows.loadFloat(texture.getNumberOfRows());
		shader.uvs.loadVec4(texture.getTexture().getUVs()[0], texture.getTexture().getUVs()[1],
				texture.getTexture().getUVs()[2], texture.getTexture().getUVs()[3]);
	}

	private void updateTexCoordInfo(float[] data) {
		data[pointer++] = 0;
		data[pointer++] = 0;
		data[pointer++] = 1;
		data[pointer++] = 1;
		data[pointer++] = 1;
		data[pointer++] = 1;
		data[pointer++] = 1;
		data[pointer++] = 1;
		data[pointer++] = 1;

	}

	private Vector3f tmp = new Vector3f();
	private Matrix4f tmpm = new Matrix4f();
	private Matrix4f modelMatrix = new Matrix4f();

	private void updateModelViewMatrix(Vector3f pos, float rot, float scale, Matrix4f viewMatrix, float[] vboData) {
		modelMatrix.identity();
		modelMatrix.translate(pos);
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.rotate((float) Math.toRadians(rot), Maths.Z);
		tmp.set(scale, scale, scale);
		modelMatrix.scale(tmp);
		tmpm = viewMatrix.mul(modelMatrix, tmpm);
		storeMatrixData(tmpm, vboData);
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
