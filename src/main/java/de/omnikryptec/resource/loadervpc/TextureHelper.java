package de.omnikryptec.resource.loadervpc;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
//TODO improve this and the purpose of this class
public class TextureHelper {
    
    private Map<String, Texture> textures = new HashMap<>();
    private ResourceProvider resProvider;
    private RenderAPI api = LibAPIManager.instance().getGLFW().getRenderAPI();
    
    public TextureHelper(ResourceProvider prov) {
        this.resProvider = prov;
    }
    
    public void add(String name, TextureConfig config) {
        textures.put(name, api.createTexture2D(resProvider.get(TextureData.class, name), config));
    }
    
    public Texture get(String name) {
        return textures.get(name);
    }
    
}
