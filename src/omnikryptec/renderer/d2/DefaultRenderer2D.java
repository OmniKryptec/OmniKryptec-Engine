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
	@Override
	public long render(RenderChunk2D global, long camChunkX, long camChunkY,
			long chunkOffsetX, long chunkOffsetY, HashMap<String, RenderChunk2D> scene) {
		sprites = new ArrayList<>();
		for(long x=-chunkOffsetX; x<=chunkOffsetX; x++) {
			for(long y=-chunkOffsetY; y<=camChunkY; y++) {
				sprites.addAll(scene.get(Scene2D.xyToString(camChunkX+x, camChunkY+y)).__getSprites());
			}
		}
		GraphicsUtil.enableDepthTesting(false);
		sprites.sort(LAYER_COMPARATOR);
		batch.begin();
		for(Sprite s : sprites) {
			batch.draw(s);
		}
		batch.end();
		return batch.getVertexCount();
	}

}
