package omnikryptec.shader_files;

import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class EntityShader extends Shader {

	public final UniformMatrix transformation = new UniformMatrix("transmatrix");
	public final UniformMatrix view = new UniformMatrix("viewmatrix");
	public final UniformMatrix projection = new UniformMatrix("projmatrix");
	public final UniformBoolean hasspecular = new UniformBoolean("hasspecular");
	public final UniformFloat reflec = new UniformFloat("reflec");
	public final UniformSampler tex = new UniformSampler("tex");
	public final UniformSampler normalmap = new UniformSampler("normaltex");
	public final UniformSampler specularmap = new UniformSampler("speculartex");
	public final UniformVec4 colmod = new UniformVec4("colormod");
	public final UniformFloat shinedamper = new UniformFloat("damp");
	public final UniformBoolean hasextrainfomap = new UniformBoolean("hasextra");
	public final UniformSampler extrainfo = new UniformSampler("extra");
	public final UniformVec3 extrainfovec = new UniformVec3("exinfovec");
	public final UniformVec4 uvs = new UniformVec4("uvs");
	public final UniformBoolean hasnormal = new UniformBoolean("hasnormal");

	public EntityShader() {
		super("EntityShader", EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_nl_shader_vert.glsl"),
				EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_nl_shader_frag.glsl"), "pos",
				"texcoords", "normal", "tangent");
		registerUniforms(transformation, view, projection, tex, normalmap, specularmap, hasspecular, reflec, colmod,
				shinedamper, hasextrainfomap, extrainfo, extrainfovec, uvs);
		start();
		tex.loadTexUnit(0);
		normalmap.loadTexUnit(1);
		specularmap.loadTexUnit(2);
		extrainfo.loadTexUnit(3);
	}

}
