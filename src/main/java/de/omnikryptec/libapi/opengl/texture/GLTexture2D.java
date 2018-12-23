package de.omnikryptec.libapi.opengl.texture;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import de.omnikryptec.libapi.exposed.LibAPIManager;
import de.omnikryptec.resource.TextureConfig;
import de.omnikryptec.resource.TextureConfig.MagMinFilter;
import de.omnikryptec.resource.TextureConfig.WrappingMode;
import de.omnikryptec.resource.TextureData;

public class GLTexture2D extends GLTexture {
    
    private static void loadTexture(TextureData texture, TextureConfig config) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL12.GL_BGRA,
                GL11.GL_UNSIGNED_BYTE, texture.getBuffer());
        if (config.mipmap() || config.anisotropicValue() > 0) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            if (config.anisotropicValue() > 0) {
                if (!GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                    if (LibAPIManager.active().debug()) {
                        System.err.println("GL_EXT_texture_filter_anisotropic is not supported");
                    }
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
    
    private static int decodeMagMin(MagMinFilter filter) {
        switch (filter) {
        case Linear:
            return GL11.GL_LINEAR;
        case Nearest:
            return GL11.GL_NEAREST;
        default:
            throw new IllegalArgumentException();
        }
    }
    
    private static int decodeWrap(WrappingMode mode) {
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
    
    public GLTexture2D(TextureData texture, TextureConfig config) {
        super(GL11.GL_TEXTURE_2D);
        bindTexture(0);
        loadTexture(texture, config);
    }
    
    @Override
    public int getWidth() {
        return data.getWidth();
    }
    
    @Override
    public int getHeight() {
        return data.getHeight();
    }
    
}
