package de.omnikryptec.gameobject.water;

import de.omnikryptec.gameobject.Entity;
import de.omnikryptec.main.AbstractScene3D;
import de.omnikryptec.renderer.d3.RenderMap;
import de.omnikryptec.renderer.d3.Renderer;
import de.omnikryptec.renderer.d3.RendererRegistration;
import de.omnikryptec.resource.model.AdvancedModel;
import de.omnikryptec.shader.base.Shader;
import de.omnikryptec.shader.base.ShaderGroup;
import de.omnikryptec.shader.base.ShaderPack;
import de.omnikryptec.util.FrustrumFilter;
import de.omnikryptec.util.Level;
import de.omnikryptec.util.Priority;

import java.util.List;

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
