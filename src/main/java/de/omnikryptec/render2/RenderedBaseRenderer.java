package de.omnikryptec.render2;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;

public class RenderedBaseRenderer implements BaseRenderer {
    
    private static final TextureConfig MYCONFIG = new TextureConfig();
    
    private final Texture NULL_TEXTURE;
    
    private RenderData2D currentMeta;
    
    public RenderedBaseRenderer() {
        this.NULL_TEXTURE = LibAPIManager.instance().getGLFW().getRenderAPI()
                .createTexture2D(TextureData.WHITE_TEXTURE_DATA, MYCONFIG);
    }
    
    @Override
    public void prepare(RenderData2D meta) {
        this.currentMeta = meta;
        // meta.getShader().bindShader(); TODO BIND THE SHADER
        for (int i = 0; i < meta.getTextures().length; i++) {
            if (meta.getTextures()[i] == null) {
                NULL_TEXTURE.bindTexture(i);
            } else {
                meta.getTextures()[i].getBaseTexture().bindTexture(i);//Should already be efficient
            }
        }
        meta.updateShader();//meh
    }
    
    @Override
    public void addData(float[] floats) {
        this.currentMeta.getShader().getBuffers().addData(floats, 0, floats.length);
    }
    
    @Override
    public void flush() {
        this.currentMeta.getShader().getBuffers().flush();
    }
}
