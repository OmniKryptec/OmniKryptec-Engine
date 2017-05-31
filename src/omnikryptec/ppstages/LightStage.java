package omnikryptec.ppstages;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import omnikryptec.entity.Light;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.VertexArrayObject;
import omnikryptec.postprocessing.FrameBufferObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.postprocessing.PostProcessingStage;
import omnikryptec.shader_files.LightShader;
import omnikryptec.util.ModelUtil;
import omnikryptec.util.RenderUtil;

public class LightStage implements PostProcessingStage{

	private static FrameBufferObject target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	private static LightShader shader = new LightShader();
	
	
	private VertexArrayObject quad;
	
	private int[] list_ind = {0,1,2};
	private boolean[] usebefore = {true,false,false};
	
	
	public LightStage(){
		quad = ModelUtil.generateQuad().getVao();
	}
	
	@Override
	public void render(FrameBufferObject before, List<FrameBufferObject> beforelist, int stage) {
		render(OmniKryptecEngine.instance().getCurrentScene(), usebefore[0]?before:beforelist.get(list_ind[0]),usebefore[1]?before:beforelist.get(list_ind[1]),usebefore[2]?before:beforelist.get(list_ind[2]));
	}
	
	public LightStage setListIndices(int diffuseDepth, int normal, int specular){
		list_ind[0] = diffuseDepth;
		usebefore[0] = diffuseDepth < 0;
		list_ind[1] = normal;
		usebefore[1] = normal < 0;
		list_ind[2] = specular;
		usebefore[2] = specular < 0;
		return this;
	}
	
	private Light l;
	private List<Light> relevant;
	private void render(Scene currentScene, FrameBufferObject unsampledfbo, FrameBufferObject normalfbo, FrameBufferObject specularfbo) {
		
		RenderUtil.enableAdditiveBlending();
		shader.start();
		LightShader.viewv.loadMatrix(currentScene.getCamera().getViewMatrix());
		LightShader.proj.loadMatrix(currentScene.getCamera().getProjectionMatrix());
		LightShader.pixSizes.loadVec2(1.0f/Display.getWidth(), 1.0f/Display.getHeight());
		unsampledfbo.bindToUnit(0, 0);
		normalfbo.bindToUnit(1, 0);
		specularfbo.bindToUnit(2, 0);
		unsampledfbo.bindDepthTexture(3);
		quad.bind(0);
		target.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 1);
		relevant = currentScene.getRenderLights();
		if(relevant!=null){
			for(int i=0; i<relevant.size(); i++){
				l = relevant.get(i);
				if(l.isActive()){
					l.doLogic0();
					LightShader.light.loadVec4(l.getPosRad());
					LightShader.lightColor.loadVec3(l.getColor());
					GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
				}
			}
		}
		target.unbindFrameBuffer();
		quad.unbind(0);
		RenderUtil.disableBlending();
		
	}

	
	@Override
	public void resize(){
		target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	}
	

	@Override
	public boolean usesDefaultRenderObject(){
		return false;
	}

	@Override
	public FrameBufferObject getFbo() {
		return target;
	}


}
