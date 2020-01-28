package de.omnikryptec.minigame;

import java.util.BitSet;

import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.libapi.openal.AudioSource;
import de.omnikryptec.libapi.openal.Sound;
import de.omnikryptec.util.math.Mathf;
import de.omnikryptec.util.updater.Time;

public class AudioTestSystem extends AbstractComponentSystem {
    
    protected AudioTestSystem(Sound sound) {
        super(new BitSet());
        this.testSource = new AudioSource();
        this.testSource.setLooping(true);
        this.testSource.play(sound);
    }
    
    private AudioSource testSource;
    private final float radius = 85;
    private  float vel = 20;
    
    private float tmp = 0;
    
    @Override
    public void update(IECSManager iecsManager, Time time) {
        tmp += time.deltaf * vel;
        vel += time.deltaf * 3;
        testSource.setVelocity(vel, 0, 0);
        testSource.setPosition(Mathf.pingpong(tmp, radius)-radius/2, -10, 0);
    }
    
}
