package de.omnikryptec.resource.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.omnikryptec.resource.objConverter.ModelData;

public class EdgeCollapse {
	
	public static ModelData collapseEdges(ModelData data, float am) {
		List<Float> processedVertices = new ArrayList<>();
		List<Float> vertices = new ArrayList<>();
		for(float f : data.getVertices()) {
			vertices.add(f);
		}
		while(processedVertices.size()/(float)(vertices.length)<am) {
			
		}
	}
	
}
