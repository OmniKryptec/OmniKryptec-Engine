#version 330


in vec2 pass_texcoords;
in vec4 colormod;
in vec3 lights;
in vec4 coln;
in vec4 cols;

layout (location = 0) out vec4 colf;
layout (location = 1) out vec4 col1;
layout (location = 2) out vec4 col2;
layout (location = 3) out vec4 col3;


uniform sampler2D tex; 
uniform sampler2D extra;

uniform vec3 exinfovec;

uniform float hasextra;



uniform mat4 viewmatrix;

uniform vec3 ambient;


void main(void){
	
	col1 = coln;
	col2 = cols;
	
	vec4 diffuset = texture(tex, pass_texcoords);

	
	vec4 col = vec4(diffuset);
	col *= colormod;
	if(col.a<0.1){
		discard;
	}
	
	
	colf = vec4(ambient*col.rgb+lights.rgb,col.a);
	if(hasextra>0.5){
		col3.rgb = texture(extra, pass_texcoords).rgb;
	}else{
		col3.rgb = exinfovec;
	}
}


