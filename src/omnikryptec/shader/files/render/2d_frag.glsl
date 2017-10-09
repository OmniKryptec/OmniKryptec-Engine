#version 330

in vec4 col;
in vec2 tex;

out vec4 ocol;

uniform sampler2D sampler;

void main(void){
	if(tex.x<0||tex.y<0){
		ocol = col;
	}else{
		ocol = texture(sampler, tex)*col;
	}
}