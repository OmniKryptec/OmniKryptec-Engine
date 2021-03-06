/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.resource.helper;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.codemakers.io.file.AdvancedFile;
import de.omnikryptec.libapi.exposed.Deletable;
import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.libapi.exposed.render.RenderAPI;
import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.resource.loadervpc.ResourceProvider;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Util;

public class TextureHelper {
    
    public static final TextureData MISSING_TEXTURE_DATA;
    private static final TextureConfig MISSING_TEXTURE_CONFIG;
    static {
        TextureData data;
        try {
            data = TextureData.decode(new AdvancedFile("intern:/de/omnikryptec/resources/loader/missing_texture.png")
                    .createInputStream());
        } catch (final Exception ex) {
            data = null;
            throw new RuntimeException(ex);
        }
        MISSING_TEXTURE_DATA = data;
        MISSING_TEXTURE_CONFIG = new TextureConfig();
    }
    
    private static final Logger LOGGER = Logger.getLogger(TextureHelper.class);
    
    private final Table<String, TextureConfig, Texture> textures;
    private final Map<String, Texture> texturesIgnoreConfig;
    private final Map<String, TextureConfig> configs;
    
    private TextureConfig defaultConfig;
    private final Texture missingTexture;
    
    private final ResourceProvider resProvider;
    private final RenderAPI api;
    
    public TextureHelper(final ResourceProvider prov) {
        this.resProvider = prov;
        this.api = LibAPIManager.instance().getGLFW().getRenderAPI();
        this.defaultConfig = new TextureConfig();
        this.textures = HashBasedTable.create();
        this.texturesIgnoreConfig = new HashMap<>();
        this.configs = new HashMap<>();
        this.missingTexture = this.api.createTexture2D(MISSING_TEXTURE_DATA, MISSING_TEXTURE_CONFIG);
    }
    
    public boolean isMissingTexture(Texture t) {
        return t == this.missingTexture;
    }
    
    public Texture get(final String name, final TextureConfig config) {
        Texture t = this.textures.get(name, config);
        if (t == null) {
            final TextureData data = this.resProvider.get(TextureData.class, name);
            if (data == null) {
                LOGGER.warn(
                        String.format("Could not find the texture \"%s\", using a placeholder texture instead", name));
                return this.missingTexture;
            }
            t = this.api.createTexture2D(data, Util.newIfNull(() -> new TextureConfig(), config));
            this.textures.put(name, config, t);
            this.texturesIgnoreConfig.put(name, t);
        }
        return t;
    }
    
    public Texture get(final String name, final String configName) {
        final Texture t = this.textures.get(name, configs.get(configName));
        if (t != null) {
            return t;
        }
        TextureConfig config = this.configs.get(configName);
        if (config == null) {
            config = this.defaultConfig;
        }
        return get(name, config);
    }
    
    /**
     * Ignores TextureConfig.
     * 
     * @param name
     * @return
     */
    public Texture get(final String name) {
        Texture t = texturesIgnoreConfig.get(name);
        if (t == null) {
            t = get(name, this.defaultConfig);
        }
        return t;
    }
    
    public void setTextureConfig(final String name, final TextureConfig config) {
        this.configs.put(name, config);
    }
    
    //Hmmm... would also need special clear method?
    //    public void setTexture(final String name, final Texture tex) {
    //        this.textures.put(name, tex);
    //    }
    
    public void setDefaultTextureConfig(final TextureConfig config) {
        this.defaultConfig = config;
    }
    
    public void clearConfigs() {
        this.configs.clear();
    }
    
    public void clearTextures() {
        this.textures.clear();
        this.texturesIgnoreConfig.clear();
    }
    
    /**
     * Does NOT add the new texture to this collection.
     * 
     * @param data
     * @param config
     * @return
     */
    public Texture createTexture(TextureData data, TextureConfig config) {
        return this.api.createTexture2D(data, config);
    }
    
    public void clearAndDeleteTextures() {
        for (final Texture t : this.textures.values()) {
            if (t instanceof Deletable) {
                ((Deletable) t).deleteAndUnregister();
            }
        }
        clearTextures();
    }
}
