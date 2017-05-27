package omnikryptec.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.entity.Camera;
import omnikryptec.entity.GameObject;
import omnikryptec.entity.Light;
import omnikryptec.logger.Logger;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.Render;

public class Scene {

    private final Map<String, RenderChunk> scene = new HashMap<>();
    private Camera cam;
    private long cox=1, coy=1, coz=1;
    private float[] clearcolor = {0,0,0,0};

    
    
    public Scene(Camera cam){
        this.cam = cam;
    }

    private String tmp;

    public boolean addGameObject(GameObject g) {
        if(g != null){
            if(g instanceof Camera && Logger.isDebugMode()){
                Logger.log("A Camera should not be added as a GameObject!", LogLevel.WARNING);
                return false;
            }
            tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
            if(!scene.containsKey(tmp)) {
                scene.put(tmp, new RenderChunk(g.getChunkX(), g.getChunkY(), g.getChunkZ(), this));
            }
            scene.get(tmp).addGameObject(g);
            return true;
        } else {
            return false;
        }
    }

    public GameObject removeGameObject(GameObject g){
    	return removeGameObject(g, true);
    }
    
    public GameObject removeGameObject(GameObject g, boolean delete) {
        if(g != null) {
            if(g.getMyChunk() != null) {
                g.getMyChunk().removeGameObject(g, delete);
            } else {
                tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
                scene.get(tmp).removeGameObject(g, delete);
                g.delete();
            }
        }
        return g;
    }

    private static String xyzToString(long x, long y, long z) {
        return x + ":" + y + ":" + z;
    }


    public Camera getCamera(){
        return cam;
    }

    public void setCamera(Camera cam){
        this.cam = cam;
    }

    private long cx, cy, cz;
    private RenderChunk tmpc;

    public void frame(Render info, IRenderer ...re) {
        cx = cam.getChunkX();
        cy = cam.getChunkY();
        cz = cam.getChunkZ();
        for(long x = -cox + cx; x <= cox + cx; x++){
            for(long y = -coy + cy; y <= coy + cy; y++){
                for(long z = -coz + cz; z <= coz + cz; z++){
                    if((tmpc = scene.get(xyzToString(x, y, z))) != null){
                    	tmpc.frame(info, re);
                    }
                }
            }
        }
        cam.doLogic0();
        doLogic();
    }

	public void doLogic() {		
	}

	public List<Light> getRelevantLights() {
		List<Light> lights = new ArrayList<>();
		Light l = new Light();
		l.setColor(1, 1, 1);
		l.setRadius(1000);
		lights.add(l);
		return lights;
	}

	public Scene setClearColor(float r, float g, float b){
		return setClearColor(r, g, b, 1);
	}
	
	public Scene setClearColor(float r, float g, float b, float a){
		clearcolor = new float[]{r,g,b,a};
		return this;
	}
	
	public Scene setClearColor(float[] f){
		clearcolor = f;
		return this;
	}
	
	public float[] getClearColor() {
		return clearcolor;
	}

}
