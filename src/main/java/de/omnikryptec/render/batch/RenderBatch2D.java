package de.omnikryptec.render.batch;

import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.RenderAPI.BufferUsage;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Renderable;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.TextureRegion;
import de.omnikryptec.libapi.exposed.render.VertexArray;
import de.omnikryptec.libapi.exposed.render.VertexBuffer;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.libapi.exposed.render.shader.Shader;
import de.omnikryptec.libapi.exposed.render.shader.UniformMatrix;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.util.data.Color;

public class RenderBatch2D implements Batch2D {
    
    private static final int FLOATS_PER_VERTEX = 2 + 2 + 4;
    
    private class MyRenderable implements Renderable {
        
        private VertexArray va;
        private VertexBuffer vb;
        
        private MyRenderable() {
            vb = RenderAPI.get().createVertexBuffer();
            va = RenderAPI.get().createVertexArray();
            va.addVertexBuffer(vb, new VertexBufferLayout.VertexBufferElement(Type.FLOAT, FLOATS_PER_VERTEX, false));
        }
        
        @Override
        public void bindRenderable() {
            va.bindArray();
        }
        
        @Override
        public void unbindRenderable() {
            va.unbindArray();
        }
        
        @Override
        public Primitive primitive() {
            //Makes sense?
            return Primitive.Quad;
        }
        
        @Override
        public int elementCount() {
            return vb.size() / FLOATS_PER_VERTEX;
        }
        
        @Override
        public boolean hasIndexBuffer() {
            return false;
        }
        
    }
    
    private boolean rendering;
    
    private FloatCollector data;
    private MyRenderable renderable;
    
    private Texture currentTexture;
    private Color color;
    
    private Shader shader;
    private UniformMatrix transform;
    
    public RenderBatch2D(int size) {
        color = new Color();
        data = new FloatCollector(size);
        renderable = new MyRenderable();
    }
    
    @Override
    public void begin() {
        rendering = true;
        shader.bindShader();
    }
    
    @Override
    public void setGlobalTransform(Matrix4fc mat) {
        if (rendering) {
            flush();
        }
        transform.loadMatrix(mat);
    }
    
    @Override
    public void setColor(Color color) {
        this.color.setFrom(color);
    }
    
    private void flushIfRequired(Texture texture, int requiredSpace) {
        if (requiredSpace > data.remaining()) {
            flush();
            if (requiredSpace > data.remaining()) {
                throw new IndexOutOfBoundsException("Can't handle mesh, buffer too small");
            }
            return;
        }
        if (!texture.equals(currentTexture)) {
            flush();
            this.currentTexture = texture;
            return;
        }
    }
    
    @Override
    public void flush() {
        currentTexture.bindTexture(0);
        renderable.vb.bindBuffer();
        renderable.vb.storeData(data.flush(), BufferUsage.Stream, renderable.vb.size());
        renderable.vb.unbindBuffer();
        RenderAPI.get().render(renderable);
    }
    
    @Override
    public void end() {
        flush();
        rendering = false;
    }
    
    @Override
    public void draw(Texture texture, Matrix3fc transform, boolean flipU, boolean flipV) {
        flushIfRequired(texture, 4 * FLOATS_PER_VERTEX);
    }
    
    @Override
    public void draw(TextureRegion texture, Matrix3fc transform, boolean flipU, boolean flipV) {
        flushIfRequired(texture.getBaseTexture(), 4 * FLOATS_PER_VERTEX);
    }
    
    @Override
    public void drawPolygon(Texture texture, float[] poly, int start, int len, int vertexcount) {
        flushIfRequired(texture, len);
    }
    
}
