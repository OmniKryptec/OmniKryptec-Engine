#version 330

in vec2 position;
in vec3 vpos;

out vec2 textureCoords;
out vec3 vposf;

uniform mat4 vm;

void main(void){

	gl_Position = vec4(position, 0.0, 1.0);
	textureCoords = position * 0.5 + 0.5;
	vposf = (vec4(vpos,1.0)*vm).xyz;
}