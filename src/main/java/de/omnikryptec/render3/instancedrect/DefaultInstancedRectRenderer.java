package de.omnikryptec.render3.instancedrect;

import de.omnikryptec.libapi.exposed.render.VertexBufferLayout;
import de.omnikryptec.render3.FloatCollector;
import de.omnikryptec.libapi.exposed.render.RenderAPI.Type;

public class DefaultInstancedRectRenderer extends InstancedRectBatchedRenderer<DefaultInstanceRectData> {
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
    private DefaultInstancedRectShader shader;
    
    public DefaultInstancedRectRenderer(boolean cache) {
        super(cache, FLOATCOLLECTOR_SIZE, INSTANCED_LAYOUT, TEXTURE_ACCUM_SIZE);
        if (!cache) {
            shader = new DefaultInstancedRectShader();
        }
    }
    
    @Override
    protected void fill(FloatCollector target, DefaultInstanceRectData id, int textureIndex) {
        id.fill(target);
        target.put(textureIndex);
    }
    
    @Override
    protected void bindShader() {
        shader.bindShader();
    }
    
    public DefaultInstancedRectShader getShader() {
        return shader;
    }
    
}
