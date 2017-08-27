#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex;

uniform vec3 levels;


float lev(float l, float levelsf){
	float level = floor(l*levelsf);
	level /= levelsf;
	return level;
}

void main(void){

	color = texture(tex, textureCoords);
	color.r = lev(color.r, levels.r);
	color.g = lev(color.g, levels.g);
	color.b = lev(color.b, levels.b);
}

