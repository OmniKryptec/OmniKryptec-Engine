$define shader test VERTEX$

#version 330 core

layout(location = 0) in vec4 pos;

uniform float instancesMax;

out float v_perc;

void main() {
	vec4 realpos = pos;
	float ins = gl_InstanceID;
	float perc = ins / instancesMax;
	
	vec4 offset = vec4(perc, 0, 0, 0);
	realpos.x *= 1f / instancesMax;
	realpos.y *= 1f / instancesMax;
	
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
	ocol.g = v_perc;
	col = ocol;
}