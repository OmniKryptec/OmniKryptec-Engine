package omnikryptec.audio;

/**
 *
 * @author Panzer1119
 */
public interface ISound {
 
    public static enum SoundType {
        NORMAL,
        STREAM;
    }
    
    public boolean play(AudioSource source);
    public boolean stop(AudioSource source);
    public void update(long currentTime);
    public void delete(AudioSource source);
    public SoundType getType();
    public int getOpenALFormat();
    public String getName();
    public int getBufferID();
    public int getSize();
    public int getChannels();
    public int getBits();
    public int getFrequency();
    public float getLength();
            
}
