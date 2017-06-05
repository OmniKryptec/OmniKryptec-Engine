package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.shader_files.CombineShader;
import omnikryptec.postprocessing.PostProcessingStage;

public class BloomStage implements PostProcessingStage {
	
	private FrameBufferObject target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	
	private CombineShader cshader = new CombineShader();
	private Vector2f combine_weights;
	
	private PostProcessingStage brightnessfilter;
	
	private PostProcessingStage bloomedeffect;
	
	public BloomStage(PostProcessingStage bloomedeffect, Vector2f weights){
		this(bloomedeffect, new Vector4f(1, 0, 0, 0), weights);
	}
	
	public BloomStage(PostProcessingStage bloomedeffect, Vector4f bloom_indicator, Vector2f weights){
		this(bloomedeffect, new BrightnessfilterStage(bloom_indicator), weights);
	}
	
	public BloomStage(PostProcessingStage bloomedeffect, PostProcessingStage bloom_indicator, Vector2f combine_weights) {
		brightnessfilter = bloom_indicator;
		this.combine_weights = combine_weights;
		this.bloomedeffect = bloomedeffect;
	}
	
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		brightnessfilter.render(before, beforelist, stage);
		before.resolveDepth(brightnessfilter.getFbo());
		bloomedeffect.render(brightnessfilter.getFbo(), beforelist, stage);
		bloomedeffect.getFbo().bindToUnit(1);
		before.bindToUnit(0);
		cshader.start();
		cshader.weights.loadVec2(combine_weights);
		target.bindFrameBuffer();
		renderQuad(true);
		target.unbindFrameBuffer();
		before.resolveDepth(target);
	}

	@Override
	public FrameBufferObject getFbo() {
		return target;
	}

	@Override
	public void resize() {
		brightnessfilter.resize();
		bloomedeffect.resize();
		target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
