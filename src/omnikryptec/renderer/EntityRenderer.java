package omnikryptec.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.display.DisplayManager;
import omnikryptec.entity.Entity;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Material;
import omnikryptec.model.TexturedModel;
import omnikryptec.shader_files.EntityLightShader;
import omnikryptec.texture.Texture;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

public class EntityRenderer implements Renderer {

	private EntityLightShader shader;

	public EntityRenderer() {
		RendererRegistration.register(this);
		shader = new EntityLightShader();
	}

	private List<Entity> stapel;
	private Entity entity;
	private TexturedModel model;
	private Material mat;
	private Texture textmp;

	@Override
	public void render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean onlyRender) {
		if (!DisplayManager.instance().getSettings().isLightForwardAllowed() && Logger.isDebugMode()) {
			Logger.log("Forward light is not enabled. Will not render.", LogLevel.WARNING);
			return;
		}
		shader.start();
		shader.view.loadMatrix(s.getCamera().getViewMatrix());
		shader.projection.loadMatrix(s.getCamera().getProjectionMatrix());
		shader.ambient.loadVec3(s.getAmbient().getArray());
		int lights = Math.min(DisplayManager.instance().getSettings().getLightMaxForward(),
				s.getForwardRenderLights().size());
		shader.activelights.loadInt(lights);
		for (int i = 0; i < lights; i++) {
			shader.lightpos[i].loadVec3(s.getForwardRenderLights().get(i).getAbsolutePos());
			shader.lightcolor[i].loadVec3(s.getForwardRenderLights().get(i).getColor().getArray());
			shader.atts[i].loadVec3(s.getForwardRenderLights().get(i).getAttenuation());
		}
		for (int i = 0; i < entities.keysArray().length; i++) {
			if (!(entities.keysArray()[i] instanceof TexturedModel)) {
				continue;
			}
			model = (TexturedModel) entities.keysArray()[i];
			model.getModel().getVao().bind(0, 1, 2, 3);
			textmp = model.getTexture();
			textmp.bindToUnit(0);
			shader.uvs.loadVec4(textmp.getUVs()[0], textmp.getUVs()[1], textmp.getUVs()[2], textmp.getUVs()[3]);
			mat = model.getMaterial();
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
			shader.reflec.loadFloat(mat.getReflectivity());
			shader.shinedamper.loadFloat(mat.getShineDamper());
			stapel = entities.get(model);
			for (int j = 0; j < stapel.size(); j++) {
				entity = stapel.get(j);
				if (entity.isActive() && RenderUtil.inRenderRange(entity, s.getCamera())) {
					if (!onlyRender) {
						entity.doLogic0();
					}
					shader.transformation.loadMatrix(Maths.createTransformationMatrix(entity));
					shader.colmod.loadVec4(entity.getColor().getVector4f());
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(),
							GL11.GL_UNSIGNED_INT, 0);
				}
			}
			stapel = null;
			// model.getModel().getVao().unbind(0, 1, 2, 3);
			if (model.getMaterial().hasTransparency()) {
				RenderUtil.cullBackFaces(true);
			}
		}
	}

	@Override
	public void cleanup() {

	}

	@Override
	public float expensiveLevel() {
		return 0;
	}

}
