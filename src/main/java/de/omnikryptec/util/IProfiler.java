package de.omnikryptec.util;

public interface IProfiler {
    
    void dealWith(long nanoSecondsPassed, Object... objects);
    
    void writeData(StringBuilder builder);
}
