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
import de.omnikryptec.render.batch.AbstractReflectedShaderSlot;
import de.omnikryptec.render.batch.ReflectedBatch2D;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectListener;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.objects.ReflectiveSprite;
import de.omnikryptec.render.objects.ReflectiveSprite.Reflection2DType;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.Renderer2D.EnvironmentKeys2D;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.updater.Time;

public class ReflectedRenderer2D implements Renderer, IRenderedObjectListener {
    
    private Comparator<Sprite> spriteComparator = Renderer2D.DEFAULT_COMPARATOR;
    private List<ReflectiveSprite> sprites = new ArrayList<>();
    private List<ReflectiveSprite> reflectors = new ArrayList<>();
    
    private SimpleBatch2D reflectionBatch;
    private ReflectedBatch2D mainBatch;
    private FrameBuffer spriteBuffer;
    private FrameBuffer renderBuffer;
    private FrameBuffer reflectionBuffer;
    
    private boolean shouldSort = false;
    
    public ReflectedRenderer2D() {
        this(1000);
    }
    
    public ReflectedRenderer2D(int vertices) {
        this.reflectionBatch = new SimpleBatch2D(vertices);
        this.mainBatch = new ReflectedBatch2D(vertices);
    }
    
    public ReflectedRenderer2D(int vertices, AbstractReflectedShaderSlot mainShaderSlot,
            AbstractProjectedShaderSlot reflectionShaderSlot) {
        this.reflectionBatch = new SimpleBatch2D(vertices, reflectionShaderSlot);
        this.mainBatch = new ReflectedBatch2D(vertices, mainShaderSlot);
    }
    
    public void setSpriteComparator(Comparator<Sprite> comparator) {
        this.spriteComparator = comparator == null ? Renderer2D.DEFAULT_COMPARATOR : comparator;
    }
    
    @Override
    public void init(LocalRendererContext context, FrameBuffer target) {
        createFBOs(context, target);
        context.getIRenderedObjectManager().addListener(ReflectiveSprite.TYPE, this);
        List<ReflectiveSprite> list = context.getIRenderedObjectManager().getFor(ReflectiveSprite.TYPE);
        for (ReflectiveSprite s : list) {
            sprites.add(s);
            if (s.getReflectionType() == Reflection2DType.Cast) {
                reflectors.add(s);
            }
        }
        this.shouldSort = true;
    }
    
    @Override
    public void onAdd(RenderedObject obj) {
        ReflectiveSprite s = (ReflectiveSprite) obj;
        this.sprites.add(s);
        if (s.getReflectionType() == Reflection2DType.Cast) {
            reflectors.add(s);
        }
        this.shouldSort = true;
    }
    
    @Override
    public void deinit(LocalRendererContext context) {
        this.sprites.clear();
        this.shouldSort = false;
        context.getIRenderedObjectManager().removeListener(ReflectiveSprite.TYPE, this);
        //delete FBOs
        renderBuffer.deleteAndUnregister();
        spriteBuffer.deleteAndUnregister();
        reflectionBuffer.deleteAndUnregister();
    }
    
    @Override
    public void onRemove(RenderedObject obj) {
        this.sprites.remove(obj);
        if (((ReflectiveSprite) obj).getReflectionType() == Reflection2DType.Cast) {
            reflectors.remove(obj);
        }
    }
    
    @Override
    public void render(Time time, IProjection projection, LocalRendererContext renderer) {
        if (shouldSort) {
            sprites.sort(spriteComparator);
            shouldSort = false;
        }
        
        renderBuffer.bindFrameBuffer();
        FrustumIntersection intersFilter = new FrustumIntersection(projection.getProjection());
        reflectionBatch.getShaderSlot().setProjection(projection);
        //render lights
        renderer.getRenderAPI().applyRenderState(Renderer2D.LIGHT_STATE);
        renderBuffer.clearColor(renderer.getEnvironmentSettings().get(EnvironmentKeys2D.AmbientLight));
        RendererUtil.render2d(reflectionBatch, renderer.getIRenderedObjectManager(), Light2D.TYPE, intersFilter);
        //render reflection
        renderer.getRenderAPI().applyRenderState(Renderer2D.SPRITE_STATE);
        reflectionBuffer.bindFrameBuffer();
        reflectionBuffer.clearColor();
        reflectionBatch.begin();
        for (ReflectiveSprite s : reflectors) {
            if (s.isVisible(intersFilter)) {
                s.drawReflection(reflectionBatch);
            }
        }
        reflectionBatch.end();
        reflectionBuffer.unbindFrameBuffer();
        //render scene
        spriteBuffer.bindFrameBuffer();
        spriteBuffer.clearColor();
        mainBatch.getShaderSlot().setProjection(projection);
        mainBatch.getShaderSlot().setReflection(reflectionBuffer.getTexture(0));
        mainBatch.begin();
        for (ReflectiveSprite s : sprites) {
            if (s.isVisible(intersFilter)) {
                mainBatch.reflectionStrength()
                        .set(s.getReflectionType() != Reflection2DType.Receive ? Color.ZERO : s.reflectiveness());
                s.draw(mainBatch);
            }
        }
        mainBatch.end();
        spriteBuffer.unbindFrameBuffer();
        //combine lights with the scene
        renderer.getRenderAPI().applyRenderState(Renderer2D.MULT_STATE);
        spriteBuffer.renderDirect(0);
        renderBuffer.unbindFrameBuffer();
        //final draw
        renderer.getRenderAPI().applyRenderState(Renderer2D.SPRITE_STATE);
        renderBuffer.renderDirect(0);
    }
    
    public void forceSort() {
        this.shouldSort = true;
    }
    
    @Override
    public void resizeFBOs(LocalRendererContext context, SurfaceBuffer screen) {
        spriteBuffer = spriteBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        renderBuffer = renderBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        reflectionBuffer = reflectionBuffer.resizedClone(screen.getWidth() / 2, screen.getHeight() / 2);
    }
    
    private void createFBOs(LocalRendererContext context, FrameBuffer screen) {
        spriteBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        
        renderBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        renderBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
        
        reflectionBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth() / 2, screen.getHeight() / 2, 0,
                1);
        reflectionBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }
}
