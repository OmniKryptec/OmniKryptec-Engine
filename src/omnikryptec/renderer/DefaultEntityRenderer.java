package omnikryptec.renderer;

import java.util.List;

import org.lwjgl.opengl.GL11;

import omnikryptec.entity.Entity;
import omnikryptec.main.Scene;
import omnikryptec.model.AdvancedModel;
import omnikryptec.model.Material;
import omnikryptec.model.TexturedModel;
import omnikryptec.shader_files.EntityShader;
import omnikryptec.texture.Texture;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

public class DefaultEntityRenderer implements Renderer {

    private final EntityShader shader;

    public DefaultEntityRenderer() {
        RendererRegistration.register(this);
        shader = new EntityShader();
    }

    private List<Entity> stapel;
    private Entity entity;
    private TexturedModel model;
    private Material mat;
    private Texture textmp;

    @Override
    public void render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities) {
        shader.start();
        shader.view.loadMatrix(s.getCamera().getViewMatrix());
        shader.projection.loadMatrix(s.getCamera().getProjectionMatrix());
        for(int i = 0; i < entities.keysArray().length; i++) {
            if(!(entities.keysArray()[i] instanceof TexturedModel)) {
                continue;
            }
            model = (TexturedModel) entities.keysArray()[i];
            model.getModel().getVao().bind(0, 1, 2, 3);
            textmp = model.getTexture();
            textmp.bindToUnit(0);
            shader.uvs.loadVec4(textmp.getUVs()[0], textmp.getUVs()[1], textmp.getUVs()[2], textmp.getUVs()[3]);
            mat = model.getMaterial();
            mat.getNormalmap().bindToUnit(1);
            if(mat.hasTransparency()) {
                RenderUtil.cullBackFaces(false);
            }
            if(mat.getSpecularmap() != null) {
                mat.getSpecularmap().bindToUnit(2);
                shader.hasspecular.loadBoolean(true);
            } else {
                shader.hasspecular.loadBoolean(false);
            }
            if(mat.getExtraInfo() != null) {
                mat.getExtraInfo().bindToUnit(3);
                shader.hasextrainfomap.loadBoolean(true);
            } else {
                shader.hasextrainfomap.loadBoolean(false);
                if(mat.getExtraInfoVec() != null) {
                    shader.extrainfovec.loadVec4(mat.getExtraInfoVec());
                } else {
                    shader.extrainfovec.loadVec4(0, 0, 0, 0);
                }
            }
            shader.reflec.loadFloat(mat.getReflectivity());
            shader.shinedamper.loadFloat(mat.getShineDamper());
            stapel = entities.get(model);
            for(int j = 0; j < stapel.size(); j++) {
                entity = stapel.get(j);
                if(entity.isActive() && RenderUtil.inRenderRange(entity, s.getCamera())) {
                    entity.doLogic0();
                    shader.transformation.loadMatrix(Maths.createTransformationMatrix(entity));
                    shader.colmod.loadVec4(entity.getColor());
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getModel().getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            stapel = null;
            //model.getModel().getVao().unbind(0, 1, 2, 3);
            if(model.getMaterial().hasTransparency()) {
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
