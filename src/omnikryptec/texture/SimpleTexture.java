package omnikryptec.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import omnikryptec.logger.Logger;
import omnikryptec.util.AdvancedFile;

public class SimpleTexture extends Texture {

	private final TextureData data;
	private final int type;
	private final int id;

	private static final List<SimpleTexture> alltex = new ArrayList<>();

	protected SimpleTexture(int textureId, TextureData data) {
		this(textureId, GL11.GL_TEXTURE_2D, data);
	}

	protected SimpleTexture(int textureId, int type, TextureData data) {
		alltex.add(this);
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

	public static TextureBuilder newTextureb(File file) {
		try {
			return newTextureb(new FileInputStream(file));
		} catch (Exception ex) {
			Logger.logErr("Error while creating FileInputStream: " + ex, ex);
			return null;
		}
	}

	public static TextureBuilder newTextureb(String path) {
		try {
			return newTextureb(TextureBuilder.class.getResourceAsStream(path));
		} catch (Exception ex) {
			Logger.logErr("Error while creating Stream from path: " + ex, ex);
			return null;
		}
	}

	public static TextureBuilder newTextureb(InputStream textureFile) {
		return new TextureBuilder(textureFile);
	}

	public static SimpleTexture newTexture(File file) {
		try {
			return newTexture(new FileInputStream(file));
		} catch (Exception ex) {
			Logger.logErr("Error while creating FileInputStream: " + ex, ex);
			return null;
		}
	}

	public static SimpleTexture newTexture(String path) {
		try {
			return newTexture(TextureBuilder.class.getResourceAsStream(path));
		} catch (Exception ex) {
			Logger.logErr("Error while creating Stream from path: " + ex, ex);
			return null;
		}
	}

	public static SimpleTexture newTexture(InputStream stream) {
		return new TextureBuilder(stream).create();
	}

	public static TextureBuilder newTextureb(AdvancedFile file) {
		return new TextureBuilder(file.createInputStream());
	}

	public static SimpleTexture newTexture(AdvancedFile file) {
		return new TextureBuilder(file.createInputStream()).create();
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

	public int getID() {
		return id;
	}

	public int getType() {
		return type;
	}

	public static void cleanup() {
		for (int i = 0; i < alltex.size(); i++) {
			alltex.get(i).delete();
		}
	}

}
