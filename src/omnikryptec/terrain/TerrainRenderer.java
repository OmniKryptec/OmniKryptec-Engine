package omnikryptec.terrain;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Entity;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.util.Constants;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

/**
 *
 * @author Panzer1119
 */
public class TerrainRenderer implements IRenderer {
	
    private final TerrainShader shader;

    public TerrainRenderer() {
        RendererRegistration.register(this);
        shader = new TerrainShader();
    }

    private List<Entity> stapel;
    private Terrain terrain;
    private TexturedModel model;

    @Override
    public void render(Scene s, RenderMap<TexturedModel, List<Entity>> entities) {
        shader.start();
        TerrainShader.viewMatrix.loadMatrix(s.getCamera().getViewMatrix());
        TerrainShader.projectionMatrix.loadMatrix(s.getCamera().getProjectionMatrix());
        for(int i = 0; i < entities.keysArray().length; i++) {
            model = entities.keysArray()[i];
            model.getModel().getVao().bind(0, 1, 2);
            //model.getTexture().bindToUnit(0);
            if(model.getMaterial().hasTransparency()) {
                RenderUtil.cullBackFaces(false);
            }
            stapel = entities.get(model);
            for(int j = 0; j < stapel.size(); j++) {
                Entity entity = stapel.get(j);
                if(entity instanceof Terrain) {
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
                if(RenderUtil.inRenderRange(terrain, s.getCamera()) || true) {
                    TerrainShader.transformationMatrix.loadMatrix(Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), Constants.MATHS_ZERO, Constants.MATHS_ONE));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            stapel = null;
            model.getModel().getVao().unbind(0, 1, 2);
            if(model.getMaterial().hasTransparency()) {
                RenderUtil.cullBackFaces(true);
            }
        }
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

}
