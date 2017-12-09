package omnikryptec.renderer.d2;

import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.display.Display;
import omnikryptec.event.event.Event;
import omnikryptec.event.event.EventType;
import omnikryptec.event.event.IEventHandler;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Light2D;
import omnikryptec.gameobject.Sprite;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.main.AbstractScene2D;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene2D;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.FrameBufferObject.DepthbufferType;
import omnikryptec.resource.texture.Texture;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.Instance;

public class DefaultRenderer2D implements Renderer2D{
	
	private SpriteBatch batch, finalBatch;
	
	public DefaultRenderer2D(Scene2D scene) {
		this(new SpriteBatch(scene.getCamera()));
	}
	
	public DefaultRenderer2D(SpriteBatch batch) {
		this.batch = batch;
		this.finalBatch = new SpriteBatch(new Camera().setDefaultScreenSpaceProjection());
		lights = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
		fbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}

	private ArrayList<Sprite> sprites;
	private ArrayList<Light2D> lightlist;
	private String stringTmp;
	private FrameBufferObject lights, fbo;
	private boolean light;
	private long last=-1;
	@Override
	public long render(AbstractScene2D sc, RenderChunk2D global, long camChunkX, long camChunkY,
			long chunkOffsetX, long chunkOffsetY, HashMap<String, RenderChunk2D> scene) {
		sprites = new ArrayList<>();
		if(GraphicsUtil.needsUpdate(last, 20)){
			light = OmniKryptecEngine.instance().getGameSettings().getBoolean(GameSettings.LIGHT_2D);
			last = Instance.getFramecount();
		}
		if(light) {
			lightlist = new ArrayList<>();
		}
		for(long x=-chunkOffsetX; x<=chunkOffsetX; x++) {
			for(long y=-chunkOffsetY; y<=chunkOffsetY; y++) {
				stringTmp = Scene2D.xyToString(camChunkX+x, camChunkY+y);
				if(scene.get(stringTmp)!=null) {
					sprites.addAll(scene.get(stringTmp).__getSprites());
					if(light) {
						lightlist.addAll(scene.get(stringTmp).__getLights());
					}
				}
			}
		}
		sprites.sort(LAYER_COMPARATOR);
		fbo.bindFrameBuffer();
		GraphicsUtil.clear(0, 0, 0, 0);
		batch.begin();
		GraphicsUtil.blendMode(BlendMode.ALPHA);
		for(Sprite s : sprites) {
			s.paint(batch);
		}
		batch.end();
		if(light) {
			lights.bindFrameBuffer();
			GraphicsUtil.clear(sc.getAmbientColor());
			GraphicsUtil.blendMode(BlendMode.ADDITIVE);
			batch.begin();
			for(Light2D s : lightlist) {
				s.paint(batch);
			}
			batch.end();
			lights.unbindFrameBuffer();
			GraphicsUtil.blendMode(BlendMode.MULTIPLICATIVE);
			finalBatch.begin();
			finalBatch.draw(lights, -1, -1, 2, 2);
			finalBatch.end();
		}
		fbo.unbindFrameBuffer();
		GraphicsUtil.blendMode(BlendMode.ALPHA);
		finalBatch.begin();
		finalBatch.draw(fbo, -1, -1, 2, 2);
		finalBatch.end();
		return 0;
	}


}
