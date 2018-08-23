#version 140

in vec2 position;

in vec4 texOffsets;
in float blendFac;
in mat4 modelViewMatrix;
in vec4 color;

out vec2 texCoords1;
out vec2 texCoords2;
out float blend;
out vec4 col;

uniform mat4 projectionMatrix;

uniform float nrRows;
uniform vec4 uvs;

void main(void){
	
	vec2 texCoords = position * 0.5 + 0.5;
	texCoords.y = 1.0 - texCoords.y;
	texCoords *= uvs.zw - uvs.xy;
	texCoords += uvs.xy;
	texCoords /= nrRows;
	texCoords1 = texCoords + (texOffsets.xy*(uvs.zw - uvs.xy)+uvs.xy);
	texCoords2 = texCoords + (texOffsets.zw*(uvs.zw - uvs.xy)+uvs.xy);
	blend = blendFac;
	col = color;
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}