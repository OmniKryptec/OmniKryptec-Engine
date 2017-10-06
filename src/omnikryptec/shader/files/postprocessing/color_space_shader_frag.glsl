#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex;

uniform vec3 levels;

#module util

void main(void){

	color = texture(tex, textureCoords);
	color.r = strictLevel(color.r, levels.r);
	color.g = strictLevel(color.g, levels.g);
	color.b = strictLevel(color.b, levels.b);
}

