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

uniform vec3 campos;

float saturate(float value){
	
	return clamp(value,0.0,1.0);
}

vec3 lighting(vec3 Scol, vec3 Spos, float rad, vec3 p, vec3 n, vec3 Mdiff, vec3 Mspec, float Mdamp){
	vec3 l = Spos - p;
	vec3 v = normalize(p);
	vec3 h = normalize(v + l);
	
	float att=0;
	if(rad>=0){
		att = saturate(1.0 - length(l)/rad);
	}else{
		att = 1;
	}
	l = normalize(l);
	vec3 Idiff = saturate(dot(l,n))*Mdiff*Scol;
	vec3 ISpec = pow(saturate(dot(h,n)), Mdamp)*Mspec*Scol;
	return att * (Idiff + ISpec);
}


void main(void){
	
	vec3 pos = vec3(( gl_FragCoord.x * pixelSize.x),
                   (gl_FragCoord.y * pixelSize.y), 0.0);
	pos.z = texture(depth, textureCoords).r;
	
	vec4 clip = invprojv * vec4(pos * 2.0 - 1.0,  1.0);
	pos = clip.xyz / clip.w;

	
	vec3 norm = normalize(texture(normal, textureCoords).xyz*2.0-1.0);	
	vec4 diff = texture(diffuse, textureCoords);
	vec4 spec = texture(specular, textureCoords);
	
	col.rgb = lighting(lightColor, lightu.rgb, lightu.w, pos, norm, diff.rgb, spec.rgb, spec.a);
	//col.a = diff.a;
	col.a = 1;
}
