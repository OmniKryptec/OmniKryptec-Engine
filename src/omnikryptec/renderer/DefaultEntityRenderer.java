package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import omnikryptec.camera.MatrixMath;
import omnikryptec.main.Scene;
import omnikryptec.shader_files.EntityShader;
import omnikryptec.storing.Entity;
import omnikryptec.storing.TexturedModel;

public class DefaultEntityRenderer implements IRenderer{
	
	private EntityShader shader;
	
	public DefaultEntityRenderer() {
		RendererRegistration.register(this);
		shader = new EntityShader();
	}
	
	private List<Entity> stapel;
	private Entity entity;

	@Override
	public void render(Scene s, Map<TexturedModel, List<Entity>> entities) {
		shader.start();
		EntityShader.projection.loadMatrix(s.getCamera().getProjectionMatrix());
		EntityShader.view.loadMatrix(s.getCamera().getViewMatrix());
		for (TexturedModel model : entities.keySet()) {
			model.getModel().getVao().bind(0,1,2,3);
			model.getTexture().bindToUnit(0);
			stapel = entities.get(model);
			for (int i = 0; i < stapel.size(); i++) {
				entity = stapel.get(i);
				entity.doLogic();
				entity.checkChunkPos();
				EntityShader.transformation.loadMatrix(MatrixMath.createTransformationMatrix(entity));
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			stapel = null;
			
			model.getModel().getVao().unbind(0,1,2,3);
		}
	}



	@Override
	public void cleanup() {
		shader.cleanup();
	}

}
