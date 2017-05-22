#version 330

in vec2 texcoords;
in vec3 pos;
in vec3 normal;
in vec3 tangent;

out vec2 pass_texcoords;

uniform mat4 transmatrix;
uniform mat4 projmatrix;
uniform mat4 viewmatrix;

void main(void){
	
	gl_Position = projmatrix * viewmatrix * transmatrix * vec4(pos,1.0);
	pass_texcoords = texcoords;
}