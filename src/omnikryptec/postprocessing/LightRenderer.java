package omnikryptec.postprocessing;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;

import omnikryptec.camera.MatrixMath;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.shader.Shader;
import omnikryptec.shader_files.LightShader;
import omnikryptec.storing.Light;
import omnikryptec.storing.Model;
import omnikryptec.util.RenderUtil;

public class LightRenderer {

	private static FrameBufferObject target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	private static LightShader shader = new LightShader();
	
	private static LightRenderer instance;
	
	private Model quad = Model.generateQuad();
	
	private LightRenderer(){
		instance = this;
	}

	public static LightRenderer instance() {
		return instance == null? new LightRenderer() : instance;
	}

	public void render(Scene currentScene, FrameBufferObject unsampledfbo, FrameBufferObject normalfbo, FrameBufferObject specularfbo) {
		RenderUtil.enableAdditiveBlending();
		shader.start();
		LightShader.planes.loadVec2(currentScene.getCamera().getPlanesForLR());
		LightShader.viewv.loadMatrix(currentScene.getCamera().getViewMatrix());
		unsampledfbo.bindToUnit(0, 0);
		normalfbo.bindToUnit(1, 0);
		specularfbo.bindToUnit(2, 0);
		unsampledfbo.bindDepthTexture(3);
		quad.getVao().bind(0);
		target.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 0);
		List<Light> relevant = currentScene.getRelevantLights();
		if(relevant!=null){
			for(Light l : relevant){
				LightShader.light.loadVec4(l.getPosRad());
				LightShader.lightColor.loadVec3(l.getColor());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
			}
		}
		target.unbindFrameBuffer();
		quad.getVao().unbind(0);
		RenderUtil.disableBlending();
		
	}

	
	public void resize(){
		target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.NONE);
	}
	
	public FrameBufferObject getTarget(){
		return target;
	}

}
