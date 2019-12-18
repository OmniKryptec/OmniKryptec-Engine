package de.omnikryptec.render;

import org.joml.Matrix4f;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.window.WindowEvent;

public class AdaptiveCamera extends Camera {
    
    public static interface ProjectionCreationDelegate {
        Matrix4f createMatrix(int width, int height);
    }
    
    private final ProjectionCreationDelegate delegate;
    
    public AdaptiveCamera(final ProjectionCreationDelegate delegate) {
        super(null);
        this.delegate = delegate;
        final RenderAPI rapi = LibAPIManager.instance().getGLFW().getRenderAPI();
        setProjection(delegate.createMatrix(rapi.getSurface().getWidth(), rapi.getSurface().getHeight()));
        LibAPIManager.ENGINE_EVENTBUS.register(this);
    }
    
    @EventSubscription
    public void onChange(final WindowEvent.ScreenBufferResized ev) {
        setProjection(this.delegate.createMatrix(ev.width, ev.height));
    }
    
}
