$define module random$
$header$
float random(vec2 v);
$header$

float random(vec2 v){
	return fract(sin(dot(v.xy,vec2(12.9898,78.233)))*43758.5453);
}
