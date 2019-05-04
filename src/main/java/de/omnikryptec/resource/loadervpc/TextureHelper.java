package de.omnikryptec.resource.loadervpc;

import java.util.HashMap;
import java.util.Map;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.Logger;

public class TextureHelper {
    
    public static final TextureData MISSING_TEXTURE_DATA;
    private static final TextureConfig MISSING_TEXTURE_CONFIG;
    static {
        TextureData data;
        try {
            data = TextureData.decode(new AdvancedFile("intern:/de/omnikryptec/resources/loader/missing_texture.png")
                    .createInputStream());
        } catch (Exception ex) {
            data = null;
            throw new RuntimeException(ex);
        }
        MISSING_TEXTURE_DATA = data;
        MISSING_TEXTURE_CONFIG = new TextureConfig();
    }
    
    private static final Logger LOGGER = Logger.getLogger(TextureHelper.class);
    
    private Map<String, Texture> textures;
    private Map<String, TextureConfig> configs;
    
    private TextureConfig defaultConfig;
    private final Texture missingTexture;
    
    private ResourceProvider resProvider;
    private RenderAPI api;
    
    public TextureHelper(ResourceProvider prov) {
        this.resProvider = prov;
        this.api = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.defaultConfig = new TextureConfig();
        textures = new HashMap<>();
        configs = new HashMap<>();
        this.missingTexture = api.createTexture2D(MISSING_TEXTURE_DATA, MISSING_TEXTURE_CONFIG);
    }
    
    public Texture get(String name, TextureConfig config) {
        Texture t = textures.get(name);
        if (t == null) {
            TextureData data = resProvider.get(TextureData.class, name);
            if (data == null) {
                LOGGER.warn("Could not find the texture \"" + name + "\", using a placeholder texture now");
                return missingTexture;
            }
            t = api.createTexture2D(data, config == null ? new TextureConfig() : config);
            textures.put(name, t);
        }
        return t;
    }
    
    public Texture get(String name, String configName) {
        Texture t = textures.get(name);
        if (t != null) {
            return t;
        }
        TextureConfig config = configs.get(configName);
        if (config == null) {
            config = defaultConfig;
        }
        return get(name, config);
    }
    
    public Texture get(String name) {
        return get(name, defaultConfig);
    }
    
    public void setTextureConfig(String name, TextureConfig config) {
        configs.put(name, config);
    }
    
    public void setTexture(String name, Texture tex) {
        textures.put(name, tex);
    }
    
    public void setDefaultTextureConfig(TextureConfig config) {
        this.defaultConfig = config;
    }
    
    public void clearConfigs() {
        configs.clear();
    }
    
    public void clearTextures() {
        textures.clear();
    }
}
