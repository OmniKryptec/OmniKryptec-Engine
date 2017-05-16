package omnikryptec.storing;

import omnikryptec.nmObjConverter.ModelDataNM;
import omnikryptec.objLoader.ModelData;

public class Model {

	private VertexArrayObject vao;
	private boolean nm;
	
	public Model(ModelData data){
		vao = VertexArrayObject.create();
		vao.storeData(data.getIndices(), data.getIndices().length, data.getVertices(), data.getTextureCoords(), data.getNormals());
		nm = false;
	}
	
	public Model(ModelDataNM data){
		vao = VertexArrayObject.create();
		vao.storeData(data.getIndices(), data.getIndices().length, data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getTangents());
		nm = true;
	}
	
	public VertexArrayObject getVao(){
		return vao;
	}
	
	public boolean isNormalMapped(){
		return nm;
	}
	
}
