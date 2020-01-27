package de.omnikryptec.libapi.openal;

import javax.sound.sampled.AudioFormat;

import de.omnikryptec.libapi.exposed.Deletable;

public abstract class ALSound implements Deletable {
    /**
     * Sound type (cached or streamed)
     */
    public static enum SoundType {
        CACHED, STREAM;
    }
    
    private final AudioFormat audioFormat;
    private final int formatId;
    private final SoundType soundType;
    
    public ALSound(AudioFormat format, SoundType type) {
        this.audioFormat = format;
        this.formatId = OpenALUtil.audioFormatToOpenALFormat(audioFormat);
        this.soundType = type;
        registerThisAsAutodeletable();
    }
    abstract void attach(AudioSource as);
    
    abstract void detach();
    
    public SoundType getType() {
        return soundType;
    }
    
    public final int getChannels() {
        return audioFormat.getChannels();
    }
    
    public final int getBits() {
        return audioFormat.getSampleSizeInBits();
    }
    
    public final int getFrequency() {
        return (int) audioFormat.getSampleRate();
    }
    
    public int getOpenALFormat() {
        return formatId;
    }
}
