package de.omnikryptec.minigame;

import de.omnikryptec.ecs.component.Component;
import de.omnikryptec.libapi.openal.AudioSource;

public class AudioComponent implements Component {
    public static int count=0;
    public AudioSource audioSource = new AudioSource();
    public boolean removeAfterEnding = true;
}
