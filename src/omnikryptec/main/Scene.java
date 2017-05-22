package omnikryptec.main;

import java.util.HashMap;
import java.util.Map;

import omnikryptec.camera.Camera;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.Render;
import omnikryptec.storing.GameObject;

public class Scene {

    private final Map<String, RenderChunk> scene = new HashMap<>();
    private Camera cam;
    private long cox, coy, coz;

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

    public GameObject removeGameObject(GameObject g) {
        if(g != null) {
            if(g.getMyChunk() != null) {
                g.getMyChunk().removeGameObject(g);
            } else {
                tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
                scene.get(tmp).removeGameObject(g);
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
        cam.doLogic();
        doLogic();
    }

	public void doLogic() {		
	}

}
