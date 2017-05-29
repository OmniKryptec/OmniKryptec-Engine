package omnikryptec.postprocessing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import omnikryptec.entity.Light;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.Scene;
import omnikryptec.model.VertexArrayObject;
import omnikryptec.postprocessing.FrameBufferObject.DepthbufferType;
import omnikryptec.shader_files.LightShader;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

public class LightStage implements PostProcessingStage{

	private static FrameBufferObject target = new FrameBufferObject(Display.getWidth(), Display.getHeight(), DepthbufferType.DEPTH_TEXTURE);
	private static LightShader shader = new LightShader();
	
	
	private VertexArrayObject quad;
	
	private int[] list_ind = {0,1,2};
	private boolean[] usebefore = {true,false,false};
	
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
	
	
	private void render(Scene currentScene, FrameBufferObject unsampledfbo, FrameBufferObject normalfbo, FrameBufferObject specularfbo) {
		RenderUtil.enableAdditiveBlending();
		shader.start();
		LightShader.planes.loadVec2(currentScene.getCamera().getPlanesForLR());
		LightShader.viewv.loadMatrix(currentScene.getCamera().getViewMatrix());
		Matrix4f abc = Matrix4f.load(currentScene.getCamera().getViewMatrix(), null);
		LightShader.viewv2.loadMatrix(abc.translate(currentScene.getCamera().getAbsolutePos()));
		LightShader.abc.loadVec3(currentScene.getCamera().getAbsolutePos());
		LightShader.proj.loadMatrix(currentScene.getCamera().getProjectionMatrix());
		//LightShader.viewv2.loadMatrix(currentScene.getCamera().getViewMatrix());
		unsampledfbo.bindToUnit(0, 0);
		normalfbo.bindToUnit(1, 0);
		specularfbo.bindToUnit(2, 0);
		unsampledfbo.bindDepthTexture(3);
		
		target.bindFrameBuffer();
		RenderUtil.clear(0, 0, 0, 1);
		List<Light> relevant = currentScene.getRelevantLights();
		if(relevant!=null){
			for(Light l : relevant){
				float[] kram = createBuffer(currentScene, l);
				quad = generateQuad(kram);
				quad.bind(0,1);
				LightShader.light.loadVec4(l.getPosRad());
				LightShader.lightColor.loadVec3(l.getColor());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
				quad.unbind(0,1);
			}
		}
		target.unbindFrameBuffer();
		//quad.unbind(0,1);
		RenderUtil.disableBlending();
		
	}


	private IntBuffer ib = BufferUtils.createIntBuffer(4);
	private FloatBuffer b = BufferUtils.createFloatBuffer(3);

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	private static FloatBuffer matrixBuffer2 = BufferUtils.createFloatBuffer(16);

	
	private float[] createBuffer(Scene s, Light l){
		int[][] pixels = {{0,0},{0,Display.getHeight()},{Display.getWidth(), Display.getHeight()},{Display.getWidth(),0}};
		ib.clear();
		ib.put(new int[]{0,0, Display.getWidth(), Display.getHeight()});
		ib.flip();
		matrixBuffer2.clear();
		s.getCamera().getProjectionMatrix().store(matrixBuffer2);
		matrixBuffer2.flip();
		matrixBuffer.clear();
		Matrix4f test = new Matrix4f();
		test.setIdentity();
		test.store(matrixBuffer);
		//Maths.createEmptyTransformationMatrix(l.getAbsolutePos()).store(matrixBuffer);
		matrixBuffer.flip();
		float[] floatarray = new float[3*4];
		for(int i=0; i<4; i++){
		
			GLU.gluUnProject(pixels[i][0], pixels[i][1], 10, matrixBuffer, matrixBuffer2, ib, b);
			Vector3f vec = new Vector3f(b.get(0), b.get(1), b.get(2));
			Vector3f.sub(vec, s.getCamera().getAbsolutePos(), vec);
			if(vec.length()!=0){
				vec.normalise();
			}
			floatarray[i*3+0] = vec.x;
			floatarray[i*3+1] = vec.y;
			floatarray[i*3+2] = vec.z;
		}
		return floatarray;
	}
	
	private static final int QUAD_VERTEX_COUNT = 4;
	private static final float[] QUAD_VERTICES = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static final int[] QUAD_INDICES = {0,3,1,1,3,2};

	private static VertexArrayObject generateQuad(float[] special) {
		VertexArrayObject vao = VertexArrayObject.create();
		vao.storeData(QUAD_INDICES, QUAD_VERTEX_COUNT, QUAD_VERTICES, special);
		return vao;
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
