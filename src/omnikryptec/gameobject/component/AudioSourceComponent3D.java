package omnikryptec.gameobject.component;

import java.util.ArrayList;

import org.joml.Vector3f;

import omnikryptec.audio.AudioSource;
import omnikryptec.gameobject.GameObject3D;
import omnikryptec.main.AbstractScene3D;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.renderer.d3.RenderChunk3D;
import omnikryptec.util.Blocker;
import omnikryptec.util.Priority;

/**
 * Component for creating sounds
 *
 * @author Panzer1119
 */
@Priority(value = 1f)
@ComponentAnnotation(supportedGameObjectClass = GameObject3D.class)
public class AudioSourceComponent3D extends Component<GameObject3D> {

    private final ArrayList<AudioSource> sources = new ArrayList<>();
    private final Blocker blocker = new Blocker(0);

    /**
     * Normal constructor
     *
     * @param sources AudioSource Array Initialize this component with
     * AudioSources
     */
    public AudioSourceComponent3D(AudioSource... sources) {
        addSources(sources);
    }

    /**
     * Returns the AudioSources
     *
     * @return AudioSource ArrayList Sources
     */
    public final ArrayList<AudioSource> getSources() {
        blocker.waitFor();
        return sources;
    }

    /**
     * Returns the AudioSource given by the name
     *
     * @param name String Name
     * @return AudioSource AudioSource or null
     */
    public final AudioSource getSource(String name) {
        for (AudioSource source : sources) {
            if (source.getSound().getName().equals(name)) {
                return source;
            }
        }
        return null;
    }

    /**
     * Adds AudioSources
     *
     * @param sources AudioSource Array AudioSources
     * @return AudioSourceComponent A reference to this AudioSourceComponent
     */
    public final AudioSourceComponent3D addSources(AudioSource... sources) {
        if (sources == null || sources.length == 0) {
            return this;
        }
        blocker.waitFor();
        blocker.setBlocked(true);
        for (AudioSource source : sources) {
            this.sources.add(source);
        }
        blocker.setBlocked(false);
        return this;
    }

    /**
     * Removes AudioSources
     *
     * @param sources AudioSource Array AudioSources
     * @return AudioSourceComponent A reference to this AudioSourceComponent
     */
    public final AudioSourceComponent3D removeSources(AudioSource... sources) {
        return removeSources(false, sources);
    }

    /**
     * Removes and deletes AudioSources
     *
     * @param delete Boolean <tt>true</tt> if the AudioSources should be deleted
     * @param sources AudioSource Array AudioSources
     * @return AudioSourceComponent A reference to this AudioSourceComponent
     */
    public final AudioSourceComponent3D removeSources(boolean delete, AudioSource... sources) {
        if (sources == null || sources.length == 0 || this.sources.isEmpty()) {
            return this;
        }
        blocker.waitFor();
        blocker.setBlocked(true);
        for (AudioSource source : sources) {
            this.sources.remove(source);
            if (delete) {
                source.delete();
            }
        }
        blocker.setBlocked(false);
        return this;
    }

    /**
     * Deletes all AudioSources
     *
     * @return AudioSourceComponent A reference to this AudioSourceComponent
     */
    public final AudioSourceComponent3D deleteAllSources() {
        for (AudioSource source : sources) {
            source.delete();
        }
        sources.clear();
        return this;
    }

    private float newDeltaPitch;
    private boolean isUsingPhysics, paused;
    private RenderChunk3D chunk;
    private AbstractScene3D scene;
    private PhysicsComponent3D physicsComponent;
    private Vector3f position, rotation;
    private javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);

    ;

	@Override
    public final void execute(GameObject3D instance) {
        blocker.waitFor();
        blocker.setBlocked(true);
        isUsingPhysics = false;
        chunk = instance.getRenderChunk();
        scene = null;
        physicsComponent = null;
        if (chunk != null) {
            scene = chunk.getScene();
            if (scene != null) {
                if (scene.isUsingPhysics()) {
                    physicsComponent = instance.getComponent(PhysicsComponent3D.class);
                    isUsingPhysics = ((physicsComponent != null) && (physicsComponent.getBody() != null));
                }
            }
        }
        position = instance.getTransform().getPosition(true);
        velocity.set(0, 0, 0);
        if (isUsingPhysics) {
            physicsComponent.getBody().getAngularVelocity(velocity);
        }
        rotation = instance.getTransform().getRotation(true).getEulerAnglesXYZ(new Vector3f());
        for(AudioSource source : sources) {
            source.setPosition(position);
            source.setVelocity(velocity);
            source.setOrientation(rotation);
        }
        if (scene != null && scene.isUsingPhysics()) {
            paused = scene.getPhysicsWorld().isSimulationPaused();
            newDeltaPitch = scene.getPhysicsWorld().getSimulationSpeed() - 1.0F;
            for(AudioSource source : sources) {
                if (source.isAffectedByPhysics()) {
                    if (paused && source.isPlaying()) {
                        source.pauseTemporarily();// FIXME StreamedSound stops
                        // forever
                    } else if (!paused && !source.isPlaying()) {
                        source.continuePlayingTemporarily();
                    }
                    source.setDeltaPitch(newDeltaPitch);
                }
                source.updateState(OmniKryptecEngine.instance().getDeltaTimef());
            }
        }
        blocker.setBlocked(false);
    }

    @Override
    public final void onDelete(GameObject3D instance) {
        blocker.waitFor();
        deleteAllSources();
    }


}
