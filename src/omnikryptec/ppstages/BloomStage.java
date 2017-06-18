package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStage;
import omnikryptec.postprocessing.SimpleStage;
import omnikryptec.shader_files.BrightnessHighlighterShader;
import omnikryptec.shader_files.CombineShader;

public class BloomStage extends PostProcessingStage {

	private CombineShader cshader = new CombineShader();
	private Vector2f combine_weights;

	private PostProcessingStage bloomindicator;

	private PostProcessingStage bloomedeffect;

	private SimpleStage brightness = new SimpleStage(new BrightnessHighlighterShader());

	public BloomStage(PostProcessingStage bloomedeffect, Vector2f weights) {
		this(bloomedeffect, new Vector4f(1, 0, 0, 0), weights);
	}

	public BloomStage(PostProcessingStage bloomedeffect, Vector4f bloom_indicator, Vector2f weights) {
		this(bloomedeffect, new FilterStage(bloom_indicator), weights);
	}

	public BloomStage(PostProcessingStage bloomedeffect, PostProcessingStage bloom_indicator,
			Vector2f combine_weights) {
		this.bloomindicator = bloom_indicator;
		this.combine_weights = combine_weights;
		this.bloomedeffect = bloomedeffect;
	}

	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		brightness.renderAndResolveDepth(before, beforelist, stage);
		bloomindicator.renderAndResolveDepth(brightness.getFbo(), beforelist, stage);
		bloomedeffect.renderAndResolveDepth(bloomindicator.getFbo(), beforelist, stage);
		bloomedeffect.getFbo().bindToUnita(1);
		before.bindToUnita(0);
		cshader.start();
		cshader.weights.loadVec2(combine_weights);
		renderQuad(true);
	}

	@Override
	public FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

	@Override
	public void onResize() {
		bloomindicator.resize();
		bloomedeffect.resize();
		brightness.resize();
	}

}
