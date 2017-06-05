#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D scene;
uniform sampler2D extra;

uniform vec4 info;

void main(void){
	
	color = texture(scene, textureCoords);
	float brightness = (color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722);
	float factor;
	if(length(info)==0){
		factor = 1;
	}else{
		factor = length(texture(extra, textureCoords)*info);
	}
	color = color * factor * brightness;
}

