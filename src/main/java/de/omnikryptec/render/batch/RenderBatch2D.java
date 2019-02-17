package de.omnikryptec.render.batch;

import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.render.IProjection;
import de.omnikryptec.util.data.Color;

public class RenderBatch2D implements Batch2D {
    
    private static final int FLOATS_PER_VERTEX = 2 + 2 + 4;
    
    private VertexManager vertexManager;
    
    private Color color;
    private IProjection projection;
    private boolean rendering;
    
    private Shader shader;
    private UniformMatrix transform;
    private UniformMatrix viewProjection;
    
    public RenderBatch2D(int vertices) {
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(Type.FLOAT, 2, false);
        layout.push(Type.FLOAT, 2, false);
        layout.push(Type.FLOAT, 4, false);
        init(new RenderedVertexManager(vertices, layout));
    }
    
    public RenderBatch2D(VertexManager vertexmanager) {
        init(vertexmanager);
    }
    
    private void init(VertexManager vertexManager) {
        this.vertexManager = vertexManager;
        color = new Color(1, 1, 1, 1);
        shader = RenderAPI.get().createShader();
        shader.create("engineRenderBatch2DShader");
        transform = shader.getUniform("u_transform");
        viewProjection = shader.getUniform("u_projview");
        
        UniformSampler sampler = shader.getUniform("sampler");
        shader.bindShader();
        transform.loadMatrix(new Matrix4f());
        setProjection(null);
        sampler.setSampler(0);
    }
    
    @Override
    public void begin() {
        rendering = true;
        shader.bindShader();
        reloadProjectionUniform();
    }
    
    @Override
    public void setGlobalTransform(Matrix4fc mat) {
        if (rendering) {
            vertexManager.forceFlush();
        } else {
            shader.bindShader();
        }
        transform.loadMatrix(mat == null ? new Matrix4f() : mat);
    }
    
    @Override
    public void setProjection(IProjection projection) {
        this.projection = projection;
        if (rendering) {
            vertexManager.forceFlush();
            reloadProjectionUniform();
        }
    }
    
    private void reloadProjectionUniform() {
        viewProjection.loadMatrix(projection == null ? new Matrix4f() : projection.getProjection());
    }
    
    @Override
    public Color color() {
        return color;
    }
    
    @Override
    public void flush() {
        vertexManager.forceFlush();
    }
    
    @Override
    public void end() {
        flush();
        rendering = false;
    }
    
    @Override
    public void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV) {
        draw(texture, transform, width, height, flipU, flipV, texture == null ? -1 : 0, texture == null ? -1 : 0,
                texture == null ? -1 : 1, texture == null ? -1 : 1);
    }
    
    @Override
    public void draw(TextureRegion texture, Matrix3x2fc transform, float width, float height, boolean flipU,
            boolean flipV) {
        float u0 = texture == null ? -1 : texture.u0();
        float v0 = texture == null ? -1 : texture.v0();
        float u1 = texture == null ? -1 : texture.u1();
        float v1 = texture == null ? -1 : texture.v1();
        draw(texture == null ? null : texture.getBaseTexture(), transform, width, height, flipU, flipV, u0, v0, u1, v1);
    }
    
    private void draw(Texture texture, Matrix3x2fc transform, float width, float height, boolean flipU, boolean flipV,
            float u0, float v0, float u1, float v1) {
        checkRendering();
        vertexManager.prepareNext(texture, 6 * FLOATS_PER_VERTEX);
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
        float[] botleftfs = { botleft.x, botleft.y, u0, v0, color.getR(), color.getG(), color.getB(), color.getA() };
        float[] botrightfs = { botright.x, botright.y, u1, v0, color.getR(), color.getG(), color.getB(), color.getA() };
        float[] topleftfs = { topleft.x, topleft.y, u0, v1, color.getR(), color.getG(), color.getB(), color.getA() };
        float[] toprightfs = { topright.x, topright.y, u1, v1, color.getR(), color.getG(), color.getB(), color.getA() };
        vertexManager.addVertex(topleftfs);
        vertexManager.addVertex(toprightfs);
        vertexManager.addVertex(botleftfs);
        
        vertexManager.addVertex(toprightfs);
        vertexManager.addVertex(botrightfs);
        vertexManager.addVertex(botleftfs);
    }
    
    @Override
    public void drawPolygon(Texture texture, float[] poly, int start, int len) {
        checkRendering();
        if (len % FLOATS_PER_VERTEX != 0) {
            throw new IllegalArgumentException("vertex size");
        }
        vertexManager.prepareNext(texture, len);
        vertexManager.addVertex(poly, start, len);
    }
    
    private void checkRendering() {
        if (!rendering) {
            throw new IllegalStateException("not rendering");
        }
    }
    
    public void drawTest() {
        vertexManager.addVertex(0, 0, 1, 1, 1, 1, 0, 0);
        vertexManager.addVertex(1, 0, 1, 1, 1, 1, 1, 0);
        vertexManager.addVertex(1, 1, 1, 1, 1, 1, 1, 1);
    }
    
}
