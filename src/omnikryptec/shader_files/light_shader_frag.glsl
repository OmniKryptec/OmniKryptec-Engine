#version 330

in vec2 textureCoords;

out vec4 col;

uniform sampler2D diffuse;
uniform sampler2D normal;
uniform sampler2D specular;
uniform sampler2D depth;

uniform vec4 light;
uniform vec3 lightColor;
uniform vec2 planes;
uniform mat4 vpos;

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

	return att * (Idiff + ISpec);
	
}

float tofloat(vec4 v){
	
	return v.x+v.y+v.z+v.w;
}

void main(void){
	mat3 testm = mat3(
	vpos[0][0], vpos[0][1], vpos[0][2],
	vpos[1][0], vpos[1][1], vpos[1][2],
	vpos[2][0], vpos[2][1], vpos[2][2]
	);
	vec3 view = normalize(testm * vec3(1,1,1));
	
	float dep = texture(depth, textureCoords).r;
	
	vec3 pos;
	pos.z = -planes.y/(planes.x+dep);
	pos.xy = view.xy/view.z*pos.z;
	
	vec3 norm = texture(normal, textureCoords).rgb-vec3(0.5);
	float len = length(norm);
	if(len>0.1){
		norm /= len;
	}else{
		norm = vec3(0,0,0);
	}
	
	vec4 diff = texture(diffuse, textureCoords);
	if(diff.a<0.1){
		discard;
	}
	vec4 spec = texture(specular, textureCoords);
	
	col.rgb = lighting(lightColor, light.xyz, light.w, pos, norm, diff.rgb, spec.rgb, spec.a);
	col.a = 1;
	//col.rgb = pos;
	//col.rgb = view.xyz;
	//col.rgb = diff.rgb;
}

