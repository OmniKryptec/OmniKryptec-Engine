package omnikryptec.renderer.d2;

import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.gameobject.Sprite;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.main.Scene2D;
import omnikryptec.renderer.d3.RenderConfiguration;

public class DefaultRenderer2D implements Renderer2D{
	
	private SpriteBatch batch;
	
	public DefaultRenderer2D(Scene2D scene) {
		this(new SpriteBatch(scene.getCamera()));
	}
	
	public DefaultRenderer2D(SpriteBatch batch) {
		this.batch = batch;
	}

	private ArrayList<Sprite> sprites;
	private String stringTmp;
	@Override
	public long render(RenderChunk2D global, long camChunkX, long camChunkY,
			long chunkOffsetX, long chunkOffsetY, HashMap<String, RenderChunk2D> scene) {
		sprites = new ArrayList<>();
		for(long x=-chunkOffsetX; x<=chunkOffsetX; x++) {
			for(long y=-chunkOffsetY; y<=camChunkY; y++) {
				stringTmp = Scene2D.xyToString(camChunkX+x, camChunkY+y);
				if(scene.get(stringTmp)!=null) {
					sprites.addAll(scene.get(stringTmp).__getSprites());
				}
			}
		}
		sprites.sort(LAYER_COMPARATOR);
		batch.begin();
		for(Sprite s : sprites) {
			batch.draw(s);
		}
		batch.end();
		return batch.getVertexCount();
	}

}
