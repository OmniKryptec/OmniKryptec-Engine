package de.omnikryptec.render.batch;

import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;

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
import de.omnikryptec.libapi.exposed.render.shader.UniformSampler;
import de.omnikryptec.resource.MeshData.Primitive;
import de.omnikryptec.resource.parser.shader.ShaderParser;
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
    
    private FloatCollector data;
    private MyRenderable renderable;
    
    private boolean rendering;
    private VertexManager vertexManager;
    
    private Color color;
    
    private Shader shader;
    private UniformMatrix transform;
    
    public RenderBatch2D(int size) {
        color = new Color();
        data = new FloatCollector(size);
        renderable = new MyRenderable();
        shader = RenderAPI.get().createShader();
        shader.create("engine_RenderBatch2D_shader");
        transform = shader.getUniform("u_transform");
        
        UniformSampler sampler = shader.getUniform("texture");
        shader.bindShader();
        sampler.setSampler(0);
    }
    
    @Override
    public void begin() {
        rendering = true;
        shader.bindShader();
    }
    
    @Override
    public void setGlobalTransform(Matrix4fc mat) {
        if (rendering) {
            vertexManager.forceFlush();
        }
        transform.loadMatrix(mat);
    }
    
    @Override
    public void setColor(Color color) {
        this.color.setFrom(color);
    }
    
    //    private void flushIfRequired(Texture texture, int requiredSpace) {
    //        if (requiredSpace > data.remaining()) {
    //            vertexManager.flush();
    //            if (requiredSpace > data.remaining()) {
    //                throw new IndexOutOfBoundsException("Can't handle mesh, buffer too small");
    //            }
    //            
    //        }else
    //        if (!texture.equals(currentTexture)) {
    //            vertexManager.flush();
    //            this.currentTexture = texture;
    //            return;
    //        }
    //    }
    
    @Override
    public void flush() {
        vertexManager.forceFlush();
        //        currentTexture.bindTexture(0);
        //        renderable.vb.storeData(data.flush(), BufferUsage.Stream, renderable.vb.size());
        //        RenderAPI.get().render(renderable);
    }
    
    @Override
    public void end() {
        vertexManager.forceFlush();
        rendering = false;
    }
    
    @Override
    public void draw(Texture texture, Matrix3fc transform, float width, float height, boolean flipU, boolean flipV) {
        vertexManager.prepareNext(texture, 6 * FLOATS_PER_VERTEX);
        
    }
    
    @Override
    public void draw(TextureRegion texture, Matrix3fc transform, float width, float height, boolean flipU,
            boolean flipV) {
        vertexManager.prepareNext(texture.getBaseTexture(), 4 * FLOATS_PER_VERTEX);
    }
    
    @Override
    public void drawPolygon(Texture texture, float[] poly, int start, int len) {
        if (len % FLOATS_PER_VERTEX != 0) {
            throw new IllegalArgumentException("vertex size");
        }
        vertexManager.prepareNext(texture, len);
        data.put(poly, start, len);
    }
    
}
