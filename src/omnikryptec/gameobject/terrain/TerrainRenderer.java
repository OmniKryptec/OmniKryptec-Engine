package omnikryptec.gameobject.terrain;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.util.Instance;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogLevel;

import org.joml.Vector3f;

/**
 *
 * @author Panzer1119
 */
public class TerrainRenderer implements Renderer {

    private final TerrainShader shader;

    public TerrainRenderer() {
        RendererRegistration.register(this);
        shader = new TerrainShader();
    }

    private List<Entity> stapel;
    private Terrain terrain;
    private TexturedModel model;
    private long vertcount = 0;

    // TODO change something
    @Override
    public long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities) {
        vertcount = 0;
        shader.start();
        TerrainShader.viewMatrix.loadMatrix(s.getCamera().getViewMatrix());
        TerrainShader.projectionMatrix.loadMatrix(s.getCamera().getProjectionMatrix());
        for (int i = 0; i < entities.keysArray().length; i++) {
            if (!(entities.keysArray()[i] instanceof TexturedModel)) {
                continue;
            }
            model = (TexturedModel) entities.keysArray()[i];
            model.getModel().getVao().bind(0, 1, 2);
            // model.getTexture().bindToUnit(0);
            if (model.getMaterial().hasTransparency()) {
                RenderUtil.cullBackFaces(false);
            }
            stapel = entities.get(model);
            for (int j = 0; j < stapel.size(); j++) {
                Entity entity = stapel.get(j);
                if (entity instanceof Terrain) {
                    terrain = (Terrain) entity;
                } else {
                    Logger.log("Non-Terrain GameObject tried to be rendered as a Terrain, but it failed", LogLevel.WARNING);
                    continue;
                }
                TerrainTexturePack texturePack = terrain.getTexturePack();
                texturePack.getBackgroundTexture().bindToUnit(0);
                texturePack.getrTexture().bindToUnit(1);
                texturePack.getgTexture().bindToUnit(2);
                texturePack.getbTexture().bindToUnit(3);
                terrain.getBlendMap().bindToUnit(4);
                if (RenderUtil.inRenderRange(terrain, s.getCamera()) || true) {
                    TerrainShader.transformationMatrix.loadMatrix(terrain.getTransformation());
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                    vertcount += model.getModel().getModelData().getVertexCount();
                }
            }
            stapel = null;
            if (model.getMaterial().hasTransparency()) {
                RenderUtil.cullBackFaces(true);
            }
        }
        return vertcount;
    }
    
    @Override
    public float expensiveLevel() {
        return 0;
    }

	@Override
	public float priority() {
		return 0;
	}

}
