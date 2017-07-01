package omnikryptec.audio;

/**
 * Sound interface
 *
 * @author Panzer1119
 */
public interface ISound {

    /**
     * Sound type (cached or streamed)
     */
    public static enum SoundType {
        NORMAL, STREAM;
    }

    /**
     * This function gets called when this ISound is played
     *
     * @param source AudioSource Source which plays this ISound
     * @return <tt>true</tt> if the function was successfully
     */
    public boolean play(AudioSource source);

    /**
     * This function gets called when this ISound is no longer played
     *
     * @param source AudioSource Source which no langer plays this ISound
     * @return <tt>true</tt> if the function was successfully
     */
    public boolean stop(AudioSource source);

    /**
     * This function gets called when this ISound is updated
     *
     * @param currentTime Long Current time in milliseconds
     */
    public void update(double currentTime);

    /**
     * This function gets called when this ISound is deleted
     *
     * @param source AudioSource Source which got deleted
     * @return <tt>true</tt> if the function was successfully
     */
    public boolean delete(AudioSource source);

    /**
     * Returns the SoundType of this ISound
     *
     * @return
     */
    public SoundType getType();

    /**
     * Returns the OpenAL-Equivalent of the AudioFormat
     *
     * @return Integer OpenAL AudioFormat
     */
    public int getOpenALFormat();

    /**
     * Returns the name of this ISound
     *
     * @return String Name
     */
    public String getName();

    /**
     * Returns the bufferID of the used Buffer
     *
     * @return Integer BufferID
     */
    public int getBufferID();

    /**
     * Returns the size of the sound
     *
     * @return Integer Size in bits
     */
    public int getSize();

    /**
     * Returns the used channels (Mono = 1, Stereo = 2)
     *
     * @return Integer Used channels
     */
    public int getChannels();

    /**
     * Returns the sampleSizeInBits
     *
     * @return Integer SampleSizeInBits
     */
    public int getBits();

    /**
     * Returns the frequency of the sound
     *
     * @return Integer Frequency
     */
    public int getFrequency();

    /**
     * Returns the length of the sound
     *
     * @return Float Length in seconds
     */
    public float getLength();

}
