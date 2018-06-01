package omnikryptec.renderer.d2;

import java.util.ArrayList;
import java.util.HashMap;

import omnikryptec.display.Display;
import omnikryptec.event.eventV2.EventSubscription;
import omnikryptec.event.eventV2.EventBus;
import omnikryptec.event.eventV2.engineevents.ResizeEvent;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Light2D;
import omnikryptec.gameobject.Sprite;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.main.AbstractScene2D;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene2D;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Color;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.EnumCollection.DepthbufferType;
import omnikryptec.util.FrustrumFilter;
import omnikryptec.util.Instance;

public class DefaultRenderer2D implements Renderer2D {

	private SpriteBatch batch, finalBatch;

	public DefaultRenderer2D() {
		this(new SpriteBatch());
		Instance.engineBus().registerEventHandler(this);
	}

	public DefaultRenderer2D(SpriteBatch batch) {
		this.batch = batch;
		this.finalBatch = new SpriteBatch(new Camera().setDefaultScreenSpaceProjection(), 1);
//		lights = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
//		fbo = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}

	private ArrayList<Sprite> sprites;
	private ArrayList<Light2D> lightlist;
	private String stringTmp;
//	private FrameBufferObject fbo;
	private boolean light;
	private long last = -1;
	private Color clearcolor2d = new Color(0, 0, 0, 0);
	private FrustrumFilter filter = new FrustrumFilter();

	@Override
	public long render(AbstractScene2D sc, RenderChunk2D global, long camChunkX, long camChunkY, int chunkOffsetX,
			int chunkOffsetY, HashMap<String, RenderChunk2D> scene) {
		batch.setCamera(sc.getCamera());
		filter.setCamera(sc.getCamera());
		sprites = new ArrayList<>();
		if (GraphicsUtil.needsUpdate(last, GameSettings.CHECKCHANGEFRAMES)) {
			light = OmniKryptecEngine.instance().getGameSettings().getBoolean(GameSettings.LIGHT_2D);
			last = Instance.getFramecount();
		}
		sprites.addAll(global.__getSprites());
		if (light) {
			lightlist = new ArrayList<>();
			lightlist.addAll(global.__getLights());
		} else {
			lightlist = null;
		}
		for (long x = -chunkOffsetX; x <= chunkOffsetX; x++) {
			for (long y = -chunkOffsetY; y <= chunkOffsetY; y++) {
				stringTmp = Scene2D.xyToString(camChunkX + x, camChunkY + y);
				if (scene.get(stringTmp) != null) {
					sprites.addAll(scene.get(stringTmp).__getSprites());
					if (light) {
						lightlist.addAll(scene.get(stringTmp).__getLights());
					}
				}
			}
		}
		sprites.sort(getLayerComparator());
//		fbo.bindFrameBuffer();
		GraphicsUtil.clear(clearcolor2d);
		batch.begin();
		GraphicsUtil.blendMode(BlendMode.ALPHA);
		for (Sprite s : sprites) {
			if (filter.intersects(s)) {
				s.paint(batch);
			}
		}
		batch.end();
//		fbo.unbindFrameBuffer();
		// if (light) {
		// lights.bindFrameBuffer();
		// GraphicsUtil.clear(sc.getAmbientColor());
		// GraphicsUtil.blendMode(BlendMode.ADDITIVE);
		// batch.begin();
		// for (Light2D s : lightlist) {
		// s.paint(batch);
		// }
		// batch.end();
		// lights.unbindFrameBuffer();
		// GraphicsUtil.blendMode(BlendMode.MULTIPLICATIVE);
		// finalBatch.begin();
		// finalBatch.draw(lights, -1, -1, 2, 2);
		// finalBatch.end();
		// }
		// GraphicsUtil.blendMode(BlendMode.ALPHA);
		//finalBatch.begin();
		//finalBatch.draw(fbo, -1, -1, 2, 2);
		//finalBatch.end();
		return batch.getVertexCount();
	}

	@EventSubscription
	public void onEvent(ResizeEvent ev) {
		//fbo.delete();
		//lights.delete();
		//lights = new FrameBufferObject(ev.getNewWidth(), ev.getNewHeight(), DepthbufferType.NONE);
		//fbo = new FrameBufferObject(ev.getNewWidth(), ev.getNewHeight(), DepthbufferType.NONE);
	}

	public Color clearColor() {
		return clearcolor2d;
	}

	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	public ArrayList<Light2D> getPreparedLights() {
		return lightlist;
	}
}
