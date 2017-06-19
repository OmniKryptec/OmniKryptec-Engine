package omnikryptec.postprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.shader_files.DebugShader;
import omnikryptec.util.RenderUtil;

public class DebugRenderer extends PostProcessingStage{
	
	private List<Integer> disabled = new ArrayList<Integer>();
	
	private DebugShader shader = new DebugShader();
	
	private boolean tmp;
	private int notr;
	
	
	public DebugRenderer() {
	}
	
	public DebugRenderer(Integer...is){
		disabled.addAll(Arrays.asList(is));
	}
	
	public DebugRenderer disableIndex(int i){
		disabled.add(i);
		return this;
	}
	
	public DebugRenderer enableIndex(int i){
		disabled.remove((Integer)i);
		return this;
	}
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		notr = 0;
		shader.start();		
		int side = calcQuadratic(beforelist.size()+1-disabled.size());
		getFbo().bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 0);
		if(tmp = !disabled.contains(0)){
			beforelist.get(0).bindDepthTexture(0);
			shader.info.loadVec3(side, 0, 0);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		}
		for(int i=0; i<beforelist.size(); i++){
			if(disabled.contains(i+1)){
				notr++;
				continue;
			}
			beforelist.get(i).bindToUnit(0);
			shader.info.loadVec3(side, i+(tmp?1:0)-notr, (i+(tmp?1:0)-notr)%side);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		}
		getFbo().unbindFrameBuffer();
	}
	
	private int calcQuadratic(int amount){
		double sqrt = Math.sqrt(amount);
		return (int) Math.ceil(sqrt);
	}
	
	@Override
	protected FrameBufferObject createFbo() {
		return new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}
	
}
