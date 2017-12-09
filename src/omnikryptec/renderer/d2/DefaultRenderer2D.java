package omnikryptec.renderer.d2;

import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.display.Display;
import omnikryptec.gameobject.Sprite;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.graphics.GraphicsUtil.BlendMode;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.main.AbstractScene2D;
import omnikryptec.main.Scene2D;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.renderer.d3.RenderConfiguration;

public class DefaultRenderer2D implements Renderer2D{
	
	private SpriteBatch batch;
	
	public DefaultRenderer2D(Scene2D scene) {
		this(new SpriteBatch(scene.getCamera()));
	}
	
	public DefaultRenderer2D(SpriteBatch batch) {
		this.batch = batch;
		lights = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}

	private ArrayList<Sprite> sprites;
	private String stringTmp;
	private FrameBufferObject lights;
	@Override
	public long render(AbstractScene2D sc, RenderChunk2D global, long camChunkX, long camChunkY,
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
		batch.flush();
		lights.bindFrameBuffer();
		GraphicsUtil.clear(sc.getAmbientColor());
		GraphicsUtil.blendMode(BlendMode.ADDITIVE);

		lights.unbindFrameBuffer();
		batch.draw(lights, 0, 0);
		batch.end();
		return batch.getVertexCount();
	}

}
