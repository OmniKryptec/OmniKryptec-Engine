$define shader raytracer COMPUTE$

#version 430

layout(binding = 0, rgba32f) uniform image2D framebuffer;

uniform vec3 eye;
uniform vec3 ray00;
uniform vec3 ray01;
uniform vec3 ray10;
uniform vec3 ray11;
uniform float time;

layout (local_size_x = 8, local_size_y = 8) in;

layout (std430, binding = 1) buffer shader_data_t
{ 
	float data[];
} shader_data;


$module random$

#define SIZE 400
#define BOX_SIZE 0.1

#define MAX_STEPS 1000

#define EPSILON 0.0001

struct box {
  vec3 min;
  vec3 max;
};

const box BIG_BOX = {vec3(0,0,0), vec3(SIZE*BOX_SIZE,SIZE*BOX_SIZE,SIZE*BOX_SIZE)};

const ivec3[] DIRECTIONS = {ivec3(1,0,0),ivec3(-1,0,0),ivec3(0,1,0),ivec3(0,-1,0),ivec3(0,0,1),ivec3(0,0,-1)};

struct hitinfo {
  vec2 lambda;
  vec3 col;
};

ivec3 positionToFloored(vec3 pos){
    return ivec3(floor(pos.x/BOX_SIZE),floor(pos.y/BOX_SIZE),floor(pos.z/BOX_SIZE));
}

int positionToArrayIndex(ivec3 bpos){
    return bpos.x + bpos.y * SIZE + bpos.z * SIZE * SIZE;
}

bool exists(ivec3 bpos){
    return bpos.x >= 0 && bpos.x < SIZE && bpos.y >= 0 && bpos.y < SIZE && bpos.z >= 0 && bpos.z < SIZE;
}

vec2 intersectBox(vec3 origin, vec3 dir, const box b) {
  vec3 tMin = (b.min - origin) / dir;
  vec3 tMax = (b.max - origin) / dir;
  vec3 t1 = min(tMin, tMax);
  vec3 t2 = max(tMin, tMax);
  float tNear = max(max(t1.x, t1.y), t1.z);
  float tFar = min(min(t2.x, t2.y), t2.z);
  return vec2(tNear, tFar);
}

bool intersectsBox(vec2 lam){
    return (lam.y>=lam.x && (lam.x>=0 || lam.y>=0));
}

float helper(vec2 lam){
    return lam.x >= 0 ? lam.x : lam.y;
}

bool intersectBoxes(vec3 origin, vec3 dir, out hitinfo info) {
    vec2 lam = intersectBox(origin, dir, BIG_BOX);
    if(intersectsBox(lam)) {
        float biggest = 0;
        ivec3 ipos;
        if(lam.x>=0) {
            lam.x += EPSILON;
            //Find small box in large box
            vec3 pos = origin + lam.x * dir;
            biggest = lam.x;
            ipos = positionToFloored(pos);
        }else{
            ipos = positionToFloored(origin);
        }
        if(exists(ipos)) {               
            //Check small boxes
            for(int i=0; i<MAX_STEPS; i++) {
                int index = positionToArrayIndex(ipos);
                if(shader_data.data[index]>= 0.999){
                    if(i<2*MAX_STEPS/3){
                    float r = random(vec3(ipos), 1)*0.9;
                    float g = random(vec3(ipos), 2)*0.9;
                    float b = random(vec3(ipos), 3)*0.9;
                    info.col = vec3(r,g,b);//vec3(length(ipos)/(sqrt(3)*SIZE));
                    }else{
                    info.col = vec3(1);
                    }
                    return true;
                }
                bool found = false;
                for(int k=0; k<6; k++) {
                    ivec3 newipos = ipos + DIRECTIONS[k];
                    if(exists(newipos)) {
                        box b = {vec3(newipos.x,newipos.y,newipos.z)*BOX_SIZE, vec3(newipos.x+1,newipos.y+1,newipos.z+1)*BOX_SIZE};
                        lam = intersectBox(origin,dir,b);
                        if(lam.y >= lam.x && lam.x > biggest) {
                            //lam.x += EPSILON;
                            biggest = lam.x;
                            
                            ipos = newipos;//positionToFloored(origin + lam.x * dir);//newipos;
                            found = true;   
                            break;    
                        }
                    }
                }
                if(!found){
                    return false;
                }
            }
        }
    }
    return false;
}

vec4 trace(vec3 origin, vec3 dir) {
  hitinfo i;
  if (intersectBoxes(origin, dir, i)) {
    return vec4(i.col, 1.0);
  }
  return vec4(0,0,0.5, 1.0);
}

void main(void) {
  ivec2 pix = ivec2(gl_GlobalInvocationID.xy);
  ivec2 size = imageSize(framebuffer);
  if (pix.x >= size.x || pix.y >= size.y) {
    return;
  }
  vec2 pos = vec2(pix) / vec2(size.x - 1, size.y - 1);
  vec3 dir = mix(mix(ray00, ray01, pos.y), mix(ray10, ray11, pos.y), pos.x);
  vec4 color = trace(eye, dir);
  imageStore(framebuffer, pix, color);
}

$define shader ca COMPUTE$

#version 430

uniform float time;

layout (local_size_x = 8, local_size_y = 8) in;

layout (std430, binding = 1) buffer shader_data_t
{ 
	vec4 color;
} shader_data;


void main(void) {
  ivec2 pix = ivec2(gl_GlobalInvocationID.xy);
  
}
