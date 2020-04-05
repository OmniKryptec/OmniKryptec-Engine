package de.omnikryptec.minigame;

import org.joml.Vector2fc;

import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.util.updater.Time;

public class AudioSystem extends AbstractComponentSystem {

    public AudioSystem() {
        super(Family.of(AudioComponent.class, PositionComponent.class));
    }

    private final ComponentMapper<AudioComponent> AC = new ComponentMapper<>(AudioComponent.class);
    private final ComponentMapper<PositionComponent> PC = new ComponentMapper<>(PositionComponent.class);
    private final ComponentMapper<MovementComponent> MC = new ComponentMapper<>(MovementComponent.class);

    @Override
    public void update(IECSManager iecsManager, Time time) {
        for (Entity e : this.entities) {
            AudioComponent ac = this.AC.get(e);
            PositionComponent pc = this.PC.get(e);
            Vector2fc ws = pc.transform.worldspacePos();
            ac.audioSource.setPosition(ws.x() / 20, ws.y() / 20, 0);
            if (e.hasComponent(this.MC.getType())) {
                MovementComponent mc = this.MC.get(e);
                ac.audioSource.setVelocity(mc.dx / 20, mc.dy / 20, 0);
            } else {
                ac.audioSource.setVelocity(0, 0, 0);
            }
            if (ac.removeAfterEnding && !ac.audioSource.isPlaying()) {
                ac.audioSource.deleteAndUnregister();
                iecsManager.removeEntity(e);
            }
        }
    }

}
