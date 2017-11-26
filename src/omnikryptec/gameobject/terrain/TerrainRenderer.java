package omnikryptec.gameobject.terrain;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.TexturedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderGroup;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.GraphicsUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class TerrainRenderer extends Renderer {

    public TerrainRenderer() {
        super(new ShaderPack(new ShaderGroup(new TerrainShader())));
        RendererRegistration.register(this);
        setExpensiveLevel(0);
        setPriority(0);
    }

    private List<Entity> stapel;
    private Terrain terrain;
    private TexturedModel model;
    private long vertcount = 0;

    // TODO change something
    @Override
    protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader ownshader, FrustrumFilter filter) {
        vertcount = 0;
        //shader.start();
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
                GraphicsUtil.cullBackFaces(false);
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
                texturePack.getBackgroundTexture().bindToUnitOptimized(0);
                texturePack.getrTexture().bindToUnitOptimized(1);
                texturePack.getgTexture().bindToUnitOptimized(2);
                texturePack.getbTexture().bindToUnitOptimized(3);
                terrain.getBlendMap().bindToUnitOptimized(4);
                if (GraphicsUtil.inRenderRange(terrain, s.getCamera()) || true) {
                    TerrainShader.transformationMatrix.loadMatrix(terrain.getTransformation());
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                    vertcount += model.getModel().getModelData().getVertexCount();
                }
            }
            stapel = null;
            if (model.getMaterial().hasTransparency()) {
                GraphicsUtil.cullBackFaces(true);
            }
        }
        return vertcount;
    }

}
