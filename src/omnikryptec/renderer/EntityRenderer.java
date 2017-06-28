package omnikryptec.renderer;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Entity;
import omnikryptec.entity.Light;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Material;
import omnikryptec.model.Model;
import omnikryptec.model.TexturedModel;
import omnikryptec.shader_files.EntityLightShader;
import omnikryptec.texture.Texture;
import omnikryptec.util.Color;
import omnikryptec.util.RenderUtil;

public class EntityRenderer implements Renderer {
	
	public static final int INSTANCED_DATA_LENGTH=20;
	private static final int INSTANCES_PER_DRAWCALL=1000000; //TODO einstellbar
	
	private EntityLightShader shader;

	public EntityRenderer() {
		RendererRegistration.register(this);
		shader = new EntityLightShader();
		
	}

	private List<Entity> stapel;
	private Entity entity;
	private TexturedModel textmodel;
	private Material mat;
	private Texture textmp;
	private long vertcount=0;
	private Vector3f pos;
	private Light l;
	private Model model;
	
	private int tmp;
	@Override
	public long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean onlyRender) {
		if (!DisplayManager.instance().getSettings().isLightForwardAllowed() && Logger.isDebugMode()) {
			Logger.log("Forward light is not enabled. Will not render.", LogLevel.WARNING);
			return 0;
		}
		vertcount = 0;
		shader.start();
		shader.view.loadMatrix(s.getCamera().getViewMatrix());
		shader.projection.loadMatrix(s.getCamera().getProjectionMatrix());
		shader.ambient.loadVec3(s.getAmbient().getArray());
		int lights = Math.min(DisplayManager.instance().getSettings().getLightMaxForward(),
				s.getForwardRenderLights().size());
		shader.activelights.loadInt(lights);
		for (int i = 0; i < lights; i++) {
			l = s.getForwardRenderLights().get(i);
			if(!onlyRender){
				l.doLogic0();
			}
			pos = l.getAbsolutePos();
			shader.lightpos[i].loadVec4(pos.x, pos.y, pos.z, l.isDirectional()?0.0f:1.0f);
			shader.lightcolor[i].loadVec3(l.getColor().getArray());
			shader.atts[i].loadVec4(l.getAttenuation());
			shader.coneinfo[i].loadVec4(l.getConeInfo());
			shader.catts[i].loadVec3(l.getConeAttenuation());
		}
		for (int i = 0; i < entities.keysArray().length; i++) {
			if (!(entities.keysArray()[i] instanceof TexturedModel)) {
				if(Logger.isDebugMode()){
					Logger.log("Wrong renderer for AdvancedModel set! ("+entities.keysArray()[i]+")", LogLevel.WARNING);
				}
				continue;
			}
			textmodel = (TexturedModel) entities.keysArray()[i];
			model = textmodel.getModel();
			model.getVao().bind(0, 1, 2, 3,4,5,6,7,8);
			textmp = textmodel.getTexture();
			textmp.bindToUnit(0);
			shader.uvs.loadVec4(textmp.getUVs()[0], textmp.getUVs()[1], textmp.getUVs()[2], textmp.getUVs()[3]);
			mat = textmodel.getMaterial();
			if (mat.getNormalmap() != null) {
				mat.getNormalmap().bindToUnit(1);
				shader.hasnormal.loadBoolean(true);
			} else {
				shader.hasnormal.loadBoolean(false);
			}
			if (mat.hasTransparency()) {
				RenderUtil.cullBackFaces(false);
			}
			if (mat.getSpecularmap() != null) {
				mat.getSpecularmap().bindToUnit(2);
				shader.hasspecular.loadBoolean(true);
			} else {
				shader.hasspecular.loadBoolean(false);
			}
			if (mat.getExtraInfo() != null) {
				mat.getExtraInfo().bindToUnit(3);
				shader.hasextrainfomap.loadBoolean(true);
			} else {
				shader.hasextrainfomap.loadBoolean(false);
				if (mat.getExtraInfoVec() != null) {
					shader.extrainfovec.loadVec3(mat.getExtraInfoVec());
				} else {
					shader.extrainfovec.loadVec3(0, 0, 0);
				}
			}
			shader.matData.loadVec4(mat.getMData());
			stapel = entities.get(textmodel);
			for(int j=0; j<stapel.size(); j+=INSTANCES_PER_DRAWCALL){
				newRender(onlyRender, s, j);
			}
			if (textmodel.getMaterial().hasTransparency()) {
				RenderUtil.cullBackFaces(true);
			}
		}
		return vertcount;
	}
	
	private FloatBuffer buffer;
	private int pointer;
	private int count;
	private float[] array;
	private int instances;
	private void newRender(boolean onlyRender, Scene s, int offset){
		instances = Math.min(stapel.size(), INSTANCES_PER_DRAWCALL+offset);
		array = new float[Math.min(stapel.size(), INSTANCES_PER_DRAWCALL)*INSTANCED_DATA_LENGTH];
		pointer = 0;
		count = 0;
		for(int j=offset; j<instances; j++){
			entity = stapel.get(j);
			if (entity.isActive() && RenderUtil.inRenderRange(entity, s.getCamera())) {
				if (!onlyRender) {
					entity.doLogic0();
				}
				updateArray(entity.getTransformationMatrix(), entity.getColor(), array);
				count++;
			}
		}
		if(buffer == null || buffer.capacity()<array.length){
			buffer = BufferUtils.createFloatBuffer(array.length);
		}
		model.getUpdateableVBO().updateData(array, buffer);
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, textmodel.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0, count);
		vertcount += model.getModelData().getVertexCount()*stapel.size();
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
//				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(),
//						GL11.GL_UNSIGNED_INT, 0);
//				vertcount += model.getModel().getModelData().getVertexCount();
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
	
	@Override
	public void cleanup() {

	}

	@Override
	public float expensiveLevel() {
		return 0;
	}

}
