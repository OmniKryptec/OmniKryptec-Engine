package de.omnikryptec.reeee;

import java.util.ArrayList;
import java.util.List;

import de.omnikryptec.util.profiling.ConsoleGraphing;

public class GraphTest {
    public static void main(String[] args) {
        List<Long> data = new ArrayList<>();
        for (int i = -5; i <= 5; i++) {
            data.add((long) i);
        }
        System.out.println(ConsoleGraphing.graph(100, 10, data, "Test:", "x-Unit", "y-Unit"));
    }
}
