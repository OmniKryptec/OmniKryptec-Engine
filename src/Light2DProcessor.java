package omnikryptec.postprocessing.stages;

import java.util.List;

import org.lwjgl.opengl.GL30;

import omnikryptec.display.Display;
import omnikryptec.gameobject.Camera;
import omnikryptec.gameobject.Light2D;
import omnikryptec.graphics.GraphicsUtil;
import omnikryptec.graphics.SpriteBatch;
import omnikryptec.postprocessing.main.FrameBufferObject;
import omnikryptec.postprocessing.main.PostProcessingStage;
import omnikryptec.renderer.d2.DefaultRenderer2D;
import omnikryptec.util.EnumCollection.BlendMode;
import omnikryptec.util.EnumCollection.DepthbufferType;
import omnikryptec.util.Instance;

public class Light2DProcessor extends PostProcessingStage {

	private DefaultRenderer2D myrenderer;
	private SpriteBatch batch;

	public Light2DProcessor(DefaultRenderer2D renderer) {
		assert renderer != null;
		this.myrenderer = renderer;
		batch = new SpriteBatch(new Camera().setDefaultScreenSpaceProjection(), 1);
	}

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		if (myrenderer.getPreparedLights() == null) {
			before.resolveToFbo(getFbo(), GL30.GL_COLOR_ATTACHMENT0);
		} else {
			getFbo().bindFrameBuffer();
			GraphicsUtil.clear(Instance.getCurrent2DScene().getAmbientColor());
			GraphicsUtil.blendMode(BlendMode.ADDITIVE);
			myrenderer.getSpriteBatch().begin();
			for (Light2D s : myrenderer.getPreparedLights()) {
				s.paint(myrenderer.getSpriteBatch());
			}
			myrenderer.getSpriteBatch().end();
			GraphicsUtil.blendMode(BlendMode.MULTIPLICATIVE);
			batch.begin();
			batch.draw(before, -1, -1, 2, 2);
			batch.end();
			getFbo().unbindFrameBuffer();
		}
	}

	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}

}
