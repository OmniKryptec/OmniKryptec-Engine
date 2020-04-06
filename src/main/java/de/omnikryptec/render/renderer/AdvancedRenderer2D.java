package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.AbstractProjectedShaderSlot;
import de.omnikryptec.render.batch.AdvancedBatch2D;
import de.omnikryptec.render.batch.AdvancedShaderSlot;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.AdvancedSprite;
import de.omnikryptec.render.objects.AdvancedSprite.Reflection2DType;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.profiling.IProfiler;
import de.omnikryptec.util.profiling.ProfileHelper;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class AdvancedRenderer2D implements Renderer {
    private static int rc = 0;
    
    private final int countIndex = rc++;
    
    private Comparator<Sprite> spriteComparator = Renderer2D.DEFAULT_COMPARATOR;
    private final List<AdvancedSprite> sprites = new ArrayList<>();
    private final List<AdvancedSprite> reflectors = new ArrayList<>();
    private final List<Sprite> lights = new ArrayList<>();
    
    private final SimpleBatch2D reflectionBatch;
    private final AdvancedBatch2D mainBatch;
    private FrameBuffer spriteBuffer;
    private FrameBuffer renderBuffer;
    private FrameBuffer reflectionBuffer;
    
    private final Color ambientLight = new Color(1, 1, 1, 1);
    
    private boolean shouldSort = false;
    private boolean enableReflections = true;
    private boolean extendedLightRange = false;
    
    public AdvancedRenderer2D() {
        this(1000);
    }
    
    public AdvancedRenderer2D(final int vertices) {
        this.reflectionBatch = new SimpleBatch2D(vertices);
        this.mainBatch = new AdvancedBatch2D(vertices);
        initStuff();
    }
    
    public AdvancedRenderer2D(final int vertices, final AbstractProjectedShaderSlot mainShaderSlot,
            final AbstractProjectedShaderSlot reflectionShaderSlot) {
        this.reflectionBatch = new SimpleBatch2D(vertices, reflectionShaderSlot);
        this.mainBatch = new AdvancedBatch2D(vertices, mainShaderSlot);
        initStuff();
    }
    
    /**
     * Only effective before adding this Renderer to a ViewManager.
     * 
     * @param b boolean
     */
    public void setUseExtendedLightRange(boolean b) {
        this.extendedLightRange = b;
    }
    
    private void initStuff() {
        Profiler.addIProfiler(toString(), this.profiler);
    }
    
    public void setEnableReflections(boolean b) {
        this.enableReflections = b;
    }
    
    public void setSpriteComparator(final Comparator<Sprite> comparator) {
        this.spriteComparator = Util.defaultIfNull(Renderer2D.DEFAULT_COMPARATOR, comparator);
    }
    
    public Color ambientLight() {
        return this.ambientLight;
    }
    
    @Override
    public void init(ViewManager vm, RenderAPI api) {
        createFBOs(api, vm.getMainView().getTargetFbo());//put this directly into the checkFBOs method?
        this.shouldSort = true;
    }
    
    public void add(AdvancedSprite sprite) {
        this.sprites.add(sprite);
        if (sprite.getReflectionType() == Reflection2DType.Cast) {
            this.reflectors.add(sprite);
        }
        this.shouldSort = true;
    }
    
    public void addLight(Sprite light) {
        this.lights.add(light);
    }
    
    public void removeLight(Sprite light) {
        this.lights.remove(light);
    }
    
    @Override
    public void deinit(ViewManager vm, RenderAPI api) {
        this.sprites.clear();
        this.shouldSort = false;
        //delete FBOs
        this.renderBuffer.deleteAndUnregister();
        this.spriteBuffer.deleteAndUnregister();
        this.reflectionBuffer.deleteAndUnregister();
    }
    
    public void remove(AdvancedSprite sprite) {
        this.sprites.remove(sprite);
        if (sprite.getReflectionType() == Reflection2DType.Cast) {
            this.reflectors.remove(sprite);
        }
    }
    
    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envS, Time time) {
        Profiler.begin(toString());
        checkFBOs(target);
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
        api.applyRenderState(Renderer2D.LIGHT_STATE);
        this.renderBuffer.clearColor(this.ambientLight);
        RendererUtil.render2d(this.reflectionBatch, this.lights, intersFilter);
        //render reflection
        api.applyRenderState(Renderer2D.SPRITE_STATE);
        if (this.enableReflections) {
            this.reflectionBuffer.bindFrameBuffer();
            this.reflectionBuffer.clearColor();
            if (this.reflectors.size() > 0) {
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
        this.reflectionBuffer.getTexture(0).bindTexture(AdvancedShaderSlot.REFLECTION_TEXTURE_UNIT);
        this.mainBatch.begin();
        for (final AdvancedSprite s : this.sprites) {
            if (s.isVisible(intersFilter)) {
                this.mainBatch.reflectionStrength()
                        .set(this.enableReflections && s.getReflectionType() != Reflection2DType.Receive ? Color.ZERO
                                : s.reflectiveness());
                this.mainBatch.setTilingFactor(s.getTilingFactor());
                s.draw(this.mainBatch);
                spritesV++;
            }
        }
        this.mainBatch.end();
        this.spriteBuffer.unbindFrameBuffer();
        //combine lights with the scene
        api.applyRenderState(Renderer2D.MULT_STATE);
        this.spriteBuffer.renderDirect(0);
        this.renderBuffer.unbindFrameBuffer();
        //final draw
        api.applyRenderState(Renderer2D.SPRITE_STATE);
        this.renderBuffer.renderDirect(0);
        Profiler.end(sorted, this.reflectors.size(), this.sprites.size(), reflV, spritesV);
    }
    
    public void forceSort() {
        this.shouldSort = true;
    }
    
    private void checkFBOs(FrameBuffer target) {
        this.spriteBuffer = this.spriteBuffer.resizeAndDeleteOrThis(target.getWidth(), target.getHeight());
        this.renderBuffer = this.renderBuffer.resizeAndDeleteOrThis(target.getWidth(), target.getHeight());
        this.reflectionBuffer = this.reflectionBuffer.resizeAndDeleteOrThis(target.getWidth() / 2,
                target.getHeight() / 2);
    }
    
    private void createFBOs(RenderAPI api, final FrameBuffer target) {
        this.spriteBuffer = api.createFrameBuffer(target.getWidth(), target.getHeight(), 0, 1);
        this.spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        
        this.renderBuffer = api.createFrameBuffer(target.getWidth(), target.getHeight(), 0, 1);
        this.renderBuffer.assignTargetB(0,
                new FBTarget(extendedLightRange ? FBAttachmentFormat.RGBA32 : FBAttachmentFormat.RGBA16, 0));
        
        this.reflectionBuffer = api.createFrameBuffer(target.getWidth() / 2, target.getHeight() / 2, 0, 1);
        this.reflectionBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }
    
    @Override
    public String toString() {
        return AdvancedRenderer2D.class.getSimpleName() + "-" + this.countIndex;
    }
    
    private final IProfiler profiler = new IProfiler() {
        private long sorted = 0;
        private final ProfileHelper sprites = new ProfileHelper();
        private final ProfileHelper reflectors = new ProfileHelper();
        private final ProfileHelper spritesV = new ProfileHelper();
        private final ProfileHelper reflectorsV = new ProfileHelper();
        
        @Override
        public void writeData(StringBuilder builder, long count) {
            builder.append("Layers sorted: " + Mathd.round(this.sorted * 100 / (double) count, 2) + "%").append('\n');
            this.sprites.append("Sprites", count, 1, builder);
            this.spritesV.append("Sprites (visible)", count, 1, builder);
            if (AdvancedRenderer2D.this.enableReflections) {
                this.reflectors.append("Reflectors", count, 1, builder);
                this.reflectorsV.append("Reflectors (visible)", count, 1, builder);
            }
        }
        
        @Override
        public void dealWith(long nanoSecondsPassed, Object... objects) {
            if ((boolean) objects[0]) {
                this.sorted++;
            }
            this.sprites.push((int) objects[2]);
            if (AdvancedRenderer2D.this.enableReflections) {
                this.reflectors.push((int) objects[1]);
                this.reflectorsV.push((long) objects[3]);
            }
            this.spritesV.push((long) objects[4]);
        }
    };
}
