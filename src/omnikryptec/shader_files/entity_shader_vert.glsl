#version 330

in vec2 texcoords;
in vec3 pos;
in vec3 normal;
in vec3 tangent;

out vec2 pass_texcoords;
out vec3 norm;
out vec3 tang;
out vec3 bitang;

uniform mat4 transmatrix;
uniform mat4 projmatrix;
uniform mat4 viewmatrix;

void main(void){
	
	vec4 worldPosition = transmatrix * vec4(pos,1.0);
	mat4 modelViewMatrix = viewmatrix * transmatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(pos,1.0);
	gl_Position = projmatrix * positionRelativeToCam;
	
	pass_texcoords = texcoords;
	
	vec3 surfaceNormal = (modelViewMatrix * vec4(normal,0.0)).xyz;
	
	norm = normalize(surfaceNormal);
	tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
	bitang = normalize(cross(norm, tang));
	
}