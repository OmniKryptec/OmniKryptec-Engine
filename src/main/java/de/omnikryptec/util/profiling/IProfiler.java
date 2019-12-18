package de.omnikryptec.util.profiling;

public interface IProfiler {

    void dealWith(long nanoSecondsPassed, Object... objects);

    void writeData(StringBuilder builder, long count);
}
