#version 140

in vec2 position;

in vec4 texOffsets;
in float blendFac;
in mat4 modelViewMatrix;

out vec2 texCoords1;
out vec2 texCoords2;
out float blend;

uniform mat4 projectionMatrix;

uniform float nrRows;



void main(void){
	
	vec2 texCoords = position * 0.5 + 0.5;
	texCoords.y = 1.0 - texCoords.y;
	texCoords /= nrRows;
	texCoords1 = texCoords + texOffsets.xy;
	texCoords2 = texCoords + texOffsets.zw;
	blend = blendFac;
	
	
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}