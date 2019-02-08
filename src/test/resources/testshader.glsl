$define shader test VERTEX$

#version 330 core

layout(location = 0) in vec4 pos;


out vec2 texc;

void main() {
	texc = vec2(pos.x, pos.y)*0.5f+vec2(0.5f);

	
	gl_Position = pos*0.5f;
	
}



$define shader test FRAGMENT$ 

#version 330 core

uniform vec4 u_col;
uniform sampler2D sampler;

in vec2 texc;

out vec4 col;

void main() {
	vec4 ocol = u_col * texture(sampler, texc);
	col = u_col;
	col=vec4(1,1,1,1);
}