package omnikryptec.gameobject.water;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.renderer.d3.RenderMap;
import omnikryptec.renderer.d3.Renderer;
import omnikryptec.renderer.d3.RendererRegistration;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.ShaderGroup;
import omnikryptec.shader.base.ShaderPack;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.Level;
import omnikryptec.util.Priority;

/**
 * WaterRenderer
 *
 * @author Panzer1119
 */
@Priority(value = 1)
@Level(value = 2)
public class WaterRenderer extends Renderer {

    public WaterRenderer() {
        super(new ShaderPack(new ShaderGroup(new WaterShader())));
        RendererRegistration.register(this);
    }

    private List<Entity> stapel;
    private long vertcount = 0;

    @Override
    protected long render(AbstractScene3D s, RenderMap<AdvancedModel, List<Entity>> entities, Shader started, FrustrumFilter filter) {
        //TODO Fill it out
        return 0;
    }
}
