package omnikryptec.animation.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.entity.Camera;
import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

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

	private List<Entity> stapel;
	private AdvancedModel model;
	private AnimatedModel animatedModel;
	private Entity entity;
	private long vertcount=0;
	
	@Override
	public long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean onlyRender) {
		final Camera camera = s.getCamera();
		vertcount = 0;
		shader.start();
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		shader.lightDirection.loadVec3(LIGHT_DIR);
		for (int i = 0; i < entities.keysArray().length; i++) {
			model = entities.keysArray()[i];
			if (!(model instanceof AnimatedModel)) {
				continue;
			}
			animatedModel = (AnimatedModel) model;
			model.getModel().getVao().bind(0, 1, 2, 3, 4, 5);
			animatedModel.getTexture().bindToUnit(0);
			RenderUtil.antialias(true);
			RenderUtil.disableBlending();
			RenderUtil.enableDepthTesting(true);
			stapel = entities.get(model);
			if (stapel != null && !stapel.isEmpty()) {
				for (int z = 0; z < stapel.size(); z++) {
					entity = stapel.get(z);
					if (entity != null && entity.isActive() && RenderUtil.inRenderRange(entity, camera)) {
						if (!onlyRender) {
							entity.doLogic0();
						}
						shader.jointTransforms.loadMatrixArray(animatedModel.getJointTransforms());
						shader.transformationMatrix.loadMatrix(Maths.createTransformationMatrix(entity));
						GL11.glDrawElements(GL11.GL_TRIANGLES, animatedModel.getModel().getVao().getIndexCount(),
								GL11.GL_UNSIGNED_INT, 0);
						vertcount += animatedModel.getModel().getModelData().getVertexCount();
					}
				}
			}
			stapel = null;
			// model.getModel().getVao().unbind(0, 1, 2, 3);
		}
		return vertcount;
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
