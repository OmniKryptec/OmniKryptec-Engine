#version 330

in vec2 position;

out vec2 textureCoords;
out mat4 invprojv;
out mat4 vmat;

uniform mat4 proj;
uniform mat4 vm;

void main(void){

	gl_Position = vec4(position, 0.0, 1.0);
	textureCoords = position * 0.5 + 0.5;
	invprojv = inverse(proj*vm);
}