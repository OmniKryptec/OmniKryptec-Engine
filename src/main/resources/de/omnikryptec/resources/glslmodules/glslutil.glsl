$define module random$
$header$
float random(vec2 v);
float random(vec2 v, vec2 d, float n);
const vec2 c_rand_vec = vec2(12.9898,78.233);
const float c_rand_float = 43758.5453;
$header$

float random(vec2 v, float n){
	return random(v, c_rand_vec, n*c_rand_float);
}

float random(vec2 v, vec2 d, float n){
	return fract(sin(dot(v.xy,d.xy))*n);
}