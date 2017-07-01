package omnikryptec.gameobject.terrain;

import omnikryptec.resource.model.Model;
import omnikryptec.resource.objConverter.ModelData;
import omnikryptec.resource.texture.Texture;

public class TerrainCreator {
	
	final float worldx, worldz, size;
	final int vertex_count;
	private final TerrainGenerator generator;
	private Model model;
	private ModelData data;
	boolean ismodelcreated=false;
	boolean isgenerated=false;
	final String texmname;
	final TerrainTexturePack texturePack;
	final Texture blendMap;
	final float y;
	
	
	public TerrainCreator(String texmname, TerrainTexturePack texturePack, Texture blendMap, final float worldx, final float worldz, final TerrainGenerator generator, final float size, final int vertex_count){
		this(texmname, texturePack, blendMap, 0, worldx, worldz, generator, size, vertex_count);
	}
	
	public TerrainCreator(String texmname, TerrainTexturePack texturePack, Texture blendMap, float y, final float worldx, final float worldz, final TerrainGenerator generator, final float size, final int vertex_count){
		this.worldx = worldx;
		this.worldz = worldz;
		this.generator = generator;
		this.size = size;
		this.vertex_count = vertex_count;
		this.texmname = texmname;
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.y = y;
	}
	
	public TerrainCreator generate(){
		data = Terrain.generateTerrain(worldx, worldz, generator, size, vertex_count);
		isgenerated = true;
		return this;
	}
	
	public TerrainCreator createModel(){
		if(!isgenerated){
			return this;
		}
		model = new Model("terrain_wx_"+worldx+"_wz_"+worldz, data);
		ismodelcreated = true;
		return this;
	}
	
	public Terrain createTerrain(){
		return new Terrain(this);
	}
	
	public ModelData getModelData(){
		return data;
	}
	
	public Model getModel(){
		return model;
	}
	
	
	
}
