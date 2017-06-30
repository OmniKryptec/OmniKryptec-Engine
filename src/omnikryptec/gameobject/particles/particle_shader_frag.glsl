#version 330

in vec2 texCoords1;
in vec2 texCoords2;
in float blend;

layout (location = 0) out vec4 out_colour;

uniform sampler2D particleTexture;


void main(void){
	
	vec4 col1 = texture(particleTexture, texCoords1);
	vec4 col2 = texture(particleTexture, texCoords2);
	
	
	out_colour = mix(col1, col2, blend);
	
	if(out_colour.a<0.5){
		discard;
	}
}
