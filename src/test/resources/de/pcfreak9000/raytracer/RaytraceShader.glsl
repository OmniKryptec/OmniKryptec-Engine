$define shader raytracer COMPUTE$

#version 430

layout(binding = 0, rgba32f) uniform image2D framebuffer;

uniform vec3 eye;
uniform vec3 ray00;
uniform vec3 ray01;
uniform vec3 ray10;
uniform vec3 ray11;
uniform float time;

uniform int SIZE;
uniform float BOX_SIZE;
uniform int MAX_STEPS;

layout (local_size_x = 8, local_size_y = 8) in;

layout (std430, binding = 1) buffer shader_data_t
{ 
	float data[];
} shader_data;

layout (std430, binding = 2) buffer speed_t
{ 
	float data[];
} speedS;

layout (std430, binding = 3) buffer red_t
{ 
	float data[];
} red;

layout (std430, binding = 4) buffer green_t
{ 
	float data[];
} green;

layout (std430, binding = 5) buffer blue_t
{ 
	float data[];
} blue;

$module random$

#define EPSILON 0.0001
#define MAX_REC 10

struct box {
  vec3 min;
  vec3 max;
};

struct Ray{
    vec3 origin;
    vec3 dir;
    float biggest;
};

struct Hitinfo {
  vec2 lambda;
  vec3 col;
};

struct Recursion {
    Ray rays[MAX_REC];
    Hitinfo infos[MAX_REC];
    int currentIndex;
    
}; 

const ivec3[] DIRECTIONS = {ivec3(1,0,0),ivec3(-1,0,0),ivec3(0,1,0),ivec3(0,-1,0),ivec3(0,0,1),ivec3(0,0,-1)};


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

bool dealWithIt(inout Ray ray, inout Hitinfo info, ivec3 ipos, ivec3 lastipos){
    int index = positionToArrayIndex(ipos);
    if(shader_data.data[index] >= 0.9){
        float r = red.data[index];
        float g = green.data[index];
        float b = blue.data[index];
        info.col += vec3(r,g,b);
        return true;
    }else{
        if(lastipos.x!=-1){
            int lastIndex = positionToArrayIndex(lastipos);
            float s1 = speedS.data[lastIndex];
            float s2 = speedS.data[index];
            float bNumber = s2/s1;
            if(bNumber != 1) {
                vec3 normal = vec3(lastipos - ipos);
                vec3 newDirection = refract(ray.dir, normal, bNumber);
                if(newDirection.x==0 && newDirection.y==0 && newDirection.z==0) {
                    //internal reflection, so use reflect instead
                    newDirection = reflect(ray.dir, normal);
                    ray.origin = ray.origin + (info.lambda.x + EPSILON)*ray.dir;
                }else{
                    ray.origin = ray.origin + (helper(info.lambda))*ray.dir;
                }                        
                ray.dir = newDirection;
                ray.biggest = 0;
            }
        }
    }
    return false;
}

ivec3 findAdjacent(inout Ray ray, inout Hitinfo info, ivec3 ipos){
    for(int k=0; k<6; k++) {
        ivec3 newipos = ipos + DIRECTIONS[k];
        if(exists(newipos)) {
            box b = {vec3(newipos.x,newipos.y,newipos.z)*BOX_SIZE, vec3(newipos.x+1,newipos.y+1,newipos.z+1)*BOX_SIZE};
            info.lambda = intersectBox(ray.origin,ray.dir,b);
            if(info.lambda.y >= info.lambda.x && info.lambda.x > ray.biggest) {
                ray.biggest = info.lambda.x;
                return newipos;
            }
        }
    }
    return ivec3(0,0,0);
}

bool intersectBoxes(Ray ray, out Hitinfo info) {
    box BIG_BOX = {vec3(0,0,0), vec3(SIZE*BOX_SIZE,SIZE*BOX_SIZE,SIZE*BOX_SIZE)};
    info.lambda = intersectBox(ray.origin, ray.dir, BIG_BOX);
    if(intersectsBox(info.lambda)) {
        ray.biggest = 0;
        ivec3 ipos;
        if(info.lambda.x >= 0) {
            info.lambda.x += EPSILON;
            //Find small box in large box
            vec3 pos = ray.origin + info.lambda.x * ray.dir;
            ray.biggest = info.lambda.x;
            ipos = positionToFloored(pos);
        } else {
            ipos = positionToFloored(ray.origin);
        }
        ivec3 lastipos = ivec3(-1);
        if(exists(ipos)) {               
            info.col = vec3(0);
            Recursion recursion;
            recursion.currentIndex = 0;
            recursion.rays[recursion.currentIndex] = ray;
            for(int rec=0; rec<MAX_REC; rec++){
                //Check small boxes
                for(int i=0; i<MAX_STEPS; i++) {
                    if(dealWithIt(ray, info, ipos, lastipos)) {
                        return true;
                    }
                    ivec3 newipos = findAdjacent(ray, info, ipos);
                    if(newipos.x!=0 || newipos.y!=0 || newipos.z!=0) {
                        lastipos = ipos;
                        ipos = newipos;
                    } else {
                        return false;
                    }
                }
            }
        }
    }
    return false;
}

vec4 trace(Ray ray) {
  Hitinfo i;
  if (intersectBoxes(ray, i)) {
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
  Ray ray = {eye, normalize(dir), 0};
  vec4 color = trace(ray);
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
