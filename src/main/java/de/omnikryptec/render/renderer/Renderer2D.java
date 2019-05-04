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
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectListener;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.RendererContext.EnvironmentKey;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements Renderer, IRenderedObjectListener {
    
    private static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0,
            s1) -> (int) Math.signum(s0.getLayer() - s1.getLayer());
    
    private static final RenderState SPRITE_STATE = RenderState.of(BlendMode.ALPHA);
    private static final RenderState LIGHT_STATE = RenderState.of(BlendMode.ADDITIVE);
    private static final RenderState MULT_STATE = RenderState.of(BlendMode.MULTIPLICATIVE);
    
    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private SimpleBatch2D batch = new SimpleBatch2D(1000);
    private SimpleBatch2D finalDraw = new SimpleBatch2D(6);
    private List<Sprite> sprites = new ArrayList<>();
    
    private FrameBuffer spriteBuffer, renderBuffer;
    private boolean shouldSort = false;
    
    public void setSpriteComparator(Comparator<Sprite> comparator) {
        this.spriteComparator = comparator == null ? DEFAULT_COMPARATOR : comparator;
    }
    
    @Override
    public void init(LocalRendererContext context, FrameBuffer target) {
        createFBOs(context, target);
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
        //delete FBOs
        renderBuffer.deleteAndUnregister();
        spriteBuffer.deleteAndUnregister();
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
        batch.getShaderSlot().setProjection(projection);
        //render lights
        renderer.getRenderAPI().applyRenderState(LIGHT_STATE);
        renderBuffer.clearColor(renderer.getEnvironmentSettings().get(EnvironmentKeys2D.AmbientLight));
        RendererUtil.render2d(batch, renderer.getIRenderedObjectManager(), Light2D.TYPE, intersFilter);
        //render scene
        spriteBuffer.bindFrameBuffer();
        spriteBuffer.clearColor();
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        RendererUtil.render2d(batch, sprites, intersFilter);
        spriteBuffer.unbindFrameBuffer();
        //combine lights with the scene
        renderer.getRenderAPI().applyRenderState(MULT_STATE);
        RendererUtil.renderBufferDirect(spriteBuffer, 0, finalDraw);
        renderBuffer.unbindFrameBuffer();
        //final draw
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        RendererUtil.renderBufferDirect(renderBuffer, 0, finalDraw);
    }
    
    @Override
    public void resizeFBOs(LocalRendererContext context, SurfaceBuffer screen) {
        spriteBuffer = spriteBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        renderBuffer = renderBuffer.resizedClone(screen.getWidth(), screen.getHeight());
    }
    
    private void createFBOs(LocalRendererContext context, FrameBuffer screen) {
        spriteBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        
        renderBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        renderBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
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
