package omnikryptec.renderer;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import omnikryptec.display.DisplayManager;
import omnikryptec.gameobject.Entity;
import omnikryptec.gameobject.Light;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Material;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.resource.texture.Texture;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.shader.files.render.ForwardPPMeshShader;
import omnikryptec.util.Color;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.Instance;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 * renders  with per-pixel light.
 * @author pcfreak9000
 *
 */
public class ForwardMeshRenderer extends Renderer<ForwardPPMeshShader> {

    public static final int INSTANCED_DATA_LENGTH = 20;
    private static final int INSTANCES_PER_DRAWCALL = Instance.getGameSettings().getMaxInstancesPerDrawcall();

    public ForwardMeshRenderer() {
        super(new ShaderPack<>(new ForwardPPMeshShader()));
        RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private Entity entity;
    private Material mat;
    private long vertcount = 0;
    private Model model;

    @Override
    public long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader b, FrustrumFilter f) {
        if (!DisplayManager.instance().getSettings().isLightForwardAllowed() && Logger.isDebugMode()) {
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
            mat = advancedModel.getMaterial();
            b.onModelRender(advancedModel);
            if (mat.hasTransparency()) {
                RenderUtil.cullBackFaces(false);
            }
            stapel = entities.get(advancedModel);
            for (int j = 0; j < stapel.size(); j += INSTANCES_PER_DRAWCALL) {
                newRender(s, j, advancedModel, f);
            }
            if (advancedModel.getMaterial().hasTransparency()) {
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

    private void newRender(Scene s, int offset, AdvancedModel amodel, FrustrumFilter f) {
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
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, amodel.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0, count);
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
