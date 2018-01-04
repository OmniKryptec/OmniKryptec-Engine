package omnikryptec.collision.d2;

import java.util.ArrayList;

import org.joml.Intersectionf;

import omnikryptec.main.AbstractScene2D;
import omnikryptec.renderer.d2.RenderChunk2D;
import omnikryptec.util.Instance;
import omnikryptec.util.logger.Logger;

public class CollisionManager {

	private Quadtree tree;
	private ArrayList<Rectangle> toCheck = new ArrayList<>();
	private boolean accept=false;

	
	public void add(Rectangle rect) {
		if(accept) {
			tree.insert(rect);
			if(rect.isDynamic()) {
				toCheck.add(rect);
			}
		}else if(Logger.isDebugMode()){
			System.out.println("Dont accept Rectangles currently!");
		}
	}
	
	public void prepareStep() {
		float x = (Instance.getCurrent2DCamera().getTransform().getChunkX2D()-AbstractScene2D.getCox())*RenderChunk2D.getWidth();
		float y = (Instance.getCurrent2DCamera().getTransform().getChunkY2D()-AbstractScene2D.getCoy())*RenderChunk2D.getHeight();
		tree = new Quadtree(0, new Rectangle(x, y, AbstractScene2D.getCox()*2*RenderChunk2D.getWidth(), AbstractScene2D.getCoy()*2*RenderChunk2D.getHeight()));
		toCheck.clear();
		accept = true;
	}
	
	
	public void step() {
		accept = false;
		ArrayList<Rectangle> possibleCollisions = new ArrayList<>();
		for(Rectangle r : toCheck) {
			r.setColliding(false);
			possibleCollisions.clear();
			tree.retrieve(possibleCollisions, r);
			for(Rectangle col : possibleCollisions) {
				if(collide(r, col)) {
					r.setColliding(true);
					break;
				}
			}
		}
	}
	
	
	public boolean collide(Rectangle r1, Rectangle r2) {
		return r1!=r2&&Intersectionf.testAarAar(r1.minX, r1.minY, r1.maxX, r1.maxY, r2.minX, r2.minY, r2.maxX, r2.maxY);
	}
	
}
