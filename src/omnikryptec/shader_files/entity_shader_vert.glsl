#version 330

in vec2 texcoords;
in vec3 pos;
in vec3 normal;
in vec3 tangent;

out vec2 pass_texcoords;
out mat3 TBN;
out vec3 norm;
out vec3 toLightVec[maxlights];
out vec4 coneDeg[maxlights];
out vec4 lightPosO[maxlights];
out vec3 toCamVec;

in mat4 transmatrix;
in vec4 colour;
out vec4 colormod;


uniform mat4 projmatrix;
uniform mat4 viewmatrix;
uniform vec4 uvs;

uniform vec4 lightpos[maxlights];
uniform vec4 coneInfo[maxlights];


uniform float hasnormal;

void main(void){
	colormod = colour;

	mat4 modelViewMatrix = viewmatrix * transmatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(pos,1.0);
	gl_Position = projmatrix * positionRelativeToCam;
	
	pass_texcoords = texcoords;
	pass_texcoords *= uvs.zw - uvs.xy;
	pass_texcoords += uvs.xy;
	
	vec3 surfaceNormal = (modelViewMatrix * vec4(normal,0.0)).xyz;
	norm = normalize(surfaceNormal);
	if(hasnormal>0.5){
		vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
		vec3 bitang = normalize(cross(norm, tang));
		TBN = mat3(
			tang.x, bitang.x, norm.x,
			tang.y, bitang.y, norm.y,
			tang.z, bitang.z, norm.z
		);
	}

	for(int i=0; i<maxlights; i++){
		toLightVec[i] = (viewmatrix * lightpos[i]).xyz - positionRelativeToCam.xyz;
		coneDeg[i].xyz = normalize(viewmatrix*vec4(coneInfo[i].xyz,0)).xyz;
		coneDeg[i].w = coneInfo[i].w;
		lightPosO[i].xyz = (viewmatrix * vec4(lightpos[i].xyz,0)).xyz;
		lightPosO[i].w = lightpos[i].w;
		if(hasnormal>0.5){
			toLightVec[i] = TBN * toLightVec[i];
			coneDeg[i].xyz = TBN * coneDeg[i].xyz;
			lightPosO[i].xyz = TBN * lightPosO[i].xyz;
		}
	}
	toCamVec = - positionRelativeToCam.xyz;
	if(hasnormal>0.5){
		toCamVec = TBN * toCamVec;
	}
}
