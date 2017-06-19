#version 330

in vec2 position;

out vec2 textureCoords;

uniform vec3 info;

void main(void){
	vec2 pos = position*0.5+0.5;
	
	pos = pos/info.x;
	pos.x += info.z/info.x;
	pos.y += floor(info.y/info.x)/info.x;
	
	gl_Position = vec4(pos*2.0-1.0, 0.0, 1.0);
	textureCoords = position * 0.5 + 0.5;
	
}