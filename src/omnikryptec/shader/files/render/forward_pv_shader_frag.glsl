#version 330
#module define_maxlights

in vec2 pass_texcoords;
in vec4 colormod;
in vec4 coln;
in vec4 cols;
in vec3 specu[$OKE_MAX_LIGHTS$];
in vec3 diffl[$OKE_MAX_LIGHTS$];
in float attenuation[$OKE_MAX_LIGHTS$];


layout (location = 0) out vec4 colf;
layout (location = 1) out vec4 col1;
layout (location = 2) out vec4 col2;
layout (location = 3) out vec4 col3;


uniform sampler2D tex; 
uniform sampler2D extra;

uniform vec3 exinfovec;

uniform float hasextra;

uniform vec3 ambient;

uniform int activelights;


void main(void){
	
	col1 = coln;
	col2 = cols;
	
	vec4 diffuset = texture(tex, pass_texcoords);

	
	vec4 col = vec4(diffuset);
	col *= colormod;
	if(col.a<0.1){
		discard;
	}
	
	
	colf = vec4(ambient*col.rgb,col.a);
	for(int i=0; i<activelights; i++){
		colf.rgb += (diffl[i]*col.rgb+specu[i])*attenuation[i];
	}
	if(hasextra>0.5){
		col3.rgb = texture(extra, pass_texcoords).rgb;
	}else{
		col3.rgb = exinfovec;
	}
}


