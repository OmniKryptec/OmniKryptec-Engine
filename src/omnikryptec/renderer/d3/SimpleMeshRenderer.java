package omnikryptec.renderer.d3;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Model;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderGroup;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.shader.files.render.SimpleMeshShader;
import omnikryptec.util.Color;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class SimpleMeshRenderer extends Renderer{
    public static final int INSTANCED_DATA_LENGTH = 20;
    private static final int INSTANCES_PER_DRAWCALL = Instance.getGameSettings().getMaxInstancesPerDrawcall();


    public SimpleMeshRenderer() {
        super(new ShaderPack(new ShaderGroup(new SimpleMeshShader())));
    	RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private Entity entity;
    private TexturedModel textmodel;
    private long vertcount = 0;
    private Model model;

    @Override
    public long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader shader, FrustrumFilter filter) {
    	vertcount = 0;
        for (AdvancedModel advancedModel : entities.keysArray()) {
            if (advancedModel == null || !(advancedModel instanceof TexturedModel)) {
                if (Logger.isDebugMode()) {
                    Logger.log("Wrong renderer for AdvancedModel set! (" + advancedModel + ")", LogLevel.WARNING);
                }
                continue;
            }
            textmodel = (TexturedModel) advancedModel;
            model = textmodel.getModel();
            shader.onModelRenderStart(textmodel);
            stapel = entities.get(textmodel);
            for (int j = 0; j < stapel.size(); j += INSTANCES_PER_DRAWCALL) {
                newRender(s, j, filter);
            }
            stapel = null;
            shader.onModelRenderEnd(textmodel);
        }
        return vertcount;
    }

    private FloatBuffer buffer;
    private int pointer;
    private int count;
    private float[] array;
    private int instances;

    private void newRender(AbstractScene3D s, int offset, FrustrumFilter filter) {
        instances = Math.min(stapel.size(), INSTANCES_PER_DRAWCALL + offset);
        array = new float[Math.min(stapel.size(), INSTANCES_PER_DRAWCALL) * INSTANCED_DATA_LENGTH];
        pointer = 0;
        count = 0;
        for (int j = offset; j < instances; j++) {
            entity = stapel.get(j);
            if (entity.isRenderingEnabled()) {
                if (filter.intersects(entity, true)) {
                	updateArray(entity.getTransformation(), entity.getColor(), array);
                    count++;
                }
            }
        }
        if (buffer == null || buffer.capacity() < array.length) {
            buffer = BufferUtils.createFloatBuffer(array.length);
        }
        model.getUpdateableVBO().updateData(array, buffer);
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, textmodel.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0, count);
        vertcount += model.getModelData().getVertexCount() * count;
    }

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