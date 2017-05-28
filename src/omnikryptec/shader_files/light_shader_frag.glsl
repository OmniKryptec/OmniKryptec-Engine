#version 330

in vec2 textureCoords;
in vec3 vposf;



out vec4 col;

uniform sampler2D diffuse;
uniform sampler2D normal;
uniform sampler2D specular;
uniform sampler2D depth;

uniform vec4 lightu;
uniform vec3 lightColor;
uniform vec2 planes;
uniform mat4 vm;
uniform mat4 vm2;
uniform vec3 cam;

float saturate(float value){
	
	return clamp(value,0.0,1.0);
}

vec3 lighting(vec3 Scol, vec3 Spos, float rad, vec3 p, vec3 n, vec3 Mdiff, vec3 Mspec, float Mrefl){
	vec3 l = Spos - p;
	vec3 v = normalize(p);
	vec3 h = normalize(v + l);
	
	float att = saturate(1.0 - length(l)/rad);
	l = normalize(l);
	
	vec3 Idiff = saturate(dot(l,n))*Mdiff*Scol;
	vec3 ISpec = pow(saturate(dot(h,n)), Mrefl)*Mspec*Scol;
	/*if(att>0.5){
		return vec3(1,0,0);
	}else{
		return Mdiff;
	}*/
	return att * (Idiff + ISpec);
	
}

float tofloat(vec3 v){
	
	const vec3 bt = vec3(1.0,1.0/256.0,1.0/(256.0*256.0));  
	return dot(v, bt);
	//return v.x+v.y+v.z;
}

void main(void){
	
	vec4 light = vec4(lightu.xyz, 1.0);
	light.w = lightu.w;
	
	vec3 view = normalize(vposf);
	//view = view*0.5+0.5;
	
	float dep = texture(depth, textureCoords).r;
	
	//view = cam;
	//view = vec4(vec4(view,1.0)*vm2).xyz;
	vec3 pos;
	pos.z = -planes.y/(planes.x+dep);
	pos.xy = view.xy/view.z*pos.z;
	//pos = vec4(vec4(pos, 1.0)*inverse(vm2)).xyz;
	pos = pos+cam;

	vec3 norm = texture(normal, textureCoords).rgb-vec3(0.5);
	float len = length(norm);
	if(len>0.1){
		norm /= len;
	}else{
		norm = vec3(0,0,0);
	}
	
	vec4 diff = texture(diffuse, textureCoords);
	vec4 spec = texture(specular, textureCoords);
	
	col.rgb = lighting(lightColor, light.xyz, light.w, pos, norm, diff.rgb, spec.rgb, spec.a);
	col.a = 1;
	//col.rgb = view;
	//col.rgb = view*0.5+0.5;
	//col.rgb = pos;
	//col.rgb = view.xyz;
	//col.rgb = diff.rgb;
}

