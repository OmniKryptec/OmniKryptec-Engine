package de.omnikryptec.render2;

import org.joml.Matrix4f;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render.Camera;
import de.omnikryptec.render.batch.SimpleShaderSlot;
import de.omnikryptec.render.batch.module.ColorModule;
import de.omnikryptec.render.batch.module.PositionModule;
import de.omnikryptec.render.batch.module.TilingModule;
import de.omnikryptec.render.batch.module.UVModule;

public class SimpleBatchedShader implements BatchedShader {
    
    public static class SimpleData2D implements RenderData2D {
        
        private SimpleBatchedShader bshad;
        private float[] floats = new float[54];
        
        public PositionModule posModule = new PositionModule();
        public UVModule uvModule = new UVModule();
        public ColorModule colorModule = new ColorModule();
        public TilingModule tilingModule = new TilingModule();
        
        public Texture[] t = new Texture[1];
        
        public SimpleData2D(SimpleBatchedShader s) {
            this.bshad = s;
        }
        
        @Override
        public float[] getVertexData() {
            bshad.helper.put(floats, 0, colorModule, tilingModule, posModule, uvModule);
            return floats;
        }
        
        @Override
        public Texture[] getTextures() {
            return t;
        }
        
        @Override
        public BatchedShader getShader() {
            return bshad;
        }
        
    }
    
    private ModuleBatchingManager helper;
    private BufferHolder buffers;
    private SimpleShaderSlot shader;
    
    public SimpleBatchedShader() {
        this.helper = new ModuleBatchingManager(5, 4);
        this.buffers = new BufferHolder(layout(), 1000, 9);
        this.shader = new SimpleShaderSlot();
        //this.shader.setProjection(new Camera(new Matrix4f()));
        this.shader.setViewProjectionMatrix(new Matrix4f());
        this.shader.setTransformMatrix(new Matrix4f());
    }
    
    private VertexBufferLayout layout() {
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(Type.FLOAT, 4, false);
        layout.push(Type.FLOAT, 1, false);
        layout.push(Type.FLOAT, 2, false);
        layout.push(Type.FLOAT, 2, false);
        return layout;
    }
    
    @Override
    public SimpleData2D createRenderData() {
        return new SimpleData2D(this);
    }
    
    @Override
    public BufferHolder getBuffers() {
        return buffers;
    }
    
    @Override
    public void bind() {
        shader.bindShaderRenderReady();
    }
    
}
