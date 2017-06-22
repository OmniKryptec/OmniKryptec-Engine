package omnikryptec.animation.renderer;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformMatrixArray;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;
import omnikryptec.util.AdvancedFile;

public class AnimatedModelShader extends Shader {

    private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
    private static final int DIFFUSE_TEX_UNIT = 0;
    
    private static final AdvancedFile VERTEX_SHADER = new AdvancedFile("omnikryptec", "animation", "renderer", "animatedEntityVertex.glsl");
    private static final AdvancedFile FRAGMENT_SHADER = new AdvancedFile("omnikryptec", "animation", "renderer", "animatedEntityFragment.glsl");

    public final UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    public final UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    public final UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
    protected UniformMatrixArray jointTransforms = new UniformMatrixArray("jointTransforms", MAX_JOINTS);
    private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

    /**
     * Creates the shader program for the {@link AnimatedModelRenderer} by
     * loading up the vertex and fragment shader code files. It also gets the
     * location of all the specified uniform variables, and also indicates that
     * the diffuse texture will be sampled from texture unit 0.
     */
    public AnimatedModelShader() {
        super(VERTEX_SHADER.createInputStream(), FRAGMENT_SHADER.createInputStream(), "in_position", "in_textureCoords", "in_normal", "in_jointIndices", "in_weights");
        registerUniforms(transformationMatrix, viewMatrix, projectionMatrix, diffuseMap, lightDirection, jointTransforms);
        start();
        diffuseMap.loadTexUnit(0);
    }
    
}
