package omnikryptec.terrain;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;

/**
 *
 * @author Panzer1119
 */
public class TerrainShader extends Shader {
    
    private static final String SHADER_FOLDER = "/omnikryptec/terrain/"; //TODO Move?

    public static final UniformMatrix transformation = new UniformMatrix("transformationMatrix");
    public static final UniformMatrix view = new UniformMatrix("viewMatrix");
    public static final UniformMatrix projection = new UniformMatrix("projectionMatrix");
    public static final UniformMatrix light = new UniformMatrix("lightPosition");
    //public static final UniformBoolean hasspecular = new UniformBoolean("hasspecular");
    public static final UniformFloat reflec = new UniformFloat("reflec");
    public static final UniformSampler tex = new UniformSampler("tex");
    //public static final UniformSampler normalmap = new UniformSampler("normaltex");
    //public static final UniformSampler specularmap = new UniformSampler("speculartex");
    
    public TerrainShader() {
        super(TerrainShader.class.getResourceAsStream(SHADER_FOLDER + "terrainVertexShader.txt"), TerrainShader.class.getResourceAsStream(SHADER_FOLDER + "terrainFragmentShader.txt"), "pos", "texcoords", "normal", "tangent", transformation, view, projection, tex, normalmap, specularmap, hasspecular, reflec);
        start();
        tex.loadTexUnit(0);
        normalmap.loadTexUnit(1);
        specularmap.loadTexUnit(2);
    }
    
}
