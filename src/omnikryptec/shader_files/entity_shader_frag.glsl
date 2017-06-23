#version 330

in vec2 pass_texcoords;
in vec3 norm;
in vec3 tang;
in vec3 bitang;
in vec3 toLightVec[4];

layout (location = 0) out vec4 col;
layout (location = 1) out vec4 col1;
layout (location = 2) out vec4 col2;
layout (location = 3) out vec4 col3;


uniform sampler2D tex; 
uniform sampler2D normaltex;
uniform sampler2D speculartex;
uniform sampler2D extra;

uniform vec4 colormod;
uniform vec4 exinfovec;

uniform float hasextra;
uniform float hasspecular;

uniform float reflec;
uniform float damp;

uniform mat4 viewmatrix;

float saturate(float value){

	return clamp(value,0.0,1.0);
}

vec3 lighting(vec3 Scol, vec3 Spos, float rad, vec3 p, vec3 n, vec3 Mdiff, vec3 Mspec, float Mdamp){
	vec3 l = Spos - p;
	float distance = length(l);
	vec3 ln = normalize(l);

	vec3 cam = (inverse(vm)*vec4(0.0,0.0,0.0,1.0)).xyz - p;
	cam = normalize(cam);

	vec3 ld = -cam;
	vec3 reflected = reflect(ld, n);

	float dot2 = saturate(dot(reflected, cam));
	float damp = pow(dot2, Mdamp);
	vec3 spec = damp * Scol * Mspec;


	float dot1 = saturate(dot(n,ln));
	vec3 diffusev = dot1 * Scol;

	float attf;
	if(rad<0){
		attf = 1;
	}else{
		attf = att.x + att.y * distance + att.z * distance * distance;
	}
	//return (diffusev+spec)/attf;
	return (diffusev*Mdiff)/attf;
}

void main(void){
	
	vec3 normalt = texture(normaltex, pass_texcoords).rgb;
	vec4 diffuset = texture(tex, pass_texcoords);
	//normalt -= 0.5;
	//normalt *= 2.0 - 1.0;
	//normalt = normalize(normalt.x*tang + normalt.y * bitang + normalt.z * norm);
	//normalt = normalt*0.5+0.5;
	mat3 TBN = mat3(tang, bitang, norm);
	normalt = normalize(TBN*(normalt.rgb)*2.0-1.0);
	
	col = vec4(diffuset);
	col *= colormod;

	col1 = vec4(normalt.rgb*0.5+0.5,1.0);
	if(col.a<0.5){
		discard;
	}
	if(hasspecular>0.5){
		col2.rgb = texture(speculartex, pass_texcoords).rgb;
		col2.a = damp;
	}else{
		col2 = vec4(reflec,reflec,reflec,damp);
	}
	if(hasextra>0.5){
		col3 = texture(extra, pass_texcoords);
	}else{
		col3 = exinfovec;
	}
}


