package omnikryptec.resource.texture;

import java.io.InputStream;

import omnikryptec.display.DisplayManager;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.settings.GameSettings;

public class TextureBuilder {

    private boolean clampEdges = OmniKryptecEngine.instance().getDisplayManager().getSettings().getBoolean(GameSettings.CLAMP_EDGES);
    private boolean mipmap = OmniKryptecEngine.instance().getDisplayManager().getSettings().getBoolean(GameSettings.MIPMAP);
    private boolean anisotropic = OmniKryptecEngine.instance().getDisplayManager().getSettings().getAnisotropicLevel() > 0;
    private boolean nearest = OmniKryptecEngine.instance().getDisplayManager().getSettings().filterNearest();

    private String name;
    private InputStream file;

    protected TextureBuilder(String name, InputStream textureFile) {
        this.name = name;
        this.file = textureFile;
    }

    public SimpleTexture create() {
        TextureData textureData = TextureUtils.decodeTextureFile(file);
        int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
        return new SimpleTexture(name, textureId, textureData);
    }

    public TextureBuilder clampEdges() {
        this.clampEdges = true;
        return this;
    }

    public TextureBuilder normalMipMap() {
        this.mipmap = true;
        this.anisotropic = false;
        return this;
    }

    public TextureBuilder nearestFiltering() {
        this.mipmap = false;
        this.anisotropic = false;
        this.nearest = true;
        return this;
    }

    public TextureBuilder anisotropic() {
        this.mipmap = true;
        this.anisotropic = true;
        return this;
    }
    
    public TextureBuilder setName(String name) {
        this.name = name;
        return this;
    }

    protected boolean isClampEdges() {
        return clampEdges;
    }

    protected boolean isMipmap() {
        return mipmap;
    }

    protected boolean isAnisotropic() {
        return anisotropic;
    }

    protected boolean isNearest() {
        return nearest;
    }
    
    protected String getName() {
        return name;
    }

}
