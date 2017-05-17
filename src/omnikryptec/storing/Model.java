package omnikryptec.storing;

import omnikryptec.objConverter.ModelData;

public class Model {

	private VertexArrayObject vao;	
//	public Model(ModelData data){
//		vao = VertexArrayObject.create();
//		vao.storeData(data.getIndices(), data.getIndices().length, data.getVertices(), data.getTextureCoords(), data.getNormals());
//		nm = false;
//	}
	
	public Model(ModelData data){
		vao = VertexArrayObject.create();
		vao.storeData(data.getIndices(), data.getIndices().length, data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getTangents());
	}
	
	public Model(VertexArrayObject vao){
		this.vao = vao;
	}
	
	public VertexArrayObject getVao(){
		return vao;
	}
	
	
}
