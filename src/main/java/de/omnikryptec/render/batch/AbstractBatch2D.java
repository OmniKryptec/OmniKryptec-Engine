package de.omnikryptec.render.batch;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;

public abstract class AbstractBatch2D {
    
    public static enum QuadSide {
        TopLeft, TopRight, BotLeft, BotRight;
    }
    
    private VertexManager vertexManager;
    private int perVertexCount = 4;
    
    private boolean rendering;
    private Matrix3x2f transformDefault;
    
    protected void init(final VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.transformDefault = null;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void begin() {
        this.rendering = true;
    }
    
    @OverridingMethodsMustInvokeSuper
    public void flush() {
        this.vertexManager.forceFlush();
    }
    
    @OverridingMethodsMustInvokeSuper
    public void end() {
        flush();
        this.rendering = false;
    }
    
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
        if (flipU && u0 != -1) {
            u0 = 1 - u0;
            u1 = 1 - u1;
        }
        if (flipV && v0 != -1) {
            v0 = 1 - v0;
            v1 = 1 - v1;
        }
        if (transform == null) {
            transform = transformDefault;
        }
        this.vertexManager.prepareNext(texture, 6 * perVertexCount);
        Vector2f botleft = new Vector2f(0);
        Vector2f botright = new Vector2f(width, 0);
        Vector2f topleft = new Vector2f(0, height);
        Vector2f topright = new Vector2f(width, height);
        if (transform != null) {
            botleft = transform.transformPosition(botleft);
            botright = transform.transformPosition(botright);
            topleft = transform.transformPosition(topleft);
            topright = transform.transformPosition(topright);
        }
        createVertices(botleft, botright, topleft, topright, u0, v0, u1, v1);
    }
    
    private void createVertices(Vector2f botleft, Vector2f botright, Vector2f topleft, Vector2f topright, float u0,
            float v0, float u1, float v1) {
        final float[] botleftfs = { botleft.x, botleft.y, u0, v0 };
        float[] botleftd = getSpecificVertexData(QuadSide.BotLeft);
        final float[] botrightfs = { botright.x, botright.y, u1, v0 };
        float[] botrightd = getSpecificVertexData(QuadSide.BotRight);
        final float[] topleftfs = { topleft.x, topleft.y, u0, v1 };
        float[] topleftd = getSpecificVertexData(QuadSide.TopLeft);
        final float[] toprightfs = { topright.x, topright.y, u1, v1 };
        float[] toprightd = getSpecificVertexData(QuadSide.TopRight);
        float[] quadData = getSpecificQuadData();
        
        addDatas(topleftfs, quadData, topleftd);
        addDatas(toprightfs, quadData, toprightd);
        addDatas(botleftfs, quadData, botleftd);
        
        addDatas(toprightfs, quadData, toprightd);
        addDatas(botrightfs, quadData, botrightd);
        addDatas(botleftfs, quadData, botleftd);
    }
    
    private void addDatas(float[] positionStuff, float[] quadstuff, float[] sidestuff) {
        this.vertexManager.addData(positionStuff);
        if (quadstuff != null) {
            this.vertexManager.addData(quadstuff);
        }
        if (sidestuff != null) {
            this.vertexManager.addData(sidestuff);
        }
    }
    
    protected float[] getSpecificQuadData() {
        return null;
    }
    
    protected float[] getSpecificVertexData(QuadSide side) {
        return null;
    }
    
    public void drawPolygon(final Texture texture, final float[] poly, final int start, final int len) {
        checkRendering();
        if (len % perVertexCount != 0) {
            throw new IllegalArgumentException("vertex size");
        }
        this.vertexManager.prepareNext(texture, len);
        this.vertexManager.addData(poly, start, len);
    }
    
    private void checkRendering() {
        if (!this.isRendering()) {
            throw new IllegalStateException("not rendering");
        }
    }
    
    public boolean isRendering() {
        return this.rendering;
    }
    
}
