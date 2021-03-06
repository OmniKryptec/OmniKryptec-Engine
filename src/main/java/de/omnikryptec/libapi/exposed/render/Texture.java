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

package de.omnikryptec.libapi.exposed.render;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;

public interface Texture {
    
    public static final Texture WHITE_1x1 = LibAPIManager.instance().getGLFW().getRenderAPI()
            .createTexture2D(TextureData.WHITE_TEXTURE_DATA, new TextureConfig());
    
    /**
     * Binds this {@link Texture} to a certain textureunit
     *
     * @param unit the textureunit in the range [0;31]
     */
    void bindTexture(int unit);
    
    /**
     * the width, in texels, of this {@link Texture}
     *
     * @return width
     */
    int getWidth();
    
    /**
     * The height, in texels, of this {@link Texture}
     *
     * @return
     */
    int getHeight();
    
    default boolean requiresInvertedVifDrawn2D() {
        return false;
    }
    
    default Texture getBaseTexture() {
        return this;
    }
}
