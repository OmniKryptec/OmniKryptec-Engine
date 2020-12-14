package de.omnikryptec.render3.sprites;

import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.renderer.ViewRenderer;
import de.omnikryptec.render.renderer.ViewManager;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements ViewRenderer {
    private static final Comparator<AbstractSprite> DEFAULT_COMPARATOR = (s0, s1) -> {
        int layers = s0.getLayer() - s1.getLayer();
        if (layers != 0) {
            return layers;
        }
        int rendererDiff = s0.getRenderer().hashCode() - s1.getRenderer().hashCode();
        return rendererDiff;
    };
    
    private List<AbstractSprite> abstractSprites;
    
    private boolean sort;
    private Comparator<AbstractSprite> comparator = DEFAULT_COMPARATOR;
    
    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target, Time time) {
        if (sort) {
            sort = false;
            abstractSprites.sort(comparator);
        }
        FrustumIntersection frustumFilter = projection.getFrustumTester();
        Matrix4fc projectionMatrix = projection.getProjection();
        int currentlayer = abstractSprites.get(0).getLayer();
        AnotherFuckingRenderer currentRenderer = abstractSprites.get(0).getRenderer();
        currentRenderer.start();
        for (AbstractSprite abstractSprite : abstractSprites) {
            if (abstractSprite.isVisible(frustumFilter)) {
                if (currentlayer != abstractSprite.getLayer()) {
                    currentlayer = abstractSprite.getLayer();
                    currentRenderer.flush();
                }
                if (currentRenderer != abstractSprite.getRenderer()) {
                    currentRenderer.flush();
                    currentRenderer = abstractSprite.getRenderer();
                    currentRenderer.setProjectionViewMatrx(projectionMatrix);
                    currentRenderer.start();
                }
                abstractSprite.draw();
                //Now render the sprite
            }
        }
        currentRenderer.flush();
    }
    
}
