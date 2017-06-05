#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D scene;
uniform sampler2D extra;



void main(void){
	
	color = texture(scene, textureCoords);
	float brightness = (color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722);
	color = color * texture(extra, textureCoords).r * brightness;
}

