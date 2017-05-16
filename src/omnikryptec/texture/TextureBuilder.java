package omnikryptec.texture;

import java.io.InputStream;

import omnikryptec.display.DisplayManager;

public class TextureBuilder {
	
	private boolean clampEdges = DisplayManager.instance().getSettings().clampEdges();
	private boolean mipmap = DisplayManager.instance().getSettings().mipmap();
	private boolean anisotropic = DisplayManager.instance().getSettings().getAnisotropicLvl()>0;
	private boolean nearest = DisplayManager.instance().getSettings().filterNearest();
	
	private InputStream file;
	
	protected TextureBuilder(InputStream textureFile){
		this.file = textureFile;
	}
	
	public Texture create(){
		TextureData textureData = TextureUtils.decodeTextureFile(file);
		int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
		return new Texture(textureId, textureData);
	}
	
	public TextureBuilder clampEdges(){
		this.clampEdges = true;
		return this;
	}
	
	public TextureBuilder normalMipMap(){
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}
	
	public TextureBuilder nearestFiltering(){
		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
		return this;
	}
	
	public TextureBuilder anisotropic(){
		this.mipmap = true;
		this.anisotropic = true;
		return this;
	}
	
	protected boolean isClampEdges() {
		return clampEdges;
	}

	protected boolean isMipmap() {
		return mipmap;
	}

	protected boolean isAnisotropic() {
		return anisotropic;
	}

	protected boolean isNearest() {
		return nearest;
	}

}
