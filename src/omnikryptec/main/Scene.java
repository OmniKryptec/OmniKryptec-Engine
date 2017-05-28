package omnikryptec.main;

import com.bulletphysics.dynamics.DynamicsWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import omnikryptec.entity.Camera;
import omnikryptec.entity.GameObject;
import omnikryptec.entity.Light;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.physics.PhysicsWorld;
import omnikryptec.renderer.IRenderer;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.renderer.RenderChunk.Render;
import omnikryptec.util.PhysicsUtil;

public class Scene {

    private final Map<String, RenderChunk> scene = new HashMap<>();
    private Camera cam;
    private long cox = 1, coy = 1, coz = 1;
    private float[] clearcolor = {0, 0, 0, 0};
    private PhysicsWorld physicsWorld = null;
    /*Temp Variables*/
    private String tmp;
    private long cx, cy, cz;
    private RenderChunk tmpc;
    
    public Scene(Camera cam){
        this.cam = cam;
    }

    public final boolean addGameObject(GameObject g) {
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

    public final GameObject removeGameObject(GameObject g){
    	return removeGameObject(g, true);
    }
    
    public final GameObject removeGameObject(GameObject g, boolean delete) {
        if(g != null) {
            if(g.getMyChunk() != null) {
                g.getMyChunk().removeGameObject(g, delete);
            } else {
                tmp = xyzToString(g.getChunkX(), g.getChunkY(), g.getChunkZ());
                scene.get(tmp).removeGameObject(g, delete);
                g.deleteOperation();
            }
        }
        return g;
    }

    public final Scene frame(Render info, IRenderer ...re) {
        if(isUsingPhysics()) {
            physicsWorld.stepSimulation();
        }
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
        return this;
    }

    public void doLogic() {		
    }

    public final List<Light> getRelevantLights() {
        List<Light> lights = new ArrayList<>();
        Light l = new Light();
        l.setColor(1, 1, 1);
        l.setRadius(1000);
        lights.add(l);
        return lights;
    }

    public final Camera getCamera(){
        return cam;
    }

    public final Scene setCamera(Camera cam){
        this.cam = cam;
        return this;
    }

    public final Scene setClearColor(float r, float g, float b) {
        return setClearColor(r, g, b, 1);
    }

    public final Scene setClearColor(float r, float g, float b, float a) {
        clearcolor = new float[]{r, g, b, a};
        return this;
    }

    public final Scene setClearColor(float[] f) {
        clearcolor = f;
        return this;
    }

    public final float[] getClearColor() {
        return clearcolor;
    }
    
    public final PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }
    
    public final Scene setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        return this;
    }
    
    public final Scene useDefaultPhysics() {
        return setPhysicsWorld(new PhysicsWorld(PhysicsUtil.createDefaultDynamicsWorld()));
    }
    
    public final boolean isUsingPhysics() {
        return physicsWorld != null;
    }

    private static String xyzToString(long x, long y, long z) {
        return x + ":" + y + ":" + z;
    }

}
