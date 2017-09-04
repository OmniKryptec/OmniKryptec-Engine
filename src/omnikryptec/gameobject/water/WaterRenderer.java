package omnikryptec.gameobject.water;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.util.FrustrumFilter;

/**
 * WaterRenderer
 *
 * @author Panzer1119
 */
public class WaterRenderer extends Renderer {

    public WaterRenderer() {
        super(new ShaderPack(new WaterShader()));
        RendererRegistration.register(this);
        setExpensiveLevel(2);
        setPriority(1);
    }

    private List<Entity> stapel;
    private WaterTile waterTile;
    //private TexturedModel model;
    private long vertcount = 0;

    @Override
    protected long render(AbstractScene s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter) {
        //TODO Fill it out
        return 0;
    }
}
