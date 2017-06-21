package omnikryptec.particles;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Camera;
import omnikryptec.logger.Logger;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.model.Model;
import omnikryptec.model.VertexBufferObject;
import omnikryptec.objConverter.ModelData;
import omnikryptec.util.Maths;
import omnikryptec.util.ModelUtil;
import omnikryptec.util.RenderUtil;

public class ParticleRenderer {

//	private static final float[] VERTICES = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	private static int maxInstancesPerSys = 10_000_000;
	private static final int INSTANCE_DATA_LENGTH = 21;

	private static FloatBuffer buffer = BufferUtils.createFloatBuffer(maxInstancesPerSys * INSTANCE_DATA_LENGTH);;

	private Model quad;
	private ParticleShader shader;

	private VertexBufferObject vbo;
	private int pointer = 0;
	
	private float[] vboData;
	private int oldsize=-1;
	
	private Camera curCam;
	
	protected ParticleRenderer() {
		quad = ModelUtil.generateQuad();
		vbo = VertexBufferObject.createEmpty(GL15.GL_ARRAY_BUFFER, maxInstancesPerSys*INSTANCE_DATA_LENGTH);
		vbo.addInstancedAttribute(quad.getVao(), 1, 4, INSTANCE_DATA_LENGTH, 0);
		vbo.addInstancedAttribute(quad.getVao(), 2, 4, INSTANCE_DATA_LENGTH, 4);
		vbo.addInstancedAttribute(quad.getVao(), 3, 4, INSTANCE_DATA_LENGTH, 8);
		vbo.addInstancedAttribute(quad.getVao(), 4, 4, INSTANCE_DATA_LENGTH, 12);
		vbo.addInstancedAttribute(quad.getVao(), 5, 4, INSTANCE_DATA_LENGTH, 16);
		vbo.addInstancedAttribute(quad.getVao(), 6, 1, INSTANCE_DATA_LENGTH, 20);
		shader = new ParticleShader();
	}
	
	private List<Particle> particleList;
	private int count;
	private long globalCount;
	
	protected void render(Map<ParticleTexture, List<Particle>> particles, Camera camera) {
		curCam = camera;
		if(buffer==null||buffer.capacity()!=maxInstancesPerSys*INSTANCE_DATA_LENGTH){
			buffer = BufferUtils.createFloatBuffer(maxInstancesPerSys * INSTANCE_DATA_LENGTH);
		}
		shader.start();
		shader.projMatrix.loadMatrix(curCam.getProjectionMatrix());
		quad.getVao().bind(0,1,2,3,4,5,6);
		globalCount = 0;
		for (ParticleTexture tmpt : particles.keySet()) {
			bindTexture(tmpt);
			particleList = particles.get(tmpt);
			pointer = 0;
			if(vboData==null||particleList.size() * INSTANCE_DATA_LENGTH!=oldsize){
				vboData = new float[(oldsize = particleList.size() * INSTANCE_DATA_LENGTH)];
			}
			count = 0;
			for (Particle par : particleList) {
				if(count>maxInstancesPerSys){
					break;
				}
				if(RenderUtil.inRenderRange(par, curCam)){
					updateModelViewMatrix(par.getAbsolutePos(), par.getRot(), par.getScale(), curCam.getViewMatrix(), vboData);
					updateTexCoordInfo(par, vboData);
					count++;
					globalCount++;
				}
			}
			vbo.updateData(vboData, buffer);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVao().getIndexCount(), count);
		}
		RenderUtil.disableBlending();
	}

	public long getParticleCount(){
		return globalCount;
	}
	
	private void bindTexture(ParticleTexture texture) {
		if (texture.useAlphaBlending()) {
			RenderUtil.enableAdditiveBlending();
		} else {
			RenderUtil.enableAlphaBlending();
		}
		texture.getTexture().bindToUnit(0);
		shader.nrOfRows.loadFloat(texture.getNumberOfRows());
	}

	private void updateTexCoordInfo(Particle par, float[] data) {
		data[pointer++] = par.getTexOffset1().x;
		data[pointer++] = par.getTexOffset1().y;
		data[pointer++] = par.getTexOffset2().x;
		data[pointer++] = par.getTexOffset2().y;
		data[pointer++] = par.getBlend();
	}
	
	private Vector3f tmp = new Vector3f();
	private Matrix4f tmpm;
	private Matrix4f modelMatrix = new Matrix4f();
	
	private void updateModelViewMatrix(Vector3f pos, float rot, float scale, Matrix4f viewMatrix, float[] vboData) {
		modelMatrix.setIdentity();
		Matrix4f.translate(pos, modelMatrix, modelMatrix);
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float) Math.toRadians(rot), Maths.Z, modelMatrix, modelMatrix);
		tmp.set(scale, scale, scale);
		Matrix4f.scale(tmp, modelMatrix, modelMatrix);
		tmpm = Matrix4f.mul(viewMatrix, modelMatrix, tmpm);
		storeMatrixData(tmpm, vboData);
	}

	private void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00;
		vboData[pointer++] = matrix.m01;
		vboData[pointer++] = matrix.m02;
		vboData[pointer++] = matrix.m03;
		vboData[pointer++] = matrix.m10;
		vboData[pointer++] = matrix.m11;
		vboData[pointer++] = matrix.m12;
		vboData[pointer++] = matrix.m13;
		vboData[pointer++] = matrix.m20;
		vboData[pointer++] = matrix.m21;
		vboData[pointer++] = matrix.m22;
		vboData[pointer++] = matrix.m23;
		vboData[pointer++] = matrix.m30;
		vboData[pointer++] = matrix.m31;
		vboData[pointer++] = matrix.m32;
		vboData[pointer++] = matrix.m33;
	}

	protected void cleanUp() {
		shader.cleanup();
	}


}
