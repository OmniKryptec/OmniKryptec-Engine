#version 330


in vec2 pass_texcoords;
in mat3 TBN;
in vec3 norm;
in vec3 toLightVec[maxlights];
in vec3 toCamVec;
in vec4 coneDeg[maxlights];
layout (location = 0) out vec4 colf;
layout (location = 1) out vec4 col1;
layout (location = 2) out vec4 col2;
layout (location = 3) out vec4 col3;


uniform sampler2D tex; 
uniform sampler2D normaltex;
uniform sampler2D speculartex;
uniform sampler2D extra;

uniform vec4 colormod;
uniform vec3 exinfovec;

uniform float hasextra;
uniform float hasspecular;
uniform float hasnormal;


uniform vec4 matData;

uniform vec3 lightColor[maxlights];
uniform vec3 atts[maxlights];
uniform vec3 lightpos[maxlights];


uniform int activelights;
uniform vec3 ambient;

uniform mat4 viewmatrix;

float saturate(float value){

	return clamp(value,0.0,1.0);
}

vec3 lighting(vec3 Scol, vec3 tcvec, vec3 tlvec, vec3 normal, vec3 Mdiff, vec3 Mspec, float Mdamp, vec3 att, vec4 conei, vec3 pos){
	float distance = length(tlvec);
	//directional light -> lightpos is the light direction
	if(conei.w==1.0){
		tlvec = pos;
	}
	vec3 toLightNormalized = normalize(tlvec);
	vec3 unitCam = normalize(tcvec);
	vec3 fromLight = -toLightNormalized;


	float dot1 = saturate(dot(normal,toLightNormalized));
	vec3 diffusev = dot1 * Scol;

	vec3 reflected = reflect(fromLight, normal);
	float dot2 = saturate(dot(reflected, unitCam));
	float damp = 0;
	//if no diffuselight there will be no specular light
	if(dot1>0.0){
		damp = pow(dot2, Mdamp);
	}
	vec3 spec = damp * Scol * Mspec;




	float attenu = 1.0/(att.x + (att.y * distance) + (att.z * distance * distance));
	attenu = min(attenu, 1.0);
	//directional light
	if(conei.w==1.0){
		attenu = 1.0;
	}else{
		//point- or spotlight
		float ltsa = dot(fromLight, conei.xyz);
		//current point is outside the lightcone
		if(ltsa < conei.w){
			attenu = 0.0;
		}
	}
	return (diffusev*Mdiff+spec)*attenu;
}

void main(void){
	
	vec4 diffuset = texture(tex, pass_texcoords);

	vec3 normalt;
	if(hasnormal>0.5){
		normalt = texture(normaltex, pass_texcoords).rgb;
		normalt = normalize(((normalt.rgb)*2.0-1.0));
	}else{
		normalt = normalize(norm);
	}
	
	vec4 col = vec4(diffuset);
	col *= colormod;

	col1 = vec4(normalt.rgb*0.5+0.5,1.0);
	if(col.a<0.1){
		discard;
	}
	if(hasspecular>0.5){
		col2.rgb = texture(speculartex, pass_texcoords).rgb;
		col2.rgb = col2.rgb * matData.rgb;
		col2.a = matData.a;
	}else{
		col2 = matData;
	}
	colf = vec4(ambient*col.rgb,col.a);
	for(int i=0; i<activelights; i++){
		colf = colf+vec4(lighting(lightColor[i], toCamVec, toLightVec[i], normalt, col.rgb, col2.xyz, col2.w, atts[i], coneDeg[i], lightpos[i]),0);
	}
	if(hasextra>0.5){
		col3.rgb = texture(extra, pass_texcoords).rgb;
	}else{
		col3.rgb = exinfovec;
	}
}


