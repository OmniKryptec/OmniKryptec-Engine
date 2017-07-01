package omnikryptec.gameobject.component;

import java.util.ArrayList;
import omnikryptec.audio.AudioEffectState;

import omnikryptec.audio.AudioSource;
import omnikryptec.gameobject.gameobject.GameObject;
import omnikryptec.main.Scene;
import omnikryptec.renderer.RenderChunk;
import omnikryptec.util.Blocker;
import org.joml.Vector3f;

/**
 * Component for creating sounds
 *
 * @author Panzer1119
 */
public class AudioSourceComponent implements Component {

    private final ArrayList<AudioSource> sources = new ArrayList<>();
    private final Blocker blocker = new Blocker(0);

    /**
     * Normal constructor
     *
     * @param sources AudioSource Array Initialize this component with
     * AudioSources
     */
    public AudioSourceComponent(AudioSource... sources) {
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
    public final AudioSourceComponent addSources(AudioSource... sources) {
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
    public final AudioSourceComponent removeSources(AudioSource... sources) {
        return removeSources(false, sources);
    }

    /**
     * Removes and deletes AudioSources
     *
     * @param delete Boolean <tt>true</tt> if the AudioSources should be deleted
     * @param sources AudioSource Array AudioSources
     * @return AudioSourceComponent A reference to this AudioSourceComponent
     */
    public final AudioSourceComponent removeSources(boolean delete, AudioSource... sources) {
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
    public final AudioSourceComponent deleteAllSources() {
        for (AudioSource source : sources) {
            source.delete();
        }
        sources.clear();
        return this;
    }

    private float newDeltaPitch;
    private boolean isUsingPhysics, paused;
    private RenderChunk chunk;
    private Scene scene;
    private PhysicsComponent physicsComponent;
    private Vector3f position, rotation;
    private javax.vecmath.Vector3f velocity = new javax.vecmath.Vector3f(0, 0, 0);

    ;

	@Override
    public final void execute(GameObject instance) {
        blocker.waitFor();
        blocker.setBlocked(true);
        isUsingPhysics = false;
        chunk = instance.getMyChunk();
        scene = null;
        physicsComponent = null;
        if (chunk != null) {
            scene = chunk.getScene();
            if (scene != null) {
                if (scene.isUsingPhysics()) {
                    physicsComponent = instance.getComponent(PhysicsComponent.class);
                    isUsingPhysics = ((physicsComponent != null) && (physicsComponent.getBody() != null));
                }
            }
        }
        position = instance.getAbsolutePos();
        velocity.set(0, 0, 0);
        if (isUsingPhysics) {
            physicsComponent.getBody().getAngularVelocity(velocity);
        }
        rotation = instance.getAbsoluteRotation();
        sources.stream().forEach((source) -> {
            source.setPosition(position);
            source.setVelocity(velocity);
            source.setOrientation(rotation);
        });
        if (scene != null && scene.isUsingPhysics()) {
            paused = scene.getPhysicsWorld().isSimulationPaused();
            newDeltaPitch = scene.getPhysicsWorld().getSimulationSpeed() - 1.0F;
            sources.stream().forEach((source) -> {
                if (source.isAffectedByPhysics()) {
                    if (paused && source.isPlaying()) {
                        source.pauseTemporarily();// FIXME StreamedSound stops
                        // forever
                    } else if (!paused && !source.isPlaying()) {
                        source.continuePlayingTemporarily();
                    }
                    source.setDeltaPitch(newDeltaPitch);
                }
                source.updateState();
            });
        }
        blocker.setBlocked(false);
    }

    @Override
    public final void onDelete(GameObject instance) {
        blocker.waitFor();
        deleteAllSources();
    }

    @Override
    public float getLevel() {
        return 1.0F;
    }

}
