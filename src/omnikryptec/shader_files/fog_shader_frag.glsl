#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D tex;
uniform sampler2D depth;

const vec4 fog = vec4(1,0,1,1);

void main(void){

	color = texture(tex, textureCoords);
	/*float depthf = texture(depth, textureCoords).r;
	depthf = 0.01;
	color = (1-depthf)*color+depthf*fog;*/
}