package de.omnikryptec.render.batch;

import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.data.Color;

public class RenderBatch2D implements Batch2D {

    private VertexManager vertexManager;

    private Color color;
    private IProjection projection;
    private boolean rendering;

    private Shader shader;
    private UniformMatrix transform;
    private UniformMatrix viewProjection;

    public RenderBatch2D(final int vertices) {
        final VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(Type.FLOAT, 2, false);
        layout.push(Type.FLOAT, 2, false);
        layout.push(Type.FLOAT, 4, false);
        init(new RenderedVertexManager(vertices, layout));
    }

    public RenderBatch2D(final VertexManager vertexmanager) {
        init(vertexmanager);
    }

    private void init(final VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        this.color = new Color(1, 1, 1, 1);
        this.shader = RenderAPI.get().createShader();
        this.shader.create("engineRenderBatch2DShader");
        this.transform = this.shader.getUniform("u_transform");
        this.viewProjection = this.shader.getUniform("u_projview");

        final UniformSampler sampler = this.shader.getUniform("sampler");
        this.shader.bindShader();
        this.transform.loadMatrix(new Matrix4f());
        setProjection(null);
        sampler.setSampler(0);
    }

    @Override
    public void begin() {
        this.rendering = true;
        this.shader.bindShader();
        reloadProjectionUniform();
    }

    @Override
    public void setGlobalTransform(final Matrix4fc mat) {
        if (this.rendering) {
            this.vertexManager.forceFlush();
        } else {
            this.shader.bindShader();
        }
        this.transform.loadMatrix(mat == null ? new Matrix4f() : mat);
    }

    @Override
    public void setProjection(final IProjection projection) {
        this.projection = projection;
        if (this.rendering) {
            this.vertexManager.forceFlush();
            reloadProjectionUniform();
        }
    }

    private void reloadProjectionUniform() {
        this.viewProjection.loadMatrix(this.projection == null ? new Matrix4f() : this.projection.getProjection());
    }

    @Override
    public Color color() {
        return this.color;
    }

    @Override
    public void flush() {
        this.vertexManager.forceFlush();
    }

    @Override
    public void end() {
        flush();
        this.rendering = false;
    }

    @Override
    public void draw(final Texture texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        draw(texture, transform, width, height, flipU, flipV, texture == null ? -1 : 0, texture == null ? -1 : 0,
                texture == null ? -1 : 1, texture == null ? -1 : 1);
    }

    @Override
    public void draw(final TextureRegion texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV) {
        final float u0 = texture == null ? -1 : texture.u0();
        final float v0 = texture == null ? -1 : texture.v0();
        final float u1 = texture == null ? -1 : texture.u1();
        final float v1 = texture == null ? -1 : texture.v1();
        draw(texture == null ? null : texture.getBaseTexture(), transform, width, height, flipU, flipV, u0, v0, u1, v1);
    }

    private void draw(final Texture texture, final Matrix3x2fc transform, final float width, final float height,
            final boolean flipU, final boolean flipV, float u0, float v0, float u1, float v1) {
        checkRendering();
        this.vertexManager.prepareNext(texture, 6 * this.vertexManager.floatsPerVertex());
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
        if (len % this.vertexManager.floatsPerVertex() != 0) {
            throw new IllegalArgumentException("vertex size");
        }
        this.vertexManager.prepareNext(texture, len);
        this.vertexManager.addVertex(poly, start, len);
    }

    private void checkRendering() {
        if (!this.rendering) {
            throw new IllegalStateException("not rendering");
        }
    }

    public void drawTest() {
        this.vertexManager.addVertex(0, 0, 1, 1, 1, 1, 0, 0);
        this.vertexManager.addVertex(1, 0, 1, 1, 1, 1, 1, 0);
        this.vertexManager.addVertex(1, 1, 1, 1, 1, 1, 1, 1);
    }

}
