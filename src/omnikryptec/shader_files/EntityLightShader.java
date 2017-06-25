package omnikryptec.shader_files;

import org.lwjgl.opengl.GL20;

import omnikryptec.display.DisplayManager;
import omnikryptec.shader.LineInsert;
import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformFloat;
import omnikryptec.shader.UniformInt;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class EntityLightShader extends Shader {

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
	public final UniformVec3[] lightpos, atts, lightcolor;
	public final UniformVec4[] coneinfo;
	public final UniformVec3 ambient = new UniformVec3("ambient");
	public final UniformInt activelights = new UniformInt("activelights");

	private static final LineInsert insert = new LineInsert() {

		@Override
		public String[] get(int type) {
			if (type == GL20.GL_FRAGMENT_SHADER || type == GL20.GL_VERTEX_SHADER) {
				return new String[] {
						"#define maxlights " + DisplayManager.instance().getSettings().getLightMaxForward() };
			} else {
				return null;
			}
		}
	};

	public EntityLightShader() {
		super("EntityLightShader", insert,
				EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_shader_vert.glsl"),
				EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_shader_frag.glsl"), "pos", "texcoords",
				"normal", "tangent");
		lightpos = new UniformVec3[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < lightpos.length; i++) {
			lightpos[i] = new UniformVec3("lightpos[" + i + "]");
		}
		registerUniforms(lightpos);
		atts = new UniformVec3[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < atts.length; i++) {
			atts[i] = new UniformVec3("atts[" + i + "]");
		}
		registerUniforms(atts);
		lightcolor = new UniformVec3[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < lightcolor.length; i++) {
			lightcolor[i] = new UniformVec3("lightColor[" + i + "]");
		}
		registerUniforms(lightcolor);
		coneinfo = new UniformVec4[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < coneinfo.length; i++) {
			coneinfo[i] = new UniformVec4("coneInfo[" + i + "]");
		}
		registerUniforms(coneinfo);
		registerUniforms(transformation, view, projection, tex, normalmap, specularmap, hasspecular, reflec, colmod,
				shinedamper, hasextrainfomap, extrainfo, extrainfovec, uvs, hasnormal, activelights, ambient);
		start();
		tex.loadTexUnit(0);
		normalmap.loadTexUnit(1);
		specularmap.loadTexUnit(2);
		extrainfo.loadTexUnit(3);
	}
}
