package de.omnikryptec.render3.d2.instanced;

import org.joml.Matrix4fc;

import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;
import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.util.data.fc.FloatCollector;

public class InstancedBatch2D extends AbstractInstancedBatch2D<InstancedData> {
    private static final int FLOATCOLLECTOR_SIZE = 40000;
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
    }
    private InstancedShader shader;
    
    public InstancedBatch2D(boolean cache) {
        super(cache, FLOATCOLLECTOR_SIZE, INSTANCED_LAYOUT, TEXTURE_ACCUM_SIZE);
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
    public void setProjectionViewMatrx(Matrix4fc mat) {
        this.shader.setProjectionViewMatrix(mat);
    }
    
}
