package omnikryptec.animation.renderer;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformMatrixArray;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;
import omnikryptec.util.MyFile;

public class AnimatedModelShader extends Shader {

    private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
    private static final int DIFFUSE_TEX_UNIT = 0;

    private static final MyFile VERTEX_SHADER = new MyFile("omnikryptec/animation/renderer", "animatedEntityVertex.glsl");
    private static final MyFile FRAGMENT_SHADER = new MyFile("omnikryptec/animation/renderer", "animatedEntityFragment.glsl");

    protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
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
        super(VERTEX_SHADER.getInputStream(), FRAGMENT_SHADER.getInputStream(), "in_position", "in_textureCoords", "in_normal", "in_jointIndices", "in_weights");
        registerUniforms(projectionViewMatrix, diffuseMap, lightDirection, jointTransforms);
        start();
        diffuseMap.loadTexUnit(0);
    }
    
}
