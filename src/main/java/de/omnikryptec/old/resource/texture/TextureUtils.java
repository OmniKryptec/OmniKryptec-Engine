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

package de.omnikryptec.old.resource.texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import de.omnikryptec.old.graphics.OpenGL;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.util.Util;
import de.omnikryptec.old.util.logger.Logger;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

public class TextureUtils {

    public static int createEmptyCubeMap(int size) {
	int texID = OpenGL.gl11genTextures();
	GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
	for (int i = 0; i < 6; i++) {
	    GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA,
		    GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
	}
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
	GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
	return texID;
    }

    public static int loadCubeMap(InputStream[] textureFiles) {
	int texID = OpenGL.gl11genTextures();
	GL13.glActiveTexture(GL13.GL_TEXTURE0);
	GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
	GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
	for (int i = 0; i < textureFiles.length; i++) {
	    TextureData data = decodeTextureFile(textureFiles[i]);
	    GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(),
		    data.getHeight(), 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
	}
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
	return texID;
    }

    protected static TextureData decodeTextureFile(InputStream in) {
	int width = 0;
	int height = 0;
	ByteBuffer buffer = null;
	try {
	    PNGDecoder decoder = new PNGDecoder(in);
	    width = decoder.getWidth();
	    height = decoder.getHeight();
	    buffer = ByteBuffer.allocateDirect(4 * width * height);
	    decoder.decode(buffer, width * 4, Format.BGRA);
	    buffer.flip();
	    in.close();
	} catch (Exception ex) {
	    Logger.logErr("Texture not found: " + ex, ex);
	}
	return new TextureData(buffer, width, height);
    }

    protected static int loadTextureToOpenGL(TextureData data, TextureBuilder builder, Properties info) {
	int texID = OpenGL.gl11genTextures();
	GL13.glActiveTexture(GL13.GL_TEXTURE0);
	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
	GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL12.GL_BGRA,
		GL11.GL_UNSIGNED_BYTE, data.getBuffer());
	if (info == null) {
	    if (builder.isMipmap()) {
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		if (builder.isAnisotropic() && GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
		    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
			    OmniKryptecEngine.instance().getDisplayManager().getSettings().getAnisotropicLevel());
		}
	    } else if (builder.isNearest()) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	    } else {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    }
	    if (builder.isClampEdges()) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    } else {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	    }
	} else {
	    String mag = Util.getString(info, "TEX_MAG", "GL_LINEAR");
	    String min = Util.getString(info, "TEX_MIN", builder.isMipmap() ? "GL_LINEAR_MIPMAP_LINEAR" : "GL_LINEAR");
	    if (mag.contains("MIPMAP") || min.contains("MIPMAP")) {
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
	    }
	    int magint = OpenGL.varToInt(mag, GL11.class);
	    int minint = OpenGL.varToInt(min, GL11.class);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magint);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minint);
	    if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic
		    && Util.getString(info, "ANISF", builder.isAnisotropic() + "").equals("true")) {
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
			OmniKryptecEngine.instance().getDisplayManager().getSettings().getAnisotropicLevel());
	    }
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
		    OpenGL.varToInt(Util.getString(info, "WRAP_S", "GL_REPEAT"), GL11.class, GL12.class));
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
		    OpenGL.varToInt(Util.getString(info, "WRAP_T", "GL_REPEAT"), GL11.class, GL12.class));
	}

	GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	return texID;
    }

}
