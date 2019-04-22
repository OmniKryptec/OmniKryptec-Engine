package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.render.RenderState.CullMode;
import de.omnikryptec.libapi.exposed.render.RenderState.DepthMode;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectListener;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.RendererContext.EnvironmentKey;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.updater.Time;
//TODO reflections
public class Renderer2D implements Renderer, IRenderedObjectListener {
    
    private static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0,
            s1) -> (int) Math.signum(s0.getLayer() - s1.getLayer());
    
    private static final RenderState SPRITE_STATE = RenderState.of(BlendMode.ALPHA);
    private static final RenderState LIGHT_STATE = RenderState.of(BlendMode.ADDITIVE);
    private static final RenderState MULT_STATE = RenderState.of(BlendMode.MULTIPLICATIVE);
    
    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private ShadedBatch2D batch = new ShadedBatch2D(1000);
    private ShadedBatch2D finalDraw = new ShadedBatch2D(6);
    private List<Sprite> sprites = new ArrayList<>();
    
    private FrameBuffer spriteBuffer, renderBuffer;
    
    private boolean shouldSort = false;
    
    @Override
    public void init(LocalRendererContext context) {
        context.getIRenderedObjectManager().addListener(Sprite.TYPE, this);
        List<Sprite> list = context.getIRenderedObjectManager().getFor(Sprite.TYPE);
        //is addAll fast enough or is a raw forloop faster?
        this.sprites.addAll(list);
        this.shouldSort = true;
    }
    
    @Override
    public void onAdd(RenderedObject obj) {
        this.sprites.add((Sprite) obj);
        this.shouldSort = true;
    }
    
    @Override
    public void deinit(LocalRendererContext context) {
        this.sprites.clear();
        this.shouldSort = false;
        context.getIRenderedObjectManager().removeListener(Sprite.TYPE, this);
    }
    
    @Override
    public void onRemove(RenderedObject obj) {
        this.sprites.remove(obj);
    }
    
    @Override
    public void render(Time time, IProjection projection, LocalRendererContext renderer) {
        if (shouldSort) {
            sprites.sort(spriteComparator);
            shouldSort = false;
        }
        renderBuffer.bindFrameBuffer();
        FrustumIntersection intersFilter = new FrustumIntersection(projection.getProjection());
        batch.setIProjection(projection);
        renderer.getRenderAPI().applyRenderState(LIGHT_STATE);
        renderBuffer.clearColor(renderer.getEnvironmentSettings().get(EnvironmentKeys2D.AmbientLight));
        List<Light2D> lightList = renderer.getIRenderedObjectManager().getFor(Light2D.TYPE);
        batch.begin();
        for (Light2D l : lightList) {
            if (l.isVisible(intersFilter)) {
                l.draw(batch);
            }
        }
        batch.end();
        spriteBuffer.bindFrameBuffer();
        spriteBuffer.clearColor();
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        batch.begin();
        for (Sprite sprite : sprites) {
            if (sprite.isVisible(intersFilter)) {
                sprite.draw(batch);
            }
        }
        batch.end();
        spriteBuffer.unbindFrameBuffer();
        renderer.getRenderAPI().applyRenderState(MULT_STATE);
        finalDraw.begin();
        finalDraw.draw(spriteBuffer.getTexture(0), null, false, false);
        finalDraw.end();
        renderBuffer.unbindFrameBuffer();
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        finalDraw.begin();
        finalDraw.draw(renderBuffer.getTexture(0), null, false, false);
        finalDraw.end();
    }
    
    @Override
    public void createOrResizeFBO(LocalRendererContext context, SurfaceBuffer screen) {
        if (spriteBuffer == null) {
            spriteBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
            spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        } else {
            spriteBuffer = spriteBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        }
        if (renderBuffer == null) {
            renderBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
            renderBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        } else {
            renderBuffer = renderBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        }
    }
    
    public static enum EnvironmentKeys2D implements Defaultable, EnvironmentKey {
        AmbientLight(new Color(1f, 1f, 1f));
        
        private final Object def;
        
        private EnvironmentKeys2D(Object o) {
            this.def = o;
        }
        
        @Override
        public <T> T getDefault() {
            return (T) def;
        }
    }
}
