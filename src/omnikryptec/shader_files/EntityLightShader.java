package omnikryptec.shader_files;

import org.lwjgl.opengl.GL20;

import omnikryptec.display.DisplayManager;
import omnikryptec.shader.Attribute;
import omnikryptec.shader.LineInsert;
import omnikryptec.shader.Shader;
import omnikryptec.shader.UniformBoolean;
import omnikryptec.shader.UniformInt;
import omnikryptec.shader.UniformMatrix;
import omnikryptec.shader.UniformSampler;
import omnikryptec.shader.UniformVec3;
import omnikryptec.shader.UniformVec4;

public class EntityLightShader extends Shader {

	public final UniformMatrix view = new UniformMatrix("viewmatrix");
	public final UniformMatrix projection = new UniformMatrix("projmatrix");
	public final UniformBoolean hasspecular = new UniformBoolean("hasspecular");
	public final UniformSampler tex = new UniformSampler("tex");
	public final UniformSampler normalmap = new UniformSampler("normaltex");
	public final UniformSampler specularmap = new UniformSampler("speculartex");
	public final UniformBoolean hasextrainfomap = new UniformBoolean("hasextra");
	public final UniformSampler extrainfo = new UniformSampler("extra");
	public final UniformVec3 extrainfovec = new UniformVec3("exinfovec");
	public final UniformVec4 uvs = new UniformVec4("uvs");
	public final UniformBoolean hasnormal = new UniformBoolean("hasnormal");
	public final UniformVec3[] lightcolor, catts;
	public final UniformVec4[] coneinfo, lightpos, atts;
	public final UniformVec3 ambient = new UniformVec3("ambient");
	public final UniformInt activelights = new UniformInt("activelights");
	public final UniformVec4 matData = new UniformVec4("matData");
	//public final UniformMatrix transformation = new UniformMatrix("transmatrix");
	//public final UniformVec4 colmod = new UniformVec4("colormod");

	
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
				EntityShader.class.getResourceAsStream(oc_shader_loc + "entity_shader_frag.glsl"), new Attribute("pos", 0), new Attribute("texcoords", 1),
				new Attribute("normal", 2), new Attribute("tangent",3), new Attribute("transmatrix", 4), new Attribute("colour", 8));
		lightpos = new UniformVec4[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < lightpos.length; i++) {
			lightpos[i] = new UniformVec4("lightpos[" + i + "]");
		}
		registerUniforms(lightpos);
		atts = new UniformVec4[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < atts.length; i++) {
			atts[i] = new UniformVec4("atts[" + i + "]");
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
		catts = new UniformVec3[DisplayManager.instance().getSettings().getLightMaxForward()];
		for (int i = 0; i < coneinfo.length; i++) {
			catts[i] = new UniformVec3("catts[" + i + "]");
		}
		registerUniforms(catts);
		registerUniforms(view, projection, tex, normalmap, specularmap, hasspecular,
				matData, hasextrainfomap, extrainfo, extrainfovec, uvs, hasnormal, activelights, ambient);
		//registerUniforms(transformation, colmod);
		start();
		tex.loadTexUnit(0);
		normalmap.loadTexUnit(1);
		specularmap.loadTexUnit(2);
		extrainfo.loadTexUnit(3);
	}
}
