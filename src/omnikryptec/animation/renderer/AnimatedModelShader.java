package omnikryptec.animation.renderer;

import omnikryptec.animation.AnimatedModel;
import omnikryptec.gameobject.Entity;
import omnikryptec.main.Scene;
import omnikryptec.resource.model.AdvancedModel;
import omnikryptec.resource.model.Material;
import omnikryptec.shader.base.Shader;
import omnikryptec.shader.base.UniformMatrix;
import omnikryptec.shader.base.UniformMatrixArray;
import omnikryptec.shader.base.UniformSampler;
import omnikryptec.shader.base.UniformVec3;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Instance;
import omnikryptec.util.RenderUtil;

public class AnimatedModelShader extends Shader {

    //private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
    //private static final int DIFFUSE_TEX_UNIT = 0;
    private static final AdvancedFile VERTEX_SHADER = new AdvancedFile("omnikryptec", "animation", "renderer", "animatedEntityVertex.glsl");
    private static final AdvancedFile FRAGMENT_SHADER = new AdvancedFile("omnikryptec", "animation", "renderer", "animatedEntityFragment.glsl");

    public final UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public final UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public final UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
    protected UniformMatrixArray jointTransforms = new UniformMatrixArray("jointTransforms", Instance.MAX_JOINTS);
    private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

    /**
     * Creates the shader program for the {@link AnimatedModelRenderer} by
     * loading up the vertex and fragment shader code files. It also gets the
     * location of all the specified uniform variables, and also indicates that
     * the diffuse texture will be sampled from texture unit 0.
     */
    public AnimatedModelShader() {
        super(VERTEX_SHADER.createInputStream(), FRAGMENT_SHADER.createInputStream(), "in_position", "in_textureCoords", "in_normal", "in_tangents", "in_jointIndices", "in_weights");
        registerUniforms(transformationMatrix, viewMatrix, projectionMatrix, diffuseMap, lightDirection, jointTransforms);
        start();
        diffuseMap.loadTexUnit(0);
    }

    private AnimatedModel model;
	public void onModelRenderStart(AdvancedModel m) {
		model = (AnimatedModel)m;
        model.getModel().getVao().bind(0, 1, 2, 3, 4, 5);
        model.getMaterial().getTexture(Material.DIFFUSE).bindToUnitOptimized(0);
		jointTransforms.loadMatrixArray(model.getJointTransforms());
        RenderUtil.disableBlending();
        RenderUtil.enableDepthTesting(true);
	}

	public void onRenderStart(Scene s) {
		viewMatrix.loadMatrix(s.getCamera().getViewMatrix());
		projectionMatrix.loadMatrix(s.getCamera().getProjectionMatrix());
	}

	public void onRenderInstance(Entity e) {
         transformationMatrix.loadMatrix(e.getTransformation());
	}
    
}
