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

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureConfig.MagMinFilter;
import de.omnikryptec.resource.TextureConfig.WrappingMode;
import de.omnikryptec.resource.TextureData;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Logger.LogType;

public class GLTexture2D extends GLTexture {

    private static void loadTexture(final TextureData texture, final TextureConfig config) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL12.GL_BGRA,
                GL11.GL_UNSIGNED_BYTE, texture.getBuffer());
        if (config.mipmap() || config.anisotropicValue() > 0) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            if (config.anisotropicValue() > 0) {
                if (!GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                    Logger.log(GLTexture2D.class, LogType.Warning,
                            "GL_EXT_texture_filter_anisotropic is not supported");
                } else {
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                            config.anisotropicValue());
                }
            }
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, decodeMagMin(config.magFilter()));
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, decodeMagMin(config.minFilter()));
        }
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, decodeWrap(config.wrappingMode()));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, decodeWrap(config.wrappingMode()));
    }

    private static int decodeMagMin(final MagMinFilter filter) {
        switch (filter) {
        case Linear:
            return GL11.GL_LINEAR;
        case Nearest:
            return GL11.GL_NEAREST;
        default:
            throw new IllegalArgumentException();
        }
    }

    private static int decodeWrap(final WrappingMode mode) {
        switch (mode) {
        case ClampToEdge:
            return GL12.GL_CLAMP_TO_EDGE;
        case Repeat:
            return GL11.GL_REPEAT;
        default:
            throw new IllegalArgumentException();
        }
    }

    //TODO maybe move the static methods in another class for broader use

    private TextureData data;

    public GLTexture2D(final TextureData texture, final TextureConfig config) {
        super(GL11.GL_TEXTURE_2D);
        bindTexture(0);
        loadTexture(texture, config);
    }

    @Override
    public int getWidth() {
        return this.data.getWidth();
    }

    @Override
    public int getHeight() {
        return this.data.getHeight();
    }

}
