package de.omnikryptec.render3.d2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.renderer.Renderer2D;
import de.omnikryptec.render3.IProjection;
import de.omnikryptec.render3.d2.sprites.AbstractSprite;
import de.omnikryptec.render3.d2.sprites.IRenderer2D;
import de.omnikryptec.render3.structure.ViewManager;
import de.omnikryptec.render3.structure.ViewRenderer;
import de.omnikryptec.util.updater.Time;

public class ViewRenderer2D implements ViewRenderer {
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
    
    public ViewRenderer2D() {
        this.abstractSprites = new ArrayList<>();
    }
    
    public void addSprite(AbstractSprite sprite) {
        this.abstractSprites.add(sprite);
        this.sort = true;
    }
    
    public void addSprites(Collection<? extends AbstractSprite> sprites) {
        this.abstractSprites.addAll(sprites);
        this.sort = true;
    }
    
    public void removeSprite(AbstractSprite sprite) {
        this.abstractSprites.remove(sprite);
    }
    
    public void removeSprites(Collection<? extends AbstractSprite> sprites) {
        this.abstractSprites.removeAll(sprites);
    }
    
    public void setComparator(Comparator<AbstractSprite> comparator) {
        this.comparator = comparator;
    }
    
    public void forceSort() {
        this.sort = true;
    }
    
    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target, Time time) {
        if (sort) {
            sort = false;
            abstractSprites.sort(comparator);
        }
        api.applyRenderState(Renderer2D.SPRITE_STATE);
        FrustumIntersection frustumFilter = projection.getFrustumTester();
        Matrix4fc projectionMatrix = projection.getProjection();
        int currentlayer = abstractSprites.get(0).getLayer();
        IRenderer2D currentRenderer = abstractSprites.get(0).getRenderer();
        currentRenderer.setProjectionViewMatrix(projectionMatrix);
        currentRenderer.start();
        for (AbstractSprite abstractSprite : abstractSprites) {
            if (abstractSprite.isVisible(frustumFilter)) {
                if (currentlayer != abstractSprite.getLayer()) {
                    currentRenderer.flush();
                    currentlayer = abstractSprite.getLayer();
                }
                if (currentRenderer != abstractSprite.getRenderer()) {
                    currentRenderer.flush();
                    currentRenderer = abstractSprite.getRenderer();
                    currentRenderer.setProjectionViewMatrix(projectionMatrix);
                    currentRenderer.start();
                }
                abstractSprite.draw();
            }
        }
        currentRenderer.flush();
    }
    
}
