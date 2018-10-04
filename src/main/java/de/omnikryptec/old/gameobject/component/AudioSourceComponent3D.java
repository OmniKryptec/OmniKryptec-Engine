/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.old.gameobject.component;

import java.util.ArrayList;

import org.joml.Vector3f;

import de.omnikryptec.old.audio.AudioSource;
import de.omnikryptec.old.gameobject.GameObject3D;
import de.omnikryptec.old.main.AbstractScene3D;
import de.omnikryptec.old.main.OmniKryptecEngine;
import de.omnikryptec.old.renderer.d3.RenderChunk3D;
import de.omnikryptec.old.util.Blocker;
import de.omnikryptec.old.util.Priority;

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
