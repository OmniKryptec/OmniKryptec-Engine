#version 330


in vec2 pass_texcoords;
in mat3 TBN;
in vec3 norm;
in vec3 toLightVec[$OKE_MAX_LIGHTS$];
in vec3 toCamVec;
in vec4 coneDeg[$OKE_MAX_LIGHTS$];
in vec4 lightPosO[$OKE_MAX_LIGHTS$];
in vec4 colormod;


layout (location = 0) out vec4 colf;
layout (location = 1) out vec4 col1;
layout (location = 2) out vec4 col2;
layout (location = 3) out vec4 col3;


uniform sampler2D tex; 
uniform sampler2D normaltex;
uniform sampler2D speculartex;
uniform sampler2D extra;

uniform vec3 exinfovec;

uniform float hasextra;
uniform float hasspecular;
uniform float hasnormal;

uniform vec4 matData;

uniform vec3 lightColor[$OKE_MAX_LIGHTS$];
uniform vec4 atts[$OKE_MAX_LIGHTS$];
uniform vec3 catts[$OKE_MAX_LIGHTS$];


uniform int activelights;
uniform vec3 ambient;

uniform mat4 viewmatrix;


#module light

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
		colf = colf+vec4(lighting(lightColor[i], toCamVec, toLightVec[i], normalt, col.rgb, col2.xyz, col2.w, atts[i], coneDeg[i], lightPosO[i], catts[i]),0);
	}
	if(hasextra>0.5){
		col3.rgb = texture(extra, pass_texcoords).rgb;
	}else{
		col3.rgb = exinfovec;
	}
}




