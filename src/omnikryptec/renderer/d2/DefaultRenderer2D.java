package omnikryptec.renderer.d2;

import java.util.HashMap;

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
	
	@Override
	public long render(RenderChunk2D global, long camChunkX, long camChunkY,
			long chunkOffsetX, long chunkOffsetY, HashMap<String, RenderChunk2D> scene) {
		return 0;
	}

}
