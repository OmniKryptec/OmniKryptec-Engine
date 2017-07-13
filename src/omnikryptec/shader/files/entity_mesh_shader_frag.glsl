#version 330


in vec2 pass_texcoords;
in vec4 colormod;

//layout (location = 0) out vec4 colf;
out vec4 colf;

uniform sampler2D tex; 

void main(void){
	
	colf = texture(tex, pass_texcoords) * colormod;
}


