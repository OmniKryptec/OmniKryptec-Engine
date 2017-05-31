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

uniform mat4 proj;

float saturate(float value){
	
	return clamp(value,0.0,1.0);
}

vec3 lighting(vec3 Scol, vec3 Spos, float rad, vec3 p, vec3 n, vec3 Mdiff, vec3 Mspec, float Mrefl){
	vec3 l = Spos - p;
	vec3 v = normalize(p);
	vec3 h = normalize(v + l);
	
	float att=0;
	if(rad>0){
		att = saturate(1.0 - length(l)/rad);
	}else{
		att = 1;
	}
	l = normalize(l);
	//att = 1;
	vec3 Idiff = saturate(dot(l,n))*Mdiff*Scol;
	vec3 ISpec = pow(saturate(dot(h,n)), Mrefl)*Mspec*Scol;
	/*if(att>0.5){
		return vec3(1,0,0);
	}else{
		return Mdiff;
	}*/
	//return vec3(att,att,att);
	return att * (Idiff + ISpec);
	//return normalize(Spos - p)*0.5+0.5;
}

float tofloat(vec3 v){
	
	const vec3 bt = vec3(1.0,1.0/256.0,1.0/(256.0*256.0));  
	return dot(v, bt);
	//return v.x+v.y+v.z;
}

/*vec3 PositionFromDepth_DarkPhoton(float depth) { 
	vec2 ndc; // Reconstructed NDC-space position 
	vec3 eye; // Reconstructed EYE-space position
	eye.z = near * far / ((depth * (far - near)) - far);
	ndc.x = ((gl_FragCoord.x * widthInv) - 0.5) * 2.0;
	ndc.y = ((gl_FragCoord.y * heightInv) - 0.5) * 2.0;
	eye.x = ( (-ndc.x * eye.z) * (right-left)/(2*near) - eye.z * (right+left)/(2*near) );
	eye.y = ( (-ndc.y * eye.z) * (top-bottom)/(2*near) - eye.z * (top+bottom)/(2*near) );
	return eye;
}*/

void main(void){
	
	vec4 light = vec4(lightu.xyz-cam, 1.0)*vm;
	light.w = lightu.w;
	//light.xyz -= cam;
	
	vec3 view = normalize(vposf);
	view =  (vec4(view, 1)*(proj)).xyz;
	
	float dep = texture(depth, textureCoords).r;
	vec3 pos;
	pos.z = -planes.y/(planes.x+dep);
	pos.xy = view.yx/view.z*pos.z;
	//pos = (vec4(pos, 0)*vm).xyz;
	//pos = (vec4(pos, 1)*inverse(proj)).xyz;
	//pos += cam;
	//pos = pos;
	
	
	vec3 norm = texture(normal, textureCoords).rgb-vec3(0.5);
	float len = length(norm);
	/*if(len>0.1){
		norm /= len;
	}else{
		norm = vec3(0,0,0);
	}*/
	vec4 diff = texture(diffuse, textureCoords);
	vec4 spec = texture(specular, textureCoords);
	
	col.rgb = lighting(lightColor, light.xyz, light.w, pos, norm, diff.rgb, spec.rgb, spec.a);
	col.a = 1;
	//col.a = diff.a;
	//col.rgb = pos*0.5+0.5;
	//col.rgb = vec3(dep,0,0);
	//col.rgb = norm+vec3(0.5);
	//col.rgb = view;
	//col.rgb = view*0.5+0.5;
	//col.rgb = pos;
	//col.rgb = view.xyz;
	//col.rgb = diff.rgb;
}

