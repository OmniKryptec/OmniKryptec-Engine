package omnikryptec.physics.d2;

import java.util.ArrayList;

import org.joml.Intersectionf;

import omnikryptec.main.AbstractScene2D;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.util.Instance;

public class CollisionManager {

	private Quadtree tree;
	private ArrayList<Rectangle> toCheck = new ArrayList<>();
	
	public void add(Rectangle rect) {
		tree.insert(rect);
		if(rect.isDynamic()) {
			toCheck.add(rect);
		}
	}
	
	public void prepareStep() {
		float x = (Instance.getCurrent2DCamera().getTransform().getChunkX2D()-AbstractScene2D.getCox())*RenderChunk2D.getWidth();
		float y = (Instance.getCurrent2DCamera().getTransform().getChunkY2D()-AbstractScene2D.getCoy())*RenderChunk2D.getHeight();
		tree = new Quadtree(0, new Rectangle(x, y, AbstractScene2D.getCox()*2*RenderChunk2D.getWidth(), AbstractScene2D.getCoy()*2*RenderChunk2D.getHeight()));
		toCheck.clear();
	}
	
	
	public void step() {
		for(Rectangle r : toCheck) {
			ArrayList<Rectangle> possibleCollisions = new ArrayList<>();
			tree.retrieve(possibleCollisions, r);
			for(Rectangle col : possibleCollisions) {
				if(collide(r, col)) {
					System.out.println("Collision!");
					//r.reset();
				}
			}
		}
	}
	
	
	public boolean collide(Rectangle r1, Rectangle r2) {
		return r1!=r2&&Intersectionf.testAarAar(r1.minX, r1.minY, r1.maxX, r1.maxY, r2.minX, r2.minY, r2.maxX, r2.maxY);
	}
	
}
