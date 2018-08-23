package de.omnikryptec.renderer.d3;

import de.omnikryptec.graphics.OpenGL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.List;

public class Query {
	
	private static final List<Query> qs = new ArrayList<>();
	
	public static final void cleanup(){
		for(Query q : qs){
			q.delete();
		}
		qs.clear();
	}
	
	public final int id;
	public final int type;
	
	public Query(int type){
		this.type = type;
		this.id = OpenGL.gl15genQueries();
		qs.add(this);
	}
	
	public Query start(){
		OpenGL.gl15beginQuery(type, id);
		return this;
	}
	
	public Query stop(){
		OpenGL.gl15endQuery(id);
		return this;
	}
	
	public boolean isResultReady(){
		return OpenGL.gl15getQueryObjecti(id, GL15.GL_QUERY_RESULT_AVAILABLE)==GL11.GL_TRUE;
	}
	
	private void delete(){
		OpenGL.gl15deleteQueries(id);
	}
}
