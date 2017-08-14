package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.List;

import omnikryptec.animation.renderer.AnimatedModelRenderer;
import omnikryptec.gameobject.terrain.TerrainRenderer;
import omnikryptec.util.SerializationUtil;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class RendererRegistration {

    private static final List<Renderer<?>> existingRenderers = new ArrayList<>();

    public static final EntityRenderer DEF_ENTITY_RENDERER;
    public static final AnimatedModelRenderer DEF_ANIMATEDMODEL_RENDERER;
    public static final TerrainRenderer DEF_TERRAIN_RENDERER;

	public static final EntityMeshRenderer SIMPLE_MESH_RENDERER;
    
    
    static {
        DEF_ANIMATEDMODEL_RENDERER = new AnimatedModelRenderer();
        DEF_ENTITY_RENDERER = new EntityRenderer();
        DEF_TERRAIN_RENDERER = new TerrainRenderer();
        SIMPLE_MESH_RENDERER = new EntityMeshRenderer();
    }

    public static boolean exists(Renderer<?> r) {
        return existingRenderers.contains(r);
    }

    public static void register(Renderer<?> r) {
        existingRenderers.add(r);
    }

    public static Renderer<?> byName(String name) {
        return byClass(SerializationUtil.classForName(name));
    }

    public static Renderer<?> byClass(Class<?> c) {
        for (Renderer<?> renderer : existingRenderers) {
            if (c == renderer.getClass()) {
                return renderer;
            }
        }
        return null;
    }

    /**
     * trigger static constructor
     */
    public static void init() {
        Logger.log("Initializing default renderer", LogLevel.FINE);
    }
}
