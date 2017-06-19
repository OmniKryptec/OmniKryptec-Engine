#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D sampler;

void main(void){
		
		color = texture(sampler, textureCoords);
}