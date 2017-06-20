package omnikryptec.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import omnikryptec.logger.Logger;

public class SimpleTexture extends Texture {

	private final TextureData data;
	private final int type;
	private final int id;
	
	
	protected SimpleTexture(int textureId, TextureData data) {
		this(textureId, GL11.GL_TEXTURE_2D, data);
	}

	protected SimpleTexture(int textureId, int type, TextureData data) {
		this.data = data;
		this.type = type;
		this.id = textureId;
	}

	@Override
	public void bindToUnita(int unit, int... info) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(type, id);
	}

	public void delete() {
		GL11.glDeleteTextures(id);
	}

	public static TextureBuilder newTexture(File file) {
		try {
			return newTexture(new FileInputStream(file));
		} catch (Exception ex) {
			Logger.logErr("Error while creating FileInputStream: " + ex, ex);
			return null;
		}
	}

	public static TextureBuilder newTexture(String path) {
		try {
			return newTexture(TextureBuilder.class.getResourceAsStream(path));
		} catch (Exception ex) {
			Logger.logErr("Error while creating Stream from path: " + ex, ex);
			return null;
		}
	}

	public static TextureBuilder newTexture(InputStream textureFile) {
		return new TextureBuilder(textureFile);
	}

	public static SimpleTexture newCubeMap(InputStream[] textureFiles) {
		int cubeMapId = TextureUtils.loadCubeMap(textureFiles);
		return new SimpleTexture(cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, null);
	}

	public static SimpleTexture newEmptyCubeMap(int size) {
		int cubeMapId = TextureUtils.createEmptyCubeMap(size);
		return new SimpleTexture(cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, null);
	}

	/**
	 * for cubemaps null
	 * 
	 * @return
	 */
	public TextureData getData() {
		return data;
	}

}
