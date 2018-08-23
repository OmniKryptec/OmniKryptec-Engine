package de.omnikryptec.animation.renderer;

import de.omnikryptec.animation.AnimatedModel;
import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.graphics.GraphicsUtil;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.renderer.d3.RenderMap;
import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.base.ShaderGroup;
import de.omnikryptec.shader.base.ShaderPack;
import de.omnikryptec.util.FrustrumFilter;
import de.omnikryptec.util.Level;
import de.omnikryptec.util.logger.LogLevel;
import de.omnikryptec.util.logger.Logger;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 *
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 *
 * @author Karl &amp; Panzer1119
 *
 */
@Level(value = 1)
public class AnimatedModelRenderer extends Renderer {

	//TODO das hier ist gurke
    public static final Vector3f LIGHT_DIR = new Vector3f(0.2f, -0.3f, -0.8f);

    /**
     * Initializes the shader program used for rendering animated models.
     */
    public AnimatedModelRenderer() {
        super(new ShaderPack(new ShaderGroup(new AnimatedModelShader())));
        RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private AnimatedModel animatedModel;
    private Entity entity;
    private long vertcount = 0;

    @Override
    protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader ownshader, FrustrumFilter filter) {
        vertcount = 0;
        //TODO in den shader verschieben bei onRenderStart odaso
        //((AnimatedModelShader)shaderpack.getDefaultShader()).lightDirection.loadVec3(LIGHT_DIR);
        for (AdvancedModel advancedModel : entities.keysArray()) {
            if (advancedModel == null || !(advancedModel instanceof AnimatedModel)) {
                if (Logger.isDebugMode()) {
                    Logger.log("Wrong renderer for AdvancedModel set! (" + advancedModel + ")", LogLevel.WARNING);
                }
                continue;
            }
            ownshader.onModelRenderStart(advancedModel);
            stapel = entities.get(animatedModel);
            if (stapel != null && !stapel.isEmpty()) {
                for (int z = 0; z < stapel.size(); z++) {
                    entity = stapel.get(z);
                    if (entity != null && entity.isRenderingEnabled() && GraphicsUtil.inRenderRange(entity, s.getCamera())) {
                    	ownshader.onRenderInstance(entity);
                        GL11.glDrawElements(GL11.GL_TRIANGLES, animatedModel.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                        vertcount += animatedModel.getModel().getModelData().getVertexCount();
                    }
                }
            }
            stapel = null;
        }
        return vertcount;
    }

}
