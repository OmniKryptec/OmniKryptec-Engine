package de.omnikryptec.render.batch;

import java.util.function.Function;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.util.data.Color;

public class RenderBatch2D implements Batch2D {
    
    private static final VertexBufferLayout MY_LAYOUT = new VertexBufferLayout();
    
    static {
        MY_LAYOUT.push(Type.FLOAT, 2, false);
        MY_LAYOUT.push(Type.FLOAT, 2, false);
        MY_LAYOUT.push(Type.FLOAT, 4, false);
        MY_LAYOUT.lock();
    }
    
    private VertexManager vertexManager;
    
    private Color color;
    private boolean rendering;
    private Matrix3x2f transformDefault;
    
    public RenderBatch2D(final int vertices) {
        init(new RenderedVertexManager(vertices, MY_LAYOUT));
    }
    
    public RenderBatch2D(Function<VertexBufferLayout, VertexManager> vertexManagerFactory) {
        init(vertexManagerFactory.apply(MY_LAYOUT));
    }
    
    protected RenderBatch2D() {
    }
    
    protected void init(final VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.color = new Color(1, 1, 1, 1);
        this.transformDefault = new Matrix3x2f();
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void begin() {
        this.rendering = true;
    }
    
    @Override
    public Color color() {
        return this.color;
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void flush() {
        this.vertexManager.forceFlush();
    }
    
    @OverridingMethodsMustInvokeSuper
    @Override
    public void end() {
        flush();
        this.rendering = false;
    }
    
    @Override
    public void draw(final Texture texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        if (texture instanceof TextureRegion) {
            draw((TextureRegion) texture, transform, width, height, flipU, flipV);
        } else {
            draw(texture, transform, width, height, flipU, flipV, texture == null ? -1 : 0, texture == null ? -1 : 0,
                    texture == null ? -1 : 1, texture == null ? -1 : 1);
        }
    }
    
    private void draw(final TextureRegion texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        final float u0 = texture == null ? -1 : texture.u0();
        final float v0 = texture == null ? -1 : texture.v0();
        final float u1 = texture == null ? -1 : texture.u1();
        final float v1 = texture == null ? -1 : texture.v1();
        draw(texture == null ? null : texture.getBaseTexture(), transform, width, height, flipU, flipV, u0, v0, u1, v1);
    }
    
    private void draw(final Texture texture, Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, boolean flipV, float u0, float v0, float u1, float v1) {
        checkRendering();
        if (texture != null) {
            flipV = flipV != texture.requiresInvertedVifDrawn2D();
        }
        if (transform == null) {
            transform = transformDefault;
        }
        this.vertexManager.prepareNext(texture, 6 * MY_LAYOUT.getCount());
        Vector2f botleft = new Vector2f(0);
        Vector2f botright = new Vector2f(width, 0);
        Vector2f topleft = new Vector2f(0, height);
        Vector2f topright = new Vector2f(width, height);
        botleft = transform.transformPosition(botleft);
        botright = transform.transformPosition(botright);
        topleft = transform.transformPosition(topleft);
        topright = transform.transformPosition(topright);
        if (flipU && u0 != -1) {
            u0 = 1 - u0;
            u1 = 1 - u1;
        }
        if (flipV && v0 != -1) {
            v0 = 1 - v0;
            v1 = 1 - v1;
        }
        final float[] botleftfs = { botleft.x, botleft.y, u0, v0, this.color.getR(), this.color.getG(),
                this.color.getB(), this.color.getA() };
        final float[] botrightfs = { botright.x, botright.y, u1, v0, this.color.getR(), this.color.getG(),
                this.color.getB(), this.color.getA() };
        final float[] topleftfs = { topleft.x, topleft.y, u0, v1, this.color.getR(), this.color.getG(),
                this.color.getB(), this.color.getA() };
        final float[] toprightfs = { topright.x, topright.y, u1, v1, this.color.getR(), this.color.getG(),
                this.color.getB(), this.color.getA() };
        this.vertexManager.addVertex(topleftfs);
        this.vertexManager.addVertex(toprightfs);
        this.vertexManager.addVertex(botleftfs);
        
        this.vertexManager.addVertex(toprightfs);
        this.vertexManager.addVertex(botrightfs);
        this.vertexManager.addVertex(botleftfs);
    }
    
    @Override
    public void drawPolygon(final Texture texture, final float[] poly, final int start, final int len) {
        checkRendering();
        if (len % MY_LAYOUT.getCount() != 0) {
            throw new IllegalArgumentException("vertex size");
        }
        this.vertexManager.prepareNext(texture, len);
        this.vertexManager.addVertex(poly, start, len);
    }
    
    private void checkRendering() {
        if (!this.isRendering()) {
            throw new IllegalStateException("not rendering");
        }
    }
    
    public boolean isRendering() {
        return this.rendering;
    }
    
    public void drawTest() {
        this.vertexManager.addVertex(0, 0, 1, 1, 1, 1, 0, 0);
        this.vertexManager.addVertex(1, 0, 1, 1, 1, 1, 1, 0);
        this.vertexManager.addVertex(1, 1, 1, 1, 1, 1, 1, 1);
    }
    
}
