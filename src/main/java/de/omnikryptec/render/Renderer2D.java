package de.omnikryptec.render;

import de.omnikryptec.util.data.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;
import org.joml.Matrix3x2f;
import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.FBTarget.TextureFormat;
import de.omnikryptec.libapi.exposed.render.RenderAPI.SurfaceBufferType;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.render.RenderState.RenderConfig;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.RendererContext.EnvironmentKey;
import de.omnikryptec.render.batch.ShadedBatch2D;
import de.omnikryptec.render.storage.IRenderedObjectListener;
import de.omnikryptec.render.storage.RenderedObject;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements Renderer, IRenderedObjectListener {
    //FIXME fix light color
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
    
    private static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0,
            s1) -> (int) Math.signum(s0.getLayer() - s1.getLayer());
    
    private static final RenderState SPRITE_STATE = new RenderState();
    private static final RenderState LIGHT_STATE = new RenderState();
    private static final RenderState MULT_STATE = new RenderState();
    
    static {
        SPRITE_STATE.setRenderConfig(RenderConfig.BLEND, true);
        SPRITE_STATE.setBlendMode(BlendMode.ALPHA);
        LIGHT_STATE.setRenderConfig(RenderConfig.BLEND, true);
        LIGHT_STATE.setBlendMode(BlendMode.ADDITIVE);
        LIGHT_STATE.setRenderConfig(RenderConfig.DEPTH_TEST, false);
        MULT_STATE.setRenderConfig(RenderConfig.BLEND, true);
        MULT_STATE.setBlendMode(BlendMode.MULTIPLICATIVE);
        MULT_STATE.setRenderConfig(RenderConfig.DEPTH_TEST, false);
    }
    
    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private ShadedBatch2D batch = new ShadedBatch2D(1000);
    //TODO finalMatrix is weird, improve renderbatch?
    private ShadedBatch2D finalDraw = new ShadedBatch2D(6);
    private Matrix3x2f finalMatrix = new Matrix3x2f().translate(-1, -1).scale(2);
    private List<Sprite> sprites = new ArrayList<>();
    
    private FrameBuffer spriteBuffer, renderBuffer;
    
    private boolean shouldSort = false;
    
    @Override
    public void init(RendererContext context) {
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
    public void deinit(RendererContext context) {
        this.sprites.clear();
        this.shouldSort = false;
        context.getIRenderedObjectManager().removeListener(Sprite.TYPE, this);
    }
    
    @Override
    public void onRemove(RenderedObject obj) {
        this.sprites.remove(obj);
    }
    
    @Override
    public void render(Time time, IProjection projection, RendererContext renderer) {
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
        //TODO clear spritebuffer depth?
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
        finalDraw.draw(spriteBuffer.getTexture(0), finalMatrix, false, false);
        finalDraw.end();
        renderBuffer.unbindFrameBuffer();
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        finalDraw.begin();
        finalDraw.draw(renderBuffer.getTexture(0), finalMatrix, false, false);
        finalDraw.end();
    }
    
    @Override
    public void createAndResizeFBO(RendererContext context, SurfaceBuffer screen) {
        if (spriteBuffer == null) {
            spriteBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
            spriteBuffer.bindFrameBuffer();
            //TODO maybe auto bind for FBTarget assignments?
            spriteBuffer.assignTarget(0, new FBTarget(TextureFormat.RGBA16, 0));
            spriteBuffer.unbindFrameBuffer();
        } else {
            spriteBuffer = spriteBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        }
        if (renderBuffer == null) {
            renderBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
            renderBuffer.bindFrameBuffer();
            renderBuffer.assignTarget(0, new FBTarget(TextureFormat.RGBA16, 0));
            renderBuffer.unbindFrameBuffer();
        } else {
            renderBuffer = renderBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        }
    }
}
