package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.AbstractProjectedShaderSlot;
import de.omnikryptec.render.batch.AbstractAdvancedShaderSlot;
import de.omnikryptec.render.batch.AdvancedBatch2D;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectListener;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.objects.AdvancedSprite;
import de.omnikryptec.render.objects.AdvancedSprite.Reflection2DType;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.Renderer2D.EnvironmentKeys2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.profiling.IProfiler;
import de.omnikryptec.util.profiling.ProfileHelper;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.updater.Time;

public class AdvancedRenderer2D implements Renderer, IRenderedObjectListener {
    private static int rc = 0;
    
    private final int countIndex = rc++;
    
    private Comparator<Sprite> spriteComparator = Renderer2D.DEFAULT_COMPARATOR;
    private final List<AdvancedSprite> sprites = new ArrayList<>();
    private final List<AdvancedSprite> reflectors = new ArrayList<>();
    
    private final SimpleBatch2D reflectionBatch;
    private final AdvancedBatch2D mainBatch;
    private FrameBuffer spriteBuffer;
    private FrameBuffer renderBuffer;
    private FrameBuffer reflectionBuffer;
    
    private boolean shouldSort = false;
    private boolean enableReflections = true;
    
    public AdvancedRenderer2D() {
        this(1000);
    }
    
    public AdvancedRenderer2D(final int vertices) {
        this.reflectionBatch = new SimpleBatch2D(vertices);
        this.mainBatch = new AdvancedBatch2D(vertices);
        initStuff();
    }
    
    public AdvancedRenderer2D(final int vertices, final AbstractAdvancedShaderSlot mainShaderSlot,
            final AbstractProjectedShaderSlot reflectionShaderSlot) {
        this.reflectionBatch = new SimpleBatch2D(vertices, reflectionShaderSlot);
        this.mainBatch = new AdvancedBatch2D(vertices, mainShaderSlot);
        initStuff();
    }
    
    private void initStuff() {
        Profiler.addIProfiler(toString(), profiler);
    }
    
    public void setEnableReflections(boolean b) {
        this.enableReflections = b;
    }
    
    public void setSpriteComparator(final Comparator<Sprite> comparator) {
        this.spriteComparator = comparator == null ? Renderer2D.DEFAULT_COMPARATOR : comparator;
    }
    
    @Override
    public void init(final LocalRendererContext context, final FrameBuffer target) {
        createFBOs(context, target);
        context.getIRenderedObjectManager().addListener(AdvancedSprite.TYPE, this);
        final List<AdvancedSprite> list = context.getIRenderedObjectManager().getFor(AdvancedSprite.TYPE);
        for (final AdvancedSprite s : list) {
            this.sprites.add(s);
            if (s.getReflectionType() == Reflection2DType.Cast) {
                this.reflectors.add(s);
            }
        }
        this.shouldSort = true;
    }
    
    @Override
    public void onAdd(final RenderedObject obj) {
        final AdvancedSprite s = (AdvancedSprite) obj;
        this.sprites.add(s);
        if (s.getReflectionType() == Reflection2DType.Cast) {
            this.reflectors.add(s);
        }
        this.shouldSort = true;
    }
    
    @Override
    public void deinit(final LocalRendererContext context) {
        this.sprites.clear();
        this.shouldSort = false;
        context.getIRenderedObjectManager().removeListener(AdvancedSprite.TYPE, this);
        //delete FBOs
        this.renderBuffer.deleteAndUnregister();
        this.spriteBuffer.deleteAndUnregister();
        this.reflectionBuffer.deleteAndUnregister();
    }
    
    @Override
    public void onRemove(final RenderedObject obj) {
        this.sprites.remove(obj);
        if (((AdvancedSprite) obj).getReflectionType() == Reflection2DType.Cast) {
            this.reflectors.remove(obj);
        }
    }
    
