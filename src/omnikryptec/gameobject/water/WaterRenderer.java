package omnikryptec.gameobject.water;

import java.util.List;

import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderMap;
import omnikryptec.renderer.Renderer;
import omnikryptec.renderer.RendererRegistration;
import omnikryptec.resource.model.AdvancedModel;

/**
 * WaterRenderer
 *
 * @author Panzer1119
 */
public class WaterRenderer extends Renderer<WaterShader> {

    public WaterRenderer() {
        super(new WaterShader());
        RendererRegistration.register(this);
        setExpensiveLevel(2);
        setPriority(1);
    }

    private List<Entity> stapel;
    private WaterTile waterTile;
    //private TexturedModel model;
    private long vertcount = 0;

    @Override
    protected long render(Scene s, RenderMap<AdvancedModel, List<Entity>> entities, boolean ownshader) {
        //TODO Fill it out
        return 0;
    }
}
