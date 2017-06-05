#version 330

in vec2 textureCoords;
in mat4 invprojv;

out vec4 col;

uniform sampler2D diffuse;
uniform sampler2D normal;
uniform sampler2D specular;
uniform sampler2D depth;

uniform vec4 lightu;
uniform vec3 lightColor;

uniform vec2 pixelSize;

uniform vec3 att; 
uniform mat4 vm;


float saturate(float value){
	
	return clamp(value,0.0,1.0);
}

vec3 lighting(vec3 Scol, vec3 Spos, float rad, vec3 p, vec3 n, vec3 Mdiff, vec3 Mspec, float Mrefl){
	vec3 l = Spos - p;
	float distance = length(l);
	vec3 ln = normalize(l);
	
	vec3 cam = (inverse(vm)*vec4(0.0,0.0,0.0,1.0)).xyz - p;
	cam = normalize(cam);
	
	vec3 ld = -cam;
	vec3 reflected = reflect(ld, n);
	
	float dot2 = saturate(dot(reflected, cam));
	float damp = pow(dot2, Mrefl);
	vec3 spec = damp * Scol * Mspec;
	
	
	float dot1 = saturate(dot(n,ln));
	vec3 diffusev = dot1 * Scol;
	
	float attf;
	if(rad<0){
		attf = 1;
	}else{
		attf = att.x + att.y * distance + att.z * distance * distance;
	}
	//return diffusev;
	return (diffusev*Mdiff)/attf;
}


void main(void){
	
	vec3 pos = vec3(( gl_FragCoord.x * pixelSize.x),
                   (gl_FragCoord.y * pixelSize.y), 0.0);
	pos.z = texture(depth, textureCoords).r;
	
	vec3 norm = normalize(texture(normal, textureCoords).xyz*2.0-1.0);
	
	vec4 clip = invprojv * vec4(pos * 2.0 - 1.0,  1.0);
	pos = clip.xyz / clip.w;
	
	vec4 diff = texture(diffuse, textureCoords);
	vec4 spec = texture(specular, textureCoords);
	
	col.rgb = lighting(lightColor, lightu.rgb, lightu.w, pos, norm, diff.rgb, spec.rgb, spec.a);
	//col.a = diff.a;
	col.a = 1;
	//col.rgb = vec3(1,1,1);
}
