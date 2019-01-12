$define shader test VERTEX$

#version 330 core

layout(location = 0) in vec4 pos;

void main() {
	gl_Position = pos;
}



$define shader test FRAGMENT$ 

#version 330 core

uniform vec4 u_col;

out vec4 col;

void main() {
	col = u_col;
}