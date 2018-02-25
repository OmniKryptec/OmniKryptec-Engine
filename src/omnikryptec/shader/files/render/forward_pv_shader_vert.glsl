#version 330

in vec2 texcoords;
in vec3 pos;
in vec3 normal;
in vec3 tangent;

out vec2 pass_texcoords;


in mat4 transmatrix;
in vec4 colour;
out vec4 colormod;
out vec4 coln;
out vec4 cols;

out vec3 specu[$MAX_LIGHTS$];
out vec3 diffl[$MAX_LIGHTS$];
out float attenuation[$MAX_LIGHTS$];

uniform mat4 projmatrix;
uniform mat4 viewmatrix;
uniform vec4 uvs;

uniform vec4 lightpos[$MAX_LIGHTS$];
uniform vec4 coneInfo[$MAX_LIGHTS$];


uniform sampler2D speculartex;

uniform vec4 matData;

uniform vec3 lightColor[$MAX_LIGHTS$];
uniform vec4 atts[$MAX_LIGHTS$];
uniform vec3 catts[$MAX_LIGHTS$];


uniform int activelights;

uniform float hasspecular;



#module util


void lighting(vec3 Scol, vec3 tcvec, vec3 tlvec, vec3 normal, vec3 Mspec, float Mdamp, vec4 att, vec4 conei, vec4 pos, vec3 catt, int i){
	float distance = length(tlvec);
	//directional light -> lightpos is the light direction
	if(pos.w==0.0){
		tlvec = -pos.xyz;
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
	specu[i] = damp * Scol * Mspec;




	float attenu = 1.0/(att.x + (att.y * distance) + (att.z * distance * distance));
	if(att.w>-1&&distance>att.w){
		attenu = 0;
	}
	//directional light
	if(pos.w==0.0){
		attenu = 1.0;
	}else{
		//point- or spotlight
		float ltsa = dot(fromLight, normalize(conei.xyz));
		//current point is outside the lightcone
		if(ltsa < conei.w){
			attenu = 0.0;
		}else{
			float disfac = (ltsa - conei.w)/(1.0 - conei.w);
			disfac = catt.x + catt.y * disfac + catt.z * disfac * disfac;
			attenu = attenu * disfac;
		}
	}
	attenu = min(attenu, 1.0);
	diffl[i] = diffusev;
	attenuation[i] = attenu;
	//return (diffusev*Mdiff+spec)*attenu;
}



void main(void){
	colormod = colour;

	mat4 modelViewMatrix = viewmatrix * transmatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(pos,1.0);
	gl_Position = projmatrix * positionRelativeToCam;
	
	pass_texcoords = texcoords;
	pass_texcoords *= uvs.zw - uvs.xy;
	pass_texcoords += uvs.xy;
	
	vec3 surfaceNormal = (modelViewMatrix * vec4(normal,0.0)).xyz;
	vec3 norm = normalize(surfaceNormal);

	
	vec3 toCamVec = - positionRelativeToCam.xyz;
	
	vec3 normalt = normalize(norm);
	
	coln = vec4(normalt.rgb*0.5+0.5,1.0);
	
	if(hasspecular>0.5){
		cols.rgb = texture(speculartex, pass_texcoords).rgb;
		cols.rgb = cols.rgb * matData.rgb;
		cols.a = matData.a;
	}else{
		cols = matData;
	}
	for(int i=0; i<activelights; i++){
		vec3 toLightVec = (viewmatrix * lightpos[i]).xyz - positionRelativeToCam.xyz;
		vec4 coneDeg;
		coneDeg.xyz = normalize(viewmatrix*vec4(coneInfo[i].xyz,0)).xyz;
		coneDeg.w = coneInfo[i].w;
		vec4 lightPosO;
		lightPosO.xyz = (viewmatrix * vec4(lightpos[i].xyz,0)).xyz;
		lightPosO.w = lightpos[i].w;
		lighting(lightColor[i], toCamVec, toLightVec, normalt, cols.xyz, cols.w, atts[i], coneDeg, lightPosO, catts[i], i);
	}
}
