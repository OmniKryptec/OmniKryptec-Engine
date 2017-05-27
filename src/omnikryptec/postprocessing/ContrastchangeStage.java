package omnikryptec.postprocessing;

import java.util.List;

import org.lwjgl.opengl.Display;

import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.ContrastchangeShader;

public class ContrastchangeStage extends PostProcessingStep {

	private static FrameBufferObject target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	private static ContrastchangeShader shader = new ContrastchangeShader();
	private float change=0;
	
	public ContrastchangeStage(){
		this(0);
	}
	
	public ContrastchangeStage(float change) {
		super(shader, target);
		this.change = change;
	}

	public float getChange(){
		return change;
	}
	
	public ContrastchangeStage setChange(float f){
		this.change = f;
		return this;
	}
	
	private int list_ind=0;
	
	public ContrastchangeStage setListIndex(int beforeI){
		list_ind = beforeI;
		return this;
	}
	
	@Override
	public void bindTexture(FrameBufferObject before, List<FrameBufferObject> beforelist, Shader using) {
		beforelist.get(list_ind).bindToUnit(0);
		ContrastchangeShader.change.loadFloat(change);
	}

	@Override
	public void afterRendering() {
	}

	@Override
	public FrameBufferObject getOnResize() {
		return target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}

}
