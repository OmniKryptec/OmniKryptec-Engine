#version 330

in vec2 pass_texcoords;
in vec3 norm;
in vec3 tang;
in vec3 bitang;

layout (location = 0) out vec4 col;
layout (location = 1) out vec4 col1;
layout (location = 2) out vec4 col2;


uniform sampler2D tex; 
uniform sampler2D normaltex;
uniform sampler2D speculartex;

uniform float hasspecular;
uniform float reflec;

void main(void){
	
	vec3 normalt = texture(normaltex, pass_texcoords).rgb;
	vec4 diffuset = texture(tex, pass_texcoords);
	normalt -= 0.5;
	normalt = normalize(normalt.x*tang + normalt.y * bitang + normalt.z * norm);
	normalt = normalt*0.5+0.5;
	
	col = vec4(diffuset);
	col1 = vec4(normalt.rgb,1.0);
	
	if(hasspecular>0.5){
		col2.rgb = texture(speculartex, pass_texcoords).rgb;
		col2.a = reflec;
	}else{
		col2 = vec4(0,0,0,reflec);
	}
	if(col.a<0.1){
		discard;
	}
}