#version 330

//const int MAX_JOINTS = 50;//max joints allowed in a skeleton
//const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 in_position;
in vec2 in_textureCoords;
in vec3 in_normal;
in vec3 in_tangents;
in ivec3 in_jointIndices;
in vec3 in_weights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform mat4 jointTransforms[$ANIM_MAX_JOINTS$];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void){
	
        mat4 projectionViewMatrix = projectionMatrix * viewMatrix;
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	//hier kann man auch die bei diesem model maximalen weights als uniform hochladen falls es weniger hat ist alles besen
	for(int i = 0; i < $ANIM_MAX_WEIGHTS$; i++){
		mat4 jointTransform = jointTransforms[in_jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(in_position, 1.0);
		totalLocalPos += posePosition * in_weights[i];
		
		vec4 worldNormal = jointTransform * vec4(in_normal, 0.0);
		totalNormal += worldNormal * in_weights[i];
	}
	
	vec4 positionRelativeToCam = modelViewMatrix * vec4(in_position, 1.0);
	//gl_Position = (projectionMatrix * positionRelativeToCam) + (projectionViewMatrix * totalLocalPos);
	gl_Position = projectionViewMatrix * totalLocalPos;
	pass_normal = totalNormal.xyz;
	pass_textureCoords = in_textureCoords;

}