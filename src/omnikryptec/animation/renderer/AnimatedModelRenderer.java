package omnikryptec.animation.renderer;

import java.util.List;
import omnikryptec.animation.AnimatedModel;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.util.RenderUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl &amp; Panzer1119
 *
 */
public class AnimatedModelRenderer implements Renderer {

	public static final Vector3f LIGHT_DIR = new Vector3f(0.2f, -0.3f, -0.8f);
	private AnimatedModelShader shader;

	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer() {
		RendererRegistration.register(this);
		this.shader = new AnimatedModelShader();
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void render(Entity entity) {
                RenderUtil.antialias(true);
                RenderUtil.disableBlending();
                RenderUtil.enableDepthTesting(true);
                AnimatedModel animatedModel = (AnimatedModel) entity.getAdvancedModel();
                
                Logger.log("Started Rendering: " + entity);
                
		animatedModel.getTexture().bindToUnit(0);
		animatedModel.getModel().getVao().bind(0, 1, 2, 3, 4);
		shader.jointTransforms.loadMatrixArray(animatedModel.getJointTransforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, animatedModel.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		animatedModel.getModel().getVao().unbind(0, 1, 2, 3, 4);
                
                Logger.log("Finished Rendering: " + entity);
	}

	private List<Entity> stapel;
	private AdvancedModel model;
        
    @Override
    public void render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities) {
        final Camera camera = s.getCamera();
        shader.start();
        shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
        shader.lightDirection.loadVec3(LIGHT_DIR);
        for(int i = 0; i < entities.keysArray().length; i++) {
            model = entities.keysArray()[i];
            if(!(model instanceof AdvancedModel)) {
                continue;
            }
            stapel = entities.get(model);
            if(stapel != null && !stapel.isEmpty()) {
                stapel.stream().forEach((entity) -> {
                    if(entity.isActive() && RenderUtil.inRenderRange(entity, camera)) {
                        render(entity);
                    }
                });
            }
        }
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

    @Override
    public float expensiveLevel() {
        return 1;
    }

}
