package omnikryptec.terrain;

import omnikryptec.texture.ITexture;

/**
 *
 * @author Panzer1119
 */
public class TerrainTexturePack {
    
    private final ITexture backgroundTexture;
    private final ITexture rTexture;
    private final ITexture gTexture;
    private final ITexture bTexture;

    public TerrainTexturePack(ITexture backgroundTexture, ITexture rTexture, ITexture gTexture, ITexture bTexture) {
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public ITexture getBackgroundTexture() {
        return backgroundTexture;
    }

    public ITexture getrTexture() {
        return rTexture;
    }

    public ITexture getgTexture() {
        return gTexture;
    }

    public ITexture getbTexture() {
        return bTexture;
    }
    
}
