package omnikryptec.storing;

import omnikryptec.texture.ITexture;

public class TexturedModel {
	
	private Model model;
	private ITexture texture;
	private Material material;
	
	
	public TexturedModel(Model m, ITexture t){
		this(m, t, new Material());
	}
	
	public TexturedModel (Model m, ITexture t, Material mat){
		this.model = m;
		this.texture = t;
		this.material = mat;
	}
	
	public Model getModel(){
		return model;
	}
	
	public ITexture getTexture(){
		return texture;
	}
	
	public Material getMaterial(){
		return material;
	}
	
}
