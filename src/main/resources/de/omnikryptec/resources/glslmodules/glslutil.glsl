$define module random$
$header$
float random(vec2 v, float n);
float random(vec2 v, vec2 d, float n);
float random(vec3 v, float n);
float random(vec3 v, vec3 d, float n);
const vec2 c_rand_vec2 = vec2(12.9898,78.233);
const vec3 c_rand_vec3 = vec3(12.9898,78.233, 52.7216);
const float c_rand_float = 43758.5453;
$header$

float random(vec2 v, float n){
	return random(v, c_rand_vec2, n*c_rand_float);
}

float random(vec2 v, vec2 d, float n){
	return fract(sin(dot(v.xy,d.xy))*n);
}

float random(vec3 v, float n){
	return random(v, c_rand_vec3, n*c_rand_float);
}

float random(vec3 v, vec3 d, float n){
	return fract(sin(dot(v.xyz,d.xyz))*n);
}
