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
        this.formatId = OpenALUtil.audioFormatToOpenALFormat(this.audioFormat);
        this.soundType = type;
        registerThisAsAutodeletable();
    }

    abstract void attach(AudioSource as);

    abstract void detach();

    public SoundType getType() {
        return this.soundType;
    }

    public final int getChannels() {
        return this.audioFormat.getChannels();
    }

    public final int getBits() {
        return this.audioFormat.getSampleSizeInBits();
    }

    public final int getFrequency() {
        return (int) this.audioFormat.getSampleRate();
    }

    public int getOpenALFormat() {
        return this.formatId;
    }
}
