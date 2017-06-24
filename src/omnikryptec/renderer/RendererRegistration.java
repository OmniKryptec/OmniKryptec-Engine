package omnikryptec.renderer;

import java.util.ArrayList;
import java.util.List;

import omnikryptec.animation.renderer.AnimatedModelRenderer;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.util.SerializationUtil;

public class RendererRegistration {

    private static final List<Renderer> existingRenderers = new ArrayList<>();

    public static final EntityRendererNoLight DEF_ENTITY_RENDERER;
    public static final EntityRenderer DEF_FORWARD_ENTITY_RENDERER;
    public static final AnimatedModelRenderer DEF_ANIMATEDMODEL_RENDERER;

    static {
        DEF_ENTITY_RENDERER = new EntityRendererNoLight();
        DEF_ANIMATEDMODEL_RENDERER = new AnimatedModelRenderer();
        DEF_FORWARD_ENTITY_RENDERER = new EntityRenderer();
    }

    public static boolean exists(Renderer r) {
        return existingRenderers.contains(r);
    }

    public static void register(Renderer r) {
        existingRenderers.add(r);
    }

    public static void cleanup() {
        for (int i = 0; i < existingRenderers.size(); i++) {
            existingRenderers.get(i).cleanup();
        }
        existingRenderers.clear();
    }
    
    public static Renderer byName(String name) {
        return byClass(SerializationUtil.classForName(name));
    }
    
    public static Renderer byClass(Class<?> c) {
        for(Renderer renderer : existingRenderers) {
            if(c == renderer.getClass()) {
                return renderer;
            }
        }
        return null;
    }

    /**
     * trigger static constructor
     */
    public static void init() {
        Logger.log("Initialized default renderer", LogLevel.FINEST);
    }
}
