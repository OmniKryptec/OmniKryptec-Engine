package omnikryptec.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;
import omnikryptec.shader_files.EntityShader;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

public class DefaultEntityRenderer implements IRenderer{
	
	private EntityShader shader;
	
	public DefaultEntityRenderer() {
		RendererRegistration.register(this);
		shader = new EntityShader();
	}
	
	private List<Entity> stapel;
	private Entity entity;
	private TexturedModel model;
	
	@Override
	public void render(Scene s, RenderMap<TexturedModel, List<Entity>> entities) {
		shader.start();
		EntityShader.view.loadMatrix(s.getCamera().getViewMatrix());
		EntityShader.projection.loadMatrix(s.getCamera().getProjectionMatrix());
		for (int i=0; i<entities.keysArray().length; i++) {
			model = entities.keysArray()[i];
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
			if(model.getMaterial().getExtraInfo()!=null){
				model.getMaterial().getExtraInfo().bindToUnit(3);
				EntityShader.hasextrainfomap.loadBoolean(true);
			}else{
				EntityShader.hasextrainfomap.loadBoolean(false);
				if(model.getMaterial().getExtraInfoVec()!=null){
					EntityShader.extrainfovec.loadVec4(model.getMaterial().getExtraInfoVec());
				}else{
					EntityShader.extrainfovec.loadVec4(0, 0, 0, 0);
				}
			}
			EntityShader.reflec.loadFloat(model.getMaterial().getReflectivity());
			EntityShader.shinedamper.loadFloat(model.getMaterial().getShineDamper());
			stapel = entities.get(model);				
			for (int j=0; j<stapel.size(); j++) {
				entity = stapel.get(j);
				if(entity.isActive()&&RenderUtil.inRenderRange(entity, s.getCamera())){
					entity.doLogic0();
					EntityShader.transformation.loadMatrix(Maths.createTransformationMatrix(entity));
					EntityShader.colmod.loadVec4(entity.getColor());
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
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



	@Override
	public float expensiveLevel() {
		return 0;
	}

}
