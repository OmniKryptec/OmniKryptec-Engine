$define shader test VERTEX$

#version 330 core

layout(location = 0) in vec4 pos;

uniform float instancesMax;

out float v_perc;
out vec2 texc;

void main() {
	vec4 realpos = pos;
	texc = vec2(pos.x, pos.y)*0.5f+vec2(0.5f);
	
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
uniform sampler2D sampler;

in float v_perc;
in vec2 texc;

out vec4 col;

void main() {
	vec4 ocol = u_col * texture(sampler, texc);
	//ocol.a = v_perc;
	col = ocol;
}