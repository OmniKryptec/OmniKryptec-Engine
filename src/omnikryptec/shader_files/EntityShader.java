package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec4;

public class EntityShader extends Shader {


	public static final UniformMatrix transformation = new UniformMatrix("transmatrix");
	public static final UniformMatrix view = new UniformMatrix("viewmatrix");
	public static final UniformMatrix projection = new UniformMatrix("projmatrix");
	public static final UniformBoolean hasspecular = new UniformBoolean("hasspecular");
	public static final UniformFloat reflec = new UniformFloat("reflec");
	public static final UniformSampler tex = new UniformSampler("tex");
	public static final UniformSampler normalmap = new UniformSampler("normaltex");
	public static final UniformSampler specularmap = new UniformSampler("speculartex");
	public static final UniformVec4 colmod = new UniformVec4("colormod");
	public static final UniformFloat shinedamper = new UniformFloat("damp");
	public static final UniformBoolean hasextrainfomap = new UniformBoolean("hasextra");
	public static final UniformSampler extrainfo = new UniformSampler("extra");
	public static final UniformVec4 extrainfovec = new UniformVec4("exinfovec");
	
	
	public EntityShader() {
		super(EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_shader_vert.glsl"),
				EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_shader_frag.glsl"), "pos", "texcoords", "normal",
				"tangent", transformation, view, projection, tex, normalmap, specularmap, hasspecular, reflec,colmod, shinedamper, hasextrainfomap, extrainfo, extrainfovec);
		start();
		tex.loadTexUnit(0);
		normalmap.loadTexUnit(1);
		specularmap.loadTexUnit(2);
		extrainfo.loadTexUnit(3);
	}

}
