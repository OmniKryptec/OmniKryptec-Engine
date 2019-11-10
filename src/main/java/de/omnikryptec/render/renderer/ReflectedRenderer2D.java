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
    private final List<ReflectiveSprite> sprites = new ArrayList<>();
    private final List<ReflectiveSprite> reflectors = new ArrayList<>();

    private final SimpleBatch2D reflectionBatch;
    private final ReflectedBatch2D mainBatch;
    private FrameBuffer spriteBuffer;
    private FrameBuffer renderBuffer;
    private FrameBuffer reflectionBuffer;

    private boolean shouldSort = false;

    public ReflectedRenderer2D() {
        this(1000);
    }

    public ReflectedRenderer2D(final int vertices) {
        this.reflectionBatch = new SimpleBatch2D(vertices);
        this.mainBatch = new ReflectedBatch2D(vertices);
    }

    public ReflectedRenderer2D(final int vertices, final AbstractReflectedShaderSlot mainShaderSlot,
            final AbstractProjectedShaderSlot reflectionShaderSlot) {
        this.reflectionBatch = new SimpleBatch2D(vertices, reflectionShaderSlot);
        this.mainBatch = new ReflectedBatch2D(vertices, mainShaderSlot);
    }

    public void setSpriteComparator(final Comparator<Sprite> comparator) {
        this.spriteComparator = comparator == null ? Renderer2D.DEFAULT_COMPARATOR : comparator;
    }

    @Override
    public void init(final LocalRendererContext context, final FrameBuffer target) {
        createFBOs(context, target);
        context.getIRenderedObjectManager().addListener(ReflectiveSprite.TYPE, this);
        final List<ReflectiveSprite> list = context.getIRenderedObjectManager().getFor(ReflectiveSprite.TYPE);
        for (final ReflectiveSprite s : list) {
            this.sprites.add(s);
            if (s.getReflectionType() == Reflection2DType.Cast) {
                this.reflectors.add(s);
            }
        }
        this.shouldSort = true;
    }

    @Override
    public void onAdd(final RenderedObject obj) {
        final ReflectiveSprite s = (ReflectiveSprite) obj;
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
        context.getIRenderedObjectManager().removeListener(ReflectiveSprite.TYPE, this);
        //delete FBOs
        this.renderBuffer.deleteAndUnregister();
        this.spriteBuffer.deleteAndUnregister();
        this.reflectionBuffer.deleteAndUnregister();
    }

    @Override
    public void onRemove(final RenderedObject obj) {
        this.sprites.remove(obj);
        if (((ReflectiveSprite) obj).getReflectionType() == Reflection2DType.Cast) {
            this.reflectors.remove(obj);
        }
    }

    @Override
    public void render(final Time time, final IProjection projection, final LocalRendererContext renderer) {
        if (this.shouldSort) {
            this.sprites.sort(this.spriteComparator);
            this.shouldSort = false;
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
        this.reflectionBuffer.bindFrameBuffer();
        this.reflectionBuffer.clearColor();
        this.reflectionBatch.begin();
        for (final ReflectiveSprite s : this.reflectors) {
            if (s.isVisible(intersFilter)) {
                s.drawReflection(this.reflectionBatch);
            }
        }
        this.reflectionBatch.end();
        this.reflectionBuffer.unbindFrameBuffer();
        //render scene
        this.spriteBuffer.bindFrameBuffer();
        this.spriteBuffer.clearColor();
        this.mainBatch.getShaderSlot().setProjection(projection);
        this.mainBatch.getShaderSlot().setReflection(this.reflectionBuffer.getTexture(0));
        this.mainBatch.begin();
        for (final ReflectiveSprite s : this.sprites) {
            if (s.isVisible(intersFilter)) {
                this.mainBatch.reflectionStrength()
                        .set(s.getReflectionType() != Reflection2DType.Receive ? Color.ZERO : s.reflectiveness());
                s.draw(this.mainBatch);
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
}
