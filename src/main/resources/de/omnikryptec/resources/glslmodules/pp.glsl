$define shader pp-vert VERTEX$
#version 330

in vec2 position;

out vec2 textureCoords;

void main(void){

	gl_Position = vec4(position, 0.0, 1.0);
	textureCoords = position * 0.5 + 0.5;
}

$define shader pp-contrast-frag FRAGMENT$
#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D img;
uniform float change;

void main(void){

	color = texture(img, textureCoords);
	color.rgb = (color.rgb - 0.5) * (1.0 + change) + 0.5;
}

$define shader pp-gaussian-blur VERTEX$
#version 330

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

$define shader pp-gaussian-blur FRAGMENT$
#version 330

out vec4 out_colour;

in vec2 blurTextureCoords[11];

uniform sampler2D tex;

void main(void){
	
	out_colour = vec4(0.0);
	out_colour += texture(tex, blurTextureCoords[0]) * 0.0093;
    out_colour += texture(tex, blurTextureCoords[1]) * 0.028002;
    out_colour += texture(tex, blurTextureCoords[2]) * 0.065984;
    out_colour += texture(tex, blurTextureCoords[3]) * 0.121703;
    out_colour += texture(tex, blurTextureCoords[4]) * 0.175713;
    out_colour += texture(tex, blurTextureCoords[5]) * 0.198596;
    out_colour += texture(tex, blurTextureCoords[6]) * 0.175713;
    out_colour += texture(tex, blurTextureCoords[7]) * 0.121703;
    out_colour += texture(tex, blurTextureCoords[8]) * 0.065984;
    out_colour += texture(tex, blurTextureCoords[9]) * 0.028002;
    out_colour += texture(tex, blurTextureCoords[10]) * 0.0093;

}

$define shader pp-brightness-accent FRAGMENT$
#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D scene;


void main(void){
	
	color = texture(scene, textureCoords);
	float brightness = (color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722);
	color = color * brightness;
}
