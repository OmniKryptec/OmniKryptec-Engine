$define shader test VERTEX$

#version 330 core

layout(location = 0) in vec4 pos;

uniform float instancesMax;

out float v_perc;

void main() {
	vec4 realpos = pos;
	realpos.x -= 0.5f;
	
	float perc = gl_InstanceID / instancesMax;
	vec4 offset = vec4(perc, 0, 0, 0);
	realpos /= instancesMax;
	gl_Position = realpos + offset;
	v_perc = perc;
}



$define shader test FRAGMENT$ 

#version 330 core

uniform vec4 u_col;

in float v_perc;

out vec4 col;

void main() {
	vec4 ocol = u_col;
	ocol.r = v_perc;
	col = ocol;
}