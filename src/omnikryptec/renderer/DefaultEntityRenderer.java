package omnikryptec.renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import omnikryptec.camera.MatrixMath;
import omnikryptec.main.Scene;
import omnikryptec.shader_files.EntityShader;
import omnikryptec.storing.Entity;
import omnikryptec.storing.TexturedModel;
import omnikryptec.util.RenderUtil;

public class DefaultEntityRenderer implements IRenderer{
	
	private EntityShader shader;
	
	public DefaultEntityRenderer() {
		RendererRegistration.register(this);
		shader = new EntityShader();
	}
	
	private List<Entity> stapel;

	@Override
	public void render(Scene s, Map<TexturedModel, List<Entity>> entities) {
		shader.start();
		EntityShader.view.loadMatrix(s.getCamera().getViewMatrix());
		EntityShader.projection.loadMatrix(s.getCamera().getProjectionMatrix());
		for (TexturedModel model : entities.keySet()) {
			model.getModel().getVao().bind(0,1,2,3);
			model.getTexture().bindToUnit(0);
			model.getMaterial().getNormalmap().bindToUnit(1);
			if(model.getMaterial().hasTransparency()){
				RenderUtil.cullBackFaces(false);
			}
			if(model.getMaterial().getSpecularmap()!=null){
				model.getMaterial().getSpecularmap().bindToUnit(2);
				EntityShader.hasspecular.loadBoolean(true);
			}else{
				EntityShader.hasspecular.loadBoolean(false);
			}
			EntityShader.reflec.loadFloat(model.getMaterial().getReflectivity());
			stapel = entities.get(model);
			for (Entity entity : stapel) {
				entity.doLogic();
				entity.checkChunkPos();
				EntityShader.transformation.loadMatrix(MatrixMath.createTransformationMatrix(entity));
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			stapel = null;
			model.getModel().getVao().unbind(0,1,2,3);
			if(model.getMaterial().hasTransparency()){
				RenderUtil.cullBackFaces(true);
			}
		}
	}



	@Override
	public void cleanup() {
		shader.cleanup();
	}

}
