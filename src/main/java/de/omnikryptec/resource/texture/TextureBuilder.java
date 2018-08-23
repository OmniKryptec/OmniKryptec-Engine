package de.omnikryptec.resource.texture;

import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.settings.GameSettings;

import java.io.InputStream;
import java.util.Properties;

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
        return create(null);
    }

    public SimpleTexture create(Properties infos) {
        TextureData textureData = TextureUtils.decodeTextureFile(file);
        int textureId = TextureUtils.loadTextureToOpenGL(textureData, this, infos);
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
