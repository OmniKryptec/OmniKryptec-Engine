#version 150

in vec2 position;

out vec2 blurTextureCoords[11];

uniform float size;
uniform float hor;

void main(void){
	
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 texCoords = position * 0.5 + 0.5;
	float pixsize = 1.0 / size;
	
	for(int i=-5; i<=5; i++){
		if(hor>0.5){
			blurTextureCoords[i+5] = texCoords + vec2(pixsize * i, 0.0);
		}else{
			blurTextureCoords[i+5] = texCoords + vec2(0.0, pixsize * i);
		}
	}
}