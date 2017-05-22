#version 330

in vec2 pass_texcoords;

out vec4 col;

uniform sampler2D tex; 

void main(void){
	
	col = texture(tex, pass_texcoords);
	
}