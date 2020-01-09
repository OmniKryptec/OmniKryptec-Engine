/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.render.renderer;

import de.omnikryptec.libapi.exposed.render.FBTarget;
import de.omnikryptec.libapi.exposed.render.FBTarget.FBAttachmentFormat;
import de.omnikryptec.libapi.exposed.render.FrameBuffer;
import de.omnikryptec.libapi.exposed.render.RenderState;
import de.omnikryptec.libapi.exposed.render.RenderState.BlendMode;
import de.omnikryptec.libapi.exposed.window.SurfaceBuffer;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.render.batch.AbstractProjectedShaderSlot;
import de.omnikryptec.render.batch.SimpleBatch2D;
import de.omnikryptec.render.objects.IRenderedObjectListener;
import de.omnikryptec.render.objects.Light2D;
import de.omnikryptec.render.objects.RenderedObject;
import de.omnikryptec.render.objects.Sprite;
import de.omnikryptec.render.renderer.RendererContext.EnvironmentKey;
import de.omnikryptec.util.data.Color;
import de.omnikryptec.util.math.Mathd;
import de.omnikryptec.util.profiling.IProfiler;
import de.omnikryptec.util.profiling.ProfileHelper;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.Defaultable;
import de.omnikryptec.util.updater.Time;
import org.joml.FrustumIntersection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Renderer2D implements Renderer, IRenderedObjectListener {
    private static int rc = 0;

    private final int countIndex = rc++;

    static final Comparator<Sprite> DEFAULT_COMPARATOR = (s0, s1) -> s0.getLayer() - s1.getLayer();

    static final RenderState SPRITE_STATE = RenderState.of(BlendMode.ALPHA);
    static final RenderState LIGHT_STATE = RenderState.of(BlendMode.ADDITIVE);
    static final RenderState MULT_STATE = RenderState.of(BlendMode.MULTIPLICATIVE);

    private Comparator<Sprite> spriteComparator = DEFAULT_COMPARATOR;
    private final List<Sprite> sprites = new ArrayList<>();

    private final SimpleBatch2D batch;
    private FrameBuffer spriteBuffer;
    private FrameBuffer renderBuffer;

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
        this.spriteComparator = comparator == null ? DEFAULT_COMPARATOR : comparator;
    }

    @Override
    public void init(final LocalRendererContext context, final FrameBuffer target) {
        createFBOs(context, target);
        context.getIRenderedObjectManager().addListener(Sprite.TYPE, this);
        final List<Sprite> list = context.getIRenderedObjectManager().getFor(Sprite.TYPE);
        //is addAll fast enough or is a raw forloop faster?
        this.sprites.addAll(list);
        this.shouldSort = true;
    }

    @Override
    public void onAdd(final RenderedObject obj) {
        this.sprites.add((Sprite) obj);
        this.shouldSort = true;
    }

    @Override
    public void deinit(final LocalRendererContext context) {
        this.sprites.clear();
        this.shouldSort = false;
        context.getIRenderedObjectManager().removeListener(Sprite.TYPE, this);
        //delete FBOs
        this.renderBuffer.deleteAndUnregister();
        this.spriteBuffer.deleteAndUnregister();
    }

    @Override
    public void onRemove(final RenderedObject obj) {
        this.sprites.remove(obj);
    }

    @Override
    public void render(final Time time, final IProjection projection, final LocalRendererContext renderer) {
        Profiler.begin(toString());
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
        renderer.getRenderAPI().applyRenderState(LIGHT_STATE);
        this.renderBuffer.clearColor(renderer.getEnvironmentSettings().get(EnvironmentKeys2D.AmbientLight));
        RendererUtil.render2d(this.batch, renderer.getIRenderedObjectManager(), Light2D.TYPE, intersFilter);
        //render scene
        this.spriteBuffer.bindFrameBuffer();
        this.spriteBuffer.clearColor();
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        int vs = RendererUtil.render2d(this.batch, this.sprites, intersFilter);
        this.spriteBuffer.unbindFrameBuffer();
        //combine lights with the scene
        renderer.getRenderAPI().applyRenderState(MULT_STATE);
        this.spriteBuffer.renderDirect(0);
        this.renderBuffer.unbindFrameBuffer();
        //final draw
        renderer.getRenderAPI().applyRenderState(SPRITE_STATE);
        this.renderBuffer.renderDirect(0);
        Profiler.end(sorted, this.sprites.size(), vs);
    }

    @Override
    public void resizeFBOs(final LocalRendererContext context, final SurfaceBuffer screen) {
        this.spriteBuffer = this.spriteBuffer.resizedClone(screen.getWidth(), screen.getHeight());
        this.renderBuffer = this.renderBuffer.resizedClone(screen.getWidth(), screen.getHeight());
    }

    private void createFBOs(final LocalRendererContext context, final FrameBuffer screen) {
        this.spriteBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
        this.spriteBuffer.assignTargetB(0, new FBTarget(FBAttachmentFormat.RGBA16, 0));

        this.renderBuffer = context.getRenderAPI().createFrameBuffer(screen.getWidth(), screen.getHeight(), 0, 1);
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
