package de.omnikryptec.resource.objConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.google.common.collect.Lists;

public class EdgeCollapse {

	private static class Pair implements Comparable<Pair>{
		private final Vector3f v1 = new Vector3f();
		private final Vector3f v2 = new Vector3f();
		private int i1, i2;
		
		public float dist() {
			return v1.distance(v2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof Pair) {
				if (v1.equals(((Pair) obj).v1) && v2.equals(((Pair) obj).v2)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int compareTo(Pair o) {
			return (int) Math.signum(dist()-o.dist());
		}
		
		@Override
		public String toString() {
			return "Pair i1:"+i1+" i2: "+i2+" d:"+dist();
		}
	}

	public static ModelData collapseEdges(ModelData in, float ratio) {
		List<Vector3f> positions = new ArrayList<>();
		fillList(in.getVertices(), positions);
		Set<Pair> pairs = new HashSet<>();
		for(int i=0; i<in.getIndices().length-1; i++) {
			Pair pair = new Pair();
			if(positions.get(in.getIndices()[i]).equals(positions.get(in.getIndices()[i+1]))) {
				continue;
			}
			pair.v1.set(positions.get(in.getIndices()[i]));
			pair.v2.set(positions.get(in.getIndices()[i+1]));
			pair.i1 = in.getIndices()[i];
			pair.i2 = in.getIndices()[i+1];
			pairs.add(pair);
		}
		List<Pair> pairslist = Lists.newArrayList(pairs);
		List<Integer> indicesList = new ArrayList<>();
		Set<Vector3f> kill = new HashSet<>();
		fillIndicesList(in.getIndices(), indicesList);
		Collections.sort(pairslist);
		//System.out.println(pairslist);
		for(int i=0; i<ratio*pairslist.size(); i++) {
			Pair pair = pairslist.get(i);
			Vector3f vvv = new Vector3f(pair.v2);
			pair.v1.add((pair.v2.sub(pair.v1)).mul(0.5f));
			kill.add(vvv);
			indicesList.replaceAll((integ)->integ==pair.i2?pair.i1:integ);
		}
		for(Vector3f k : kill) {
			positions.remove(k);
		}
		ModelData newm = new ModelData(toArray(positions), in.getTextureCoords(), in.getNormals(), in.getTangents(), toArrayI(indicesList), in.getFurthestPoint());
		return newm;
	}
	
	private static void fillIndicesList(int[] is, List<Integer> dest) {
		for(int i : is) {
			dest.add(i);
		}
	}
	
	private static int[] toArrayI(List<Integer> vecs) {
		int[] fs = new int[vecs.size()];
		int i=0;
		for(Integer v : vecs) {
			fs[i++] = v;
		}
		return fs;
	}
	
	private static float[] toArray(List<Vector3f> vecs) {
		float[] fs = new float[vecs.size()*3];
		int i=0;
		for(Vector3f v : vecs) {
			fs[i++] = v.x;
			fs[i++] = v.y;
			fs[i++] = v.z;
		}
		return fs;
	}
	
	private static void fillList(float[] fs, List<Vector3f> dest) {
		for(int i=0; i<fs.length; i+=3) {
			dest.add(new Vector3f(fs[i], fs[i+1], fs[i+2]));
		}
	}
}
