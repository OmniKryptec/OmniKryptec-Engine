#version 330

in vec2 texcoords;
in vec3 pos;
in vec3 normal;
in vec3 tangent;

out vec2 pass_texcoords;

in mat4 transmatrix;
in vec4 colour;
out vec4 colormod;


uniform mat4 projmatrix;
uniform mat4 viewmatrix;
uniform vec4 uvs;


void main(void){
	colormod = colour;

	mat4 modelViewMatrix = viewmatrix * transmatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(pos,1.0);
	gl_Position = projmatrix * positionRelativeToCam;
	
	pass_texcoords = texcoords;
	pass_texcoords *= uvs.zw - uvs.xy;
	pass_texcoords += uvs.xy;
}
