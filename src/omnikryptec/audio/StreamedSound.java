package omnikryptec.audio;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import omnikryptec.util.AdvancedFile;
import omnikryptec.util.AudioUtil;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.LogEntry.LogLevel;

/**
 * Streamed sound
 *
 * @author Panzer1119
 */
public class StreamedSound implements ISound {

    public static final int STANDARD_BUFFER_COUNT = 3;
    public static final int STANDARD_BUFFER_LENGTH = 1000;
    protected static final ArrayList<StreamedSound> streamedSounds = new ArrayList<>();

    private final ArrayList<Integer> buffersCreated = new ArrayList<>();
    private final String name;
    private final AudioSource source;
    private final AudioInputStream audioInputStream;
    private final AudioFormat audioFormat;
    private final ByteBuffer pcm;
    private final int bufferCount;
    private final int bufferTime;
    private final int format;
    private double lastTime = 0;

    private Instant lastTimeT = Instant.now();

    /**
     * Creates a StreamedSound Object
     *
     * @param name Name of the Sound
     * @param source AudioSource component
     * @param audioInputStream AudioInputStream
     */
    public StreamedSound(String name, AudioSource source, AudioInputStream audioInputStream) {
        this(name, source, audioInputStream, STANDARD_BUFFER_COUNT, STANDARD_BUFFER_LENGTH);
    }

    /**
     * Creates a StreamedSound Object
     *
     * @param name Name of the Sound
     * @param source AudioSource component
     * @param audioInputStream AudioInputStream
     * @param bufferCount Number of used Buffers
     * @param bufferTime Buffered time in milliseconds
     */
    public StreamedSound(String name, AudioSource source, AudioInputStream audioInputStream, int bufferCount,
            int bufferTime) {
        this.name = name;
        this.source = source;
        this.audioInputStream = audioInputStream;
        this.audioFormat = audioInputStream.getFormat();
        this.bufferCount = bufferCount;
        this.bufferTime = bufferTime;
        this.pcm = BufferUtils.createByteBuffer((int) (audioFormat.getSampleRate() * 4 * (this.bufferTime / 1000)));
        this.format = getOpenALFormat();
        streamedSounds.add(this);
    }

    private final void initBuffers() {
        for (int i = 0; i < bufferCount; i++) {
            final int bufferID = AL10.alGenBuffers();
            buffersCreated.add(bufferID);
            final boolean refilled = refillBuffer();
            AL10.alBufferData(bufferID, format, pcm, (int) audioFormat.getSampleRate());
            AL10.alSourceQueueBuffers(source.getSourceID(), bufferID);
        }
    }

    private final void deleteBuffers() {
        int bufferRemovedID = 0;
        while ((bufferRemovedID = AL10.alSourceUnqueueBuffers(source.getSourceID())) != 0) {
        }
        for (int bufferID : buffersCreated) {
            AL10.alDeleteBuffers(bufferID);
        }
        buffersCreated.clear();
    }

    /**
     * Returns the AudioInputStream
     *
     * @return AudioInputStream Audiostream
     */
    public final AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    /**
     * Returns the AudioFormat
     *
     * @return AudioFormat Audioformat
     */
    public final AudioFormat getAudioFormat() {
        return audioFormat;
    }

    @Override
    public final boolean play(AudioSource source) {
        initBuffers();
        return true;
    }

    @Override
    public final boolean stop(AudioSource source) {
        deleteBuffers();
        return true;
    }

    @Override
    public final SoundType getType() {
        return SoundType.STREAM;
    }

    @Override
    public final int getOpenALFormat() {
        return AudioUtil.audioFormatToOpenALFormat(audioFormat);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getBufferID() {
        return -1;
    }

    @Override
    public final int getSize() {
        return -1;
    }

    @Override
    public final int getChannels() {
        return audioFormat.getChannels();
    }

    @Override
    public final int getBits() {
        return audioFormat.getSampleSizeInBits();
    }

    @Override
    public final int getFrequency() {
        return (int) audioFormat.getSampleRate();
    }

    @Override
    public final float getLength() {
        return -1;
    }

    @Override
    public final void update(double currentTime) {
        if (buffersCreated.isEmpty()) {
            return;
        }
        final double duration = currentTime - lastTime;
        lastTime = currentTime;
        int bufferProcessedID = 0;
        while ((bufferProcessedID = AL10.alGetSourcei(source.getSourceID(), AL10.AL_BUFFERS_PROCESSED)) != 0) {
            final int bufferRemovedID = AL10.alSourceUnqueueBuffers(source.getSourceID());
            final boolean refilled = refillBuffer();
            if (refilled) {
                AL10.alBufferData(bufferRemovedID, format, pcm, (int) audioFormat.getSampleRate());
                AL10.alSourceQueueBuffers(source.getSourceID(), bufferRemovedID);
            } else if (Logger.isDebugMode()) {
                Logger.log("Buffer could not be refilled!", LogLevel.WARNING);
            }
        }
        if (!source.isPlaying()) {
            source.stop();
        }
    }

    private final boolean refillBuffer() {
        try {
            Instant now = Instant.now();
            lastTimeT = now;
            pcm.clear();
            final byte[] data = new byte[pcm.capacity()];
            final int read = audioInputStream.read(data);
            pcm.put(data);
            pcm.flip();
            return read >= 0;
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while refilling buffer: " + ex, ex);
            }
            return false;
        }
    }

    /**
     * Creates a new StreamSound of an ofAdvancedFile
     *
     * @param name String Name of the Sound
     * @param source AudioSource Destination for this StreamedSound
     * @param file ofAdvancedFile File to load from
     * @return StreamedSound Sound
     */
    public static final StreamedSound ofAdvancedFile(String name, AudioSource source, AdvancedFile file) {
        return ofInputStream(name, source, file.createInputStream());
    }

    /**
     * Creates a new StreamSound of an InputStream
     *
     * @param name String Name of the Sound
     * @param source AudioSource Destination for this StreamedSound
     * @param inputStream InputStream Stream to load from
     * @return StreamedSound Sound
     */
    public static final StreamedSound ofInputStream(String name, AudioSource source, InputStream inputStream) {
        return ofInputStream(name, source, inputStream, STANDARD_BUFFER_COUNT, STANDARD_BUFFER_LENGTH);
    }

    /**
     * Creates a new StreamSound of an InputStream
     *
     * @param name String Name of the Sound
     * @param source AudioSource Destination for this StreamedSound
     * @param inputStream InputStream Stream to load from
     * @param bufferCount Integer Number of used Buffers
     * @param bufferTime Integer Buffered time in milliseconds
     * @return StreamedSound Sound
     */
    public static final StreamedSound ofInputStream(String name, AudioSource source, InputStream inputStream,
            int bufferCount, int bufferTime) {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            return new StreamedSound(name, source, audioInputStream, bufferCount, bufferTime);
        } catch (Exception ex) {
            if (Logger.isDebugMode()) {
                Logger.logErr("Error while creating StreamedSound of InputStream: " + ex, ex);
            }
            return null;
        }
    }

    @Override
    public boolean delete(AudioSource source) {
        try {
            audioInputStream.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
