package omnikryptec.component;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import omnikryptec.audio.AudioSource;
import omnikryptec.entity.GameObject;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.util.Blocker;

/**
 *
 * @author Panzer1119
 */
public class AudioSourceComponent implements Component {
    
    private final ArrayList<AudioSource> sources = new ArrayList<>();
    private final Blocker blocker = new Blocker(0);
    
    public AudioSourceComponent(AudioSource... sources) {
        addSources(sources);
    }
    
    public final ArrayList<AudioSource> getSources() {
        blocker.waitFor();
        return sources;
    }
    
    public final AudioSource getSource(String name) {
        for(AudioSource source : sources) {
            if(source.getSoundName().equals(name)) {
                return source;
            }
        }
        return null;
    }
    
    public final AudioSourceComponent addSources(AudioSource... sources) {
        if(sources == null || sources.length == 0) {
            return this;
        }
        blocker.waitFor();
        blocker.setBlocked(true);
        for(AudioSource source : sources) {
            this.sources.add(source);
        }
        blocker.setBlocked(false);
        return this;
    }
    
    public final AudioSourceComponent removeSources(AudioSource... sources) {
        return removeSources(false, sources);
    }
    
    public final AudioSourceComponent removeSources(boolean delete, AudioSource... sources) {
        if(sources == null || sources.length == 0 || this.sources.isEmpty()) {
            return this;
        }
        blocker.waitFor();
        blocker.setBlocked(true);
        for(AudioSource source : sources) {
            this.sources.remove(source);
            if(delete) {
                source.delete();
            }
        }
        blocker.setBlocked(false);
        return this;
    }
    
    public final AudioSourceComponent deleteAllSources() {
        for(AudioSource source : sources) {
            source.delete();
        }
        sources.clear();
        return this;
    }

    @Override
    public final void execute(GameObject instance) {
        blocker.waitFor();
        blocker.setBlocked(true);
        boolean isUsingPhysics = false;
        RenderChunk chunk = instance.getMyChunk();
        Scene scene = null;
        PhysicsComponent physicsComponent = null;
        if(chunk != null) {
            scene = instance.getMyChunk().getScene();
            if(scene != null) {
                if(scene.isUsingPhysics()) {
                    physicsComponent = instance.getComponent(PhysicsComponent.class);
                    isUsingPhysics = ((physicsComponent != null) && (physicsComponent.getBody() != null));
                }
            }
        }
        final Vector3f position = instance.getAbsolutePos();
        final javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);
        if(isUsingPhysics) {
            physicsComponent.getBody().getAngularVelocity(velocity);
        }
        for(AudioSource source : sources) {
            source.setPosition(position);
            source.setVelocity(velocity);
        }
        blocker.setBlocked(false);
    }

    @Override
    public final void onDelete(GameObject instance) {
        blocker.waitFor();
        deleteAllSources();
    }

	@Override
	public float getLvl() {
		// TODO Auto-generated method stub
		return 0;
	}
    
}
