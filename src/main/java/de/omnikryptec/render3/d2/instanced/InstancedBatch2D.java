package de.omnikryptec.render3.d2.instanced;

import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.util.data.fc.FloatCollector;

public class InstancedBatch2D extends AbstractInstancedBatch2D<InstancedData> {
    
    
    private static final int FLOATCOLLECTOR_SIZE = 20000;
    static final int TEXTURE_ACCUM_SIZE = 8;
    private static final VertexBufferLayout INSTANCED_LAYOUT = new VertexBufferLayout();
    
    static {
        INSTANCED_LAYOUT.push(Type.FLOAT, 2, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 2, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 2, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 4, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 4, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 4, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 4, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 2, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 1, false, 1);
        INSTANCED_LAYOUT.push(Type.FLOAT, 1, false, 1);
    }
    public static final InstancedBatch2D DEFAULT_BATCH = new InstancedBatch2D();

    
    private InstancedShader shader;
    
    public InstancedBatch2D() {
        this(false, FLOATCOLLECTOR_SIZE);
    }
    
    public InstancedBatch2D(int floatcollectorsize) {
        this(false, floatcollectorsize);
    }
    
    public InstancedBatch2D(boolean cache) {
        this(cache, FLOATCOLLECTOR_SIZE);
    }
    
    public InstancedBatch2D(boolean cache, int floatcollectorsize) {
        super(cache, floatcollectorsize, INSTANCED_LAYOUT, TEXTURE_ACCUM_SIZE);
        if (!cache) {
            shader = new InstancedShader();
        }
    }
    
    @Override
    protected void fill(FloatCollector target, InstancedData id, int textureIndex) {
        id.fill(target);
        target.put(textureIndex);
    }
    
    @Override
    protected void bindShader() {
        shader.bindShader();
    }
    
    public InstancedShader getShader() {
        return shader;
    }
    
    @Override
    public void setProjectionViewMatrix(Matrix4fc mat) {
        this.shader.setProjectionViewMatrix(mat);
    }
    
}
