#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex;
uniform sampler2D depth;
uniform vec2 planes;

uniform vec4 fog;

uniform float density;
uniform float gradient;

void main(void){
	color = texture(tex, textureCoords);
	float depthf = texture(depth, textureCoords).r;
	depthf = planes.y/(planes.x+depthf);
	float ff = exp(-pow((depthf*density), gradient));
	color = mix(fog,color,ff);
}