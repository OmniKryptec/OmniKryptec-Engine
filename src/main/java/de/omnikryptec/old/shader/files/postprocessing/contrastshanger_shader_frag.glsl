#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D img;
uniform float change;

void main(void){

	color = texture(img, textureCoords);
	color.rgb = (color.rgb - 0.5) * (1.0 + change) + 0.5;
}