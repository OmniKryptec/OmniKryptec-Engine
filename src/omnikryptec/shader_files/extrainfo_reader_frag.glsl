#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex;
uniform sampler2D extra;

uniform vec4 channels;

void main(void){
	
	float factor;
	if(length(channels)==0){
		factor = 1;
	}else{
		factor = length(texture(extra, textureCoords)*channels);
	}
	color = texture(tex, textureCoords)*factor;
}