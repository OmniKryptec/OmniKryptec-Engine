package omnikryptec.terrain;

import java.util.List;
import omnikryptec.entity.Entity;
import omnikryptec.logger.Logger;
import omnikryptec.main.Scene;
import omnikryptec.model.TexturedModel;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.util.Constants;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

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
    public void render(Scene s, RenderMap entities) {
        Logger.log("Started Terrain Rendering");
        shader.start();
        TerrainShader.viewMatrix.loadMatrix(s.getCamera().getViewMatrix());
        TerrainShader.projectionMatrix.loadMatrix(s.getCamera().getProjectionMatrix());
        TerrainShader.lightPosition.loadVec3(s.getCamera().getAbsolutePos());
        for(int i = 0; i < entities.keysArray().length; i++) {
            model = entities.keysArray()[i];
            model.getModel().getVao().bind(0/*, 1, 2*/);
            model.getTexture().bindToUnit(0);
            //model.getMaterial().getNormalmap().bindToUnit(1);
            if(model.getMaterial().hasTransparency()) {
                RenderUtil.cullBackFaces(false);
            }
            /*
            if(model.getMaterial().getSpecularmap() != null) {
                model.getMaterial().getSpecularmap().bindToUnit(2);
                TerrainShader.hasspecular.loadBoolean(true);
            } else {
                TerrainShader.hasspecular.loadBoolean(false);
            }
            */
            TerrainShader.lightColour.loadVec3(1, 1, 1);
            TerrainShader.shineDamper.loadFloat(0);
            TerrainShader.reflectivity.loadFloat(model.getMaterial().getReflectivity());
            stapel = entities.get(model);				
            for(int j = 0; j < stapel.size(); j++) {
                Entity entity = stapel.get(j);
                if(entity instanceof Terrain) {
                    terrain = (Terrain) entity;
                    Logger.log("Found Terrain");
                } else {
                    Logger.log("Found Non-Terrain");
                    continue;
                }
                //if(RenderUtil.inRenderRange(terrain, s.getCamera())) {
                    TerrainShader.transformationMatrix.loadMatrix(Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), Constants.MATHS_ZERO, Constants.MATHS_ONE));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                //}
            }
            stapel = null;
            model.getModel().getVao().unbind(0/*, 1, 2*/);
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
