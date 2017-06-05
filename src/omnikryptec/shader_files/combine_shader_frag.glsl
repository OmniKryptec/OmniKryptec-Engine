#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex1;
uniform sampler2D tex2;

uniform vec2 weights;

void main(void){
	
	color = texture(tex1, textureCoords) * weights.x + texture(tex2, textureCoords) * weights.y;
}

