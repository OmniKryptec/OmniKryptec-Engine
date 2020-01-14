package de.omnikryptec.libapi.openal;

public class OpenAL {

    private static boolean created = false;
    
    public void shutdown() {
    }
    
    public OpenAL() {
        if(created) {
            throw new IllegalStateException("OpenAL has already been created");
        }
    }
}
