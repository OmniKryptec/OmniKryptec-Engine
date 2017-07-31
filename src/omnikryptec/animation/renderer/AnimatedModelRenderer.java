package omnikryptec.animation.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

import org.joml.Vector3f;

/**
 *
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 *
 * @author Karl &amp; Panzer1119
 *
 */
public class AnimatedModelRenderer extends Renderer<AnimatedModelShader> {

    public static final Vector3f LIGHT_DIR = new Vector3f(0.2f, -0.3f, -0.8f);

    /**
     * Initializes the shader program used for rendering animated models.
     */
    public AnimatedModelRenderer() {
        super(new ShaderPack<>(new AnimatedModelShader()));
        setExpensiveLevel(1);
        setPriority(0);
        RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private AnimatedModel animatedModel;
    private Entity entity;
    private long vertcount = 0;

    @Override
    protected long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader ownshader, FrustrumFilter filter) {
        final Camera camera = s.getCamera();
        vertcount = 0;
        shaderpack.getDefaultShader().projectionMatrix.loadMatrix(camera.getProjectionMatrix());
        shaderpack.getDefaultShader().viewMatrix.loadMatrix(camera.getViewMatrix());
        shaderpack.getDefaultShader().lightDirection.loadVec3(LIGHT_DIR);
        for (AdvancedModel advancedModel : entities.keysArray()) {
            if (advancedModel == null || !(advancedModel instanceof AnimatedModel)) {
                if (Logger.isDebugMode()) {
                    Logger.log("Wrong renderer for AdvancedModel set! (" + advancedModel + ")", LogLevel.WARNING);
                }
                continue;
            }
            animatedModel = (AnimatedModel) advancedModel;
            animatedModel.getModel().getVao().bind(0, 1, 2, 3, 4, 5);
            animatedModel.getTexture().bindToUnitOptimized(0);
            RenderUtil.antialias(true);
            RenderUtil.disableBlending();
            RenderUtil.enableDepthTesting(true);
            stapel = entities.get(animatedModel);
            if (stapel != null && !stapel.isEmpty()) {
                for (int z = 0; z < stapel.size(); z++) {
                    entity = stapel.get(z);
                    if (entity != null && entity.isRenderingEnabled() && RenderUtil.inRenderRange(entity, camera)) {
                        shaderpack.getDefaultShader().jointTransforms.loadMatrixArray(animatedModel.getJointTransforms());
                        shaderpack.getDefaultShader().transformationMatrix.loadMatrix(entity.getTransformation());
                        GL11.glDrawElements(GL11.GL_TRIANGLES, animatedModel.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                        vertcount += animatedModel.getModel().getModelData().getVertexCount();
                    }
                }
            }
            stapel = null;
            // model.getModel().getVao().unbind(0, 1, 2, 3);
        }
        return vertcount;
    }

}
