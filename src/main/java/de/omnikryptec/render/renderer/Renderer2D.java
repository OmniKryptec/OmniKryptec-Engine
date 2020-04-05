package de.omnikryptec.render.renderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.FrustumIntersection;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.AbstractProjectedShaderSlot;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.SimpleSprite;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.ViewManager.EnvironmentKey;
import de.omnikryptec.util.Util;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.profiling.IProfiler;
import de.omnikryptec.util.profiling.ProfileHelper;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.settings.Settings;
import de.omnikryptec.util.updater.Time;

public class Renderer2D implements Renderer {
    private static int rc = 0;

    private final int countIndex = rc++;

    static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0, s1) -> s0.getLayer() - s1.getLayer();

    public static final RenderState SPRITE_STATE = RenderState.of(BlendMode.ALPHA);
    public static final RenderState LIGHT_STATE = RenderState.of(BlendMode.ADDITIVE);
    public static final RenderState MULT_STATE = RenderState.of(BlendMode.MULTIPLICATIVE);

    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Sprite> lights = new ArrayList<>();

    private final SimpleBatch2D batch;
    private FrameBuffer spriteBuffer;
    private FrameBuffer renderBuffer;

    private final Color ambientColor = new Color(1, 1, 1, 1);
    private boolean enableTiling = false;

    private boolean shouldSort = false;

    public Renderer2D() {
        this(1000);
    }

    public Renderer2D(final int vertices) {
        this.batch = new SimpleBatch2D(vertices);
        initStuff();
    }

    public Renderer2D(final int vertices, final AbstractProjectedShaderSlot shaderslot) {
        this.batch = new SimpleBatch2D(vertices, shaderslot);
        initStuff();
    }

    private void initStuff() {
        Profiler.addIProfiler(toString(), this.profiler);
    }

    public void setSpriteComparator(final Comparator<Sprite> comparator) {
        this.spriteComparator = Util.defaultIfNull(DEFAULT_COMPARATOR, comparator);
    }

    public Color ambientLight() {
        return this.ambientColor;
    }

    public void setEnableTiling(boolean b) {
        this.enableTiling = b;
    }

    @Override
    public void init(ViewManager vm, RenderAPI api) {
        createFBOs(api, vm.getMainView().getTargetFbo());
        this.shouldSort = true;
    }

    public void add(Sprite sprite) {
        this.sprites.add(sprite);
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
    }

    public void remove(Sprite sprite) {
        this.sprites.remove(sprite);
    }

    @Override
    public void render(ViewManager viewManager, RenderAPI api, IProjection projection, FrameBuffer target,
            Settings<EnvironmentKey> envSettings, Time time) {
        Profiler.begin(toString());
        checkFBOs(target);
        boolean sorted = false;
        if (this.shouldSort) {
            this.sprites.sort(this.spriteComparator);
            this.shouldSort = false;
            sorted = true;
        }
        this.renderBuffer.bindFrameBuffer();
        final FrustumIntersection intersFilter = new FrustumIntersection(projection.getProjection());
        this.batch.getShaderSlot().setProjection(projection);
        //render lights
        api.applyRenderState(LIGHT_STATE);
        this.renderBuffer.clearColor(this.ambientColor);
        RendererUtil.render2d(this.batch, this.lights, intersFilter);
        //render scene
        this.spriteBuffer.bindFrameBuffer();
        this.spriteBuffer.clearColor();
        api.applyRenderState(SPRITE_STATE);
        this.batch.begin();
        int vs = 0;
        if (!this.enableTiling) {
            this.batch.setTilingFactor(1);
        }
        for (final Sprite s : this.sprites) {
            if (s.isVisible(intersFilter)) {
                if (this.enableTiling) {
                    if (s instanceof SimpleSprite) {
                        this.batch.setTilingFactor(((SimpleSprite) s).getTilingFactor());
                    } else {
                        this.batch.setTilingFactor(1);
                    }
                }
                s.draw(this.batch);
                vs++;
            }
        }
        this.batch.end();
        this.spriteBuffer.unbindFrameBuffer();
        //combine lights with the scene
        api.applyRenderState(MULT_STATE);
        this.spriteBuffer.renderDirect(0);
        this.renderBuffer.unbindFrameBuffer();
        //final draw
        api.applyRenderState(SPRITE_STATE);
        this.renderBuffer.renderDirect(0);
        Profiler.end(sorted, this.sprites.size(), vs);
    }

    private void checkFBOs(FrameBuffer target) {
        this.spriteBuffer = this.spriteBuffer.resizeAndDeleteOrThis(target.getWidth(), target.getHeight());
        this.renderBuffer = this.renderBuffer.resizeAndDeleteOrThis(target.getWidth(), target.getHeight());
    }

    private void createFBOs(RenderAPI api, final FrameBuffer screen) {
        this.spriteBuffer = api.createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        this.spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));

        this.renderBuffer = api.createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        this.renderBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));
    }

    @Override
    public String toString() {
        return Renderer2D.class.getSimpleName() + "-" + this.countIndex;
    }

    private final IProfiler profiler = new IProfiler() {
        private long sorted = 0;
        private final ProfileHelper sprites = new ProfileHelper();
        private final ProfileHelper spritesV = new ProfileHelper();

        @Override
        public void writeData(StringBuilder builder, long count) {
            builder.append("Layers sorted: " + Mathd.round(this.sorted * 100 / (double) count, 2) + "%").append('\n');
            this.sprites.append("Sprites", count, 1, builder);
            this.spritesV.append("Sprites (visible): ", count, 1, builder);
        }

        @Override
        public void dealWith(long nanoSecondsPassed, Object... objects) {
            if ((boolean) objects[0]) {
                this.sorted++;
            }
            this.sprites.push((int) objects[1]);
            this.spritesV.push((int) objects[2]);
        }
    };

    public static enum EnvironmentKeys2D implements Defaultable, EnvironmentKey {
        AmbientLight(new Color(1f, 1f, 1f));

        private final Object def;

        private EnvironmentKeys2D(final Object o) {
            this.def = o;
        }

        @Override
        public <T> T getDefault() {
            return (T) this.def;
        }
    }
}
