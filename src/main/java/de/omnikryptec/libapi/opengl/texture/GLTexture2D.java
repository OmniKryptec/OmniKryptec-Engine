/*
 *    Copyright 2017 - 2019 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
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

package de.omnikryptec.libapi.opengl.texture;

import org.lwjgl.opengl.GL11;

import de.omnikryptec.libapi.opengl.OpenGLUtil;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureData;

public class GLTexture2D extends GLTexture {
    
    private final TextureData data;
    
    public GLTexture2D(final TextureData texture, final TextureConfig config) {
        super(GL11.GL_TEXTURE_2D);
        this.data = texture;
        bindTexture(0);
        OpenGLUtil.loadTexture(texture);
        OpenGLUtil.configureTexture(config);
    }
    
    @Override
    public float getWidth() {
        return this.data.getWidth();
    }
    
    @Override
    public float getHeight() {
        return this.data.getHeight();
    }
    
    @Override
    public boolean requiresInvertedVifDrawn2D() {
        return true;
    }
    
}
