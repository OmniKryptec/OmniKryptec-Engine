package omnikryptec.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import omnikryptec.logger.Logger;

public class Texture implements ITexture{

	public final int textureId;
	private final TextureData data;
	private final int type;
		
	protected Texture(int textureId, TextureData data) {
		this(textureId, GL11.GL_TEXTURE_2D, data);
	}

	protected Texture(int textureId, int type, TextureData data) {
		this.textureId = textureId;
		this.data = data;
		this.type = type;
	}
	
	
	@Override
	public void bindToUnit(int unit, int...info) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
		GL11.glBindTexture(type, textureId);
	}

	public void delete() {
		GL11.glDeleteTextures(textureId);
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

	
	public static Texture newCubeMap(InputStream[] textureFiles) {
		int cubeMapId = TextureUtils.loadCubeMap(textureFiles);
		return new Texture(cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, null);
	}
	
	public static Texture newEmptyCubeMap(int size) {
		int cubeMapId = TextureUtils.createEmptyCubeMap(size);
		return new Texture(cubeMapId, GL13.GL_TEXTURE_CUBE_MAP, null);
	}
	
	/**
	 * for cubemaps null
	 * @return
	 */
	public TextureData getData(){
		return data;
	}


}