    @Override
    public void render(final Time time, final IProjection projection, final LocalRendererContext renderer) {
        Profiler.begin(toString());
        boolean sorted = false;
        long spritesV = 0, reflV = 0;
        if (this.shouldSort) {
            this.sprites.sort(this.spriteComparator);
            this.shouldSort = false;
            sorted = true;
        }
        
        this.renderBuffer.bindFrameBuffer();
        final FrustumIntersection intersFilter = new FrustumIntersection(projection.getProjection());
        this.reflectionBatch.getShaderSlot().setProjection(projection);
        //render lights
        renderer.getRenderAPI().applyRenderState(Renderer2D.LIGHT_STATE);
        this.renderBuffer.clearColor(renderer.getEnvironmentSettings().get(EnvironmentKeys2D.AmbientLight));
        RendererUtil.render2d(this.reflectionBatch, renderer.getIRenderedObjectManager(), Light2D.TYPE, intersFilter);
        //render reflection
        renderer.getRenderAPI().applyRenderState(Renderer2D.SPRITE_STATE);
        if (enableReflections) {
            this.reflectionBuffer.bindFrameBuffer();
            this.reflectionBuffer.clearColor();
            if (reflectors.size() > 0) {
                this.reflectionBatch.begin();
                for (final AdvancedSprite s : this.reflectors) {
                    if (s.isVisible(intersFilter)) {
                        s.drawReflection(this.reflectionBatch);
                        reflV++;
                    }
                }
                this.reflectionBatch.end();
            }
            this.reflectionBuffer.unbindFrameBuffer();
        }
        //render scene
        this.spriteBuffer.bindFrameBuffer();
        this.spriteBuffer.clearColor();
        this.mainBatch.getShaderSlot().setProjection(projection);
        this.mainBatch.getShaderSlot().setReflection(this.reflectionBuffer.getTexture(0));
        this.mainBatch.begin();
        for (final AdvancedSprite s : this.sprites) {
            if (s.isVisible(intersFilter)) {
                this.mainBatch.reflectionStrength()
                        .set(enableReflections && s.getReflectionType() != Reflection2DType.Receive ? Color.ZERO
                                : s.reflectiveness());
                s.draw(this.mainBatch);
                spritesV++;
            }
        }
        this.mainBatch.end();
        this.spriteBuffer.unbindFrameBuffer();
        //combine lights with the scene
        renderer.getRenderAPI().applyRenderState(Renderer2D.MULT_STATE);
        this.spriteBuffer.renderDirect(0);
        this.renderBuffer.unbindFrameBuffer();
        //final draw
        renderer.getRenderAPI().applyRenderState(Renderer2D.SPRITE_STATE);
        this.renderBuffer.renderDirect(0);
        Profiler.end(sorted, reflectors.size(), sprites.size(), reflV, spritesV);
    }
    
    public void forceSort() {
        this.shouldSort = true;
    }
    
    @Override
    public void resizeFBOs(final LocalRendererContext context, final SurfaceBuffer screen) {
        this.spriteBuffer = this.spriteBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        this.renderBuffer = this.renderBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        this.reflectionBuffer = this.reflectionBuffer.resizedClone(screen.getWidth() / 2, screen.getHeight() / 2);
    }
    
    private void createFBOs(final LocalRendererContext context, final FrameBuffer screen) {
        this.spriteBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        this.spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        
        this.renderBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        this.renderBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        
        this.reflectionBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth() / 2, screen.getHeight() / 2,
                0, 1);
        this.reflectionBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }
    
    @Override
    public String toString() {
        return AdvancedRenderer2D.class.getSimpleName() + "-" + countIndex;
    }
    
    private final IProfiler profiler = new IProfiler() {
        private long sorted = 0;
        private ProfileHelper sprites = new ProfileHelper();
        private ProfileHelper reflectors = new ProfileHelper();
        private ProfileHelper spritesV = new ProfileHelper();
        private ProfileHelper reflectorsV = new ProfileHelper();
        
        @Override
        public void writeData(StringBuilder builder, long count) {
            builder.append("Layers sorted: " + Mathd.round(sorted * 100 / (double) count, 2) + "%").append('\n');
            sprites.append("Sprites", count, 1, builder);
            spritesV.append("Sprites (visible): ", count, 1, builder);
            if (enableReflections) {
                reflectors.append("Reflectors: ", count, 1, builder);
                reflectorsV.append("Reflectors (visible)", count, 1, builder);
            }
        }
        
        @Override
        public void dealWith(long nanoSecondsPassed, Object... objects) {
            if ((boolean) objects[0]) {
                sorted++;
            }
            sprites.push((int) objects[2]);
            if (enableReflections) {
                reflectors.push((int) objects[1]);
                reflectorsV.push((long) objects[3]);
            }
            spritesV.push((long) objects[4]);
        }
    };
}
